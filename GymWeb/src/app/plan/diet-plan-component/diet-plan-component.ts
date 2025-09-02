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
interface SimpleFoodItemDTO {
  foodName: string;
  category: string;
  quantity: number;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  fiber: number;
}

interface SimpleMealDTO {
  mealType: string;
  totalCalories: number;
  totalProtein: number;
  totalCarbs: number;
  totalFat: number;
  foodItems: SimpleFoodItemDTO[];
}

interface SimpleDayMealPlanDTO {
  dayNumber: number;
  dayName: string;
  totalDailyCalories: number;
  totalDailyProtein: number;
  totalDailyCarbs: number;
  totalDailyFat: number;
  meals: SimpleMealDTO[];
}

interface SimpleDietPlanDTO {
  id: number;
  userId: number;
  dailyCalorieTarget: number;
  dailyProteinTarget: number;
  dailyCarbsTarget: number;
  dailyFatTarget: number;
  createdDate: string;
  dailyPlans: SimpleDayMealPlanDTO[];
}

@Component({
  selector: 'app-diet-plan',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './diet-plan-component.html',
  styleUrls: ['./diet-plan-component.css'],
})
export class DietPlanComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  userProfile: UserProfileDTO | null = null;
  dietPlan: SimpleDietPlanDTO | null = null;
  selectedDay: SimpleDayMealPlanDTO | null = null;
  selectedDayIndex = 0;
  isLoading = false;

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
      this.loadDietPlan();
    } else {
      this.tokenService.email$
        .pipe(takeUntil(this.destroy$))
        .subscribe((email) => {
          if (email && !this.userProfile) this.loadUserData();
        });
    }
  }

  loadDietPlan() {
    if (this.userProfile) {
      this.isLoading = true;
      this.fitnessService
        .getUserPlansOptimized(this.userProfile)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response: OptimizedPlansResponseDTO) => {
            if (response.user) {
              this.userProfile = response.user;
            }
            this.dietPlan = response.dietPlan ?? null;
            this.selectedDayIndex = 0;
            this.selectedDay = this.dietPlan?.dailyPlans?.[0] ?? null;
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error loading diet plan:', error);
            this.isLoading = false;
          },
        });
    }
  }

  selectDay(index: number) {
    this.selectedDayIndex = index;
    this.selectedDay = this.dietPlan?.dailyPlans?.[index] ?? null;
  }

  regenerateDietPlan() {
    if (!this.userProfile) return;
    this.isLoading = true;
    this.fitnessService
      .regenerateDietPlan(this.userProfile)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (plan) => {
          this.dietPlan = plan;
          if (
            this.dietPlan?.dailyPlans &&
            this.dietPlan.dailyPlans.length > 0
          ) {
            this.selectedDay =
              this.dietPlan.dailyPlans[this.selectedDayIndex] ??
              this.dietPlan.dailyPlans[0];
          } else {
            this.selectedDay = null;
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error regenerating diet plan:', error);
          this.isLoading = false;
        },
      });
  }

  getMealIcon(mealType: string): string {
    switch (mealType?.toLowerCase()) {
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
    if (!target) return 0;
    return Math.min((actual / target) * 100, 100);
  }

  getCategoryColor(category: string): string {
    switch (category?.toLowerCase()) {
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
    return days[index] ?? `Day ${index + 1}`;
  }

  getDayNumber(index: number): number {
    return index + 1;
  }

  getMealsOfSelectedDay(): SimpleMealDTO[] {
    return this.selectedDay?.meals ?? [];
  }

  getFoodItemsOfMeal(meal: SimpleMealDTO): SimpleFoodItemDTO[] {
    return meal?.foodItems ?? [];
  }

  getDailyCalorieTarget(): number {
    return this.dietPlan?.dailyCalorieTarget ?? 0;
  }

  getDailyProteinTarget(): number {
    return this.dietPlan?.dailyProteinTarget ?? 0;
  }

  getDailyCarbsTarget(): number {
    return this.dietPlan?.dailyCarbsTarget ?? 0;
  }

  getDailyFatTarget(): number {
    return this.dietPlan?.dailyFatTarget ?? 0;
  }

  getSelectedDayCalories(): number {
    return this.selectedDay?.totalDailyCalories ?? 0;
  }

  getSelectedDayProtein(): number {
    return this.selectedDay?.totalDailyProtein ?? 0;
  }

  getSelectedDayCarbs(): number {
    return this.selectedDay?.totalDailyCarbs ?? 0;
  }

  getSelectedDayFat(): number {
    return this.selectedDay?.totalDailyFat ?? 0;
  }

  getCaloriesProgress(): number {
    return this.getProgressPercentage(
      this.getSelectedDayCalories(),
      this.getDailyCalorieTarget()
    );
  }

  getProteinProgress(): number {
    return this.getProgressPercentage(
      this.getSelectedDayProtein(),
      this.getDailyProteinTarget()
    );
  }

  getCarbsProgress(): number {
    return this.getProgressPercentage(
      this.getSelectedDayCarbs(),
      this.getDailyCarbsTarget()
    );
  }

  getFatProgress(): number {
    return this.getProgressPercentage(
      this.getSelectedDayFat(),
      this.getDailyFatTarget()
    );
  }

  formatNumber(value: number, decimals: number = 1): string {
    return value?.toFixed(decimals) ?? '0';
  }

  getTotalMeals(): number {
    return this.selectedDay?.meals?.length ?? 0;
  }

  getWeeklyAverageCalories(): number {
    if (!this.dietPlan?.dailyPlans?.length) return 0;
    return (
      this.dietPlan.dailyPlans.reduce(
        (sum, day) => sum + (day?.totalDailyCalories ?? 0),
        0
      ) / this.dietPlan.dailyPlans.length
    );
  }

  getWeeklyAverageProtein(): number {
    if (!this.dietPlan?.dailyPlans?.length) return 0;
    return (
      this.dietPlan.dailyPlans.reduce(
        (sum, day) => sum + (day?.totalDailyProtein ?? 0),
        0
      ) / this.dietPlan.dailyPlans.length
    );
  }

  formatMealType(mealType: string): string {
    return mealType
      ? mealType.charAt(0).toUpperCase() + mealType.slice(1).toLowerCase()
      : 'Meal';
  }

  formatCategory(category: string): string {
    return category
      ? category.charAt(0).toUpperCase() + category.slice(1).toLowerCase()
      : 'Food';
  }

  // Additional helper methods for template usage
  getFormattedWeight(item: SimpleFoodItemDTO): string {
    return `${item.quantity.toFixed(0)}g`;
  }

  getNutritionValue(value: number): string {
    return value.toFixed(1);
  }

  getMacroDisplayText(label: string, value: number): string {
    switch (label.toLowerCase()) {
      case 'protein':
        return `P: ${value.toFixed(0)}g`;
      case 'carbs':
        return `C: ${value.toFixed(0)}g`;
      case 'fat':
        return `F: ${value.toFixed(0)}g`;
      default:
        return `${value.toFixed(0)}`;
    }
  }

  getCalorieDisplay(calories: number): string {
    return `${calories.toFixed(0)} kcal`;
  }

  getMacroPercentage(actual: number, target: number): number {
    if (!target || target === 0) return 0;
    return Math.round((actual / target) * 100);
  }
}
