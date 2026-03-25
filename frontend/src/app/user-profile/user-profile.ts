import { Component, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { forkJoin } from 'rxjs';

interface User {
  id: number;
  username: string;
}

interface Post {
  id: number;
  content: string;
  mediaUrl?: string;
  createdAt: string;
  likeCount: number;
  commentCount: number;
}

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './user-profile.html'
})
export class UserProfile {

  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);

  posts = signal<Post[]>([]);
  followerCount = signal<number | null>(null);
  followingCount = signal<number | null>(null);
  user = signal<User | null>(null); // ✅ only once

  userId!: number;

  constructor() {
    this.route.paramMap.subscribe(params => {
      this.userId = Number(params.get('id'));
      this.loadAll();
    });
  }

  loadAll() {
    forkJoin({
      user: this.http.get<any>(`http://localhost:8080/api/users/${this.userId}`),
      posts: this.http.get<any>(`http://localhost:8080/api/posts/${this.userId}`),
      followers: this.http.get<any>(`http://localhost:8080/api/followers/${this.userId}/followers/count`),
      following: this.http.get<any>(`http://localhost:8080/api/followers/${this.userId}/following/count`)
    }).subscribe({
      next: res => {
        this.user.set(res.user.data);
        this.posts.set(res.posts.data);
        this.followerCount.set(res.followers.data);
        this.followingCount.set(res.following.data);
      },
      error: err => console.error(err)
    });
  }
}