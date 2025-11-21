import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductDTO } from '../models/product.model';
import { CartResponse, CartItem } from '../models/cart.model';
import { Order } from '../models/order.model';
import { WatchlistItem } from '../models/watchlist.model';
import { Stats } from '../models/stats.model';
import { User } from '../models/user.model';
import { LoginRequest, RegisterRequest, LoginResponse } from '../models/user.model';

const API_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private token: string | null = null;

  constructor(private http: HttpClient) {
    this.token = localStorage.getItem('token');
  }

  private getHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    if (this.token) {
      headers = headers.set('Authorization', `Bearer ${this.token}`);
    }
    return headers;
  }

  setToken(token: string | null): void {
    this.token = token;
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }

  // Auth APIs
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_URL}/login`, credentials);
  }

  register(userData: RegisterRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${API_URL}/signup`, userData);
  }

  // Product APIs
  getAllProducts(): Observable<ProductDTO[]> {
    return this.http.get<ProductDTO[]>(`${API_URL}/products/all`, { headers: this.getHeaders() });
  }

  getProductById(productId: number): Observable<ProductDTO> {
    return this.http.get<ProductDTO>(`${API_URL}/products/${productId}`, { headers: this.getHeaders() });
  }

  createProduct(product: any): Observable<ProductDTO> {
    return this.http.post<ProductDTO>(`${API_URL}/products`, product, { headers: this.getHeaders() });
  }

  updateProduct(productId: number, product: any): Observable<ProductDTO> {
    return this.http.patch<ProductDTO>(`${API_URL}/products/${productId}`, product, { headers: this.getHeaders() });
  }

  // Cart APIs
  getCart(userId: number): Observable<CartResponse> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<CartResponse>(`${API_URL}/cart/view`, { headers: this.getHeaders(), params });
  }

  addToCart(userId: number, productId: number, quantity: number): Observable<CartResponse> {
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('productId', productId.toString())
      .set('quantity', quantity.toString());
    return this.http.post<CartResponse>(`${API_URL}/cart/add`, null, { headers: this.getHeaders(), params });
  }

  updateCartQuantity(cartItemId: number, newQuantity: number, userId: number): Observable<CartResponse> {
    const params = new HttpParams()
      .set('cartItemId', cartItemId.toString())
      .set('newQuantity', newQuantity.toString())
      .set('userId', userId.toString());
    return this.http.patch<CartResponse>(`${API_URL}/cart/updateQuantity`, null, { headers: this.getHeaders(), params });
  }

  removeFromCart(productId: number, userId: number): Observable<CartResponse> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.delete<CartResponse>(`${API_URL}/cart/remove/${productId}`, { headers: this.getHeaders(), params });
  }

  clearCart(userId: number): Observable<CartResponse> {
    return this.http.delete<CartResponse>(`${API_URL}/cart/clear/${userId}`, { headers: this.getHeaders() });
  }

  // Order APIs
  placeOrder(userId: number, orderItems?: Array<{ productId: number; quantity: number }>): Observable<{ message: string }> {
    const params = new HttpParams().set('userId', userId.toString());
    const body = orderItems && orderItems.length > 0 ? { order: orderItems } : null;
    return this.http.post<{ message: string }>(`${API_URL}/orders`, body, { headers: this.getHeaders(), params });
  }

  getAllOrders(userId?: number): Observable<Order[]> {
    let params = new HttpParams();
    if (userId) {
      params = params.set('userId', userId.toString());
    }
    return this.http.get<Order[]>(`${API_URL}/orders/all`, { headers: this.getHeaders(), params });
  }

  getOrderById(orderId: number): Observable<Order> {
    return this.http.get<Order>(`${API_URL}/orders/${orderId}`, { headers: this.getHeaders() });
  }

  cancelOrder(orderId: number): Observable<{ message: string }> {
    return this.http.patch<{ message: string }>(`${API_URL}/orders/${orderId}/cancel`, null, { headers: this.getHeaders() });
  }

  completeOrder(orderId: number): Observable<{ message: string }> {
    return this.http.patch<{ message: string }>(`${API_URL}/orders/${orderId}/complete`, null, { headers: this.getHeaders() });
  }

  // Watchlist APIs
  getWatchlist(userId: number): Observable<WatchlistItem[]> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<WatchlistItem[]>(`${API_URL}/watchlist/products/all`, { headers: this.getHeaders(), params });
  }

  addToWatchlist(userId: number, productId: number): Observable<string> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.post<string>(`${API_URL}/watchlist/product/${productId}`, null, { headers: this.getHeaders(), params, responseType: 'text' as 'json' });
  }

  removeFromWatchlist(userId: number, productId: number): Observable<string> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.delete<string>(`${API_URL}/watchlist/product/${productId}`, { headers: this.getHeaders(), params, responseType: 'text' as 'json' });
  }

  // Stats APIs
  getFrequentProducts(userId: number, topN: number): Observable<Stats[]> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<Stats[]>(`${API_URL}/products/frequent/${topN}`, { headers: this.getHeaders(), params });
  }

  getRecentProducts(userId: number, topN: number): Observable<Stats[]> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<Stats[]>(`${API_URL}/products/recent/${topN}`, { headers: this.getHeaders(), params });
  }

  getMostProfitableProducts(topN: number): Observable<Stats[]> {
    return this.http.get<Stats[]>(`${API_URL}/products/profit/${topN}`, { headers: this.getHeaders() });
  }

  getMostPopularProducts(topN: number): Observable<Stats[]> {
    return this.http.get<Stats[]>(`${API_URL}/products/popular/${topN}`, { headers: this.getHeaders() });
  }

  // Admin APIs
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${API_URL}/admin/users`, { headers: this.getHeaders() });
  }

  deleteUser(userId: number): Observable<string> {
    return this.http.delete<string>(`${API_URL}/admin/deleteUser/${userId}`, { headers: this.getHeaders(), responseType: 'text' as 'json' });
  }

  getAdminOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${API_URL}/admin/orders`, { headers: this.getHeaders() });
  }

  updateOrderStatus(orderId: number, status: string): Observable<string> {
    const params = new HttpParams().set('status', status);
    return this.http.put<string>(`${API_URL}/admin/updateOrderStatus/${orderId}`, null, { headers: this.getHeaders(), params, responseType: 'text' as 'json' });
  }
}

