import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import {
  isAuthenticated,
  isAccessTokenExpired,
  fetchUserInfo,
  parseIdToken,
  logout as authLogout,
  refreshAccessToken,
  type UserInfo,
} from '../services/auth.service';

interface AuthContextValue {
  user: UserInfo | null;
  loading: boolean;
  authenticated: boolean;
  logout: () => void;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser]                   = useState<UserInfo | null>(null);
  const [loading, setLoading]             = useState(true);
  const [authenticated, setAuthenticated] = useState(false);

  const loadUser = useCallback(async () => {
    if (!isAuthenticated()) {
      setUser(null);
      setAuthenticated(false);
      setLoading(false);
      return;
    }

    // Si el access token está expirado, refrescar primero
    if (isAccessTokenExpired()) {
      const refreshed = await refreshAccessToken();
      if (!refreshed) {
        setUser(null);
        setAuthenticated(false);
        setLoading(false);
        return;
      }
    }

    // Fast path: parsear ID token desde cookie
    const fromToken = parseIdToken();
    if (fromToken) {
      setUser(fromToken);
      setAuthenticated(true);
      setLoading(false);
      return;
    }

    // Slow path: llamar al endpoint userinfo
    try {
      const info = await fetchUserInfo();
      setUser(info);
      setAuthenticated(true);
    } catch {
      setUser(null);
      setAuthenticated(false);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadUser();

    // Refresca el token cada 4 minutos en segundo plano
    const interval = setInterval(async () => {
      if (isAuthenticated() && isAccessTokenExpired()) {
        const refreshed = await refreshAccessToken();
        if (!refreshed) authLogout();
      }
    }, 4 * 60 * 1000);

    return () => clearInterval(interval);
  }, [loadUser]);

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        authenticated,
        logout: authLogout,
        refreshUser: loadUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}