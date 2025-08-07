import { Component } from '@angular/core';
import { Plan } from '../../models/plan.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pricing',
  imports: [CommonModule],
  templateUrl: './pricing.html',
  styleUrl: './pricing.css',
})
export class Pricing {
  currentPlan: string = 'Free';
  plans: Plan[] = [];

  ngOnInit(): void {
    this.loadPlans();
  }

  loadPlans() {
    this.plans = [
      {
        name: 'Free',
        price: '₹0/month',
        features: ['Basic Workout Plans', 'Limited Diet Suggestions'],
      },
      {
        name: 'Pro',
        price: '₹499/month',
        features: [
          'AI Workout & Diet Plans',
          'Progress Tracking',
          'Priority Support',
        ],
      },
      {
        name: 'Elite',
        price: '₹999/month',
        features: [
          'All Pro Features',
          '1-on-1 Coaching',
          'Custom AI Adjustments',
        ],
      },
    ];
  }

  subscribe(planName: string) {
    // Replace with actual payment logic later
    alert(`Subscribed to ${planName} plan!`);
    this.currentPlan = planName;
  }
}
