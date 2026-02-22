import { Component, input, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatMenuModule } from '@angular/material/menu';


interface Comment {
    id: number;
    content: string;
    authorId: number;
    authorUsername: string;
    postId: number;
    createdAt?: string;
    updatedAt?: string;
}

@Component({
    selector: 'app-comment',
    standalone: true,
    imports: [CommonModule, FormsModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatMenuModule],
    templateUrl: './comment.html',
    styleUrls: ['./comment.css']
})
export class CommentComponent {
    postId = input<number>();

    comments = signal<Comment[]>([]);
    newCommentContent = signal('');
    loadingComments = signal(false);
    postingComment = signal(false);
    errorMessage = signal('');
    showComments = signal(false);

    private commentApi = 'http://localhost:8080/api/posts';

    constructor(
        private http: HttpClient,
        public auth: AuthService
    ) {
        effect(() => {
            const id = this.postId();
            if (id) {
                this.loadComments();
            }
        });
    }


    loadComments(): void {
        this.loadingComments.set(true);
        const opts = this.auth.authHeaders();
        const postId = this.postId();

        if (!postId) return;

        this.http.get<any>(`${this.commentApi}/${postId}/comments`, opts)
            .subscribe({
                next: res => {
                    const data = Array.isArray(res?.data) ? res.data : [];
                    console.log("Fetched comments: ", res);
                    this.comments.set(data);
                    this.loadingComments.set(false);
                },
                error: err => {
                    if (err.status === 401) {
                        this.auth.logout
                        return;
                    }
                    console.log(err);
                    this.loadingComments.set(false);
                }
            });
    }

    postComment(): void {
        if (!this.newCommentContent().trim()) {
            this.errorMessage.set('Comment cannot be empty');
            return;
        }

        this.postingComment.set(true);
        const opts = this.auth.authHeaders();
        const postId = this.postId();

        if (!postId) return;

        const payload = {
            content: this.newCommentContent()
        };

        this.http.post<any>(`${this.commentApi}/${postId}/comments`, payload, opts)
            .subscribe({
                next: (res) => {
                    console.log(res);
                    this.newCommentContent.set('');
                    this.postingComment.set(false);
                    this.errorMessage.set('');
                    this.loadComments();
                },
                error: err => {
                    if (err.status === 401) {
                        this.auth.logout();
                        return;
                    }
                    this.errorMessage.set(err?.error?.message || 'Failed to post comment');
                    this.postingComment.set(false);
                    console.log(err);
                }
            });
    }

    deleteComment(commentId: number): void {
        if (!confirm('Are you sure you want to delete this comment?')) {
            return;
        }

        const postId = this.postId();
        if (!postId) return;


        const opts = this.auth.authHeaders();
        this.http.delete<any>(`${this.commentApi}/${postId}/comments/${commentId}`, opts)
            .subscribe({
                next: () => {
                    this.loadComments();
                },
                error: err => {
                    if (err.status === 401) {
                        this.auth.logout();
                        return;
                    }
                    this.errorMessage.set('Failed to delete comment');
                    console.log(err);
                }
            });
    }

    toggleComments(): void {
        this.showComments.set(!this.showComments());
    }


}
