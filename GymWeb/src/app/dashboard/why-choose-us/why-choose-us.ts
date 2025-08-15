import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-why-choose-us',
  imports: [CommonModule],
  templateUrl: './why-choose-us.html',
  styleUrl: './why-choose-us.css',
})
export class WhyChooseUs {
  benefits = [
    {
      icon: 'fas fa-dumbbell',
      title: 'AI-Personalized Workouts',
      description:
        'Custom routines tailored to your goals and progress, powered by intelligent algorithms.',
    },
    {
      icon: 'fas fa-utensils',
      title: 'Smart Nutrition Plans',
      description:
        'Get meal plans that align with your fitness needs and dietary preferences.',
    },
    {
      icon: 'fas fa-chart-line',
      title: 'Progress Analytics',
      description:
        'Track, analyze, and improve with real-time data visualizations and insights.',
    },
    {
      icon: 'fas fa-user-shield',
      title: 'Secure & Private',
      description:
        'Your data is encrypted and your privacy is our top priority.',
    },
  ];
}
