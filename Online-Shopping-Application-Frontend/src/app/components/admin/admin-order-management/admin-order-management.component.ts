import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../services/api.service';
import { Order } from '../../../models/order.model';

@Component({
  selector: 'app-admin-order-management',
  templateUrl: './admin-order-management.component.html',
  styleUrls: ['./admin-order-management.component.css']
})
export class AdminOrderManagementComponent implements OnInit {
  orders: Order[] = [];
  displayedColumns: string[] = ['orderId', 'datePlaced', 'orderStatus', 'totalPrice', 'actions'];

  constructor(
    private apiService: ApiService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.apiService.getAdminOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
      },
      error: (error) => {
        this.snackBar.open('Failed to load orders', 'Close', { duration: 3000 });
      }
    });
  }

  viewOrder(orderId: number): void {
    // Navigate to order detail (reuse user order detail component)
    this.router.navigate(['/user/orders', orderId]);
  }

  cancelOrder(orderId: number): void {
    if (confirm('Are you sure you want to cancel this order?')) {
      this.apiService.updateOrderStatus(orderId, 'Canceled').subscribe({
        next: () => {
          this.snackBar.open('Order canceled successfully', 'Close', { duration: 3000 });
          this.loadOrders();
        },
        error: (error) => {
          this.snackBar.open(error.error?.error || 'Failed to cancel order', 'Close', { duration: 3000 });
        }
      });
    }
  }

  completeOrder(orderId: number): void {
    if (confirm('Are you sure you want to complete this order?')) {
      this.apiService.updateOrderStatus(orderId, 'Completed').subscribe({
        next: () => {
          this.snackBar.open('Order completed successfully', 'Close', { duration: 3000 });
          this.loadOrders();
        },
        error: (error) => {
          this.snackBar.open(error.error?.error || 'Failed to complete order', 'Close', { duration: 3000 });
        }
      });
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString();
  }
}

