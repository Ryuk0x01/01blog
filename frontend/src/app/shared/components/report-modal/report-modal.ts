import { Component, Inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-report-modal',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule
  ],
  template: `
    <h2 mat-dialog-title>Report {{ data.type === 'USER' ? 'User' : 'Post' }}</h2>
    <mat-dialog-content>
      <p>Why are you reporting this {{ data.type === 'USER' ? 'user' : 'post' }}?</p>
      <mat-form-field appearance="outline" style="width: 100%">
        <mat-label>Reason</mat-label>
        <textarea matInput [(ngModel)]="reason" rows="4"></textarea>
      </mat-form-field>
      @if (error()) {
        <p class="error">{{ error() }}</p>
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="warn" [disabled]="!reason || loading()" (click)="submitReport()">
        {{ loading() ? 'Submitting...' : 'Report' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .error { color: #f44336; margin-top: 8px; }
  `]
})
export class ReportModalComponent {
  reason = '';
  loading = signal(false);
  error = signal('');

  constructor(
    public dialogRef: MatDialogRef<ReportModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { targetId: number, type: 'USER' | 'POST' },
    private http: HttpClient,
    private auth: AuthService
  ) { }

  submitReport(): void {
    if (!this.reason) return;
    if (!confirm('Are you sure you want to submit this report?')) return;
    this.loading.set(true);
    this.error.set('');

    const body = {
      type: this.data.type,
      targetId: this.data.targetId,
      description: this.reason
    };

    this.http.post('/api/reports', body, this.auth.authHeaders())
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.dialogRef.close(true);
        },
        error: (err) => {
          this.loading.set(false);
          this.error.set(err.error?.message || 'Failed to submit report');
        }
      });
  }
}
