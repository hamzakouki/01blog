import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {

  // backend URL
  private apiUrl = 'http://localhost:8080/api/auth/login';

  // UI state
  message = '';
  loading = false;

  // Reactive form
  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
  });

  constructor(private http: HttpClient) {}

  login() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      this.message = 'Please fill in all fields correctly';
      return;
    }

    this.loading = true;
    this.message = '';

    this.http.post<any>(this.apiUrl, this.loginForm.value).subscribe({
      next: (response) => {
        this.loading = false;
        this.message = response.message;

        // Example: save token later
        // localStorage.setItem('token', response.data.token);

        console.log('Login response:', response);
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;

        if (error.error?.message) {
          this.message = error.error.message;
        } else {
          this.message = 'Login failed. Please try again.';
        }
      }
    });
  }
}
