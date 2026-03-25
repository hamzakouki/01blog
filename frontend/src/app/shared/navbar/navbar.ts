import { Component, inject, signal  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar {

  auth = inject(AuthService);
  router = inject(Router);
  http = inject(HttpClient);

  unreadCount = signal(0);

  constructor() {
    this.loadUnreadCount();
    setInterval(() => {
      this.loadUnreadCount();
    }, 30000);
  }

  loadUnreadCount() {

    this.http
      .get<{ data: { unreadCount: number } }>(
        'http://localhost:8080/api/notifications/unread/count'
      )
      .subscribe({
        next: res => {
          this.unreadCount.set(res.data.unreadCount);
        },
        error: err => console.error(err)
      });

  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}