import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { CartService } from '../../../services/cart.service';
import { CartItem } from '../../../models/cart.model';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css']
})
export class ShoppingCartComponent implements OnInit, OnDestroy {
  cartItems: CartItem[] = [];
  displayedColumns: string[] = ['productName', 'quantity', 'price', 'total', 'actions'];
  totalPrice = 0;
  isLoading = false;
  private cartSubscription?: Subscription;

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private cartService: CartService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCart();
    // Subscribe to cart updates
    this.cartSubscription = this.cartService.cartUpdated$.subscribe(() => {
      this.loadCart();
    });
  }

  ngOnDestroy(): void {
    if (this.cartSubscription) {
      this.cartSubscription.unsubscribe();
    }
  }

  loadCart(): void {
    const userId = this.authService.getUserId();
    if (!userId) {
      this.cartItems = [];
      this.totalPrice = 0;
      return;
    }

    this.isLoading = true;
    this.apiService.getCart(userId).subscribe({
      next: (response) => {
        this.cartItems = response.items || [];
        this.totalPrice = response.totalPrice || 0;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Load cart error:', error);
        this.cartItems = [];
        this.totalPrice = 0;
        this.isLoading = false;
        // Don't show error if cart is just empty
        if (error.status !== 404 && error.status !== 0) {
          this.snackBar.open('Failed to load cart', 'Close', { duration: 3000 });
        }
      }
    });
  }

  updateQuantity(cartItemId: number, quantity: number): void {
    const userId = this.authService.getUserId();
    if (!userId) {
      this.snackBar.open('Please login first', 'Close', { duration: 3000 });
      return;
    }

    if (quantity <= 0) {
      const item = this.cartItems.find(i => i.itemId === cartItemId);
      if (item) {
        this.removeItem(item.productId);
      }
      return;
    }

    this.apiService.updateCartQuantity(cartItemId, quantity, userId).subscribe({
      next: () => {
        this.cartService.notifyUpdate();
        this.snackBar.open('Quantity updated', 'Close', { duration: 2000 });
      },
      error: (error) => {
        console.error('Update quantity error:', error);
        let errorMessage = error.error?.error || 'Failed to update quantity';
        if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please make sure the backend is running.';
        }
        this.snackBar.open(errorMessage, 'Close', { duration: 3000 });
      }
    });
  }

  removeItem(productId: number): void {
    const userId = this.authService.getUserId();
    if (!userId) {
      this.snackBar.open('Please login first', 'Close', { duration: 3000 });
      return;
    }

    this.apiService.removeFromCart(productId, userId).subscribe({
      next: () => {
        this.cartService.notifyUpdate();
        this.snackBar.open('Item removed from cart', 'Close', { duration: 2000 });
      },
      error: (error) => {
        console.error('Remove item error:', error);
        let errorMessage = error.error?.error || 'Failed to remove item';
        if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please make sure the backend is running.';
        }
        this.snackBar.open(errorMessage, 'Close', { duration: 3000 });
      }
    });
  }

  clearCart(): void {
    if (!confirm('Are you sure you want to clear the cart?')) {
      return;
    }

    const userId = this.authService.getUserId();
    if (!userId) {
      this.snackBar.open('Please login first', 'Close', { duration: 3000 });
      return;
    }

    this.apiService.clearCart(userId).subscribe({
      next: () => {
        this.cartService.notifyUpdate();
        this.snackBar.open('Cart cleared', 'Close', { duration: 2000 });
      },
      error: (error) => {
        console.error('Clear cart error:', error);
        let errorMessage = error.error?.error || 'Failed to clear cart';
        if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please make sure the backend is running.';
        }
        this.snackBar.open(errorMessage, 'Close', { duration: 3000 });
      }
    });
  }

  placeOrder(): void {
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
              this.placeOrderWithUserId(user.userId);
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

    this.placeOrderWithUserId(userId);
  }

  private placeOrderWithUserId(userId: number): void {
    if (this.cartItems.length === 0) {
      this.snackBar.open('Cart is empty', 'Close', { duration: 3000 });
      return;
    }

    // Place order from backend cart (no body, backend will use cart)
    this.apiService.placeOrder(userId).subscribe({
      next: () => {
        this.snackBar.open('Order placed successfully!', 'Close', { duration: 3000 });
        this.cartService.notifyUpdate(); // Refresh cart display
      },
      error: (error) => {
        console.error('Place order error:', error);
        let errorMessage = error.error?.error || 'Failed to place order';
        if (error.status === 0) {
          errorMessage = 'Cannot connect to server. Please make sure the backend is running.';
        }
        this.snackBar.open(errorMessage, 'Close', { duration: 5000 });
      }
    });
  }

  getItemTotal(item: CartItem): number {
    return item.price * item.quantity;
  }
}

