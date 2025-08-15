import { Component } from '@angular/core';
import { WorkoutPlan } from './workout-plan/workout-plan';
import { DietPlan } from './diet-plan/diet-plan';
import { Progress } from './progress/progress';
import { UserSummary } from './user-summary/user-summary';

@Component({
  selector: 'app-user-dashboard',
  imports: [WorkoutPlan, DietPlan, Progress],
  templateUrl: './user-dashboard.html',
  styleUrl: './user-dashboard.css',
})
export class UserDashboard {}
