// Match these interfaces to your backend DTOs
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string; // Include if your backend sends it
  userId: number; // Include if your backend sends it
  name: string; // Include if your backend sends it
  email: string; // Include if your backend sends it
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role?: string; // optional since backend sets default
}
