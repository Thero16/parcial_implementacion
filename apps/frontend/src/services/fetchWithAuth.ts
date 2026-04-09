import { logout, isAccessTokenExpired, refreshAccessToken, getTokens } from './auth.service';

let isRefreshing = false;
let refreshPromise: Promise<boolean> | null = null;

async function ensureFreshToken(): Promise<boolean> {
  if (!isAccessTokenExpired()) return true;


  if (isRefreshing && refreshPromise) return refreshPromise;

  isRefreshing = true;
  refreshPromise = refreshAccessToken()
    .then((tokens) => !!tokens)
    .catch(() => false)
    .finally(() => {
      isRefreshing = false;
      refreshPromise = null;
    });

  return refreshPromise;
}

export async function fetchWithAuth(
  input: RequestInfo | URL,
  init: RequestInit = {}
): Promise<Response> {

  const ok = await ensureFreshToken();
  if (!ok) {
    logout();
    throw new Error('Session expired. Please log in again.');
  }


  const { access_token } = getTokens();
  const headers = new Headers(init.headers);
  headers.set('Authorization', `Bearer ${access_token}`);
  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  const response = await fetch(input, { ...init, headers });

  
  if (response.status === 401) {
    const refreshed = await refreshAccessToken();
    if (!refreshed) {
      logout();
      throw new Error('Session expired. Please log in again.');
    }

    const { access_token: newToken } = getTokens();
    headers.set('Authorization', `Bearer ${newToken}`);
    return fetch(input, { ...init, headers });
  }

  return response;
}