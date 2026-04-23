import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [
    RouterModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <div class="not-found-container">
      <div class="not-found-card">
        <h1>404</h1>
        <h2>Page Not Found</h2>
        <p>
          The page you're looking for doesn't exist
          or may have been moved.
        </p>

        <button
          mat-flat-button
          color="primary"
          routerLink="/home"
          class="back-btn"
        >
          <mat-icon>home</mat-icon>
          Back to Home
        </button>
      </div>
    </div>
  `,
  styles: [`
    .not-found-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f8fafc;
      padding: 24px;
    }

    .not-found-card {
      background: white;
      padding: 48px;
      border-radius: 24px;
      text-align: center;
      max-width: 500px;
      width: 100%;
      box-shadow: 0 20px 40px rgba(0,0,0,0.08);
      border: 1px solid #e2e8f0;
    }

    h1 {
      font-size: 72px;
      font-weight: 800;
      margin: 0;
      color: #6366f1;
      line-height: 1;
    }

    h2 {
      font-size: 28px;
      font-weight: 700;
      margin: 16px 0 12px;
      color: #1e293b;
    }

    p {
      font-size: 15px;
      color: #64748b;
      line-height: 1.6;
      margin-bottom: 32px;
    }

    .back-btn {
      height: 48px !important;
      padding: 0 28px !important;
      border-radius: 14px !important;
      font-weight: 700 !important;
    }

    .back-btn mat-icon {
      margin-right: 8px;
    }
  `]
})
export class NotFoundComponent {}