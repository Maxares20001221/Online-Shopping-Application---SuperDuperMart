import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { Order } from '../../../models/order.model';
import { Stats } from '../../../models/stats.model';

@Component({
  selector: 'app-user-home',
  templateUrl: './user-home.component.html',
  styleUrls: ['./user-home.component.css']
})
export class UserHomeComponent implements OnInit {
  orders: Order[] = [];
  frequentProducts: Stats[] = [];
  recentProducts: Stats[] = [];
  displayedColumns: string[] = ['orderId', 'datePlaced', 'orderStatus', 'totalPrice', 'actions'];
  username: string = '';
  email: string = '';
  showDebugInfo: boolean = false;

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadData();
  }

  loadUserInfo(): void {
    const username = this.authService.getUsername();
    const email = this.authService.getEmail();

    if (username) {
      this.username = username;
    } else if (email) {
      this.username = email.split('@')[0];
    }

    if (email) {
      this.email = email;
    }
  }

  loadData(): void {
    const userId = this.authService.getUserId();
    
    if (!userId) {
      this.snackBar.open('User information not found. Please login again.', 'Close', { duration: 3000 });
      this.router.navigate(['/login']);
      return;
    }

    this.loadDataWithUserId(userId);
  }

  private loadDataWithUserId(userId: number): void {

    // Load orders for current user
    this.apiService.getAllOrders(userId).subscribe({
      next: (orders) => {
        this.orders = orders;
      },
      error: (error) => {
        console.error('Load orders error:', error);
        this.snackBar.open('Failed to load orders', 'Close', { duration: 3000 });
      }
    });

    // Load frequent products
    this.apiService.getFrequentProducts(userId, 3).subscribe({
      next: (products) => {
        this.frequentProducts = products;
      },
      error: (error) => {
        console.error('Failed to load frequent products', error);
      }
    });

    // Load recent products
    this.apiService.getRecentProducts(userId, 3).subscribe({
      next: (products) => {
        this.recentProducts = products;
      },
      error: (error) => {
        console.error('Failed to load recent products', error);
      }
    });
  }

  viewOrder(orderId: number): void {
    this.router.navigate(['/user/orders', orderId]);
  }

  cancelOrder(orderId: number): void {
    if (confirm('Are you sure you want to cancel this order?')) {
      this.apiService.cancelOrder(orderId).subscribe({
        next: () => {
          this.snackBar.open('Order canceled successfully', 'Close', { duration: 3000 });
          this.loadData();
        },
        error: (error) => {
          this.snackBar.open(error.error?.error || 'Failed to cancel order', 'Close', { duration: 3000 });
        }
      });
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString();
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'Completed':
        return 'primary';
      case 'Canceled':
        return 'warn';
      default:
        return 'accent';
    }
  }

  toggleDebugInfo(): void {
    this.showDebugInfo = !this.showDebugInfo;
  }

  getToken(): string | null {
    return this.authService.getToken();
  }

  getUserId(): number | null {
    return this.authService.getUserId();
  }

  getRole(): string | null {
    return this.authService.getRole();
  }

  copyToken(): void {
    const token = this.getToken();
    if (token) {
      navigator.clipboard.writeText(token).then(() => {
        this.snackBar.open('Token copied to clipboard!', 'Close', { duration: 2000 });
      });
    }
  }
}

