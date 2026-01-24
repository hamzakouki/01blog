import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private tokenSignal = signal<string | null>(localStorage.getItem('token'));

  user = computed(() => {
    const token = this.tokenSignal();
    if (!token) return null;
    return this.decodeToken(token);
  });

  role = computed(() => this.user()?.role ?? null);

  isLoggedIn = computed(() => !!this.user());

  setToken(token: string) {
    localStorage.setItem('token', token);
    this.tokenSignal.set(token);
  }

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
