// workout-plan.component.ts - UPDATED TO MATCH NEW SERVICE
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import {
  FitnessService,
  UserProfileDTO,
  SimpleWorkoutPlanDTO,
  SimpleDayWorkoutPlanDTO,
  SimpleWorkoutExerciseDTO,
  OptimizedPlansResponseDTO,
} from '../fitness-service';
import { Token } from '../../core/services/token';

@Component({
  selector: 'app-workout-plan',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './workout-plan-component.html',
  styleUrls: ['./workout-plan-component.css'],
})
export class WorkoutPlanComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  userProfile: UserProfileDTO | null = null;
  workoutPlan: SimpleWorkoutPlanDTO | null = null;
  selectedDay: SimpleDayWorkoutPlanDTO | null = null;
  selectedDayIndex = 0;
  isLoading = false;
  expandedExercises: Set<number> = new Set();

  constructor(
    private fitnessService: FitnessService,
    private tokenService: Token
  ) {}

  ngOnInit() {
    this.loadUserData();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadUserData() {
    // Get user info from token service
    const userEmail = this.tokenService.getEmail();
    const userName = this.tokenService.getName();

    if (userEmail && userName) {
      // Create user profile for API call
      this.userProfile = {
        name: userName,
        email: userEmail,
        age: 0, // These will be populated from backend
        height: 0,
        weight: 0,
        gender: '',
        goal: '',
        activityLevel: '',
        preference: '',
        profileComplete: false,
      };
      this.loadWorkoutPlan();
    } else {
      // Listen for token changes
      this.tokenService.email$
        .pipe(takeUntil(this.destroy$))
        .subscribe((email) => {
          if (email && !this.userProfile) {
            this.loadUserData();
          }
        });
    }
  }

  loadWorkoutPlan() {
    if (this.userProfile) {
      this.isLoading = true;
      this.fitnessService
        .getUserPlansOptimized(this.userProfile)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response: OptimizedPlansResponseDTO) => {
            this.userProfile = response.user;
            this.workoutPlan = response.workoutPlan || null;
            if (this.workoutPlan?.weeklyPlan?.length) {
              this.selectedDay = this.workoutPlan.weeklyPlan[0];
            }
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error loading workout plan:', error);
            this.isLoading = false;
          },
        });
    }
  }

  selectDay(dayIndex: number) {
    this.selectedDayIndex = dayIndex;
    if (this.workoutPlan?.weeklyPlan) {
      this.selectedDay = this.workoutPlan.weeklyPlan[dayIndex];
    }
  }

  toggleExerciseDetails(exerciseIndex: number) {
    if (this.expandedExercises.has(exerciseIndex)) {
      this.expandedExercises.delete(exerciseIndex);
    } else {
      this.expandedExercises.add(exerciseIndex);
    }
  }

  isExerciseExpanded(exerciseIndex: number): boolean {
    return this.expandedExercises.has(exerciseIndex);
  }

  getFocusAreaIcon(focusArea: string): string {
    switch (focusArea?.toLowerCase()) {
      case 'upper_body':
        return 'fa-hand-rock';
      case 'lower_body':
        return 'fa-running';
      case 'full_body':
        return 'fa-user';
      case 'cardio':
        return 'fa-heartbeat';
      case 'rest':
        return 'fa-bed';
      default:
        return 'fa-dumbbell';
    }
  }

  getMuscleGroupColor(muscleGroup: string): string {
    switch (muscleGroup?.toLowerCase()) {
      case 'chest':
        return '#ff4c4c';
      case 'back':
        return '#28a745';
      case 'arms':
        return '#ffc107';
      case 'shoulders':
        return '#6f42c1';
      case 'legs':
        return '#fd7e14';
      case 'core':
        return '#20c997';
      case 'full_body':
        return '#17a2b8';
      default:
        return '#6c757d';
    }
  }

  getEquipmentIcon(equipment: string): string {
    switch (equipment?.toLowerCase()) {
      case 'barbell':
        return 'fa-weight-hanging';
      case 'dumbbell':
        return 'fa-dumbbell';
      case 'resistance_band':
        return 'fa-link';
      case 'none':
        return 'fa-hand-paper';
      default:
        return 'fa-tools';
    }
  }

  regenerateWorkoutPlan() {
    if (this.userProfile) {
      this.isLoading = true;
      this.fitnessService
        .regenerateWorkoutPlan(this.userProfile)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (plan: SimpleWorkoutPlanDTO) => {
            this.workoutPlan = plan;
            if (this.workoutPlan?.weeklyPlan?.length) {
              this.selectedDay =
                this.workoutPlan.weeklyPlan[this.selectedDayIndex] ||
                this.workoutPlan.weeklyPlan[0];
            }
            this.expandedExercises.clear();
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error regenerating workout plan:', error);
            this.isLoading = false;
          },
        });
    }
  }

  getTotalWeeklyCalories(): number {
    if (!this.workoutPlan?.weeklyPlan) return 0;
    return this.workoutPlan.weeklyPlan.reduce(
      (total, day) => total + (day?.totalCaloriesBurned || 0),
      0
    );
  }

  getActiveWorkoutDays(): number {
    if (!this.workoutPlan?.weeklyPlan) return 0;
    return this.workoutPlan.weeklyPlan.filter((day) => day && !day.restDay)
      .length;
  }

  getAverageWorkoutDuration(): number {
    if (!this.workoutPlan?.weeklyPlan) return 0;
    const activeDays = this.workoutPlan.weeklyPlan.filter(
      (day) => day && !day.restDay
    );
    if (activeDays.length === 0) return 0;

    const totalDuration = activeDays.reduce(
      (total, day) => total + (day?.estimatedDurationMinutes || 0),
      0
    );
    return Math.round(totalDuration / activeDays.length);
  }

  // Helper methods for template
  getDayName(index: number): string {
    const days = [
      'Monday',
      'Tuesday',
      'Wednesday',
      'Thursday',
      'Friday',
      'Saturday',
      'Sunday',
    ];
    return days[index] || `Day ${index + 1}`;
  }

  getDayNumber(index: number): number {
    return index + 1;
  }

  formatFocusArea(focusArea: string): string {
    return (
      focusArea
        ?.replace('_', ' ')
        .toLowerCase()
        .replace(/\b\w/g, (l) => l.toUpperCase()) || 'General'
    );
  }

  formatMuscleGroup(muscleGroup: string): string {
    return (
      muscleGroup
        ?.replace('_', ' ')
        .toLowerCase()
        .replace(/\b\w/g, (l) => l.toUpperCase()) || 'General'
    );
  }

  formatEquipment(equipment: string): string {
    return (
      equipment
        ?.replace('_', ' ')
        .toLowerCase()
        .replace(/\b\w/g, (l) => l.toUpperCase()) || 'None'
    );
  }

  // Get exercises for current day
  getTodayExercises(): SimpleWorkoutExerciseDTO[] {
    return this.selectedDay?.exercises || [];
  }

  // Check if current day is rest day
  isRestDay(): boolean {
    return this.selectedDay?.restDay || false;
  }
}
