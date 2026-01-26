import { Routes } from '@angular/router';

import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Home } from './home/home';
import { MainLayout } from './layout/main-layout/main-layout';

import { authGuard } from './auth/guards/auth.guard';
import { adminGuard } from './auth/guards/admin.guard';
import { Users } from './users/users';
import { Notifications } from './notifications/notifications';
import { Profile } from './profile/profile';
import { AdminDashboard } from './admin/admin-dashboard/admin-dashboard';

export const routes: Routes = [

  // 🔓 Public
  { path: 'login', component: Login },
  { path: 'register', component: Register },

  // 🔒 Authenticated layout
  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      { path: '', component: Home },
      { path: 'users', component: Users },
      { path: 'notifications', component: Notifications },
      { path: 'profile', component: Profile },
      { path: 'admin', component: AdminDashboard, canActivate: [adminGuard] },
    ]
  },

  { path: '**', redirectTo: '' }
];
