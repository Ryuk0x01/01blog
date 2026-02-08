import { Component, signal, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatCardModule],
  templateUrl: './create-post.html',
  styleUrls: ['./create-post.css']
})
export class CreatePostComponent {
  newPostTitle = signal('');
  newPostContent = signal('');
  selectedFile = signal<File | null>(null);
  creatingPost = signal(false);

  @Output() postCreated = new EventEmitter<FormData>();

  createPost() {
    if (!this.newPostTitle() || !this.newPostContent()) return;

    this.creatingPost.set(true);
    const formData = new FormData();
    formData.append('title', this.newPostTitle());
    formData.append('content', this.newPostContent());
    if (this.selectedFile()) formData.append('file', this.selectedFile()!);

    this.postCreated.emit(formData);

    // Reset form
    this.newPostTitle.set('');
    this.newPostContent.set('');
    this.selectedFile.set(null);
    this.creatingPost.set(false);
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) this.selectedFile.set(file);
  }
}
