import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Plan } from './plan/plan/plan';
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

export const routes: Routes = [
  { path: '', redirectTo: 'default', pathMatch: 'full' },

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

  // User Routes (Protected in real setup)
  { path: 'dashboard', component: UserDashboard },
  { path: 'plan', component: Plan },
  { path: 'progress', component: Progress },
  { path: 'subscription', component: Pricing },
  { path: 'complete-profile', component: CompleteProfile },

  // Admin Routes (Also protected in real setup)
  { path: 'admin', component: AdminDashboard },

  // Fallback
  { path: '**', redirectTo: 'login' },
];
