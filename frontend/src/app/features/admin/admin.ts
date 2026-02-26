import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar';
import { Router } from '@angular/router';

interface AdminUser {
    id: number;
    username: string;
    email: string;
    role: string;
    banned: boolean;
}

interface AdminPost {
    id: number;
    title: string;
    content: string;
    authorUsername: string;
    hidden: boolean;
    createdAt: string;
}

interface AdminReport {
    id: number;
    reporterUsername: string;
    type: string;
    targetId: number;
    targetName: string;
    description: string;
    createdAt: string;
}

@Component({
    selector: 'app-admin',
    standalone: true,
    imports: [
        CommonModule,
        MatIconModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        SidebarComponent
    ],
    templateUrl: './admin.html',
    styleUrls: ['./admin.css']
})
export class AdminComponent implements OnInit {

    activeTab = signal<'users' | 'posts' | 'reports'>('users');
    users = signal<AdminUser[]>([]);
    posts = signal<AdminPost[]>([]);
    reports = signal<AdminReport[]>([]);
    stats = signal<{ users: number; posts: number; reports: number }>({ users: 0, posts: 0, reports: 0 });
    loading = signal(true);

    constructor(
        private http: HttpClient,
        private auth: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadStats();
        this.loadUsers();
    }

    setTab(tab: 'users' | 'posts' | 'reports'): void {
        this.activeTab.set(tab);
        if (tab === 'users') this.loadUsers();
        else if (tab === 'posts') this.loadPosts();
        else this.loadReports();
    }

    loadStats(): void {
        this.http.get<any>('/api/admin/stats', this.auth.authHeaders()).subscribe({
            next: (res) => this.stats.set(res),
            error: (err) => {
                if (err.status === 401 || err.status === 403) {
                    this.router.navigate(['/']);
                }
            }
        });
    }

    loadUsers(): void {
        this.loading.set(true);
        this.http.get<any>('/api/admin/users', this.auth.authHeaders()).subscribe({
            next: (res) => {
                this.users.set(res?.data || []);
                this.loading.set(false);
            },
            error: () => this.loading.set(false)
        });
    }

    loadPosts(): void {
        this.loading.set(true);
        this.http.get<any>('/api/admin/posts', this.auth.authHeaders()).subscribe({
            next: (res) => {
                this.posts.set(res?.data || []);
                this.loading.set(false);
            },
            error: () => this.loading.set(false)
        });
    }

    loadReports(): void {
        this.loading.set(true);
        this.http.get<any>('/api/admin/reports', this.auth.authHeaders()).subscribe({
            next: (res) => {
                this.reports.set(res?.data || []);
                this.loading.set(false);
            },
            error: () => this.loading.set(false)
        });
    }

    deleteUser(id: number): void {
        if (!confirm('Are you sure you want to delete this user?')) return;
        this.http.delete<any>(`/api/admin/users/${id}`, this.auth.authHeaders()).subscribe({
            next: () => {
                this.users.set(this.users().filter(u => u.id !== id));
                this.stats.set({ ...this.stats(), users: this.stats().users - 1 });
            }
        });
    }

    toggleUserBan(id: number): void {
        this.http.put<any>(`/api/admin/users/${id}/ban`, {}, this.auth.authHeaders()).subscribe({
            next: () => {
                this.users.set(this.users().map(u => u.id === id ? { ...u, banned: !u.banned } : u));
            }
        });
    }

    deletePost(id: number): void {
        if (!confirm('Are you sure you want to delete this post?')) return;
        this.http.delete<any>(`/api/admin/posts/${id}`, this.auth.authHeaders()).subscribe({
            next: () => {
                this.posts.set(this.posts().filter(p => p.id !== id));
                this.stats.set({ ...this.stats(), posts: this.stats().posts - 1 });
            }
        });
    }

    togglePostHide(id: number): void {
        this.http.put<any>(`/api/admin/posts/${id}/hide`, {}, this.auth.authHeaders()).subscribe({
            next: () => {
                this.posts.set(this.posts().map(p => p.id === id ? { ...p, hidden: !p.hidden } : p));
            }
        });
    }

    dismissReport(id: number): void {
        if (!confirm('Dismiss this report?')) return;
        this.http.delete<any>(`/api/admin/reports/${id}`, this.auth.authHeaders()).subscribe({
            next: () => {
                this.reports.set(this.reports().filter(r => r.id !== id));
                this.stats.set({ ...this.stats(), reports: this.stats().reports - 1 });
            }
        });
    }
}
