import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { UserProfileService } from '../../core/services/user';

@Component({
  selector: 'app-complete-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './complete-profile.html',
  styleUrls: ['./complete-profile.css'],
})
export class CompleteProfile {
  profileForm: FormGroup;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserProfileService,
    private router: Router
  ) {
    this.profileForm = this.fb.group({
      age: ['', [Validators.required, Validators.min(10), Validators.max(100)]],
      height: ['', [Validators.required, Validators.min(50)]],
      weight: ['', [Validators.required, Validators.min(20)]],
      gender: ['', Validators.required],
      goal: ['', Validators.required],
      activityLevel: ['', Validators.required],
      preference: [''],
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.errorMessage = 'Please fill in all required fields correctly.';
      return;
    }

    this.userService.saveProfile(this.profileForm.value).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () =>
        (this.errorMessage = 'Failed to update profile. Please try again.'),
    });
  }
}
