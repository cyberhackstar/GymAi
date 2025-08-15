import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Exercise {
  name: string;
  sets: number;
  reps: string;
  caloriesBurned: number;
  completed: boolean;
  image: string;
}

interface WorkoutDay {
  day: string;
  exercises: Exercise[];
  caloriesBurnedTaken: number;
  totalCaloriesBurned: number;
  progressPercent: number;
  progressColor?: string;
  activeExerciseIndex: number;
}

interface UserProfile {
  userId?: number;
  name?: string;
  email?: string;
  age: number;
  height: number;
  weight: number;
  gender: string;
  goal: string;
  activityLevel: string;
  preference: string;
}

@Component({
  selector: 'app-workout-plan',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './workout-plan.html',
  styleUrls: ['./workout-plan.css'],
})
export class WorkoutPlan implements OnInit {
  workoutPlan: WorkoutDay[] = [];
  userProfile: UserProfile | null = null;
  lightboxImage: string | null = null;
  showAllDays = false;

  ngOnInit() {
    // Simulate user profile (will come from backend/JWT later)
    this.userProfile = {
      userId: 1,
      name: 'John Doe',
      email: 'john@example.com',
      age: 28,
      height: 175,
      weight: 70,
      gender: 'Male',
      goal: 'Build Muscle',
      activityLevel: 'Moderate',
      preference: 'No Equipment',
    };

    // Simulated WGER workout data
    this.workoutPlan = [
      {
        day: 'Monday',
        exercises: [
          {
            name: 'Bench Press',
            sets: 4,
            reps: '8-10',
            caloriesBurned: 150,
            completed: false,
            image:
              'https://wger.de/media/exercise-images/822/74affc0d-03b6-4f33-b5f4-a822a2615f68.png',
          },
          {
            name: 'Push Ups',
            sets: 3,
            reps: '15',
            caloriesBurned: 100,
            completed: false,
            image:
              'https://wger.de/media/exercise-images/822/74affc0d-03b6-4f33-b5f4-a822a2615f68.png',
          },
          {
            name: 'Tricep Dips',
            sets: 3,
            reps: '12',
            caloriesBurned: 80,
            completed: false,
            image:
              'https://wger.de/media/exercise-images/822/74affc0d-03b6-4f33-b5f4-a822a2615f68.png',
          },
        ],
        caloriesBurnedTaken: 0,
        totalCaloriesBurned: 330,
        progressPercent: 0,
        activeExerciseIndex: 0,
      },
      {
        day: 'Tuesday',
        exercises: [
          {
            name: 'Squats',
            sets: 4,
            reps: '10',
            caloriesBurned: 180,
            completed: false,
            image:
              'https://wger.de/media/exercise-images/822/74affc0d-03b6-4f33-b5f4-a822a2615f68.png',
          },
          {
            name: 'Lunges',
            sets: 3,
            reps: '12',
            caloriesBurned: 120,
            completed: false,
            image:
              'https://wger.de/media/exercise-images/822/74affc0d-03b6-4f33-b5f4-a822a2615f68.png',
          },
          {
            name: 'Leg Press',
            sets: 3,
            reps: '10',
            caloriesBurned: 160,
            completed: false,
            image:
              'https://wger.de/media/exercise-images/822/74affc0d-03b6-4f33-b5f4-a822a2615f68.png',
          },
        ],
        caloriesBurnedTaken: 0,
        totalCaloriesBurned: 460,
        progressPercent: 0,
        activeExerciseIndex: 0,
      },
    ];
  }

  toggleDays() {
    this.showAllDays = !this.showAllDays;
  }

  updateDayProgress(day: WorkoutDay) {
    day.caloriesBurnedTaken = day.exercises
      .filter((ex) => ex.completed)
      .reduce((sum, ex) => sum + ex.caloriesBurned, 0);

    day.progressPercent = Math.min(
      Math.round((day.caloriesBurnedTaken / day.totalCaloriesBurned) * 100),
      100
    );

    if (day.progressPercent === 100) day.progressColor = '#4CAF50';
    else if (day.progressPercent >= 50) day.progressColor = '#FF9800';
    else day.progressColor = '#F44336';
  }

  nextExercise(day: WorkoutDay) {
    if (day.activeExerciseIndex < day.exercises.length - 1) {
      day.activeExerciseIndex++;
    }
  }

  prevExercise(day: WorkoutDay) {
    if (day.activeExerciseIndex > 0) {
      day.activeExerciseIndex--;
    }
  }

  goToExercise(day: WorkoutDay, index: number) {
    if (index >= 0 && index < day.exercises.length) {
      day.activeExerciseIndex = index;
    }
  }

  openLightbox(image: string) {
    this.lightboxImage = image;
  }

  closeLightbox() {
    this.lightboxImage = null;
  }
}
