import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';
import { WatchlistService } from '../../../services/watchlist.service';
import { ProductDTO } from '../../../models/product.model';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  product: ProductDTO | null = null;
  quantity: number = 1;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiService: ApiService,
    private authService: AuthService,
    private cartService: CartService,
    private watchlistService: WatchlistService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const productId = this.route.snapshot.paramMap.get('id');
    if (productId) {
      this.loadProduct(parseInt(productId, 10));
    }
  }

  loadProduct(productId: number): void {
    this.apiService.getProductById(productId).subscribe({
      next: (product) => {
        this.product = product;
      },
      error: (error) => {
        this.snackBar.open('Failed to load product', 'Close', { duration: 3000 });
        this.router.navigate(['/user/products']);
      }
    });
  }

  addToCart(): void {
    if (!this.product) return;

    if (!this.authService.isAuthenticated()) {
      this.snackBar.open('Please login first', 'Close', { duration: 3000 });
      return;
    }

    let userId = this.authService.getUserId();
    
    // If userId is not found, try to get it from API
    if (!userId) {
      const email = this.authService.getEmail();
      if (email) {
        this.apiService.getAllUsers().subscribe({
          next: (users) => {
            const user = users.find(u => u.email === email);
            if (user) {
              localStorage.setItem('userId', user.userId.toString());
              this.addToCartWithUserId(user.userId);
            } else {
              this.snackBar.open('User not found. Please login again', 'Close', { duration: 3000 });
            }
          },
          error: () => {
            this.snackBar.open('Cannot get user information. Please login again', 'Close', { duration: 3000 });
          }
        });
      } else {
        this.snackBar.open('Please login first', 'Close', { duration: 3000 });
      }
      return;
    }

    this.addToCartWithUserId(userId);
  }

  private addToCartWithUserId(userId: number): void {
    if (!this.product) return;

    // Add to backend cart
    this.apiService.addToCart(userId, this.product.productId, this.quantity).subscribe({
      next: () => {
        this.snackBar.open(`${this.product!.name} added to cart`, 'Close', { duration: 2000 });
        this.cartService.notifyUpdate(); // Notify cart component to refresh
      },
      error: (error) => {
        console.error('Add to cart error:', error);
        let errorMessage = error.error?.error || 'Failed to add to cart';
        if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please make sure the backend is running.';
        }
        this.snackBar.open(errorMessage, 'Close', { duration: 3000 });
      }
    });
  }

  addToWatchlist(): void {
    if (!this.product) return;

    if (!this.authService.isAuthenticated()) {
      this.snackBar.open('Please login first', 'Close', { duration: 3000 });
      return;
    }

    let userId = this.authService.getUserId();
    
    // If userId is not found, try to get it from API
    if (!userId) {
      const email = this.authService.getEmail();
      if (email) {
        this.apiService.getAllUsers().subscribe({
          next: (users) => {
            const user = users.find(u => u.email === email);
            if (user) {
              localStorage.setItem('userId', user.userId.toString());
              this.addToWatchlistWithUserId(user.userId);
            } else {
              this.snackBar.open('User not found. Please login again', 'Close', { duration: 3000 });
            }
          },
          error: () => {
            this.snackBar.open('Cannot get user information. Please login again', 'Close', { duration: 3000 });
          }
        });
      } else {
        this.snackBar.open('Please login first', 'Close', { duration: 3000 });
      }
      return;
    }

    this.addToWatchlistWithUserId(userId);
  }

  private addToWatchlistWithUserId(userId: number): void {
    if (!this.product) return;

    this.apiService.addToWatchlist(userId, this.product.productId).subscribe({
      next: () => {
        this.snackBar.open(`${this.product!.name} added to watchlist`, 'Close', { duration: 2000 });
        this.watchlistService.notifyUpdate(); // Notify subscribers to refresh watchlist
      },
      error: (error) => {
        console.error('Add to watchlist error:', error);
        let errorMessage = error.error?.error || 'Failed to add to watchlist';
        if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please make sure the backend is running.';
        }
        this.snackBar.open(errorMessage, 'Close', { duration: 3000 });
      }
    });
  }

  decreaseQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  increaseQuantity(): void {
    this.quantity++;
  }
}

