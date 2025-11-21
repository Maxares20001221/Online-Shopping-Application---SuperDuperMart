import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { CartItem } from '../models/cart.model';

@Injectable({
  providedIn: 'root'
})
export class CartLocalStorageService {
  private readonly CART_KEY = 'shopping_cart';
  private cartUpdated = new Subject<void>();

  // Observable for cart updates
  cartUpdated$ = this.cartUpdated.asObservable();

  getCart(): CartItem[] {
    const cartJson = localStorage.getItem(this.CART_KEY);
    return cartJson ? JSON.parse(cartJson) : [];
  }

  addItem(item: CartItem): void {
    const cart = this.getCart();
    const existingItem = cart.find(i => i.productId === item.productId);
    
    if (existingItem) {
      existingItem.quantity += item.quantity;
    } else {
      cart.push(item);
    }
    
    this.saveCart(cart);
    this.cartUpdated.next(); // Notify subscribers
  }

  updateItem(productId: number, quantity: number): void {
    const cart = this.getCart();
    const item = cart.find(i => i.productId === productId);
    
    if (item) {
      if (quantity <= 0) {
        this.removeItem(productId);
      } else {
        item.quantity = quantity;
        this.saveCart(cart);
        this.cartUpdated.next(); // Notify subscribers
      }
    }
  }

  removeItem(productId: number): void {
    const cart = this.getCart().filter(item => item.productId !== productId);
    this.saveCart(cart);
    this.cartUpdated.next(); // Notify subscribers
  }

  clearCart(): void {
    localStorage.removeItem(this.CART_KEY);
    this.cartUpdated.next(); // Notify subscribers
  }

  getTotalItems(): number {
    return this.getCart().reduce((sum, item) => sum + item.quantity, 0);
  }

  getTotalPrice(): number {
    return this.getCart().reduce((sum, item) => sum + (item.price * item.quantity), 0);
  }

  private saveCart(cart: CartItem[]): void {
    localStorage.setItem(this.CART_KEY, JSON.stringify(cart));
  }
}

