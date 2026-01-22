import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Home } from './home/home'; // create this later

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' }, // default route goes to home
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'home', component: Home }, // create later
  { path: '**', redirectTo: '/login' } // fallback
];
