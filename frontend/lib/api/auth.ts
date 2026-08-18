import { apiClient } from "@/lib/api/client";
import type { ApiResponse, AuthResponse, UserResponse } from "@/lib/auth/types";

export async function loginRequest(email: string, password: string): Promise<AuthResponse> {
  const { data } = await apiClient.post<ApiResponse<AuthResponse>>("/auth/login", { email, password });
  return data.data;
}

export async function registerRequest(username: string, email: string, password: string): Promise<AuthResponse> {
  const { data } = await apiClient.post<ApiResponse<AuthResponse>>("/auth/register", { username, email, password });
  return data.data;
}

export async function logoutRequest(refreshToken: string): Promise<void> {
  await apiClient.post("/auth/logout", { refreshToken });
}

export async function meRequest(): Promise<UserResponse> {
  const { data } = await apiClient.get<ApiResponse<UserResponse>>("/auth/me");
  return data.data;
}