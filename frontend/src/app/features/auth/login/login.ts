import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {

  private apiUrl = 'http://localhost:8080/api/auth/login';

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required)
  });

  loading = false;
  message = '';

  constructor(private http: HttpClient) {}

  login() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.message = '';

    this.http.post<any>(this.apiUrl, this.loginForm.value).subscribe({
      next: (res) => {
        this.loading = false;
        this.message = res.message || 'Login successful!';
        // Save JWT token if you want
        // localStorage.setItem('token', res.data.token);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.message = err.error?.message || 'Login failed. Please try again.';
      }
    });
  }

}
