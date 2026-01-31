import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { provideHttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './app/auth/services/auth.interceptor';
import { AuthErrorInterceptor } from './app/auth/services/auth-error.interceptor';

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),

    provideHttpClient(),

    // 1️⃣ Add token to requests
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },

    // 2️⃣ Handle expired/invalid token responses
    { provide: HTTP_INTERCEPTORS, useClass: AuthErrorInterceptor, multi: true },
  ]
}).catch(err => console.error(err));
