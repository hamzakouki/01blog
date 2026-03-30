import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../auth/services/auth.service';
import { interval, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar implements OnInit, OnDestroy {

  auth = inject(AuthService);
  router = inject(Router);
  http = inject(HttpClient);

  unreadCount = signal(0);

  private destroy$ = new Subject<void>();

  ngOnInit() {
    // initial load
    this.loadUnreadCount();

    // polling every 30s (ONLY while component is alive)
    interval(30000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.loadUnreadCount();
      });
  }

  loadUnreadCount() {
    // 🚫 stop if user is not logged in
    if (!this.auth.isLoggedIn()) {
      this.unreadCount.set(0); // reset UI
      return;
    }

    this.http
      .get<{ data: { unreadCount: number } }>(
        'http://localhost:8080/api/notifications/unread/count'
      )
      .subscribe({
        next: res => {
          this.unreadCount.set(res.data.unreadCount);
        },
        error: err => {
          // 🚫 ignore 401 (user logged out / token expired)
          if (err.status === 401) return;

          console.error(err);
        }
      });
  }

  logout() {
    this.auth.logout();

    // stop all polling immediately
    this.destroy$.next();
    this.destroy$.complete();

    this.unreadCount.set(0);

    this.router.navigate(['/login']);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}