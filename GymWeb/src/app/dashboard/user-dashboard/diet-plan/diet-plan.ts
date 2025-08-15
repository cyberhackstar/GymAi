import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Meal {
  time: string;
  meal: string;
  protein: number;
  carbs: number;
  fat: number;
  calories: number;
  taken: boolean;
  image: string;
}
interface DayPlan {
  day: string;
  meals: Meal[];
  dailyCaloriesTaken: number;
  dailyCalories: number;
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

  toggleDays() {
    this.showAllDays = !this.showAllDays;
  }

  ngOnInit() {
    this.dietPlan = [
      {
        day: 'Monday',
        meals: [
          {
            time: '8:00 AM',
            meal: 'Oatmeal with berries',
            protein: 10,
            carbs: 30,
            fat: 5,
            calories: 200,
            taken: false,
            image: 'https://img.spoonacular.com/recipes/654515-312x231.jpg',
          },
          {
            time: '12:00 PM',
            meal: 'Grilled chicken salad',
            protein: 25,
            carbs: 10,
            fat: 8,
            calories: 250,
            taken: false,
            image: 'https://img.spoonacular.com/recipes/654515-312x231.jpg',
          },
          {
            time: '7:00 PM',
            meal: 'Salmon with vegetables',
            protein: 30,
            carbs: 15,
            fat: 10,
            calories: 300,
            taken: false,
            image: 'https://img.spoonacular.com/recipes/654515-312x231.jpg',
          },
        ],
        dailyCaloriesTaken: 0,
        dailyCalories: 750,
        progressPercent: 0,
        activeMealIndex: 0,
      },
      {
        day: 'Tuesday',
        meals: [
          {
            time: '8:00 AM',
            meal: 'Greek yogurt with nuts',
            protein: 15,
            carbs: 12,
            fat: 9,
            calories: 220,
            taken: false,
            image: 'https://img.spoonacular.com/recipes/654515-312x231.jpg',
          },
          {
            time: '12:00 PM',
            meal: 'Turkey wrap',
            protein: 20,
            carbs: 25,
            fat: 7,
            calories: 280,
            taken: false,
            image: 'https://img.spoonacular.com/recipes/654515-312x231.jpg',
          },
          {
            time: '7:00 PM',
            meal: 'Beef stir fry',
            protein: 28,
            carbs: 20,
            fat: 12,
            calories: 350,
            taken: false,
            image: 'https://img.spoonacular.com/recipes/654515-312x231.jpg',
          },
        ],
        dailyCaloriesTaken: 0,
        dailyCalories: 850,
        progressPercent: 0,
        activeMealIndex: 0,
      },
    ];
  }

  updateCalories() {
    this.dietPlan.forEach((dayPlan) => {
      dayPlan.dailyCaloriesTaken = dayPlan.meals
        .filter((m) => m.taken)
        .reduce((sum, m) => sum + m.calories, 0);
    });
  }

  updateDailyProgress(dayPlan: DayPlan) {
    dayPlan.dailyCaloriesTaken = dayPlan.meals
      .filter((m) => m.taken)
      .reduce((sum, m) => sum + m.calories, 0);
    dayPlan.progressPercent = Math.min(
      Math.round((dayPlan.dailyCaloriesTaken / dayPlan.dailyCalories) * 100),
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
