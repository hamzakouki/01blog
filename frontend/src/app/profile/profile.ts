import { Component, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/services/auth.service';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

interface Post {
  id: number;
  content: string; // ✅ FIXED
  mediaUrl?: string;
  createdAt: string;
  likeCount: number;
  commentCount: number;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, DatePipe, FormsModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css']
})
export class Profile {

  private http = inject(HttpClient);
  private auth = inject(AuthService);

  posts = signal<Post[]>([]);
  followerCount = signal<number | null>(null);
  followingCount = signal<number | null>(null);
  loading = signal(true);

  // ✅ edit state
  editingPostId = signal<number | null>(null);
  editedContent = signal('');

  constructor() {
    const userId = this.auth.userId();
    if (!userId) return;

    this.loadAll(userId);
  }

  loadAll(userId: number) {
    this.loading.set(true);

    forkJoin({
      posts: this.http.get<any>(`http://localhost:8080/api/posts/${userId}`),
      followers: this.http.get<any>(`http://localhost:8080/api/followers/${userId}/followers/count`),
      following: this.http.get<any>(`http://localhost:8080/api/followers/${userId}/following/count`)
    }).subscribe({
      next: res => {
        this.posts.set(res.posts.data);
        this.followerCount.set(res.followers.data);
        this.followingCount.set(res.following.data);
        this.loading.set(false);
      },
      error: err => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }

  // 🗑 DELETE
  deletePost(postId: number) {
    if (!confirm("Delete this post?")) return;

    this.http.delete(`http://localhost:8080/api/posts/${postId}`)
      .subscribe(() => {
        this.posts.update(posts => posts.filter(p => p.id !== postId));
      });
  }

  // ✏️ START EDIT
  startEdit(post: Post) {
    this.editingPostId.set(post.id);
    this.editedContent.set(post.content);
  }

  // ❌ CANCEL
  cancelEdit() {
    this.editingPostId.set(null);
    this.editedContent.set('');
  }

  // 💾 SAVE
  saveEdit(postId: number) {
    const formData = new FormData();
    formData.append('content', this.editedContent()); // ✅ FIXED

    this.http.put<any>(`http://localhost:8080/api/posts/update/${postId}`, formData)
      .subscribe({
        next: res => {
          this.posts.update(posts =>
            posts.map(p =>
              p.id === postId ? { ...p, content: res.data.content } : p
            )
          );
          this.cancelEdit();
        },
        error: err => console.error(err)
      });
  }
}