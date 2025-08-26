import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { Token } from '../../core/services/token';
import { AuthService } from '../../core/services/auth';
import { UserProfileService } from '../../core/services/user';
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
} from '../../models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatIconModule,
    RouterModule,
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
})
export class Register {
  registerForm: FormGroup;
  errorMessage = '';
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tokenService: Token,
    private userService: UserProfileService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.errorMessage = 'Please fill out all required fields.';
      return;
    }

    const { password, confirmPassword, email, name } = this.registerForm.value;
    if (password !== confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    const payload: RegisterRequest = { name, email, password };

    // Step 1: Register the user
    this.authService.register(payload).subscribe({
      next: () => {
        // Step 2: Auto-login after successful registration
        const loginPayload: LoginRequest = { email, password };

        this.authService.login(loginPayload).subscribe({
          next: (res: LoginResponse) => {
            if (res && res.accessToken && res.refreshToken) {
              // ✅ Save tokens
              this.tokenService.setToken(res.accessToken);
              this.tokenService.setRefreshToken(res.refreshToken);

              // Step 3: Check if profile is completed
              this.userService.isProfileCompleted().subscribe({
                next: (completed) => {
                  if (completed) {
                    this.router.navigate(['/dashboard']);
                  } else {
                    this.router.navigate(['/complete-profile']);
                  }
                },
                error: () => {
                  // if API fails, push to complete profile page
                  this.router.navigate(['/complete-profile']);
                },
              });
            } else {
              this.errorMessage =
                'Registration succeeded but login tokens were not received.';
            }
          },
          error: (err) => {
            console.error('Auto-login error:', err);
            this.errorMessage =
              'Registration succeeded but auto-login failed. Please login manually.';
            this.router.navigate(['/login']);
          },
        });
      },
      error: (err) => {
        console.error('Registration error:', err);
        this.errorMessage =
          err?.error?.message || 'Registration failed. Try again.';
      },
    });
  }
}
