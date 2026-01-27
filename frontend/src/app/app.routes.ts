import { Routes } from '@angular/router';
import { authRoutes } from './features/auth/auth-routing';

export const routes: Routes = [
  {
    path: 'auth',
    children: authRoutes
  },

  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  { path: '**', redirectTo: 'auth/login' }
];
