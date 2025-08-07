import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { WorkoutPlan } from './components/workout-plan/workout-plan';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Navbar } from './shared/components/navbar/navbar';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Plan } from './plan/plan/plan';
import { Progress } from './progress/progress/progress';
import { Pricing } from './subscription/pricing/pricing';
import { AdminDashboard } from './admin/admin-dashboard/admin-dashboard';
import { Dashboard } from './dashboard/dashboard/dashboard';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    CommonModule,
    HttpClientModule,
    Register,
    Login,
    Navbar,
    WorkoutPlan,
    Dashboard,
    Plan,
    Progress,
    Pricing,
    AdminDashboard,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('GymWeb');
}
