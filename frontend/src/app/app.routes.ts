import { Routes } from '@angular/router';
import { authRoutes } from './features/auth/auth-routing';
import { AuthGuard } from './core/guards/auth-guard';
import { AdminGuard } from './core/guards/admin-guard';
import { HomeComponent } from './features/home/home';
import { ExploreComponent } from './features/explore/explore';
import { ProfileComponent } from './features/profile/profile';
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

  {
    path: 'explore',
    component: ExploreComponent,
    canActivate: [AuthGuard]
  },

  {
    path: 'notifications',
    loadComponent: () => import('./features/notifications/notifications').then(m => m.NotificationsComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin/admin').then(m => m.AdminComponent),
    canActivate: [AuthGuard, AdminGuard]
  },

  {
    path: 'profile/:id',
    component: ProfileComponent,
    canActivate: [AuthGuard]
  },

  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: 'auth/login' }
];
