// ─────────────────────────────────────────────────────────────────
//  Auth Service — Keycloak PKCE (Authorization Code Flow)
//  Handles token exchange, cookie persistence, and refresh.
// ─────────────────────────────────────────────────────────────────

import Cookies from 'js-cookie';
import keycloakConfig from '../keycloak.config';

// Cookie names
export const COOKIE_ACCESS_TOKEN  = 'll_access_token';
export const COOKIE_REFRESH_TOKEN = 'll_refresh_token';
export const COOKIE_ID_TOKEN      = 'll_id_token';

const COOKIE_OPTIONS: Cookies.CookieAttributes = {
  expires: 1,
  secure: window.location.protocol === 'https:',
  sameSite: 'Strict',
  path: '/',
};

// ── Types ────────────────────────────────────────────────────────

export interface TokenSet {
  access_token: string;
  refresh_token?: string;
  id_token?: string;
  expires_in?: number;
  token_type?: string;
}

export interface UserInfo {
  sub: string;
  preferred_username?: string;
  given_name?: string;
  family_name?: string;
  name?: string;
  email?: string;
  [key: string]: unknown;
}

// ── PKCE helpers ─────────────────────────────────────────────────

function generateCodeVerifier(): string {
  const array = new Uint8Array(64);
  window.crypto.getRandomValues(array);
  return btoa(String.fromCharCode(...array))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '');
}

async function generateCodeChallenge(verifier: string): Promise<string> {
  const data = new TextEncoder().encode(verifier);
  const digest = await window.crypto.subtle.digest('SHA-256', data);
  return btoa(String.fromCharCode(...new Uint8Array(digest)))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '');
}

// ── URLs ─────────────────────────────────────────────────────────

function tokenEndpoint(): string {
  return `${keycloakConfig.url}/realms/${keycloakConfig.realm}/protocol/openid-connect/token`;
}

function authEndpoint(): string {
  return `${keycloakConfig.url}/realms/${keycloakConfig.realm}/protocol/openid-connect/auth`;
}

function logoutEndpoint(): string {
  return `${keycloakConfig.url}/realms/${keycloakConfig.realm}/protocol/openid-connect/logout`;
}

function userInfoEndpoint(): string {
  return `${keycloakConfig.url}/realms/${keycloakConfig.realm}/protocol/openid-connect/userinfo`;
}

function redirectUri(): string {
  return `${window.location.origin}/callback`;
}

// ── Core auth functions ───────────────────────────────────────────

export async function initiateLogin(): Promise<void> {
  const verifier  = generateCodeVerifier();
  const challenge = await generateCodeChallenge(verifier);
  const state     = generateCodeVerifier().slice(0, 32);

  sessionStorage.setItem('pkce_verifier', verifier);
  sessionStorage.setItem('pkce_state', state);

  const params = new URLSearchParams({
    response_type: 'code',
    client_id:      keycloakConfig.clientId,
    redirect_uri:   redirectUri(),
    scope:          'openid profile email',
    code_challenge: challenge,
    code_challenge_method: 'S256',
    state,
  });

  window.location.href = `${authEndpoint()}?${params.toString()}`;
}

export async function handleCallback(code: string, returnedState: string): Promise<TokenSet> {
  const verifier = sessionStorage.getItem('pkce_verifier');
  const state    = sessionStorage.getItem('pkce_state');

  if (!verifier || !state) throw new Error('PKCE verifier missing. Please log in again.');
  if (returnedState !== state) throw new Error('State mismatch. Possible CSRF attack.');

  sessionStorage.removeItem('pkce_verifier');
  sessionStorage.removeItem('pkce_state');

  const body = new URLSearchParams({
    grant_type:    'authorization_code',
    client_id:      keycloakConfig.clientId,
    redirect_uri:   redirectUri(),
    code,
    code_verifier:  verifier,
  });

  const response = await fetch(tokenEndpoint(), {
    method:  'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body:    body.toString(),
  });

  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.error_description || 'Token exchange failed');
  }

  const tokens: TokenSet = await response.json();
  saveTokens(tokens);
  return tokens;
}

export function saveTokens(tokens: TokenSet): void {
  Cookies.set(COOKIE_ACCESS_TOKEN, tokens.access_token, COOKIE_OPTIONS);
  if (tokens.refresh_token) Cookies.set(COOKIE_REFRESH_TOKEN, tokens.refresh_token, COOKIE_OPTIONS);
  if (tokens.id_token)      Cookies.set(COOKIE_ID_TOKEN, tokens.id_token, COOKIE_OPTIONS);
}

export function getTokens(): Partial<TokenSet> {
  return {
    access_token:  Cookies.get(COOKIE_ACCESS_TOKEN),
    refresh_token: Cookies.get(COOKIE_REFRESH_TOKEN),
    id_token:      Cookies.get(COOKIE_ID_TOKEN),
  };
}

export function clearTokens(): void {
  Cookies.remove(COOKIE_ACCESS_TOKEN,  { path: '/' });
  Cookies.remove(COOKIE_REFRESH_TOKEN, { path: '/' });
  Cookies.remove(COOKIE_ID_TOKEN,      { path: '/' });
}

export function isAuthenticated(): boolean {
  return !!Cookies.get(COOKIE_ACCESS_TOKEN);
}

/**
 * Decode the access token and check if it's expired (with 30s buffer).
 */
export function isAccessTokenExpired(): boolean {
  const accessToken = Cookies.get(COOKIE_ACCESS_TOKEN);
  if (!accessToken) return true;

  try {
    const payload = accessToken.split('.')[1];
    const decoded = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    return decoded.exp * 1000 < Date.now() + 30_000;
  } catch {
    return true;
  }
}

export async function refreshAccessToken(): Promise<TokenSet | null> {
  const refreshToken = Cookies.get(COOKIE_REFRESH_TOKEN);
  if (!refreshToken) return null;

  const body = new URLSearchParams({
    grant_type:     'refresh_token',
    client_id:       keycloakConfig.clientId,
    refresh_token:   refreshToken,
  });

  const response = await fetch(tokenEndpoint(), {
    method:  'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body:    body.toString(),
  });

  if (!response.ok) {
    clearTokens();
    return null;
  }

  const tokens: TokenSet = await response.json();
  saveTokens(tokens);
  return tokens;
}

export async function fetchUserInfo(): Promise<UserInfo> {
  const accessToken = Cookies.get(COOKIE_ACCESS_TOKEN);
  if (!accessToken) throw new Error('No access token');

  const response = await fetch(userInfoEndpoint(), {
    headers: { Authorization: `Bearer ${accessToken}` },
  });

  if (!response.ok) throw new Error('Failed to fetch user info');
  return response.json();
}

export function parseIdToken(): UserInfo | null {
  const idToken = Cookies.get(COOKIE_ID_TOKEN);
  if (!idToken) return null;

  try {
    const payload = idToken.split('.')[1];
    const decoded = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    return decoded as UserInfo;
  } catch {
    return null;
  }
}

export function logout(): void {
  const idToken = Cookies.get(COOKIE_ID_TOKEN);
  clearTokens();

  const params = new URLSearchParams({
    client_id:    keycloakConfig.clientId,
    post_logout_redirect_uri: window.location.origin + '/login',
  });

  if (idToken) params.set('id_token_hint', idToken);

  window.location.href = `${logoutEndpoint()}?${params.toString()}`;
}