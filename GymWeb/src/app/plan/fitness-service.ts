import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  userId?: number;
  name: string;
  email: string;
  age: number;
  height: number;
  weight: number;
  gender: string;
  goal: string;
  activity_level: string;
  preference: string;
}

export interface Food {
  id: number;
  name: string;
  calories_per100g: number;
  protein_per100g: number;
  carbs_per100g: number;
  fat_per100g: number;
  fiber_per100g: number;
  diet_type: string;
  meal_type: string;
  category: string;
}

export interface FoodItem {
  food: Food;
  quantity: number;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  fiber: number;
}

export interface Meal {
  meal_type: string;
  food_items: FoodItem[];
  total_calories: number;
  total_protein: number;
  total_carbs: number;
  total_fat: number;
}

export interface DayMealPlan {
  day_number: number;
  day_name: string;
  meals: Meal[];
  total_daily_calories: number;
  total_daily_protein: number;
  total_daily_carbs: number;
  total_daily_fat: number;
}

export interface DietPlan {
  user_id: number;
  daily_plans: DayMealPlan[];
  daily_calorie_target: number;
  daily_protein_target: number;
  daily_carbs_target: number;
  daily_fat_target: number;
  created_date: string;
}

export interface Exercise {
  id: number;
  name: string;
  category: string;
  muscle_group: string;
  equipment: string;
  difficulty: string;
  calories_burned_per_minute: number;
  description: string;
  instructions: string;
}

export interface WorkoutExercise {
  exercise: Exercise;
  sets: number;
  reps: number;
  duration_minutes: number;
  weight: number;
  rest_seconds: number;
  calories_burned: number;
}

export interface DayWorkoutPlan {
  day_number: number;
  day_name: string;
  focus_area: string;
  exercises: WorkoutExercise[];
  estimated_duration_minutes: number;
  total_calories_burned: number;
  rest_day: boolean;
}

export interface WorkoutPlan {
  user_id: number;
  weekly_plan: DayWorkoutPlan[];
  plan_type: string;
  difficulty_level: string;
  created_date: string;
}

export interface CompleteFitnessPlan {
  user: User;
  diet_plan: DietPlan;
  workout_plan: WorkoutPlan;
  generated_date: string;
  summary: string;
}

export interface NutritionAnalysis {
  userId: number;
  dailyCalories: number;
  dailyProtein: number;
  dailyCarbs: number;
  dailyFat: number;
  bmr: number;
  tdee: number;
}

export interface UserPlansResponse {
  user: User;
  dietPlan: DietPlan;
  workoutPlan: WorkoutPlan;
}

@Injectable({
  providedIn: 'root',
})
export class FitnessService {
  private baseUrl = 'http://localhost:8083/api/fitness';

  constructor(private http: HttpClient) {}

  generateCompletePlan(user: User): Observable<CompleteFitnessPlan> {
    return this.http.post<CompleteFitnessPlan>(
      `${this.baseUrl}/generate-complete-plan`,
      user
    );
  }

  generateDietPlan(user: User): Observable<DietPlan> {
    return this.http.post<DietPlan>(`${this.baseUrl}/generate-diet-plan`, user);
  }

  generateWorkoutPlan(user: User): Observable<WorkoutPlan> {
    return this.http.post<WorkoutPlan>(
      `${this.baseUrl}/generate-workout-plan`,
      user
    );
  }

  getUserPlans(userId: number): Observable<UserPlansResponse> {
    return this.http.get<UserPlansResponse>(
      `${this.baseUrl}/user/${userId}/plans`
    );
  }

  updateUserAndRegeneratePlans(
    userId: number,
    user: User
  ): Observable<CompleteFitnessPlan> {
    return this.http.put<CompleteFitnessPlan>(
      `${this.baseUrl}/user/${userId}/update-and-regenerate`,
      user
    );
  }

  getNutritionAnalysis(userId: number): Observable<NutritionAnalysis> {
    return this.http.get<NutritionAnalysis>(
      `${this.baseUrl}/user/${userId}/nutrition-analysis`
    );
  }

  // Add these methods to your FitnessService (optional)
  regenerateDietPlanOnly(userId: number): Observable<DietPlan> {
    return this.http.post<DietPlan>(
      `${this.baseUrl}/user/${userId}/regenerate-diet`,
      {}
    );
  }

  regenerateWorkoutPlanOnly(userId: number): Observable<WorkoutPlan> {
    return this.http.post<WorkoutPlan>(
      `${this.baseUrl}/user/${userId}/regenerate-workout`,
      {}
    );
  }
}
