// dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  CompleteFitnessPlan,
  FitnessService,
  NutritionAnalysis,
  User,
} from '../fitness-service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './dashboard-component.html',
  styleUrls: ['./dashboard-component.css'],
})
export class DashboardComponent implements OnInit {
  user: User = {
    name: '',
    email: '',
    age: 25,
    height: 170,
    weight: 70,
    gender: 'MALE',
    goal: 'MUSCLE_GAIN',
    activity_level: 'MODERATELY_ACTIVE',
    preference: 'VEG',
  };

  completePlan: CompleteFitnessPlan | null = null;
  nutritionAnalysis: NutritionAnalysis | null = null;
  isLoading = false;
  showUserForm = true;

  // Chart data
  chartData = {
    calories: [2800, 2750, 2900, 2850, 2950, 3000, 2900],
    protein: [180, 175, 185, 170, 190, 195, 180],
    workouts: [1, 1, 0, 1, 1, 1, 0],
  };

  weekDays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

  constructor(private fitnessService: FitnessService) {}

  ngOnInit() {
    // Check if user data exists in local storage
    const savedUser = localStorage.getItem('fitnessUser');
    if (savedUser) {
      this.user = JSON.parse(savedUser);
      this.showUserForm = false;
      this.loadUserData();
    }
  }

  onSubmit() {
    if (this.validateForm()) {
      this.generateCompletePlan();
    }
  }

  validateForm(): boolean {
    return (
      this.user.name.trim() !== '' &&
      this.user.email.trim() !== '' &&
      this.user.age > 0 &&
      this.user.height > 0 &&
      this.user.weight > 0
    );
  }

  generateCompletePlan() {
    this.isLoading = true;
    this.fitnessService.generateCompletePlan(this.user).subscribe({
      next: (plan: CompleteFitnessPlan) => {
        this.completePlan = plan;
        this.user = plan.user;
        localStorage.setItem('fitnessUser', JSON.stringify(this.user));
        this.loadNutritionAnalysis();
        this.showUserForm = false;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error generating plan:', error);
        this.isLoading = false;
      },
    });
  }

  loadUserData() {
    if (this.user.userId) {
      this.isLoading = true;
      this.fitnessService.getUserPlans(this.user.userId).subscribe({
        next: (response) => {
          this.completePlan = {
            user: response.user,
            diet_plan: response.dietPlan,
            workout_plan: response.workoutPlan,
            generated_date: new Date().toISOString().split('T')[0],
            summary: `Plan for ${response.user.name}`,
          };
          this.loadNutritionAnalysis();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading user data:', error);
          this.isLoading = false;
        },
      });
    }
  }

  loadNutritionAnalysis() {
    if (this.user.userId) {
      this.fitnessService.getNutritionAnalysis(this.user.userId).subscribe({
        next: (analysis) => {
          this.nutritionAnalysis = analysis;
        },
        error: (error) => {
          console.error('Error loading nutrition analysis:', error);
        },
      });
    }
  }

  updateProfile() {
    if (this.user.userId) {
      this.isLoading = true;
      this.fitnessService
        .updateUserAndRegeneratePlans(this.user.userId, this.user)
        .subscribe({
          next: (plan) => {
            this.completePlan = plan;
            localStorage.setItem('fitnessUser', JSON.stringify(this.user));
            this.loadNutritionAnalysis();
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error updating profile:', error);
            this.isLoading = false;
          },
        });
    }
  }

  editProfile() {
    this.showUserForm = true;
  }

  getWeeklyWorkouts(): number {
    if (!this.completePlan?.workout_plan) return 0;
    return this.completePlan.workout_plan.weekly_plan.filter(
      (day) => !day.rest_day
    ).length;
  }

  getAverageCaloriesBurned(): number {
    if (!this.completePlan?.workout_plan) return 0;
    const totalCalories = this.completePlan.workout_plan.weekly_plan.reduce(
      (sum, day) => sum + day.total_calories_burned,
      0
    );
    return Math.round(totalCalories / 7);
  }

  getBMIStatus(): string {
    if (!this.user.height || !this.user.weight) return 'Unknown';
    const bmi = this.user.weight / (this.user.height / 100) ** 2;
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Normal';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  }

  getBMI(): number {
    if (!this.user.height || !this.user.weight) return 0;
    return (
      Math.round((this.user.weight / (this.user.height / 100) ** 2) * 10) / 10
    );
  }

  clearData() {
    localStorage.removeItem('fitnessUser');
    this.user = {
      name: '',
      email: '',
      age: 25,
      height: 170,
      weight: 70,
      gender: 'MALE',
      goal: 'MUSCLE_GAIN',
      activity_level: 'MODERATELY_ACTIVE',
      preference: 'VEG',
    };
    this.completePlan = null;
    this.nutritionAnalysis = null;
    this.showUserForm = true;
  }
}
