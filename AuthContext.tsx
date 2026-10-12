import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from "react";
import { TOKEN_KEY } from "../services/api";
import { authApi } from "../services/bizflow";
import type { User } from "../types";

interface AuthState {
  user: User | null;
  loading: boolean; // true while we check a saved token on page load
  login: (email: string, password: string) => Promise<void>;
  register: (data: { name: string; business_name: string; email: string; password: string }) => Promise<void>;
  logout: () => void;
  setUser: (u: User) => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // On first load, if a token is saved, ask the backend who it belongs to.
  useEffect(() => {
    if (!localStorage.getItem(TOKEN_KEY)) {
      setLoading(false);
      return;
    }
    authApi
      .me()
      .then(setUser)
      .catch(() => localStorage.removeItem(TOKEN_KEY))
      .finally(() => setLoading(false));
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    const res = await authApi.login(email, password);
    localStorage.setItem(TOKEN_KEY, res.access_token);
    setUser(res.user);
  }, []);

  const register = useCallback(
    async (data: { name: string; business_name: string; email: string; password: string }) => {
      const res = await authApi.register(data);
      localStorage.setItem(TOKEN_KEY, res.access_token);
      setUser(res.user);
    },
    [],
  );

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, setUser }}>{children}</AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
