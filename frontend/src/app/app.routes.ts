import { Routes } from '@angular/router';
import { authRoutes } from './features/auth/auth-routing';
import { AuthGuard } from './core/guards/auth-guard';
import { HomeComponent } from './features/auth/home/home';
export const routes: Routes = [
  {
    path: 'auth',
    children: authRoutes
  },

  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard]
  },

  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: 'auth/login' }
];
