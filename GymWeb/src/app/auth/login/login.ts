import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Token } from '../../core/services/token';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth';
import { LoginRequest, LoginResponse } from '../../models/auth.model';
import { UserProfileService } from '../../core/services/user';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatIconModule,
    RouterModule,
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {
  loginForm: FormGroup;
  errorMessage = '';
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tokenService: Token,
    private userService: UserProfileService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Please enter a valid email and password.';
      return;
    }

    const payload: LoginRequest = this.loginForm.value;

    this.authService.login(payload).subscribe({
      next: (res: LoginResponse) => {
        if (res && res.accessToken && res.refreshToken) {
          // ✅ Save both tokens
          this.tokenService.setToken(res.accessToken);
          this.tokenService.setRefreshToken(res.refreshToken);

          // ✅ Check if profile is completed
          this.userService.isProfileCompleted().subscribe({
            next: (completed) => {
              if (completed) {
                this.router.navigate(['/dashboard']);
              } else {
                this.router.navigate(['/complete-profile']);
              }
            },
            error: () => {
              this.router.navigate(['/complete-profile']);
            },
          });
        } else {
          this.errorMessage = 'Login failed: Tokens not received.';
        }
      },
      error: (err) => {
        console.error('Login error:', err);
        this.errorMessage = 'Invalid credentials. Please try again.';
      },
    });
  }
}
