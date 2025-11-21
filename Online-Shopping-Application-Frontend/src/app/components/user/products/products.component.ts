import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';
import { WatchlistService } from '../../../services/watchlist.service';
import { ProductDTO } from '../../../models/product.model';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products: ProductDTO[] = [];
  displayedColumns: string[] = ['productId', 'name', 'price', 'actions'];
  watchlistItems: any[] = [];

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private cartService: CartService,
    private watchlistService: WatchlistService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadWatchlist();
  }

  loadProducts(): void {
    this.apiService.getAllProducts().subscribe({
      next: (products) => {
        this.products = products;
      },
      error: (error) => {
        this.snackBar.open('Failed to load products', 'Close', { duration: 3000 });
      }
    });
  }


  loadWatchlist(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.apiService.getWatchlist(userId).subscribe({
        next: (items) => {
          this.watchlistItems = items;
        },
        error: () => {
          // Watchlist might be empty
        }
      });
    }
  }

  addToCart(product: ProductDTO): void {
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
              this.addToCartWithUserId(user.userId, product);
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

    this.addToCartWithUserId(userId, product);
  }

  private addToCartWithUserId(userId: number, product: ProductDTO): void {
    // Add to backend cart
    this.apiService.addToCart(userId, product.productId, 1).subscribe({
      next: () => {
        this.snackBar.open(`${product.name} added to cart`, 'Close', { duration: 2000 });
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

  addToWatchlist(product: ProductDTO): void {
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
              this.addToWatchlistWithUserId(user.userId, product);
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

    this.addToWatchlistWithUserId(userId, product);
  }

  private addToWatchlistWithUserId(userId: number, product: ProductDTO): void {
    this.apiService.addToWatchlist(userId, product.productId).subscribe({
      next: () => {
        this.snackBar.open(`${product.name} added to watchlist`, 'Close', { duration: 2000 });
        this.watchlistService.notifyUpdate(); // Notify subscribers to refresh watchlist
        this.loadWatchlist(); // Also update local state
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

  viewProduct(productId: number): void {
    this.router.navigate(['/user/products', productId]);
  }
}

