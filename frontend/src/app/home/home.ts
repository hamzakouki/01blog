import { Component, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router'; // ✅ ADD THIS
import { AuthService } from '../auth/services/auth.service';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog';

interface ToggleLikeResponse {
  status: string;
  data: any | null;
  message: string;
}

interface Comment {
  id: number;
  content: string;
  authorUsername: string;
  createdAt: string;
}

interface Post {
  id: number;
  content: string;
  mediaUrl?: string;
  authorId: number;
  authorUsername: string;
  createdAt: string;
  likeCount: number;
  commentCount: number;

  showComments?: boolean;
  comments?: Comment[];
  newComment?: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
  imports: [CommonModule, FormsModule, HttpClientModule, DatePipe, RouterModule, ConfirmDialogComponent]
})
export class Home {

  private http = inject(HttpClient);

  posts = signal<Post[]>([]);

  newPostContent = '';
  newPostFile?: File;
  loadingPost = false;

  //======================this is for report ===============
  auth = inject(AuthService);
  reportingPost = signal<Post | null>(null);
  reportReason = signal('');
  pendingReport = signal<Post | null>(null);
  successMessage = signal<string | null>(null);
  //==================================================
  constructor() {
    this.loadFeed();
  }

  // 📰 FEED (from followed users)
  loadFeed() {
    this.http
      .get<{ data: Post[] }>('http://localhost:8080/api/posts/feed')
      .subscribe({
        next: res => this.posts.set(res.data),
        error: err => console.error(err)
      });
  }

  // 📁 FILE SELECT
  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.newPostFile = input.files?.[0];
  }

  // ✍️ CREATE POST
  createPost() {

    if (!this.newPostContent && !this.newPostFile) return;

    this.loadingPost = true;

    const formData = new FormData();
    formData.append('content', this.newPostContent);

    if (this.newPostFile) {
      formData.append('media', this.newPostFile);
    }

    this.http.post<{ data: Post }>(
      'http://localhost:8080/api/posts/create',
      formData
    ).subscribe({
      next: res => {
        this.posts.update(p => [res.data, ...p]);

        this.newPostContent = '';
        this.newPostFile = undefined;
        this.loadingPost = false;
      },
      error: err => {
        this.loadingPost = false;
        console.error(err);
      }
    });
  }

  // 👍 LIKE
  toggleLike(post: Post) {
    this.http.post<ToggleLikeResponse>(`http://localhost:8080/api/likes/${post.id}`, {})
      .subscribe({
        next: (res) => {
          console.log('Toggle like response:', res);

          post.likeCount += res.message === "Post liked successfully" ? 1 : -1;

          this.posts.set([...this.posts()]);
        },
        error: err => console.error(err)
      });
  }

  // 💬 TOGGLE COMMENTS
  toggleComments(post: Post) {
    post.showComments = !post.showComments;

    if (post.showComments && !post.comments) {
      this.http
        .get<{ data: Comment[] }>(
          `http://localhost:8080/api/comments/post/${post.id}`
        )
        .subscribe({
          next: res => {
            post.comments = res.data;
            this.posts.set([...this.posts()]);
          },
          error: err => console.error(err)
        });
    }

    this.posts.set([...this.posts()]);
  }

  // ➕ ADD COMMENT
  addComment(post: Post) {

    if (!post.newComment) return;

    this.http.post<{ data: Comment }>(
      'http://localhost:8080/api/comments/create',
      {
        postId: post.id,
        content: post.newComment
      }
    ).subscribe({
      next: res => {

        if (!post.comments) post.comments = [];

        post.comments.push(res.data);
        post.commentCount++;

        post.newComment = '';
        this.posts.set([...this.posts()]);
      },
      error: err => console.error(err)
    });
  }


  //=================== methods for report =======================
  openReportModal(post: Post) {
    this.reportingPost.set(post);
    this.reportReason.set('');
  }

  submitReport() {
    const post = this.reportingPost();
    if (!post || !this.reportReason()) return;

    this.reportingPost.set(null); // close modal
    this.pendingReport.set(post); // open confirm
  }

  sendReport() {
    const post = this.pendingReport();
    if (!post) return;

    this.http.post('http://localhost:8080/api/reports/create', {
      reportedUserId: null,
      postId: post.id,
      reason: this.reportReason()
    }).subscribe(() => {
      this.pendingReport.set(null);
      this.successMessage.set('Post reported successfully');

      setTimeout(() => this.successMessage.set(null), 3000);
    });
  }

  //==============================================================

}