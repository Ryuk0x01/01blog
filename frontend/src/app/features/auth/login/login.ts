import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
  standalone: true
})

export class Login {
  email = '';
  password = '';

  constructor(private auth: AuthService, private router: Router) { }

  onSubmit() {
    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: (res: any) => {
        this.auth.setToken(res.token);
        console.log(res.token);
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.log("Error : ", err);
        alert('Login failed')
      }
    });
  }

  ngOnInit() {
    if (this.auth.getToken()) {
      this.router.navigate(['/home']);
    }
  }
}