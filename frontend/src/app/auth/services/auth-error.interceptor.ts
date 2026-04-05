import { Injectable, inject } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpErrorResponse
} from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AuthErrorInterceptor implements HttpInterceptor {
  private router = inject(Router);
  private auth = inject(AuthService);

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    return next.handle(req).pipe(
      
      catchError((error: HttpErrorResponse) => {
        const isApiRequest = req.url.startsWith('http://localhost:8080/api/');
        const isAuthRoute = req.url.includes('/api/auth') || req.url.includes('/api/login') || req.url.includes('/api/register');

        if (isApiRequest && !isAuthRoute && (error.status === 401 || error.status === 403)) {
          this.auth.logout();
          this.router.navigate(['/login'], { replaceUrl: true });
        }
        return throwError(() => error);
      })
    );
  }
}
