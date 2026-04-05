import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/services/auth.service';
import { formatDistanceToNow } from 'date-fns';

interface Report {
  id: number;
  reporterId: number;
  reporterUsername: string;
  reportedUserId?: number;
  reportedUsername?: string;
  reportedPostId?: number;
  reason: string;
  handled: boolean;
  createdAt: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboard implements OnInit {

  private http = inject(HttpClient);
  auth = inject(AuthService);

  reports = signal<Report[]>([]);
  loading = signal(true);
  error = signal('');
  userBanStatus = signal<Record<number, boolean>>({});
  postHiddenStatus = signal<Record<number, boolean>>({});

  ngOnInit() {
    this.loadReports();
  }

  loadReports() {
    this.loading.set(true);
    this.error.set('');
    this.http.get<{ data: Report[] }>('http://localhost:8080/api/reports/feed')
      .subscribe({
        next: res => {
          this.reports.set(res.data);
          this.loading.set(false);
          this.loadMetaStatuses(res.data);
        },
        error: err => {
          this.error.set('Failed to load reports');
          console.error(err);
          this.loading.set(false);
        }
      });
  }

  private loadMetaStatuses(reports: Report[]) {
    reports.forEach(report => {
      if (report.reportedUserId && this.userBanStatus()[report.reportedUserId] === undefined) {
        this.http.get<{ data: boolean }>(`http://localhost:8080/api/users/checkban/${report.reportedUserId}`)
          .subscribe({
            next: resp => {
              this.userBanStatus.set({ ...this.userBanStatus(), [report.reportedUserId!]: resp.data });
            },
            error: err => {
              console.warn('Failed to check user ban status:', err);
              this.userBanStatus.set({ ...this.userBanStatus(), [report.reportedUserId!]: false });
            }
          });
      }

      if (report.reportedPostId && this.postHiddenStatus()[report.reportedPostId] === undefined) {
        this.http.get<{ data: boolean }>(`http://localhost:8080/api/posts/checkhidde/${report.reportedPostId}`)
          .subscribe({
            next: resp => {
              this.postHiddenStatus.set({ ...this.postHiddenStatus(), [report.reportedPostId!]: resp.data });
            },
            error: err => {
              console.warn('Failed to check post hidden status:', err);
              this.postHiddenStatus.set({ ...this.postHiddenStatus(), [report.reportedPostId!]: false });
            }
          });
      }
    });
  }

  handleUser(report: Report, action: 'ban' | 'unban' | 'delete') {
    if (!report.reportedUserId) return;
    const userId = report.reportedUserId;
    let request;

    if (action === 'delete') {
      request = this.http.delete(`http://localhost:8080/api/users/${userId}`);
    } else if (action === 'ban') {
      request = this.http.put(`http://localhost:8080/api/users/ban/${userId}`, {});
    } else {
      request = this.http.put(`http://localhost:8080/api/users/unban/${userId}`, {});
    }

    request.subscribe({
      next: () => {
        if (action === 'delete') {
          const copy = { ...this.userBanStatus() };
          delete copy[userId];
          this.userBanStatus.set(copy);
        } else {
          this.userBanStatus.set({ ...this.userBanStatus(), [userId]: action === 'ban' });
        }

        this.markReportHandled(report.id);
      },
      error: err => {
        console.error(err);

        if (err.status === 401) {
          this.error.set('Unauthorized. Check your login and permissions.');
        } else if (err.status === 409) {
          this.error.set('Unable to delete user: user has related data (posts/comments). Clear dependencies and retry.');
        } else {
          this.error.set(err.error?.message || 'Failed to update user status.');
        }
      }
    });
  }

  handlePost(report: Report, action: 'hide' | 'unhide' | 'delete') {
    if (!report.reportedPostId) return;
    const postId = report.reportedPostId;
    let request;

    if (action === 'delete') {
      request = this.http.delete(`http://localhost:8080/api/posts/${postId}`);
    } else if (action === 'hide') {
      request = this.http.post(`http://localhost:8080/api/posts/hidde/${postId}`, {});
    } else {
      request = this.http.post(`http://localhost:8080/api/posts/unhidde/${postId}`, {});
    }

    request.subscribe({
      next: () => {
        if (action === 'delete') {
          const copy = { ...this.postHiddenStatus() };
          delete copy[postId];
          this.postHiddenStatus.set(copy);
        } else {
          this.postHiddenStatus.set({ ...this.postHiddenStatus(), [postId]: action === 'hide' });
          this.markReportHandled(report.id);
        }

      },
      error: err => {
        console.error(err);
        this.error.set('Failed to update post status.');
      }
    });
  }

  isUserBanned(userId?: number) {
    if (!userId) return false;
    return !!this.userBanStatus()[userId];
  }

  isPostHidden(postId?: number) {
    if (!postId) return false;
    return !!this.postHiddenStatus()[postId];
  }

  markReportHandled(reportId: number) {
    this.http.put(`http://localhost:8080/api/reports/handle/${reportId}`, {})
      .subscribe({
        next: () => {
          const updated = this.reports().map(r => r.id === reportId ? { ...r, handled: true } : r);
          this.reports.set(updated);
        },
        error: err => console.error(err)
      });
  }

  timeAgo(dateStr: string) {
    return formatDistanceToNow(new Date(dateStr), { addSuffix: true });
  }
}