import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../services/api.service';
import { Order } from '../../../models/order.model';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.css']
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  displayedColumns: string[] = ['productName', 'quantity', 'price', 'total'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id');
    if (orderId) {
      this.loadOrder(parseInt(orderId, 10));
    }
  }

  loadOrder(orderId: number): void {
    this.apiService.getOrderById(orderId).subscribe({
      next: (order) => {
        this.order = order;
      },
      error: (error) => {
        this.snackBar.open('Failed to load order', 'Close', { duration: 3000 });
        this.router.navigate(['/user/home']);
      }
    });
  }

  cancelOrder(): void {
    if (!this.order) return;

    if (this.order.orderStatus !== 'Processing') {
      this.snackBar.open('Only Processing orders can be canceled', 'Close', { duration: 3000 });
      return;
    }

    if (confirm('Are you sure you want to cancel this order?')) {
      this.apiService.cancelOrder(this.order.orderId).subscribe({
        next: () => {
          this.snackBar.open('Order canceled successfully', 'Close', { duration: 3000 });
          this.loadOrder(this.order!.orderId);
        },
        error: (error) => {
          this.snackBar.open(error.error?.error || 'Failed to cancel order', 'Close', { duration: 3000 });
        }
      });
    }
  }

  viewProduct(productId: number): void {
    this.router.navigate(['/user/products', productId]);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString();
  }

  getItemTotal(item: any): number {
    return item.price * item.quantity;
  }
}

