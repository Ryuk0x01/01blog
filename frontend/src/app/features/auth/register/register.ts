import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {

  username = '';
  email = '';
  password = '';
  confirmPassword = '';

  constructor(private auth: AuthService, private router: Router) { }

  onSubmit() {

    if (this.password !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    this.auth.register({ username: this.username, email: this.email, password: this.password })
      .subscribe({
        next: (res: any) => {
          console.log(res);
          alert('Registration successful! Please login.');
          this.router.navigate(['/auth/login']);
        },
        error: (err) => {
          console.log("Error : ", err);
          alert('Registration failed');
        }
      });
  }

  ngOnInit() {
    if (this.auth.getToken()) {
      this.router.navigate(['/home']);
    }
  }

}
