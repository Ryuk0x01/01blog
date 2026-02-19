import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatCardModule,
    RouterModule,
    SidebarComponent
  ],
  templateUrl: './explore.html',
  styleUrls: ['./explore.css']
})
export class ExploreComponent {
  query = signal('');
  results = signal<Array<{ id: number; username: string }>>([]);
  loading = signal(false);
  followState = signal<Record<number, boolean>>({});

  constructor(
    private auth: AuthService,
    private router: Router,
    private http: HttpClient
  ) { }

  search(): void {
    if (!this.query().trim()) return;

    this.loading.set(true);
    this.http.get<any[]>(`/api/users/search?query=${this.query()}`, this.auth.authHeaders())
      .subscribe({
        next: (users) => {
          this.results.set(users);
          this.loading.set(false);

          // Initialize follow state (mock)
          const map: Record<number, boolean> = {};
          users.forEach(u => (map[u.id] = false));
          this.followState.set(map);
        },
        error: (err) => {
          console.error('Search failed', err);
          this.loading.set(false);
        }
      });
  }

  toggleFollow(userId: number) {
    const s = { ...this.followState() };
    s[userId] = !s[userId];
    this.followState.set(s);
  }

  viewProfile(id: number): void {
    this.router.navigate(['/profile', id]);
  }
}
