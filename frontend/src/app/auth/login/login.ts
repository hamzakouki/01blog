import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {

  email = signal('');
  password = signal('');
  error = signal('');
  loading = signal(false);

constructor(
  private http: HttpClient,
  private router: Router,
  private auth: AuthService
) {}

  login() {
    this.error.set('');

    // 1️⃣ Validate fields
    if (!this.email() || !this.password()) {
      this.error.set('Email and password are required');
      return;
    }

    this.loading.set(true);

    // 2️⃣ Call backend
    this.http.post('http://localhost:8080/api/auth/login', {
      email: this.email(),
      password: this.password()
    }).subscribe({
      next: (res: any) => {
        this.loading.set(false);
        if (res?.data?.token) {
          console.log(res);
          this.auth.setToken(res.data.token);
          this.router.navigate(['/']);
        } else {
          this.error.set(res?.message || 'Login failed');
        }
      },
      error: (err) => {
        this.loading.set(false);
        if (err.error?.message === "Validation failed") {
          this.error.set('Please provide valid email and password');
          return;
        }
        this.error.set(err.error?.message || 'Invalid email or password');
      }
    });
  }
}
