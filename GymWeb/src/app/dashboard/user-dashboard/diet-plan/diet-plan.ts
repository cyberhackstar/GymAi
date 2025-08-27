import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  DietWorkoutPlan,
  User,
} from '../../../core/services/diet-workout-plan';

interface FoodItem {
  id: number;
  name: string;
  category: string;
  diet_type: string;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  fiber: number;
  quantity: number;
  taken?: boolean;
  image?: string;
}

interface Meal {
  meal_type: string;
  food_items: FoodItem[];
  total_calories: number;
  total_protein: number;
  total_carbs: number;
  total_fat: number;
}

interface DayPlan {
  day: string;
  meals: Meal[];
  total_daily_calories: number;
  total_daily_protein: number;
  total_daily_carbs: number;
  total_daily_fat: number;
  dailyCaloriesTaken: number;
  progressPercent: number;
  progressColor?: string;
  activeMealIndex: number;
}

@Component({
  selector: 'app-diet-plan',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './diet-plan.html',
  styleUrls: ['./diet-plan.css'],
})
export class DietPlan implements OnInit {
  dietPlan: DayPlan[] = [];
  lightboxImage: string | null = null;
  showAllDays = false;
  loading = false;

  constructor(private dietService: DietWorkoutPlan) {}

  ngOnInit() {
    this.fetchDietPlan();
  }

  fetchDietPlan() {
    this.loading = true;

    const user: User = {
      name: 'John Doe',
      email: 'john@example.com',
      age: 25,
      height: 175,
      weight: 70,
      gender: 'MALE',
      goal: 'MUSCLE_GAIN',
      activity_level: 'MODERATELY_ACTIVE',
      preference: 'VEG',
    };

    this.dietService.generateDietPlan(user).subscribe({
      next: (res: any) => {
        /** Mapping backend response structure with inner 'food' object fields **/
        this.dietPlan = res.daily_plans.map((day: any) => ({
          day: day.day_name,
          meals: day.meals.map((meal: any, mealIdx: number) => ({
            meal_type: meal.meal_type,
            food_items: meal.food_items.map((fi: any) => ({
              id: fi.food.id,
              name: fi.food.name,
              category: fi.food.category,
              diet_type: fi.food.diet_type,
              calories: fi.calories,
              protein: fi.protein,
              carbs: fi.carbs,
              fat: fi.fat,
              fiber: fi.fiber,
              quantity: fi.quantity,
              taken: false,
              image: 'https://img.spoonacular.com/recipes/654515-312x231.jpg', // replace with a real image URL
            })),
            total_calories: meal.total_calories,
            total_protein: meal.total_protein,
            total_carbs: meal.total_carbs,
            total_fat: meal.total_fat,
          })),
          total_daily_calories: day.total_daily_calories,
          total_daily_protein: day.total_daily_protein,
          total_daily_carbs: day.total_daily_carbs,
          total_daily_fat: day.total_daily_fat,
          dailyCaloriesTaken: 0,
          progressPercent: 0,
          activeMealIndex: 0,
        }));
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching diet plan', err);
        this.loading = false;
      },
    });
  }

  getMealTime(mealType: string, index: number): string {
    switch (mealType) {
      case 'BREAKFAST':
        return '8:00 AM';
      case 'LUNCH':
        return '12:00 PM';
      case 'SNACK':
        return '4:00 PM';
      case 'DINNER':
        return '7:00 PM';
      default:
        return `${8 + index * 4}:00 AM`;
    }
  }

  toggleDays() {
    this.showAllDays = !this.showAllDays;
  }

  updateCalories() {
    this.dietPlan.forEach((dayPlan) => {
      let caloriesTaken = 0;
      dayPlan.meals.forEach((meal) => {
        meal.food_items.forEach((item) => {
          if (item.taken) caloriesTaken += item.calories;
        });
      });
      dayPlan.dailyCaloriesTaken = caloriesTaken;
    });
  }

  updateDailyProgress(dayPlan: DayPlan) {
    let caloriesTaken = 0;
    dayPlan.meals.forEach((meal) => {
      meal.food_items.forEach((item) => {
        if (item.taken) caloriesTaken += item.calories;
      });
    });
    dayPlan.dailyCaloriesTaken = caloriesTaken;
    dayPlan.progressPercent = Math.min(
      Math.round((caloriesTaken / dayPlan.total_daily_calories) * 100),
      100
    );
    if (dayPlan.progressPercent === 100) dayPlan.progressColor = '#4CAF50';
    else if (dayPlan.progressPercent >= 50) dayPlan.progressColor = '#FF9800';
    else dayPlan.progressColor = '#F44336';
  }

  nextMeal(dayPlan: DayPlan) {
    if (dayPlan.activeMealIndex < dayPlan.meals.length - 1) {
      dayPlan.activeMealIndex++;
    }
  }

  prevMeal(dayPlan: DayPlan) {
    if (dayPlan.activeMealIndex > 0) {
      dayPlan.activeMealIndex--;
    }
  }

  goToMeal(dayPlan: DayPlan, index: number) {
    if (index >= 0 && index < dayPlan.meals.length) {
      dayPlan.activeMealIndex = index;
    }
  }

  openLightbox(image: string) {
    this.lightboxImage = image;
  }

  closeLightbox() {
    this.lightboxImage = null;
  }
}
