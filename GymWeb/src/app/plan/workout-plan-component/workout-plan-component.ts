import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import {
  FitnessService,
  UserProfileDTO,
  OptimizedPlansResponseDTO,
} from '../fitness-service';
import { Token } from '../../core/services/token';

// Interfaces matching your backend response
interface SimpleExerciseDTO {
  exerciseName: string;
  category: string;
  muscleGroup: string;
  equipment: string;
  difficulty: string;
  description: string;
  instructions: string;
  sets: number;
  reps: number;
  durationMinutes: number;
  weight: number;
  restSeconds: number;
  caloriesBurned: number;
  caloriesBurnedPerMinute: number;
}

interface SimpleDayWorkoutPlanDTO {
  dayNumber: number;
  dayName: string;
  focusArea: string;
  restDay: boolean;
  estimatedDurationMinutes: number;
  totalCaloriesBurned: number;
  exercises: SimpleExerciseDTO[];
}

interface SimpleWorkoutPlanDTO {
  id?: number;
  userId: number;
  planType: string;
  difficultyLevel: string;
  createdDate: string;
  weeklyPlan: SimpleDayWorkoutPlanDTO[];
}

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
      .getUserPlansOptimized(this.userProfile)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: OptimizedPlansResponseDTO) => {
          this.workoutPlan = response.workoutPlan ?? null;
          if (response.user) {
            this.userProfile = response.user;
          }
          this.selectedDay = this.workoutPlan?.weeklyPlan?.[0] ?? null;
          this.selectedDayIndex = 0;
          this.expandedExercises.clear();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading workout plan:', error);
          this.isLoading = false;
        },
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
        error: (error) => {
          console.error('Error regenerating workout plan:', error);
          this.isLoading = false;
        },
      });
  }

  // Formatting and helper methods
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

  formatFocusArea(area: string): string {
    return area
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, (l) => l.toUpperCase());
  }

  formatMuscleGroup(group: string): string {
    return group
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, (l) => l.toUpperCase());
  }

  formatEquipment(equipment: string): string {
    return equipment
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, (l) => l.toUpperCase());
  }

  formatDifficulty(difficulty: string): string {
    return difficulty.toLowerCase().replace(/\b\w/g, (l) => l.toUpperCase());
  }

  // Calculation methods
  getTotalWorkoutDays(): number {
    if (!this.workoutPlan?.weeklyPlan) return 0;
    return this.workoutPlan.weeklyPlan.filter((day) => !day.restDay).length;
  }

  getAverageDuration(): number {
    if (!this.workoutPlan?.weeklyPlan) return 0;
    const workoutDays = this.workoutPlan.weeklyPlan.filter(
      (day) => !day.restDay
    );
    if (workoutDays.length === 0) return 0;
    const totalDuration = workoutDays.reduce(
      (sum, day) => sum + day.estimatedDurationMinutes,
      0
    );
    return Math.round(totalDuration / workoutDays.length);
  }

  getTotalWeeklyCalories(): number {
    if (!this.workoutPlan?.weeklyPlan) return 0;
    return this.workoutPlan.weeklyPlan.reduce(
      (sum, day) => sum + day.totalCaloriesBurned,
      0
    );
  }

  // Exercise display methods
  getExerciseDisplayReps(exercise: SimpleExerciseDTO): string {
    if (exercise.reps > 0) {
      return `${exercise.reps} reps`;
    } else if (exercise.durationMinutes > 0) {
      return `${exercise.durationMinutes} min`;
    }
    return 'N/A';
  }

  getExerciseDisplaySets(exercise: SimpleExerciseDTO): string {
    return `${exercise.sets} sets`;
  }

  getExerciseDisplayRest(exercise: SimpleExerciseDTO): string {
    if (exercise.restSeconds >= 60) {
      const minutes = Math.floor(exercise.restSeconds / 60);
      const seconds = exercise.restSeconds % 60;
      return seconds > 0 ? `${minutes}m ${seconds}s` : `${minutes}m`;
    }
    return `${exercise.restSeconds}s`;
  }

  getExerciseDisplayWeight(exercise: SimpleExerciseDTO): string {
    return exercise.weight > 0 ? `${exercise.weight}kg` : 'Bodyweight';
  }

  // Day navigation helpers
  canNavigateToDay(index: number): boolean {
    return Boolean(
      this.workoutPlan?.weeklyPlan &&
        index >= 0 &&
        index < this.workoutPlan.weeklyPlan.length
    );
  }

  navigateToNextDay(): void {
    if (this.canNavigateToDay(this.selectedDayIndex + 1)) {
      this.selectDay(this.selectedDayIndex + 1);
    }
  }

  navigateToPreviousDay(): void {
    if (this.canNavigateToDay(this.selectedDayIndex - 1)) {
      this.selectDay(this.selectedDayIndex - 1);
    }
  }

  // Template helper methods
  getDayNumberDisplay(day: SimpleDayWorkoutPlanDTO): string {
    return `Day ${day.dayNumber}`;
  }

  hasExercises(day: SimpleDayWorkoutPlanDTO): boolean {
    return day.exercises && day.exercises.length > 0;
  }

  getRestDayMessage(): string {
    return 'Rest day - take time to recover and let your muscles rebuild stronger!';
  }

  getWorkoutSummary(): string {
    if (!this.workoutPlan) return '';
    const workoutDays = this.getTotalWorkoutDays();
    const avgDuration = this.getAverageDuration();
    const totalCalories = this.getTotalWeeklyCalories();

    return `${workoutDays} workout days/week • ${avgDuration}min avg • ${totalCalories} cal/week`;
  }

  // Validation methods
  isValidDay(
    day: SimpleDayWorkoutPlanDTO | null
  ): day is SimpleDayWorkoutPlanDTO {
    return day !== null && day !== undefined;
  }

  isValidWorkoutPlan(
    plan: SimpleWorkoutPlanDTO | null
  ): plan is SimpleWorkoutPlanDTO {
    return (
      plan !== null &&
      plan !== undefined &&
      plan.weeklyPlan &&
      plan.weeklyPlan.length > 0
    );
  }
}
