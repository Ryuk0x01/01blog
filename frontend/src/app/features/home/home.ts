import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth';
import { postComponent } from '../posts/posts';
import { CreatePostComponent } from './creat-posts/create-post';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    postComponent
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {

  showCreatePost = signal(false);

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  logout() {
    this.auth.logout();
    this.router.navigate(['/auth/login']);
  }

}
