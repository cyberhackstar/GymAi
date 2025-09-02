import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import {
  FitnessService,
  SimpleDayWorkoutPlanDTO,
  SimpleWorkoutPlanDTO,
  UserProfileDTO,
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
  selectedDay: SimpleDayWorkoutPlanDTO | null = null; // Correct type!
  selectedDayIndex = 0;
  isLoading = false;
  expandedExercises = new Set<number>();

  constructor(
    private fitnessService: FitnessService,
    private tokenService: Token
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadUserProfile(): void {
    const email = this.tokenService.getEmail();
    const name = this.tokenService.getName();

    if (email && name) {
      this.userProfile = {
        name,
        email,
        age: 0,
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
      this.tokenService.email$
        .pipe(takeUntil(this.destroy$))
        .subscribe((email) => {
          if (email && !this.userProfile) this.loadUserProfile();
        });
    }
  }

  loadWorkoutPlan(): void {
    if (!this.userProfile) return;

    this.isLoading = true;
    this.fitnessService
      .getWorkoutPlan(this.userProfile)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (plan) => {
          this.workoutPlan = plan ?? null;
          this.selectedDay = this.workoutPlan?.weeklyPlan?.[0] ?? null;
          this.selectedDayIndex = 0;
          this.expandedExercises.clear();
          this.isLoading = false;
        },
        error: () => (this.isLoading = false),
      });
  }

  selectDay(index: number): void {
    this.selectedDayIndex = index;
    this.selectedDay = this.workoutPlan?.weeklyPlan?.[index] ?? null;
  }

  toggleExerciseDetails(index: number): void {
    if (this.expandedExercises.has(index)) {
      this.expandedExercises.delete(index);
    } else {
      this.expandedExercises.add(index);
    }
  }

  isExerciseExpanded(index: number): boolean {
    return this.expandedExercises.has(index);
  }

  regenerateWorkoutPlan(): void {
    if (!this.userProfile) return;

    this.isLoading = true;
    this.fitnessService
      .regenerateWorkoutPlan(this.userProfile)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (plan) => {
          this.workoutPlan = plan ?? null;
          this.selectedDay =
            this.workoutPlan?.weeklyPlan?.[this.selectedDayIndex] ??
            this.workoutPlan?.weeklyPlan?.[0] ??
            null;
          this.expandedExercises.clear();
          this.isLoading = false;
        },
        error: () => (this.isLoading = false),
      });
  }

  // Formatting helpers

  getFocusIcon(area?: string): string {
    switch (area?.toLowerCase()) {
      case 'upper_body':
        return 'hand-rock';
      case 'lower_body':
        return 'running';
      case 'full_body':
        return 'user';
      case 'cardio':
        return 'heartbeat';
      case 'rest':
        return 'bed';
      default:
        return 'dumbbell';
    }
  }

  getMuscleColor(group?: string): string {
    switch (group?.toLowerCase()) {
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

  getEquipmentIcon(equipment?: string): string {
    switch (equipment?.toLowerCase()) {
      case 'barbell':
        return 'weight-hanging';
      case 'dumbbell':
        return 'dumbbell';
      case 'resistance_band':
        return 'link';
      case 'none':
        return 'hand-paper';
      default:
        return 'tools';
    }
  }
}
