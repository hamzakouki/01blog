import { Component, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AuthService } from '../auth/services/auth.service';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog';

interface User {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
  isFollowing: boolean;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, ConfirmDialogComponent],
  templateUrl: './users.html',
  styleUrls: ['./users.css']
})
export class Users {
  private http = inject(HttpClient);
  auth = inject(AuthService);

  users = signal<User[]>([]);
  searchTerm = signal('');
  loading = signal(true);
  // i add it here 
  reportingUser = signal<User | null>(null);
  reportReason = signal('');

  // succes message report 
  successMessage = signal<string | null>(null);

  pendingReport = signal<User | null>(null);



  constructor() {
    // 🔁 React when userId becomes available
    effect(() => {
      const currentUserId = this.auth.userId();
      // console.log('Current User ID:', currentUserId);

      if (!currentUserId) return;

      this.fetchUsers(currentUserId);
    });
  }

  private fetchUsers(currentUserId: number) {
    this.loading.set(true);

    this.http
      .get<{ data: User[] }>('http://localhost:8080/api/users/all')
      .subscribe({
        next: res => {
          const users = res.data
            .filter(u => u.id !== currentUserId) // 🚀 remove current user
            .map(u => ({
              ...u,
              isFollowing: false
            }));


          this.users.set(users);
          this.loading.set(false);

          // Load follow status
          users.forEach(user => {

            if (user.id === currentUserId) return;

            this.http
              .get<{ data: boolean }>(
                `http://localhost:8080/api/followers/${currentUserId}/is-following/${user.id}`
              )
              .subscribe({
                next: r => {
                  user.isFollowing = r.data;
                  this.users.set([...users]);
                }
              });
          });
        },
        error: err => {
          console.error(err);
          this.loading.set(false);
        }
      });
  }

  follow(user: User) {
    this.http
      .post(`http://localhost:8080/api/followers/${user.id}`, {})
      .subscribe(() => {
        user.isFollowing = true;
        this.users.set([...this.users()]);
      });
  }

  unfollow(user: User) {
    this.http
      .delete(`http://localhost:8080/api/followers/${user.id}`)
      .subscribe(() => {
        user.isFollowing = false;
        this.users.set([...this.users()]);
      });
  }


  openReportModal(user: User) {
    this.reportingUser.set(user);
    // console.log('Reporting user:', user);
    this.reportReason.set('');
  }

  submitReport() {
    const user = this.reportingUser();
    if (!user || !this.reportReason()) return;

    // ✅ CLOSE report modal FIRST
    this.reportingUser.set(null);

    // ✅ THEN open confirmation
    this.pendingReport.set(user);
  }

  //===================================


  filteredUsers() {
    const term = this.searchTerm().toLowerCase();
    return this.users().filter(u =>
      u.username.toLowerCase().includes(term)
    );
  }

  isAdmin() {
    return this.auth.role() === 'ADMIN';
  }

  sendReport() {
    const user = this.pendingReport();
    if (!user) return;

    this.http.post('http://localhost:8080/api/reports/create', {
      reportedUserId: user.id,
      postId: null,
      reason: this.reportReason()
    }).subscribe(() => {
      this.pendingReport.set(null);
      this.reportingUser.set(null);
      this.successMessage.set('User reported successfully');

      setTimeout(() => this.successMessage.set(null), 3000);
    });
  }

}
