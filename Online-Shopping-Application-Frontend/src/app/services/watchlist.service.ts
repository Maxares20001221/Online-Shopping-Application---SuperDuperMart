import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { WatchlistItem } from '../models/watchlist.model';

@Injectable({
  providedIn: 'root'
})
export class WatchlistService {
  private watchlistUpdated = new Subject<void>();

  // Observable for watchlist updates
  watchlistUpdated$ = this.watchlistUpdated.asObservable();

  // Notify subscribers that watchlist has been updated
  notifyUpdate(): void {
    this.watchlistUpdated.next();
  }
}

