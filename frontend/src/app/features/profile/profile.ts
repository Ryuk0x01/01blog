import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth';

import { MatDialog } from '@angular/material/dialog';
import { ReportModalComponent } from '../../shared/components/report-modal/report-modal';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar';
import { postComponent } from '../posts/posts';

interface ProfileData {
    id: number;
    username: string;
    postsCount: number;
    followersCount: number;
    followingCount: number;
    following: boolean;
}

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [
        CommonModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        SidebarComponent,
        postComponent
    ],
    templateUrl: './profile.html',
    styleUrls: ['./profile.css']
})
export class ProfileComponent implements OnInit {

    userId!: number;
    currentUserId = signal<number | null>(null);
    profile = signal<ProfileData | null>(null);
    loading = signal(true);
    isOwnProfile = signal(false);
    followLoading = signal(false);

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private http: HttpClient,
        private auth: AuthService,
        private dialog: MatDialog
    ) {
        this.currentUserId.set(this.auth.getUserId());
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.userId = +params['id'];
            this.loadProfile();
        });
    }

    private loadProfile(): void {
        this.loading.set(true);
        this.http
            .get<any>(`/api/profile/${this.userId}`, this.auth.authHeaders())
            .subscribe({
                next: (res) => {
                    const data = res?.data as ProfileData;
                    this.profile.set(data);
                    this.loading.set(false);

                    // Check if this is the current user's own profile
                    const currentUsername = this.auth.getUsername();
                    this.isOwnProfile.set(data.username === currentUsername);
                },
                error: (err) => {
                    this.loading.set(false);
                    if (err.status === 401) {
                        this.auth.logout();
                        this.router.navigate(['/auth/login']);
                    } else {
                        this.router.navigate(['/']);
                    }
                }
            });
    }



    toggleFollow(): void {
        const p = this.profile();
        if (!p || this.followLoading()) return;

        this.followLoading.set(true);

        if (p.following) {
            this.http
                .delete<any>(`/api/users/${this.userId}/unfollow`, this.auth.authHeaders())
                .subscribe({
                    next: () => {
                        this.profile.set({
                            ...p,
                            following: false,
                            followersCount: p.followersCount - 1
                        });
                        this.followLoading.set(false);
                    },
                    error: () => this.followLoading.set(false)
                });
        } else {
            this.http
                .post<any>(`/api/users/${this.userId}/follow`, {}, this.auth.authHeaders())
                .subscribe({
                    next: () => {
                        this.profile.set({
                            ...p,
                            following: true,
                            followersCount: p.followersCount + 1
                        });
                        this.followLoading.set(false);
                    },
                    error: () => this.followLoading.set(false)
                });
        }
    }

    getInitial(username: string): string {
        return username ? username.charAt(0).toUpperCase() : '?';
    }

    openReport(): void {
        this.dialog.open(ReportModalComponent, {
            data: { targetId: this.userId, type: 'USER' }
        });
    }

    viewProfile(id: number): void {
        this.router.navigate(['/profile', id]);
    }
}
