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
import { AuthService } from '../../../core/services/auth';
import { CreatePostComponent } from "./creat-posts/create-post";

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
  imports: [CommonModule, FormsModule, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, MatIconModule, MatProgressSpinnerModule, CreatePostComponent],
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

  private authHeaders(): { headers?: HttpHeaders } {
    const token = this.auth.getToken();
    if (!token) return {};
    return { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) };
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/auth/login']);
  }

  loadPosts(): void {
    this.loadingPosts.set(true);
    this.errorMessage.set('');

    const opts = this.authHeaders();
    this.http.get<any>(this.postsApi, opts)
      .subscribe({
        next: res => {
          const data = Array.isArray(res?.data) ? res.data : [];
          this.posts.set(data);
          this.loadingPosts.set(false);
        },
        error: err => {
          this.errorMessage.set(err?.message || 'Failed to load posts');
          this.loadingPosts.set(false);
        }
      });
  }

  createPost(): void {
    if (!this.newPostTitle() || !this.newPostContent()) {
      this.errorMessage.set('Please fill in title and content');
      return;
    }

    this.creatingPost.set(true);

    const formData = new FormData();
    formData.append('title', this.newPostTitle());
    formData.append('content', this.newPostContent());

    if (this.selectedFile()) {
      formData.append('file', this.selectedFile()!);
    }

    const opts = this.authHeaders();
    this.http.post<any>(this.postsApi, formData, opts)
      .subscribe({
        next: () => {
          this.newPostTitle.set('');
          this.newPostContent.set('');
          this.selectedFile.set(null);
          this.creatingPost.set(false);
          this.loadPosts();
        },
        error: err => {
          this.errorMessage.set(err?.error?.message || 'Failed to create post');
          this.creatingPost.set(false);
          console.log(err);
        }
      });
  }

  likePost(postId: number): void {
    const opts = this.authHeaders();
    this.http.post<any>(`${this.postReactionApi}/${postId}/like`, {}, opts)
      .subscribe({
        next: (res) => {
          console.log(res);
          const likes = this.postLikes();
          likes[postId] = !likes[postId];
          this.postLikes.set({ ...likes });
        },
        error: err => {
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

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Validate file type (images and videos)
      const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'video/mp4', 'video/webm', 'video/ogg'];
      if (!validTypes.includes(file.type)) {
        this.errorMessage.set('Please select a valid image or video file');
        return;
      }
      // Validate file size (max 50MB)
      const maxSize = 50 * 1024 * 1024;
      if (file.size > maxSize) {
        this.errorMessage.set('File size must be less than 50MB');
        return;
      }
      this.selectedFile.set(file);
      this.errorMessage.set('');
    }
  }

  handlePostCreated(formData: FormData): void {
    this.showCreatePost.set(false);
    this.loadPosts();
  }
}
