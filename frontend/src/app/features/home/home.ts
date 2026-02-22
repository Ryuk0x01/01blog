import { Component, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { postComponent } from '../posts/posts';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar';
;

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    postComponent,
    SidebarComponent
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {

  showCreatePost = signal(false);

  constructor() { }

}
