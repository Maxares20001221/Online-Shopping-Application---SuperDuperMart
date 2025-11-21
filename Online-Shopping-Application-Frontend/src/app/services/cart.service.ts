import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { CartResponse } from '../models/cart.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartUpdated = new Subject<void>();

  // Observable for cart updates
  cartUpdated$ = this.cartUpdated.asObservable();

  // Notify subscribers that cart has been updated
  notifyUpdate(): void {
    this.cartUpdated.next();
  }
}

