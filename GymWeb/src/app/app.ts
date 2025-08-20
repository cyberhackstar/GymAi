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
import { WhyChooseUs } from './dashboard/why-choose-us/why-choose-us';
import { Contact } from './contact/contact';
import { BmiCalculator } from './bmi-calculator/bmi-calculator';
import { Footer } from './shared/components/footer/footer';
import { Hero } from './dashboard/hero/hero';
import { ForgotPassword } from './auth/forgot-password/forgot-password';
import { UserDashboard } from './dashboard/user-dashboard/user-dashboard';
import { UserSummary } from './dashboard/user-dashboard/user-summary/user-summary';
import { UserManagement } from './admin/user-management/user-management';
import { SupportTicketAdmin } from './admin/support-ticket-admin/support-ticket-admin';
import { SupportTicketUser } from './dashboard/user-dashboard/support-ticket-user/support-ticket-user';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    CommonModule,
    HttpClientModule,
    // Register,
    // Login,
    Navbar,
    // WorkoutPlan,
    // UserDashboard,
    // Plan,
    // Progress,
    // Pricing,
    // AdminDashboard,
    // WhyChooseUs,
    // Pricing,
    // Contact,
    // BmiCalculator,
    Footer,
    // UserSummary,
    // Hero,
    // ForgotPassword,
    // UserManagement,
    // SupportTicketAdmin,
    // SupportTicketUser,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal(
    'GymAI - Your AI-Powered Fitness Companion'
  );
}
