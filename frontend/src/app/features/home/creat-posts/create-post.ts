import { Component, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { AuthService } from '../../../core/services/auth';
import { postComponent } from '../../posts/posts';


@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule
  ],
  templateUrl: './create-post.html',
  styleUrls: ['./create-post.css']
})
export class CreatePostComponent {

  newPostTitle = signal('');
  newPostContent = signal('');
  selectedFile = signal<File | null>(null);
  creatingPost = signal(false);
  errorMessage = signal('');

  private postsApi = 'http://localhost:8080/api/posts';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private post: postComponent
  ) {}


  createPost() {
    if (!this.newPostTitle() || !this.newPostContent()) {
      this.errorMessage.set('Title and content are required');
      return;
    }

    this.creatingPost.set(true);
    this.errorMessage.set('');

    const formData = new FormData();
    formData.append('title', this.newPostTitle());
    formData.append('content', this.newPostContent());

    if (this.selectedFile()) {
      formData.append('file', this.selectedFile()!);
    }

    this.http.post(this.postsApi, formData, this.auth.authHeaders())
      .subscribe({
        next: () => {
          this.newPostTitle.set('');
          this.newPostContent.set('');
          this.selectedFile.set(null);
          this.creatingPost.set(false);
          this.post.loadPosts();          
        },
        error: err => {
          this.creatingPost.set(false);
          this.errorMessage.set(
            err?.error?.message || 'Failed to create post'
          );
        }
      });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    const validTypes = [
      'image/jpeg', 'image/png', 'image/gif',
      'image/webp', 'video/mp4', 'video/webm', 'video/ogg'
    ];

    if (!validTypes.includes(file.type)) {
      this.errorMessage.set('Invalid file type');
      return;
    }

    if (file.size > 50 * 1024 * 1024) {
      this.errorMessage.set('File too large (max 50MB)');
      return;
    }

    this.selectedFile.set(file);
  }
}
