import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import {
  isAuthenticated,
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
  const [authenticated, setAuthenticated] = useState(false); // ← reactive state

  const loadUser = useCallback(async () => {
    if (!isAuthenticated()) {
      setUser(null);
      setAuthenticated(false);
      setLoading(false);
      return;
    }

    // Fast path: parse ID token from cookie (no network request)
    const fromToken = parseIdToken();
    if (fromToken) {
      setUser(fromToken);
      setAuthenticated(true);
      setLoading(false);
      return;
    }

    // Slow path: call userinfo endpoint
    try {
      const info = await fetchUserInfo();
      setUser(info);
      setAuthenticated(true);
    } catch {
      // Token may be expired — try refreshing
      const refreshed = await refreshAccessToken();
      if (refreshed) {
        try {
          const info = await fetchUserInfo();
          setUser(info);
          setAuthenticated(true);
        } catch {
          setUser(null);
          setAuthenticated(false);
        }
      } else {
        setUser(null);
        setAuthenticated(false);
      }
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadUser(); }, [loadUser]);

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        authenticated, // ← comes from state, not from isAuthenticated()
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