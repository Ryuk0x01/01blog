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
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

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

  private searchSubject = new Subject<string>();

  query = signal('');
  results = signal<Array<{ id: number; username: string }>>([]);
  loading = signal(false);
  // followState = signal<Record<number, boolean>>({});

  constructor(
    private auth: AuthService,
    private router: Router,
    private http: HttpClient
  ) {
    this.searchSubject
      .pipe(
        debounceTime(400),
        distinctUntilChanged()
      )
      .subscribe(query => {
        this.performSearch(query);
      });

  }

  search(): void {
    this.searchSubject.next(this.query());
  }

  private performSearch(query: string): void {
    if (!query.trim()) {
      this.results.set([]);
      return;
    }

    this.loading.set(true);

    this.http.get<any[]>(`/api/users/search?query=${query}`, this.auth.authHeaders())
      .subscribe({
        next: (users) => {
          this.results.set(users);
          this.loading.set(false);

          // const map: Record<number, boolean> = {};
          // users.forEach(u => (map[u.id] = false));
          // this.followState.set(map);
        },
        error: () => {
          this.loading.set(false);
        }
      });
  }

  viewProfile(id: number): void {
    this.router.navigate(['/profile', id]);
  }
}
