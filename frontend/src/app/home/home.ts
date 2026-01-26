import { Component, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface Post {
  id: number;
  content: string;
  mediaUrl?: string;
  authorId: number;
  authorUsername: string;
  authorRole: string;
  createdAt: string;
  likeCount: number;
  commentCount: number;
  showComments?: boolean;
}

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
  imports: [CommonModule, FormsModule, HttpClientModule, DatePipe]
})
export class Home {

  private http = inject(HttpClient);

  posts = signal<Post[]>([]);
  newPostContent = '';
  newPostFile?: File;
  loadingPost = false;

  constructor() {
    this.loadFeed();
  }

  loadFeed() {
    this.http.get<{ data: Post[] }>('http://localhost:8080/api/posts/feed').subscribe({
      next: res => this.posts.set(res.data),
      error: err => console.error(err)
    });
  }

  onFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    this.newPostFile = target.files?.[0];
  }

  createPost() {
    if (!this.newPostContent && !this.newPostFile) return;

    this.loadingPost = true;
    const formData = new FormData();
    formData.append('content', this.newPostContent);
    if (this.newPostFile) formData.append('media', this.newPostFile);

    this.http.post<{ data: Post }>('http://localhost:8080/api/posts/create', formData)
      .subscribe({
        next: res => {
          this.loadingPost = false;
          this.posts.update(p => [res.data, ...p]);
          this.newPostContent = '';
          this.newPostFile = undefined;
        },
        error: err => {
          this.loadingPost = false;
          console.error(err);
        }
      });
  }

  toggleLike(post: Post) {
    this.http.post(`http://localhost:8080/api/likes/${post.id}`, {}).subscribe({
      next: () => this.loadFeed(), // Simple refresh for now
      error: err => console.error(err)
    });
  }

  toggleComments(post: Post) {
    post.showComments = !post.showComments;
    this.posts.set([...this.posts()]);
  }
}
