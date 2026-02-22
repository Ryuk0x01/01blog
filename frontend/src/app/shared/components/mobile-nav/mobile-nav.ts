import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../core/services/auth';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-mobile-nav',
    standalone: true,
    imports: [CommonModule, RouterModule, MatIconModule],
    template: `
    <nav class="mobile-nav glass">
      <a class="nav-item" routerLink="/home" routerLinkActive="active">
        <mat-icon>home</mat-icon>
      </a>
      <a class="nav-item" routerLink="/explore" routerLinkActive="active">
        <mat-icon>search</mat-icon>
      </a>
      @if (isAdmin()) {
        <a class="nav-item" routerLink="/admin" routerLinkActive="active">
          <mat-icon>admin_panel_settings</mat-icon>
        </a>
      }
      <a class="nav-item notifications-link" routerLink="/notifications" routerLinkActive="active">
        <mat-icon>notifications</mat-icon>
        @if (unreadNotifications() > 0) {
          <span class="badge">{{ unreadNotifications() }}</span>
        }
      </a>
      <a class="nav-item" [routerLink]="'/profile/' + currentUserId()" routerLinkActive="active">
        <mat-icon>person</mat-icon>
      </a>
    </nav>
  `,
    styles: [`
    .mobile-nav {
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      height: 64px;
      display: none;
      align-items: center;
      justify-content: space-around;
      padding-bottom: env(safe-area-inset-bottom);
      z-index: 1000;
      border-top: 1px solid var(--glass-border);
      background: var(--glass-bg);
      backdrop-filter: blur(12px);
      -webkit-backdrop-filter: blur(12px);
    }

    .nav-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: var(--text-muted);
      text-decoration: none;
      width: 48px;
      height: 48px;
      border-radius: 50%;
      position: relative;
    }

    .nav-item mat-icon {
      font-size: 26px;
      width: 26px;
      height: 26px;
    }

    .nav-item.active {
      color: var(--primary);
    }

    .badge {
      position: absolute;
      top: 8px;
      right: 8px;
      background: var(--secondary);
      color: white;
      font-size: 10px;
      font-weight: 700;
      min-width: 16px;
      height: 16px;
      padding: 0 4px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 2px solid white;
    }

    @media (max-width: 640px) {
      .mobile-nav {
        display: flex;
      }
    }
  `]
})
export class MobileNavComponent implements OnInit {
    auth = inject(AuthService);
    http = inject(HttpClient);
    unreadNotifications = signal(0);

    currentUserId() {
        return this.auth.getUserId();
    }

    isAdmin() {
        return this.auth.getUserRole() === 'ADMIN';
    }

    ngOnInit() {
        this.loadUnreadCount();
    }

    loadUnreadCount() {
        this.http.get<{ count: number }>('/api/notifications/unread-count', this.auth.authHeaders()).subscribe({
            next: (res) => {
                this.unreadNotifications.set(res.count);
            },
            error: () => { }
        });
    }
}
