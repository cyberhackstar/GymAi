import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Dashboard } from './dashboard/dashboard/dashboard';
import { Plan } from './plan/plan/plan';
import { Progress } from './progress/progress/progress';
import { Pricing } from './subscription/pricing/pricing';
import { AdminDashboard } from './admin/admin-dashboard/admin-dashboard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Public Routes
  { path: 'login', component: Login },
  { path: 'register', component: Register },

  // User Routes (Protected in real setup)
  { path: 'dashboard', component: Dashboard },
  { path: 'plan', component: Plan },
  { path: 'progress', component: Progress },
  { path: 'subscription', component: Pricing },

  // Admin Routes (Also protected in real setup)
  { path: 'admin', component: AdminDashboard },

  // Fallback
  { path: '**', redirectTo: 'login' },
];
