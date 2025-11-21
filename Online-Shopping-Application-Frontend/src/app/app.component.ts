import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'SuperDuperMart';

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  logout(): void {
    this.authService.logout();
  }

  navigateToUserHome(): void {
    if (this.authService.isAuthenticated() && !this.authService.isAdmin()) {
      this.router.navigate(['/user/home']);
    }
  }

  navigateToAdminHome(): void {
    if (this.authService.isAuthenticated() && this.authService.isAdmin()) {
      this.router.navigate(['/admin/home']);
    }
  }
}

