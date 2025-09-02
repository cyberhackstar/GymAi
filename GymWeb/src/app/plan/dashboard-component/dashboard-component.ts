// dashboard.component.ts - UPDATED TO MATCH BACKEND INTERFACES
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import {
  FitnessService,
  OptimizedPlansResponseDTO,
  UserProfileCheckDTO,
  UserProfileDTO,
} from '../fitness-service';
import { Token } from '../../core/services/token';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './dashboard-component.html',
  styleUrls: ['./dashboard-component.css'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  // User data from token
  userEmail: string = '';
  userName: string = '';

  // Default user profile template
  private defaultUserProfile: UserProfileDTO = {
    name: '',
    email: '',
    age: 25,
    height: 170,
    weight: 70,
    gender: 'MALE',
    goal: 'MUSCLE_GAIN',
    activityLevel: 'MODERATELY_ACTIVE',
    preference: 'VEG',
    profileComplete: false,
  };

  // User profile for form - initialized with default
  userProfile: UserProfileDTO = { ...this.defaultUserProfile };

  // Plan data
  plansResponse: OptimizedPlansResponseDTO | null = null;
  isLoading = false;
  showUserForm = false;
  isProfileComplete = false;
  plansExist = false;

  // Chart data (mock data - you can integrate with real tracking later)
  chartData = {
    calories: [2800, 2750, 2900, 2850, 2950, 3000, 2900],
    protein: [180, 175, 185, 170, 190, 195, 180],
    workouts: [1, 1, 0, 1, 1, 1, 0],
  };

  weekDays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

  constructor(
    private optimizedFitnessService: FitnessService,
    private tokenService: Token
  ) {}

  ngOnInit() {
    // Initialize with default profile
    this.userProfile = { ...this.defaultUserProfile };
    // Get user info from token and initialize
    this.initializeUserData();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  initializeUserData() {
    // Get current values from token service
    this.userEmail = this.tokenService.getEmail() || '';
    this.userName = this.tokenService.getName() || '';

    if (this.userEmail && this.userName) {
      this.userProfile.email = this.userEmail;
      this.userProfile.name = this.userName;
      this.checkUserProfileStatus();
    } else {
      // Listen for token changes if not immediately available
      this.tokenService.email$
        .pipe(takeUntil(this.destroy$))
        .subscribe((email) => {
          if (email && !this.userEmail) {
            this.userEmail = email;
            this.userProfile.email = email;
            this.checkUserProfileStatusIfReady();
          }
        });

      this.tokenService.name$
        .pipe(takeUntil(this.destroy$))
        .subscribe((name) => {
          if (name && !this.userName) {
            this.userName = name;
            this.userProfile.name = name;
            this.checkUserProfileStatusIfReady();
          }
        });
    }
  }

  checkUserProfileStatusIfReady() {
    if (this.userEmail && this.userName) {
      this.checkUserProfileStatus();
    }
  }

  checkUserProfileStatus() {
    if (!this.userEmail) return;

    this.isLoading = true;

    // Send user data directly to backend
    const userToCheck: UserProfileDTO = {
      name: this.userName,
      email: this.userEmail,
      age: 0,
      height: 0,
      weight: 0,
      gender: '',
      goal: '',
      activityLevel: '',
      preference: '',
      profileComplete: false,
    };

    this.optimizedFitnessService
      .checkUserProfile(userToCheck)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: UserProfileCheckDTO) => {
          if (response.exists && response.user) {
            this.userProfile = response.user;
            this.isProfileComplete = response.user.profileComplete;

            if (this.isProfileComplete) {
              this.loadUserPlans();
            } else {
              this.showUserForm = true;
              this.isLoading = false;
            }
          } else {
            // User doesn't exist, show form to create profile
            this.showUserForm = true;
            this.isLoading = false;
          }
        },
        error: (error) => {
          console.error('Error checking user profile:', error);
          this.showUserForm = true;
          this.isLoading = false;
        },
      });
  }

  loadUserPlans() {
    if (!this.userProfile.email) return;

    this.isLoading = true;
    this.optimizedFitnessService
      .getUserPlansOptimized(this.userProfile)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: OptimizedPlansResponseDTO) => {
          this.plansResponse = response;
          this.plansExist = response.plansExist;
          this.userProfile = response.user;
          this.showUserForm = false;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading user plans:', error);
          this.isLoading = false;
        },
      });
  }

  onSubmit() {
    if (!this.validateForm()) return;

    this.isLoading = true;
    this.optimizedFitnessService
      .completeUserProfile(this.userProfile)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: OptimizedPlansResponseDTO) => {
          this.plansResponse = response;
          this.plansExist = response.plansExist;
          this.userProfile = response.user;
          this.isProfileComplete = true;
          this.showUserForm = false;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error completing profile:', error);
          this.isLoading = false;
        },
      });
  }

  validateForm(): boolean {
    return (
      this.userProfile.name.trim() !== '' &&
      this.userProfile.email.trim() !== '' &&
      this.userProfile.age > 0 &&
      this.userProfile.height > 0 &&
      this.userProfile.weight > 0
    );
  }

  updateProfile() {
    if (!this.validateForm()) return;

    this.isLoading = true;
    this.optimizedFitnessService
      .updateProfileAndRegeneratePlans(this.userProfile)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: OptimizedPlansResponseDTO) => {
          this.plansResponse = response;
          this.showUserForm = false;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error updating profile:', error);
          this.isLoading = false;
        },
      });
  }

  editProfile() {
    this.showUserForm = true;
  }

  getWeeklyWorkouts(): number {
    if (!this.plansResponse?.workoutPlan?.weeklyPlan) return 0;
    return this.plansResponse.workoutPlan.weeklyPlan.filter(
      (day) => day && !day.restDay
    ).length;
  }

  getAverageCaloriesBurned(): number {
    if (!this.plansResponse?.workoutPlan?.weeklyPlan) return 0;
    const totalCalories = this.plansResponse.workoutPlan.weeklyPlan.reduce(
      (sum, day) => sum + (day?.totalCaloriesBurned || 0),
      0
    );
    return Math.round(totalCalories / 7);
  }

  getBMIStatus(): string {
    if (!this.userProfile.height || !this.userProfile.weight) return 'Unknown';
    const bmi = this.userProfile.weight / (this.userProfile.height / 100) ** 2;
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Normal';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  }

  getBMI(): number {
    if (!this.userProfile.height || !this.userProfile.weight) return 0;
    return (
      Math.round(
        (this.userProfile.weight / (this.userProfile.height / 100) ** 2) * 10
      ) / 10
    );
  }

  // Helper methods for template - with null safety
  getWorkoutDayRestStatus(index: number): boolean {
    const weeklyPlan = this.plansResponse?.workoutPlan?.weeklyPlan;
    if (!weeklyPlan || !weeklyPlan[index]) return false;
    return weeklyPlan[index].restDay || false;
  }

  getTodaysWorkout() {
    const weeklyPlan = this.plansResponse?.workoutPlan?.weeklyPlan;
    if (!weeklyPlan || !weeklyPlan[0]) return null;

    const todayWorkout = weeklyPlan[0];
    return todayWorkout && !todayWorkout.restDay ? todayWorkout : null;
  }

  getTodaysWorkoutExercises() {
    const todayWorkout = this.getTodaysWorkout();
    if (!todayWorkout || !todayWorkout.exercises) return [];
    return todayWorkout.exercises.slice(0, 3) || [];
  }

  getTodaysWorkoutExercisesCount(): number {
    const todayWorkout = this.getTodaysWorkout();
    if (!todayWorkout || !todayWorkout.exercises) return 0;
    return todayWorkout.exercises.length || 0;
  }

  isTodayRestDay(): boolean {
    const weeklyPlan = this.plansResponse?.workoutPlan?.weeklyPlan;
    if (!weeklyPlan || !weeklyPlan[0]) return false;
    return weeklyPlan[0].restDay || false;
  }

  // Get today's meals safely
  getTodaysMeals() {
    const dailyPlans = this.plansResponse?.dietPlan?.dailyPlans;
    if (!dailyPlans || !dailyPlans[0] || !dailyPlans[0].meals) return [];
    return dailyPlans[0].meals;
  }

  // Get user stats for display
  getUserAge(): number {
    return this.userProfile.age || 0;
  }

  getUserHeight(): number {
    return this.userProfile.height || 0;
  }

  getUserWeight(): number {
    return this.userProfile.weight || 0;
  }

  getUserGoal(): string {
    return this.userProfile.goal ? this.formatGoal(this.userProfile.goal) : '';
  }

  getUserActivityLevel(): string {
    return this.userProfile.activityLevel
      ? this.formatActivityLevel(this.userProfile.activityLevel)
      : '';
  }

  getUserPreference(): string {
    return this.userProfile.preference || '';
  }

  private formatGoal(goal: string): string {
    return goal
      .replace('_', ' ')
      .toLowerCase()
      .replace(/\b\w/g, (l) => l.toUpperCase());
  }

  private formatActivityLevel(level: string): string {
    return level
      .replace('_', ' ')
      .toLowerCase()
      .replace(/\b\w/g, (l) => l.toUpperCase());
  }

  clearData() {
    if (!this.userProfile.email) return;

    if (
      confirm(
        'Are you sure you want to delete all your fitness data? This action cannot be undone.'
      )
    ) {
      this.isLoading = true;
      this.optimizedFitnessService
        .deleteUserPlans(this.userProfile)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.plansResponse = null;
            this.plansExist = false;
            this.showUserForm = true;
            // Reset profile but keep name and email
            this.userProfile = {
              ...this.defaultUserProfile,
              name: this.userName,
              email: this.userEmail,
            };
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error clearing data:', error);
            this.isLoading = false;
          },
        });
    }
  }

  logout() {
    this.tokenService.clearToken();
    // Redirect to login page - adjust route as needed
    window.location.href = '/login';
  }

  // Helper method to get current date for template
  getCurrentDate(): Date {
    return new Date();
  }

  // Inside DashboardComponent class

  get dailyCalorieTarget(): number | null {
    return this.plansResponse?.dietPlan?.dailyCalorieTarget ?? null;
  }

  get dailyProteinTarget(): number | null {
    return this.plansResponse?.dietPlan?.dailyProteinTarget ?? null;
  }

  get dailyCarbsTarget(): number | null {
    return this.plansResponse?.dietPlan?.dailyCarbsTarget ?? null;
  }

  get weeklyPlan() {
    return this.plansResponse?.workoutPlan?.weeklyPlan ?? [];
  }

  get nutrition() {
    return this.plansResponse?.nutritionAnalysis ?? null;
  }
}
