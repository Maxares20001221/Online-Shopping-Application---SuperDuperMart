import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  registerForm: FormGroup;
  isLoginMode = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
    }
    return null;
  }

  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
  }

  onLogin(): void {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.authService.login(email, password).subscribe({
        next: (response) => {
          // 登录响应现在包含 userId 和 username
          const usernameFromResponse = response.username || (email?.split('@')[0] ?? '');
          this.authService.setUserInfo(response.token, response.role, email, response.userId, usernameFromResponse);
          this.snackBar.open('Login successful!', 'Close', { duration: 3000 });
          this.navigateAfterLogin(response.role);
        },
        error: (error) => {
          this.snackBar.open(error.error?.error || 'Login failed', 'Close', { duration: 3000 });
        }
      });
    }
  }

  onRegister(): void {
    if (this.registerForm.valid) {
      const { username, email, password } = this.registerForm.value;
      this.authService.register(username, email, password).subscribe({
        next: (response) => {
          this.snackBar.open(response.message || 'Registration successful!', 'Close', { duration: 3000 });
          this.isLoginMode = true;
          this.registerForm.reset();
        },
        error: (error) => {
          console.error('Registration error:', error);
          let errorMessage = 'Registration failed';
          
          if (error.error) {
            if (error.error.error) {
              errorMessage = error.error.error;
            } else if (error.error.message) {
              errorMessage = error.error.message;
            } else if (typeof error.error === 'string') {
              errorMessage = error.error;
            }
          } else if (error.message) {
            errorMessage = error.message;
          }
          
          // 检查是否是网络错误
          if (error.status === 0) {
            errorMessage = 'Cannot connect to server. Please make sure the backend is running on http://localhost:8080';
          } else if (error.status === 400) {
            errorMessage = errorMessage || 'Invalid registration data';
          } else if (error.status === 409) {
            errorMessage = errorMessage || 'Username or email already exists';
          }
          
          this.snackBar.open(errorMessage, 'Close', { duration: 5000 });
        }
      });
    }
  }

  private navigateAfterLogin(role: string): void {
    if (role === 'ADMIN') {
      this.router.navigate(['/admin/home']);
    } else {
      this.router.navigate(['/user/home']);
    }
  }
}

