// optimized-fitness.service.ts - UPDATED TO MATCH BACKEND
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// ===== DTOs - UPDATED TO MATCH BACKEND EXACTLY =====
export interface UserProfileDTO {
  userId?: number;
  name: string;
  email: string;
  age: number;
  height: number;
  weight: number;
  gender: string;
  goal: string;
  activityLevel: string;
  preference: string;
  profileComplete: boolean;
}

export interface UserProfileCheckDTO {
  exists: boolean;
  profileComplete: boolean;
  user?: UserProfileDTO;
  message: string;
}

// Updated to match backend SimpleFoodItemDTO
export interface SimpleFoodItemDTO {
  foodName: string; // Changed from 'name' to 'foodName'
  category: string;
  quantity: number;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  fiber: number;
}

// Updated to match backend SimpleMealDTO
export interface SimpleMealDTO {
  mealType: string;
  totalCalories: number;
  totalProtein: number;
  totalCarbs: number;
  totalFat: number;
  foodItems: SimpleFoodItemDTO[];
}

// Updated to match backend SimpleDayMealPlanDTO
export interface SimpleDayMealPlanDTO {
  dayNumber: number;
  dayName: string;
  totalDailyCalories: number;
  totalDailyProtein: number;
  totalDailyCarbs: number;
  totalDailyFat: number;
  meals: SimpleMealDTO[];
}

// Updated to match backend SimpleDietPlanDTO
export interface SimpleDietPlanDTO {
  id?: number;
  userId: number;
  dailyCalorieTarget: number;
  dailyProteinTarget: number;
  dailyCarbsTarget: number;
  dailyFatTarget: number;
  createdDate: string;
  dailyPlans: SimpleDayMealPlanDTO[];
}

// Updated to match backend SimpleWorkoutExerciseDTO
export interface SimpleWorkoutExerciseDTO {
  exerciseName: string; // Changed from 'exercise' to 'exerciseName'
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

// Updated to match backend SimpleDayWorkoutPlanDTO
export interface SimpleDayWorkoutPlanDTO {
  dayNumber: number;
  dayName: string;
  focusArea: string;
  restDay: boolean;
  estimatedDurationMinutes: number;
  totalCaloriesBurned: number;
  exercises: SimpleWorkoutExerciseDTO[];
}

// Updated to match backend SimpleWorkoutPlanDTO
export interface SimpleWorkoutPlanDTO {
  id?: number;
  userId: number;
  planType: string;
  difficultyLevel: string;
  createdDate: string;
  weeklyPlan: SimpleDayWorkoutPlanDTO[];
}

// Updated to match backend NutritionAnalysis
export interface NutritionAnalysis {
  userId: number;
  dailyCalories: number;
  dailyProtein: number;
  dailyCarbs: number;
  dailyFat: number;
  bmr: number;
  tdee: number;
  recommendation: string; // Added the missing recommendation property
}

// Updated to match backend OptimizedPlansResponseDTO
export interface OptimizedPlansResponseDTO {
  user: UserProfileDTO;
  dietPlan?: SimpleDietPlanDTO;
  workoutPlan?: SimpleWorkoutPlanDTO;
  nutritionAnalysis?: NutritionAnalysis;
  plansExist: boolean;
  summary: string;
}

@Injectable({
  providedIn: 'root',
})
export class FitnessService {
  private baseUrl = environment.planUrl;

  constructor(private http: HttpClient) {}

  // Check if user profile exists and is complete
  checkUserProfile(
    userProfile: UserProfileDTO
  ): Observable<UserProfileCheckDTO> {
    return this.http.post<UserProfileCheckDTO>(
      `${this.baseUrl}/user/profile-check`,
      userProfile
    );
  }

  // Get user plans (fetches existing or generates if missing)
  getUserPlansOptimized(
    userProfile: UserProfileDTO
  ): Observable<OptimizedPlansResponseDTO> {
    return this.http.post<OptimizedPlansResponseDTO>(
      `${this.baseUrl}/user/plans`,
      userProfile
    );
  }

  // Create/Update user profile and generate plans
  completeUserProfile(
    userProfile: UserProfileDTO
  ): Observable<OptimizedPlansResponseDTO> {
    return this.http.post<OptimizedPlansResponseDTO>(
      `${this.baseUrl}/user/complete-profile`,
      userProfile
    );
  }

  // Update user profile only (without regenerating plans)
  updateUserProfile(userProfile: UserProfileDTO): Observable<UserProfileDTO> {
    return this.http.put<UserProfileDTO>(
      `${this.baseUrl}/user/update-profile`,
      userProfile
    );
  }

  // Regenerate diet plan only
  regenerateDietPlan(
    userProfile: UserProfileDTO
  ): Observable<SimpleDietPlanDTO> {
    return this.http.post<SimpleDietPlanDTO>(
      `${this.baseUrl}/user/regenerate-diet`,
      userProfile
    );
  }

  // Regenerate workout plan only
  regenerateWorkoutPlan(
    userProfile: UserProfileDTO
  ): Observable<SimpleWorkoutPlanDTO> {
    return this.http.post<SimpleWorkoutPlanDTO>(
      `${this.baseUrl}/user/regenerate-workout`,
      userProfile
    );
  }

  // Update profile and regenerate all plans
  updateProfileAndRegeneratePlans(
    userProfile: UserProfileDTO
  ): Observable<OptimizedPlansResponseDTO> {
    return this.http.put<OptimizedPlansResponseDTO>(
      `${this.baseUrl}/user/update-and-regenerate`,
      userProfile
    );
  }

  // Get nutrition analysis only
  getNutritionAnalysis(
    userProfile: UserProfileDTO
  ): Observable<NutritionAnalysis> {
    return this.http.post<NutritionAnalysis>(
      `${this.baseUrl}/user/nutrition-analysis`,
      userProfile
    );
  }

  // Get only diet plan
  getDietPlan(userProfile: UserProfileDTO): Observable<SimpleDietPlanDTO> {
    return this.http.post<SimpleDietPlanDTO>(
      `${this.baseUrl}/user/diet-plan`,
      userProfile
    );
  }

  // Get only workout plan
  getWorkoutPlan(
    userProfile: UserProfileDTO
  ): Observable<SimpleWorkoutPlanDTO> {
    return this.http.post<SimpleWorkoutPlanDTO>(
      `${this.baseUrl}/user/workout-plan`,
      userProfile
    );
  }

  // Delete user plans
  deleteUserPlans(userProfile: UserProfileDTO): Observable<string> {
    return this.http.post(`${this.baseUrl}/user/plans/delete`, userProfile, {
      responseType: 'text',
    });
  }

  // Get user profile only
  getUserProfile(userProfile: UserProfileDTO): Observable<UserProfileDTO> {
    return this.http.post<UserProfileDTO>(
      `${this.baseUrl}/user/profile`,
      userProfile
    );
  }

  // Health check
  healthCheck(): Observable<string> {
    return this.http.get(`${this.baseUrl}/health`, { responseType: 'text' });
  }
}
