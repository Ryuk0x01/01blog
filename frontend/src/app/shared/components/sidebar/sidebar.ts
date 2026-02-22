
import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../core/services/auth';

@Component({
    selector: 'app-sidebar',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatButtonModule,
        MatIconModule
    ],
    templateUrl: './sidebar.html',
    styleUrls: ['./sidebar.css']
})
export class SidebarComponent {

    showCreatePost = signal(false);
    currentUserId = signal<number | null>(null);
    isAdmin = signal<boolean>(false);
    unreadNotifications = signal<number>(0);

    constructor(
        private auth: AuthService,
        private router: Router,
        private http: HttpClient
    ) {
        this.currentUserId.set(this.auth.getUserId());
        this.isAdmin.set(this.auth.getUserRole() === 'USER' ? false : this.auth.getUserRole() === 'ADMIN');
        this.loadUnreadNotifications();
    }

    loadUnreadNotifications() {
        this.http.get<any>('/api/notifications/unread-count', this.auth.authHeaders())
            .subscribe({
                next: (res) => this.unreadNotifications.set(res.count),
                error: () => { }
            });
    }

    logout() {
        this.auth.logout();
        this.router.navigate(['/auth/login']);
    }
}
