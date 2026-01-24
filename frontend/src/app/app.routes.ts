import { Routes } from '@angular/router';

import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Home } from './home/home';

import { authGuard } from './auth/guards/auth.guard';
import { adminGuard } from './auth/guards/admin.guard';

// (admin component later)
// import { AdminDashboard } from './admin/admin';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },

  { path: '', component: Home, canActivate: [authGuard] },

  // { path: 'admin', component: AdminDashboard, canActivate: [authGuard, adminGuard] },

  { path: '**', redirectTo: '' }
];
