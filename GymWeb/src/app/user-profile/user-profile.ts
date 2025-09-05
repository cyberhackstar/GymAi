import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FitnessService } from '../plan/fitness-service';

export interface UserProfileDTO {
  userId?: number;
  name: string;
  email: string;
  age: number;
  height: number;
  weight: number;
  gender: string;
  goal: string;
  activityLevel: string;
  preference: string;
  profileComplete: boolean;
}

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-profile.html',
  styleUrls: ['./user-profile.css'],
})
export class UserProfile implements OnInit {
  profileForm: FormGroup;
  userProfile: UserProfileDTO | null = null;
  isEditing = false;
  isLoading = false;
  isSaving = false;
  errorMessage = '';
  successMessage = '';

  genderOptions = [
    { value: 'MALE', label: 'Male' },
    { value: 'FEMALE', label: 'Female' },
  ];

  goalOptions = [
    { value: 'WEIGHT_LOSS', label: 'Weight Loss', icon: '🎯' },
    { value: 'MUSCLE_GAIN', label: 'Muscle Gain', icon: '💪' },
    { value: 'MAINTENANCE', label: 'Maintenance', icon: '⚖️' },
  ];

  activityLevelOptions = [
    {
      value: 'SEDENTARY',
      label: 'Sedentary',
      description: 'Little to no exercise',
    },
    {
      value: 'LIGHTLY_ACTIVE',
      label: 'Lightly Active',
      description: '1-3 days per week',
    },
    {
      value: 'MODERATELY_ACTIVE',
      label: 'Moderately Active',
      description: '3-5 days per week',
    },
    {
      value: 'VERY_ACTIVE',
      label: 'Very Active',
      description: '6-7 days per week',
    },
    {
      value: 'EXTREMELY_ACTIVE',
      label: 'Extremely Active',
      description: 'Intense daily training',
    },
  ];

  preferenceOptions = [
    { value: 'VEG', label: 'Vegetarian', icon: '🥬' },
    { value: 'NON_VEG', label: 'Non-Vegetarian', icon: '🍖' },
    { value: 'VEGAN', label: 'Vegan', icon: '🌱' },
  ];

  constructor(private fb: FormBuilder, private fitnessService: FitnessService) {
    this.profileForm = this.createForm();
  }

  ngOnInit() {
    this.loadUserProfile();
  }

  createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      age: ['', [Validators.required, Validators.min(13), Validators.max(120)]],
      height: [
        '',
        [Validators.required, Validators.min(100), Validators.max(250)],
      ],
      weight: [
        '',
        [Validators.required, Validators.min(30), Validators.max(300)],
      ],
      gender: ['', Validators.required],
      goal: ['', Validators.required],
      activityLevel: ['', Validators.required],
      preference: ['', Validators.required],
    });
  }

  loadUserProfile() {
    this.isLoading = true;
    // Simulate loading existing profile - replace with actual service call
    setTimeout(() => {
      this.userProfile = {
        userId: 2,
        name: 'test',
        email: 'test@test.com',
        age: 25,
        height: 170.0,
        weight: 70.0,
        gender: 'MALE',
        goal: 'MUSCLE_GAIN',
        activityLevel: 'MODERATELY_ACTIVE',
        preference: 'VEG',
        profileComplete: true,
      };

      if (this.userProfile) {
        this.profileForm.patchValue(this.userProfile);
      }
      this.isLoading = false;
    }, 1000);
  }

  toggleEdit() {
    this.isEditing = !this.isEditing;
    this.clearMessages();

    if (!this.isEditing && this.userProfile) {
      // Reset form if canceling edit
      this.profileForm.patchValue(this.userProfile);
    }
  }

  saveProfile() {
    if (this.profileForm.valid) {
      this.isSaving = true;
      this.clearMessages();

      const updatedProfile: UserProfileDTO = {
        ...this.profileForm.value,
        userId: this.userProfile?.userId,
        profileComplete: true,
      };

      this.fitnessService.getUserProfile(updatedProfile).subscribe({
        next: (response) => {
          this.userProfile = response;
          this.isEditing = false;
          this.isSaving = false;
          this.successMessage = 'Profile updated successfully!';
          setTimeout(() => this.clearMessages(), 3000);
        },
        error: (error) => {
          this.isSaving = false;
          this.errorMessage = 'Failed to update profile. Please try again.';
          console.error('Profile update error:', error);
          setTimeout(() => this.clearMessages(), 5000);
        },
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  markFormGroupTouched() {
    Object.keys(this.profileForm.controls).forEach((key) => {
      const control = this.profileForm.get(key);
      control?.markAsTouched();
    });
  }

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  getBMI(): number {
    if (this.userProfile?.height && this.userProfile?.weight) {
      const heightInMeters = this.userProfile.height / 100;
      return Number(
        (this.userProfile.weight / (heightInMeters * heightInMeters)).toFixed(1)
      );
    }
    return 0;
  }

  getBMICategory(): string {
    const bmi = this.getBMI();
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Normal';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  }

  getFieldError(fieldName: string): string {
    const field = this.profileForm.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['email']) return 'Invalid email format';
      if (field.errors['minlength']) return `${fieldName} is too short`;
      if (field.errors['min']) return `${fieldName} value is too low`;
      if (field.errors['max']) return `${fieldName} value is too high`;
    }
    return '';
  }

  // Helper methods for template
  getGoalLabel(): string {
    return (
      this.goalOptions.find((g) => g.value === this.userProfile?.goal)?.label ||
      ''
    );
  }

  getActivityLabel(): string {
    return (
      this.activityLevelOptions.find(
        (a) => a.value === this.userProfile?.activityLevel
      )?.label || ''
    );
  }

  getPreferenceLabel(): string {
    return (
      this.preferenceOptions.find(
        (p) => p.value === this.userProfile?.preference
      )?.label || ''
    );
  }
}
