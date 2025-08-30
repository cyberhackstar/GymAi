// workout-plan.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  DayWorkoutPlan,
  FitnessService,
  User,
  WorkoutPlan,
} from '../fitness-service';

@Component({
  selector: 'app-workout-plan',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './workout-plan-component.html',
  styleUrls: ['./workout-plan-component.css'],
})
export class WorkoutPlanComponent implements OnInit {
  user: User | null = null;
  workoutPlan: WorkoutPlan | null = null;
  selectedDay: DayWorkoutPlan | null = null;
  selectedDayIndex = 0;
  isLoading = false;
  expandedExercises: Set<number> = new Set();

  constructor(private fitnessService: FitnessService) {}

  ngOnInit() {
    this.loadUserData();
  }

  loadUserData() {
    const savedUser = localStorage.getItem('fitnessUser');
    if (savedUser) {
      this.user = JSON.parse(savedUser);
      this.loadWorkoutPlan();
    }
  }

  loadWorkoutPlan() {
    if (this.user?.userId) {
      this.isLoading = true;
      this.fitnessService.getUserPlans(this.user.userId).subscribe({
        next: (response) => {
          this.workoutPlan = response.workoutPlan;
          this.selectedDay = this.workoutPlan.weekly_plan[0];
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
    if (this.workoutPlan) {
      this.selectedDay = this.workoutPlan.weekly_plan[dayIndex];
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
    switch (focusArea.toLowerCase()) {
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
    switch (muscleGroup.toLowerCase()) {
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
    switch (equipment.toLowerCase()) {
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
    if (this.user) {
      this.isLoading = true;
      this.fitnessService.generateWorkoutPlan(this.user).subscribe({
        next: (plan) => {
          this.workoutPlan = plan;
          this.selectedDay =
            this.workoutPlan.weekly_plan[this.selectedDayIndex];
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
    if (!this.workoutPlan) return 0;
    return this.workoutPlan.weekly_plan.reduce(
      (total, day) => total + day.total_calories_burned,
      0
    );
  }

  getActiveWorkoutDays(): number {
    if (!this.workoutPlan) return 0;
    return this.workoutPlan.weekly_plan.filter((day) => !day.rest_day).length;
  }

  getAverageWorkoutDuration(): number {
    if (!this.workoutPlan) return 0;
    const activeDays = this.workoutPlan.weekly_plan.filter(
      (day) => !day.rest_day
    );
    if (activeDays.length === 0) return 0;

    const totalDuration = activeDays.reduce(
      (total, day) => total + day.estimated_duration_minutes,
      0
    );
    return Math.round(totalDuration / activeDays.length);
  }
}
