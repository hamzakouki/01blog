import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface Notification {
  id: number;
  message: string;
  type: string;
  read: boolean;

  relatedPostId: number | null;

  relatedUserId: number | null;
  relatedUsername: string | null;

  createdAt: string;
}

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './notifications.html',
  styleUrls: ['./notifications.css']
})
export class Notifications {

  private http = inject(HttpClient);

  notifications = signal<Notification[]>([]);
  loading = signal(true);

  constructor() {
    this.fetchNotifications();
  }

  fetchNotifications() {

    this.loading.set(true);

    this.http
      .get<{ data: Notification[] }>('http://localhost:8080/api/notifications')
      .subscribe({

        next: res => {
          this.notifications.set(res.data);
          this.loading.set(false);
        },

        error: err => {
          console.error(err);
          this.loading.set(false);
        }

      });
  }

  markAsRead(notification: Notification) {

    if (notification.read) return;

    this.http
      .put(`http://localhost:8080/api/notifications/${notification.id}/read`, {})
      .subscribe(() => {

        notification.read = true;
        this.notifications.set([...this.notifications()]);

      });
  }

  deleteNotification(notification: Notification) {

    this.http
      .delete(`http://localhost:8080/api/notifications/${notification.id}`)
      .subscribe(() => {

        const updated = this.notifications()
          .filter(n => n.id !== notification.id);

        this.notifications.set(updated);

      });
  }

}