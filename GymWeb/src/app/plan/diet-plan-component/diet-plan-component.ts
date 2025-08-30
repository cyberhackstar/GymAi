// diet-plan.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  DayMealPlan,
  DietPlan,
  FitnessService,
  User,
} from '../fitness-service';

@Component({
  selector: 'app-diet-plan',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './diet-plan-component.html',
  styleUrls: ['./diet-plan-component.css'],
})
export class DietPlanComponent implements OnInit {
  user: User | null = null;
  dietPlan: DietPlan | null = null;
  selectedDay: DayMealPlan | null = null;
  selectedDayIndex = 0;
  isLoading = false;

  constructor(private fitnessService: FitnessService) {}

  ngOnInit() {
    this.loadUserData();
  }

  loadUserData() {
    const savedUser = localStorage.getItem('fitnessUser');
    if (savedUser) {
      this.user = JSON.parse(savedUser);
      this.loadDietPlan();
    }
  }

  loadDietPlan() {
    if (this.user?.userId) {
      this.isLoading = true;
      this.fitnessService.getUserPlans(this.user.userId).subscribe({
        next: (response) => {
          this.dietPlan = response.dietPlan;
          this.selectedDay = this.dietPlan.daily_plans[0];
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading diet plan:', error);
          this.isLoading = false;
        },
      });
    }
  }

  selectDay(dayIndex: number) {
    this.selectedDayIndex = dayIndex;
    if (this.dietPlan) {
      this.selectedDay = this.dietPlan.daily_plans[dayIndex];
    }
  }

  getMealIcon(mealType: string): string {
    switch (mealType.toLowerCase()) {
      case 'breakfast':
        return 'fa-sun';
      case 'lunch':
        return 'fa-utensils';
      case 'dinner':
        return 'fa-moon';
      case 'snack':
        return 'fa-cookie-bite';
      default:
        return 'fa-utensils';
    }
  }

  getProgressPercentage(actual: number, target: number): number {
    return Math.min((actual / target) * 100, 100);
  }

  regenerateDietPlan() {
    if (this.user) {
      this.isLoading = true;
      this.fitnessService.generateDietPlan(this.user).subscribe({
        next: (plan) => {
          this.dietPlan = plan;
          this.selectedDay = this.dietPlan.daily_plans[this.selectedDayIndex];
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error regenerating diet plan:', error);
          this.isLoading = false;
        },
      });
    }
  }

  getCategoryColor(category: string): string {
    switch (category.toLowerCase()) {
      case 'grains':
        return '#ffc107';
      case 'protein':
        return '#28a745';
      case 'vegetables':
        return '#20c997';
      case 'fruits':
        return '#fd7e14';
      case 'dairy':
        return '#6f42c1';
      case 'nuts':
        return '#dc3545';
      default:
        return '#6c757d';
    }
  }
}
