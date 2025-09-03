import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface Feature {
  icon: string;
  title: string;
  description: string;
  benefits: string[];
}

interface Testimonial {
  name: string;
  role: string;
  image: string;
  rating: number;
  comment: string;
}

interface PricingPlan {
  name: string;
  price: string;
  period: string;
  features: string[];
  popular?: boolean;
  buttonText: string;
}

@Component({
  selector: 'app-learn-more',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './learn-more.html',
  styleUrls: ['./learn-more.css'],
})
export class LearnMore {
  features: Feature[] = [
    {
      icon: 'fas fa-brain',
      title: 'AI-Powered Workouts',
      description:
        'Our advanced AI analyzes your fitness level, goals, and preferences to create personalized workout routines.',
      benefits: [
        'Adaptive training programs',
        'Real-time form corrections',
        'Progress tracking and analytics',
        'Injury prevention algorithms',
      ],
    },
    {
      icon: 'fas fa-utensils',
      title: 'Smart Nutrition Planning',
      description:
        'Get custom meal plans based on your dietary preferences, allergies, and fitness goals.',
      benefits: [
        'Macro-balanced meal plans',
        'Grocery shopping lists',
        'Recipe recommendations',
        'Calorie tracking automation',
      ],
    },
    {
      icon: 'fas fa-chart-line',
      title: 'Advanced Analytics',
      description:
        'Track your progress with detailed analytics and insights powered by machine learning.',
      benefits: [
        'Performance metrics dashboard',
        'Trend analysis and predictions',
        'Goal achievement tracking',
        'Comparative progress reports',
      ],
    },
    {
      icon: 'fas fa-mobile-alt',
      title: 'Mobile Integration',
      description:
        'Access your personalized fitness plan anywhere with our responsive mobile platform.',
      benefits: [
        'Offline workout access',
        'Wearable device sync',
        'Push notifications',
        'Social sharing features',
      ],
    },
  ];

  testimonials: Testimonial[] = [
    {
      name: 'Sarah Johnson',
      role: 'Fitness Enthusiast',
      image: '/api/placeholder/80/80',
      rating: 5,
      comment:
        'GymAI transformed my fitness journey! The AI-powered workouts adapt perfectly to my progress and keep me motivated every day.',
    },
    {
      name: 'Mike Chen',
      role: 'Personal Trainer',
      image: '/api/placeholder/80/80',
      rating: 5,
      comment:
        "As a trainer, I'm impressed by the precision of GymAI's recommendations. It's like having a virtual coaching assistant.",
    },
    {
      name: 'Emily Rodriguez',
      role: 'Busy Professional',
      image: '/api/placeholder/80/80',
      rating: 4,
      comment:
        'The nutrition planning feature is a game-changer. I save hours each week on meal prep and still eat healthy!',
    },
  ];

  pricingPlans: PricingPlan[] = [
    {
      name: 'Basic',
      price: '$9.99',
      period: '/month',
      features: [
        'Basic AI workout plans',
        'Nutrition tracking',
        'Progress analytics',
        'Mobile app access',
        'Email support',
      ],
      buttonText: 'Start Basic',
    },
    {
      name: 'Pro',
      price: '$19.99',
      period: '/month',
      popular: true,
      features: [
        'Advanced AI workouts',
        'Custom meal planning',
        'Real-time form analysis',
        'Wearable integration',
        'Priority support',
        'Social features',
      ],
      buttonText: 'Go Pro',
    },
    {
      name: 'Elite',
      price: '$39.99',
      period: '/month',
      features: [
        'Premium AI coaching',
        'Personalized nutrition',
        '1-on-1 virtual sessions',
        'Advanced biometrics',
        '24/7 premium support',
        'Exclusive content',
      ],
      buttonText: 'Get Elite',
    },
  ];

  stats = [
    { value: '50K+', label: 'Active Users' },
    { value: '1M+', label: 'Workouts Completed' },
    { value: '95%', label: 'User Satisfaction' },
    { value: '24/7', label: 'AI Support' },
  ];

  faqs = [
    {
      question: 'How does the AI create personalized workouts?',
      answer:
        'Our AI analyzes your fitness level, goals, available equipment, time constraints, and past performance to create optimal workout routines. It continuously adapts based on your progress and feedback.',
      expanded: false,
    },
    {
      question: 'Can I use GymAI without gym equipment?',
      answer:
        'Absolutely! GymAI offers bodyweight workouts, home equipment routines, and gym-based programs. You can specify your available equipment, and the AI will adapt accordingly.',
      expanded: false,
    },
    {
      question: 'How accurate is the nutrition planning?',
      answer:
        'Our nutrition AI considers your dietary preferences, allergies, cultural foods, and fitness goals to create meal plans with 95% accuracy in macro distribution and calorie targeting.',
      expanded: false,
    },
    {
      question: 'Is my health data secure?',
      answer:
        'Yes, we use enterprise-grade encryption and comply with HIPAA standards. Your data is never shared with third parties and you maintain full control over your information.',
      expanded: false,
    },
    {
      question: 'Can I cancel my subscription anytime?',
      answer:
        "Yes, you can cancel your subscription at any time. There are no cancellation fees, and you'll retain access to your plan until the end of your billing period.",
      expanded: false,
    },
  ];

  toggleFaq(index: number): void {
    this.faqs[index].expanded = !this.faqs[index].expanded;
  }

  getStars(rating: number): number[] {
    return Array(rating).fill(0);
  }

  scrollToSection(sectionId: string): void {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  onGetStarted(): void {
    // Navigate to signup or dashboard
    console.log('Get Started clicked');
  }

  onSelectPlan(plan: string): void {
    // Navigate to checkout with selected plan
    console.log('Selected plan:', plan);
  }
}
