export interface User {
  userId: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

export interface LoginRequest {
  username: string; // 实际是 email
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  message: string;
  token: string;
  role: string;
  userId?: number;
  username?: string;
}

