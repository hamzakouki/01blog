import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private tokenSignal = signal<string | null>(localStorage.getItem('token'));

  // Decoded user info from JWT
  user = computed(() => {
    const token = this.tokenSignal();
    if (!token) return null;
    return this.decodeToken(token);
  });

  // User role
  role = computed(() => this.user()?.role ?? null);

  // User ID
  userId = computed(() => this.user()?.id ?? null);

  // Check if logged in
  isLoggedIn = computed(() => !!this.user());

  // Set token after login
  setToken(token: string) {
    localStorage.setItem('token', token);
    this.tokenSignal.set(token);
  }

  // Logout
  logout() {
    localStorage.removeItem('token');
    this.tokenSignal.set(null);
  }

  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }
}
