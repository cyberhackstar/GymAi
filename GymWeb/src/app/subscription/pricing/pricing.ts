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
  plans = [
    {
      title: 'Starter',
      price: 9.99,
      label: 'Perfect for beginners',
      features: [
        'AI-based workout plan',
        'Basic nutrition tracking',
        'Limited analytics',
      ],
    },
    {
      title: 'Pro',
      price: 19.99,
      label: 'Ideal for regular gym-goers',
      features: [
        'Full AI workout customization',
        'Advanced meal suggestions',
        'Progress tracking & reports',
      ],
    },
    {
      title: 'Elite',
      price: 29.99,
      label: 'For serious transformation',
      features: [
        'Real-time AI coaching',
        'Custom macros & meals',
        'Weekly fitness feedback',
      ],
    },
  ];
}
