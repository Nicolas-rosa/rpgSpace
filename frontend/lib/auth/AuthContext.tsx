"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { useRouter } from "next/navigation";
import { loginRequest, logoutRequest, meRequest, registerRequest } from "@/lib/api/auth";
import { tokenStorage } from "@/lib/auth/storage";
import type { UserResponse } from "@/lib/auth/types";

interface AuthContextValue {
  user: UserResponse | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const router = useRouter();
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const storedUser = tokenStorage.getUser();
    const hasTokens = Boolean(tokenStorage.getAccessToken() && tokenStorage.getRefreshToken());

    if (!hasTokens) {
      tokenStorage.clear();
      setUser(null);
      setIsLoading(false);
      return;
    }

    setUser(storedUser);

    meRequest()
      .then((freshUser) => {
        tokenStorage.setUser(freshUser);
        setUser(freshUser);
      })
      .catch(() => {
        tokenStorage.clear();
        setUser(null);
      })
      .finally(() => setIsLoading(false));
  }, []);

  const login = useCallback(
    async (email: string, password: string) => {
      const auth = await loginRequest(email, password);
      tokenStorage.saveSession(auth);
      setUser(auth.user);
      router.push("/dashboard");
    },
    [router]
  );

  const register = useCallback(
    async (username: string, email: string, password: string) => {
      const auth = await registerRequest(username, email, password);
      tokenStorage.saveSession(auth);
      setUser(auth.user);
      router.push("/dashboard");
    },
    [router]
  );

  const logout = useCallback(async () => {
    const refreshToken = tokenStorage.getRefreshToken();
    if (refreshToken) {
      try {
        await logoutRequest(refreshToken);
      } catch {
        // Ignore server errors on logout; clear local session regardless.
      }
    }
    tokenStorage.clear();
    setUser(null);
    router.push("/login");
  }, [router]);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: Boolean(user),
      isLoading,
      login,
      register,
      logout,
    }),
    [user, isLoading, login, register, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth deve ser usado dentro de <AuthProvider>.");
  }
  return context;
}