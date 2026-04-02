import { getTokens } from "../services/auth.service";

export function getUserRoles(): string[] {
  const token = getTokens().access_token;

  if (!token) return [];

  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.realm_access?.roles || [];
  } catch {
    return [];
  }
}

export function hasRole(role: string): boolean {
  return getUserRoles().includes(role);
}