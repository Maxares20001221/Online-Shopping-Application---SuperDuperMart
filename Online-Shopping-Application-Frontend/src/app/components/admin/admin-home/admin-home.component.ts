import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../services/api.service';
import { Order } from '../../../models/order.model';
import { Stats } from '../../../models/stats.model';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent implements OnInit {
  orders: Order[] = [];
  mostProfitableProduct: Stats | null = null;
  top3PopularProducts: Stats[] = [];
  totalSoldItems = 0;
  displayedColumns: string[] = ['orderId', 'datePlaced', 'orderStatus', 'totalPrice', 'actions'];

  constructor(
    private apiService: ApiService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    // Load orders
    this.apiService.getAdminOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.calculateTotalSoldItems(orders);
      },
      error: (error) => {
        this.snackBar.open('Failed to load orders', 'Close', { duration: 3000 });
      }
    });

    // Load most profitable product
    this.apiService.getMostProfitableProducts(1).subscribe({
      next: (products) => {
        this.mostProfitableProduct = products.length > 0 ? products[0] : null;
      },
      error: (error) => {
        console.error('Failed to load profitable products', error);
      }
    });

    // Load top 3 popular products
    this.apiService.getMostPopularProducts(3).subscribe({
      next: (products) => {
        this.top3PopularProducts = products;
      },
      error: (error) => {
        console.error('Failed to load popular products', error);
      }
    });
  }

  calculateTotalSoldItems(orders: Order[]): void {
    this.totalSoldItems = orders
      .filter(order => order.orderStatus === 'Completed')
      .reduce((total, order) => {
        return total + order.items.reduce((sum, item) => sum + item.quantity, 0);
      }, 0);
  }

  viewOrder(orderId: number): void {
    this.router.navigate(['/admin/orders']);
  }

  cancelOrder(orderId: number): void {
    if (confirm('Are you sure you want to cancel this order?')) {
      this.apiService.updateOrderStatus(orderId, 'Canceled').subscribe({
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

  completeOrder(orderId: number): void {
    if (confirm('Are you sure you want to complete this order?')) {
      this.apiService.updateOrderStatus(orderId, 'Completed').subscribe({
        next: () => {
          this.snackBar.open('Order completed successfully', 'Close', { duration: 3000 });
          this.loadData();
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

