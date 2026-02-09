import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth';
import { CreatePostComponent } from "./creat-posts/create-post";
import { CommentComponent } from '../comments/comment';

interface Post {
  id: number;
  title: string;
  content: string;
  authorId: number;
  authorUsername: string;
  mediaUrl?: string;
  createdAt?: string;
  updatedAt?: string;
}

interface Profile {
  id: number;
  username: string;
  email?: string;
  bio?: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    CreatePostComponent,
    CommentComponent
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {
  posts = signal<Post[]>([]);
  showCreatePost = signal(false);
  loadingPosts = signal(false);
  errorMessage = signal('');
  selectedProfile = signal<Profile | null>(null);

  // Create post form
  newPostTitle = signal('');
  newPostContent = signal('');
  selectedFile = signal<File | null>(null);
  creatingPost = signal(false);

  // Like/dislike/comment tracking (postId -> state)
  postLikes = signal<Record<number, boolean>>({});
  postCommentCounts = signal<Record<number, number>>({});

  private postsApi = 'http://localhost:8080/api/posts';
  private postReactionApi = 'http://localhost:8080/api/posts';


  constructor(
    private auth: AuthService,
    private router: Router,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.loadPosts();
  }



  logout() {
    this.auth.logout();
    this.router.navigate(['/auth/login']);
  }

  loadPosts(): void {
    this.loadingPosts.set(true);
    this.errorMessage.set('');

    this.http.get<any>(this.postsApi, this.auth.authHeaders())
      .subscribe({
        next: res => {
          const data = Array.isArray(res?.data) ? res.data : [];
          this.posts.set(data);
          this.loadingPosts.set(false);
        },
        error: err => {
          if (err.status === 401) {
            this.logout();
            return;
          }
          this.errorMessage.set(err?.message || 'Failed to load posts');
          this.loadingPosts.set(false);
        }
      });
  }

  likePost(postId: number): void {
    this.http.post<any>(`${this.postReactionApi}/${postId}/like`, {}, this.auth.authHeaders())
      .subscribe({
        next: (res) => {
          console.log(res);
          const likes = this.postLikes();
          likes[postId] = !likes[postId];
          this.postLikes.set({ ...likes });
        },
        error: err => {
          if (err.status === 401) {
            this.logout();
            return;
          }
          console.log(err);
          this.errorMessage.set('Failed to like post');
        }
      });
  }


  toggleComment(postId: number): void {
    const counts = this.postCommentCounts();
    if (!counts[postId]) {
      counts[postId] = 0;
    }
    counts[postId]++;
    this.postCommentCounts.set({ ...counts });
  }

}
