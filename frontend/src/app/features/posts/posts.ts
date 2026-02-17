import { Component, Input, signal } from '@angular/core';
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
import { HomeComponent } from '../home/home';

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
        CreatePostComponent
    ],
    templateUrl: './posts.html',
    styleUrls: ['./posts.css']
})
export class postComponent {
    posts = signal<Post[]>([]);
    loadingPosts = signal(false);
    errorMessage = signal('');
    selectedProfile = signal<Profile | null>(null);

    @Input() showCreatePost = false;

    // Create post form
    newPostTitle = signal('');
    newPostContent = signal('');
    selectedFile = signal<File | null>(null);
    creatingPost = signal(false);

    postLikes = signal<Record<number, boolean>>({});
    postCommentCounts = signal<Record<number, number>>({});
    postLikesCount = signal<Record<number, number>>({});

    private postsApi = 'http://localhost:8080/api/posts';


    constructor(
        private auth: AuthService,
        private http: HttpClient,
        private home: HomeComponent
    ) { }



    ngOnInit(): void {
        this.loadPosts();
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
                    console.log('Posts loaded:', data);

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
                        this.home.logout();
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
                        this.home.logout();
                        return;
                    }
                    this.errorMessage.set('Failed to like post');
                }
            });
    }

}