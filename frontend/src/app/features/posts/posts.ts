import { Component, Input, OnChanges, SimpleChanges, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth';
import { CommentComponent } from '../comments/comment';
import { CreatePostComponent } from '../home/creat-posts/create-post';
import { Router } from '@angular/router';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ReportModalComponent } from '../../shared/components/report-modal/report-modal';

interface Post {
    id: number;
    title: string;
    content: string;
    authorId: number;
    authorUsername: string;
    mediaUrl?: string;
    createdAt?: string;
    updatedAt?: string;

    likesCount?: number;
    commentsCount?: number;
    likedByCurrentUser?: boolean;
}

interface Profile {
    id: number;
    username: string;
    email?: string;
    bio?: string;
}

@Component({
    selector: 'app-posts',
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
        CommentComponent,
        CreatePostComponent,
        MatDialogModule
    ],
    templateUrl: './posts.html',
    styleUrls: ['./posts.css']
})
export class postComponent implements OnChanges {
    @Input() userId: number | null = null;
    @Input() showCreateButton: boolean = true;

    posts = signal<Post[]>([]);
    loadingPosts = signal(false);
    errorMessage = signal('');
    selectedProfile = signal<Profile | null>(null);

    showCreatePost = signal(false);

    // Edit post state
    editingPostId = signal<number | null>(null);
    editTitle = signal('');
    editContent = signal('');
    editSelectedFile = signal<File | null>(null);
    editing = signal(false);
    postLikes = signal<Record<number, boolean>>({});
    postCommentCounts = signal<Record<number, number>>({});
    postLikesCount = signal<Record<number, number>>({});

    private postsApi = '/api/posts';


    constructor(
        public auth: AuthService,
        private http: HttpClient,
        private router: Router,
        private dialog: MatDialog
    ) { }



    ngOnInit(): void {
        this.loadPosts();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['userId'] && !changes['userId'].firstChange) {
            this.loadPosts();
        }
    }


    loadPosts(): void {
        this.loadingPosts.set(true);
        this.errorMessage.set('');

        const url = this.userId
            ? `/api/posts/user/${this.userId}`
            : `${this.postsApi}/feed`;

        this.http.get<any>(url, this.auth.authHeaders())
            .subscribe({
                next: res => {
                    const data = Array.isArray(res?.data) ? res.data : [];
                    this.posts.set(data);
                    this.loadingPosts.set(false);
                    console.log('Posts loaded: ---- >> ', data);

                    // Initialize likes and comment counts
                    const likes: Record<number, boolean> = {};
                    const commentCounts: Record<number, number> = {};
                    const likesCount: Record<number, number> = {};
                    data.forEach((post: any) => {
                        likes[post.id] = post.likedByCurrentUser || false;
                        commentCounts[post.id] = post.commentsCount || 0;
                        likesCount[post.id] = post.likesCount || 0;
                    });
                    this.postLikesCount.set(likesCount);
                    this.postLikes.set(likes);
                    this.postCommentCounts.set(commentCounts);
                },
                error: err => {
                    if (err.status === 401) {
                        this.auth.logout();
                        this.router.navigate(['/auth/login']);
                        return;
                    }
                    this.errorMessage.set(err?.message || 'Failed to load posts');
                    this.loadingPosts.set(false);
                }
            });
    }

    likePost(postId: number): void {
        this.http.post<any>(`${this.postsApi}/${postId}/like`, {}, this.auth.authHeaders())
            .subscribe({
                next: () => {

                    const likes = { ...this.postLikes() };
                    const likesCount = { ...this.postLikesCount() };

                    if (likes[postId]) {
                        // unlike
                        likesCount[postId] = (likesCount[postId] || 1) - 1;
                    } else {
                        // like
                        likesCount[postId] = (likesCount[postId] || 0) + 1;
                    }

                    likes[postId] = !likes[postId];

                    this.postLikes.set(likes);
                    this.postLikesCount.set(likesCount);
                },
                error: err => {
                    if (err.status === 401) {
                        this.auth.logout();
                        this.router.navigate(['/auth/login']);
                        return;
                    }
                    this.errorMessage.set('Failed to like post');
                }
            });
    }

    startEdit(post: Post): void {
        this.editingPostId.set(post.id);
        this.editTitle.set(post.title || '');
        this.editContent.set(post.content || '');
        this.editSelectedFile.set(null);
    }

    onEditFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            this.editSelectedFile.set(input.files[0]);
        }
    }

    cancelEdit(): void {
        this.editingPostId.set(null);
        this.editTitle.set('');
        this.editContent.set('');
        this.editSelectedFile.set(null);
        this.editing.set(false);
    }

    saveEdit(postId: number): void {
        this.editing.set(true);
        const title = this.editTitle();
        const content = this.editContent();
        const file = this.editSelectedFile();

        const formData = new FormData();
        formData.append('title', title);
        formData.append('content', content);

        if (file) {
            formData.append('file', file);
        }

        this.http.put<any>(`${this.postsApi}/${postId}`, formData, this.auth.authHeaders())
            .subscribe({
                next: res => {
                    const updated = res?.data || { id: postId, title, content };
                    const updatedPosts = this.posts().map(p => p.id === postId ? { ...p, title: updated.title, content: updated.content, mediaUrl: updated.mediaUrl ?? p.mediaUrl, updatedAt: updated.updatedAt ?? p.updatedAt } : p);
                    this.posts.set(updatedPosts);
                    this.cancelEdit();
                },
                error: err => {
                    if (err.status === 401) {
                        this.auth.logout();
                        this.router.navigate(['/auth/login']);
                        return;
                    }
                    this.errorMessage.set('Failed to update post');
                    this.editing.set(false);
                }
            });
    }

    deletePost(postId: number): void {
        if (!confirm('Delete this post?')) return;

        this.http.delete<any>(`${this.postsApi}/${postId}`, this.auth.authHeaders())
            .subscribe({
                next: () => {
                    const remaining = this.posts().filter(p => p.id !== postId);
                    this.posts.set(remaining);
                },
                error: err => {
                    if (err.status === 401) {
                        this.auth.logout();
                        this.router.navigate(['/auth/login']);
                        return;
                    }
                    this.errorMessage.set('Failed to delete post');
                }
            });
    }

    reportPost(postId: number): void {
        this.dialog.open(ReportModalComponent, {
            width: '400px',
            data: { targetId: postId, type: 'POST' }
        });
    }
}