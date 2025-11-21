import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { WatchlistService } from '../../../services/watchlist.service';
import { WatchlistItem } from '../../../models/watchlist.model';

@Component({
  selector: 'app-watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})
export class WatchlistComponent implements OnInit, OnDestroy {
  watchlistItems: WatchlistItem[] = [];
  displayedColumns: string[] = ['productName', 'price', 'actions'];
  private watchlistSubscription?: Subscription;

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private watchlistService: WatchlistService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadWatchlist();
    // Subscribe to watchlist updates
    this.watchlistSubscription = this.watchlistService.watchlistUpdated$.subscribe(() => {
      this.loadWatchlist();
    });
  }

  ngOnDestroy(): void {
    if (this.watchlistSubscription) {
      this.watchlistSubscription.unsubscribe();
    }
  }

  loadWatchlist(): void {
    let userId = this.authService.getUserId();
    
    // If userId is not found, try to get it
    if (!userId) {
      const email = this.authService.getEmail();
      if (email) {
        this.apiService.getAllUsers().subscribe({
          next: (users) => {
            const user = users.find(u => u.email === email);
            if (user) {
              localStorage.setItem('userId', user.userId.toString());
              this.loadWatchlistWithUserId(user.userId);
            }
          },
          error: () => {
            // Watchlist might be empty
            this.watchlistItems = [];
          }
        });
      }
      return;
    }

    this.loadWatchlistWithUserId(userId);
  }

  private loadWatchlistWithUserId(userId: number): void {
    this.apiService.getWatchlist(userId).subscribe({
      next: (items) => {
        this.watchlistItems = items;
      },
      error: (error) => {
        // Watchlist might be empty
        this.watchlistItems = [];
      }
    });
  }

  removeFromWatchlist(productId: number): void {
    let userId = this.authService.getUserId();
    
    if (!userId) {
      const email = this.authService.getEmail();
      if (email) {
        this.apiService.getAllUsers().subscribe({
          next: (users) => {
            const user = users.find(u => u.email === email);
            if (user) {
              localStorage.setItem('userId', user.userId.toString());
              this.removeFromWatchlistWithUserId(user.userId, productId);
            }
          },
          error: () => {
            this.snackBar.open('Cannot get user information', 'Close', { duration: 3000 });
          }
        });
      }
      return;
    }

    this.removeFromWatchlistWithUserId(userId, productId);
  }

  private removeFromWatchlistWithUserId(userId: number, productId: number): void {
    this.apiService.removeFromWatchlist(userId, productId).subscribe({
      next: () => {
        this.snackBar.open('Removed from watchlist', 'Close', { duration: 2000 });
        this.watchlistService.notifyUpdate(); // Notify subscribers
      },
      error: (error) => {
        this.snackBar.open('Failed to remove from watchlist', 'Close', { duration: 3000 });
      }
    });
  }
}

