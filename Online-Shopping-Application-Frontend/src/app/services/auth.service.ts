import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from './api.service';
import { LoginRequest, RegisterRequest } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  login(email: string, password: string) {
    const credentials: LoginRequest = {
      username: email, // 后端要求 username 字段实际是 email
      password: password
    };
    return this.apiService.login(credentials);
  }

  register(username: string, email: string, password: string) {
    const userData: RegisterRequest = {
      username,
      email,
      password
    };
    return this.apiService.register(userData);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('email');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    this.apiService.setToken(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN';
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getEmail(): string | null {
    return localStorage.getItem('email');
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  getUserId(): number | null {
    const userId = localStorage.getItem('userId');
    return userId ? parseInt(userId, 10) : null;
  }

  setUserInfo(token: string, role: string, email: string, userId?: number, username?: string): void {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
    localStorage.setItem('email', email);
    if (userId) {
      localStorage.setItem('userId', userId.toString());
    }
    if (username) {
      localStorage.setItem('username', username);
    }
    this.apiService.setToken(token);
  }
}

