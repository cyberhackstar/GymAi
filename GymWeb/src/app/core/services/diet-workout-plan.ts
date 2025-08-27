import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

// Interfaces for responses
export interface User {
  userId?: number;
  name: string;
  email: string;
  age: number;
  height: number;
  weight: number;
  gender: string; // "MALE" | "FEMALE"
  goal: string; // "WEIGHT_LOSS" | "WEIGHT_GAIN" | "MUSCLE_GAIN" | "MAINTENANCE"
  activity_level: string; // "SEDENTARY" | "LIGHTLY_ACTIVE" | ...
  preference: string; // "VEG" | "NON_VEG" | "VEGAN"
}

export interface DietPlan {
  id?: number;
  meals: any; // Adjust based on your backend model
}

export interface WorkoutPlan {
  id?: number;
  exercises: any; // Adjust based on your backend model
}

export interface CompleteFitnessPlan {
  user: User;
  dietPlan: DietPlan;
  workoutPlan: WorkoutPlan;
  generatedDate: string;
}

export interface UserPlansResponse {
  user: User;
  dietPlan: DietPlan;
  workoutPlan: WorkoutPlan;
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
@Injectable({
  providedIn: 'root',
})
export class DietWorkoutPlan {
  private baseUrl = environment.planUrl;
  // private apiUrl = environment.userUrl;

  constructor(private http: HttpClient) {}

  // Generate Complete Plan
  generateCompletePlan(user: User): Observable<CompleteFitnessPlan> {
    return this.http.post<CompleteFitnessPlan>(
      `${this.baseUrl}/generate-complete-plan`,
      user
    );
  }

  // Generate Diet Plan
  generateDietPlan(user: User): Observable<DietPlan> {
    return this.http.post<DietPlan>(`${this.baseUrl}/generate-diet-plan`, user);
  }

  // Generate Workout Plan
  generateWorkoutPlan(user: User): Observable<WorkoutPlan> {
    return this.http.post<WorkoutPlan>(
      `${this.baseUrl}/generate-workout-plan`,
      user
    );
  }

  // Get User Plans
  getUserPlans(userId: number): Observable<UserPlansResponse> {
    return this.http.get<UserPlansResponse>(
      `${this.baseUrl}/user/${userId}/plans`
    );
  }

  // Update user and regenerate plans
  updateUserAndRegenerate(
    userId: number,
    updatedUser: User
  ): Observable<CompleteFitnessPlan> {
    return this.http.put<CompleteFitnessPlan>(
      `${this.baseUrl}/user/${userId}/update-and-regenerate`,
      updatedUser
    );
  }

  // Get Nutrition Analysis
  getNutritionAnalysis(userId: number): Observable<NutritionAnalysis> {
    return this.http.get<NutritionAnalysis>(
      `${this.baseUrl}/user/${userId}/nutrition-analysis`
    );
  }
}
