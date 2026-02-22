import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar';

interface NotificationItem {
    id: number;
    actorUsername: string;
    type: string;
    message: string;
    referenceId: number;
    read: boolean;
    createdAt: string;
}

@Component({
    selector: 'app-notifications',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatIconModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        SidebarComponent
    ],
    templateUrl: './notifications.html',
    styleUrls: ['./notifications.css']
})
export class NotificationsComponent implements OnInit {

    notifications = signal<NotificationItem[]>([]);
    loading = signal(true);

    constructor(
        private http: HttpClient,
        private auth: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadNotifications();
    }

    loadNotifications(): void {
        this.loading.set(true);
        this.http
            .get<any>('/api/notifications', this.auth.authHeaders())
            .subscribe({
                next: (res) => {
                    this.notifications.set(res?.data || []);
                    this.loading.set(false);
                },
                error: (err) => {
                    this.loading.set(false);
                    if (err.status === 401) {
                        this.auth.logout();
                        this.router.navigate(['/auth/login']);
                    }
                }
            });
    }

    markAsRead(id: number): void {
        this.http
            .put<any>(`/api/notifications/${id}/read`, {}, this.auth.authHeaders())
            .subscribe({
                next: () => {
                    const updated = this.notifications().map(n =>
                        n.id === id ? { ...n, read: true } : n
                    );
                    this.notifications.set(updated);
                }
            });
    }

    markAllAsRead(): void {
        this.http
            .put<any>('/api/notifications/read-all', {}, this.auth.authHeaders())
            .subscribe({
                next: () => {
                    const updated = this.notifications().map(n => ({ ...n, read: true }));
                    this.notifications.set(updated);
                }
            });
    }

    onNotificationClick(n: NotificationItem): void {
        if (!n.read) {
            this.markAsRead(n.id);
        }
        if (n.type === 'FOLLOW') {
            this.router.navigate(['/profile', n.referenceId]);
        }
    }

    getIcon(type: string): string {
        return type === 'NEW_POST' ? 'article' : 'person_add';
    }
}
