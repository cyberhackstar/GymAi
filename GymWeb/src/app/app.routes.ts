import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Progress } from './progress/progress/progress';
import { Pricing } from './subscription/pricing/pricing';
import { AdminDashboard } from './admin/admin-dashboard/admin-dashboard';
import { ForgotPassword } from './auth/forgot-password/forgot-password';
import { CompleteProfile } from './user/complete-profile/complete-profile';
import { UserDashboard } from './dashboard/user-dashboard/user-dashboard';
import { BmiCalculator } from './bmi-calculator/bmi-calculator';
import { Contact } from './contact/contact';
import { DefaultDashboard } from './dashboard/default-dashboard/default-dashboard';
import { AboutUs } from './about-us/about-us';
import { WhyChooseUs } from './dashboard/why-choose-us/why-choose-us';
import { OAuthCallbackComponent } from './auth/OAuthCallbackComponent';
import { Team } from './team/team';
import { Services } from './services/services';
import { DietPlanComponent } from './plan/diet-plan-component/diet-plan-component';
import { WorkoutPlanComponent } from './plan/workout-plan-component/workout-plan-component';
import { DashboardComponent } from './plan/dashboard-component/dashboard-component';
import { LearnMore } from './learn-more/learn-more';
import { UserProfile } from './user-profile/user-profile';
import { HomeComponent } from './home-component/home-component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  // Public Routes
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'calculator', component: BmiCalculator },
  { path: 'contact', component: Contact },
  { path: 'default', component: DefaultDashboard },
  { path: 'about', component: AboutUs },
  { path: 'whyChooseUs', component: WhyChooseUs },
  { path: 'oauth-callback', component: OAuthCallbackComponent },
  { path: 'team', component: Team },
  { path: 'services', component: Services },
  { path: 'learn-more', component: LearnMore },
  { path: 'user-profile', component: UserProfile },
  { path: 'home', component: HomeComponent },

  // User Routes (Protected in real setup)
  { path: 'dashboard', component: UserDashboard },
  // { path: 'plan', component: Plan },
  { path: 'progress', component: Progress },
  { path: 'subscription', component: Pricing },
  { path: 'complete-profile', component: CompleteProfile },

  { path: 'diet', component: DietPlanComponent },
  { path: 'workout', component: WorkoutPlanComponent },
  { path: 'plan-dashboard', component: DashboardComponent },

  // Admin Routes (Also protected in real setup)
  { path: 'admin', component: AdminDashboard },

  // Fallback
  { path: '**', redirectTo: 'login' },
];
