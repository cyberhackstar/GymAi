import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-plan',
  imports: [CommonModule],
  templateUrl: './plan.html',
  styleUrl: './plan.css',
})
export class Plan {
  workoutPlan: string[] = [];
  dietPlan: string[] = [];

  ngOnInit(): void {
    this.fetchPlans();
  }

  fetchPlans() {
    // TODO: Replace with actual API call to your plan-service
    this.workoutPlan = [
      'Push-ups - 3 sets of 15 reps',
      'Squats - 3 sets of 20 reps',
      'Plank - 3 sets of 60 seconds',
    ];

    this.dietPlan = [
      'Breakfast: Oats with fruits',
      'Lunch: Grilled chicken with quinoa',
      'Dinner: Salad with lentils',
    ];
  }
}
