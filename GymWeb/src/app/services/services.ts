import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

interface ServicePricing {
  amount: number;
  period: string;
  note?: string;
}

interface Service {
  id: number;
  title: string;
  description: string;
  icon: string;
  iconClass: string;
  features: string[];
  pricing?: ServicePricing;
  ctaText?: string;
  popular?: boolean;
  featured?: boolean;
}

interface AdditionalFeature {
  icon: string;
  title: string;
  description: string;
}

interface HowItWorksStep {
  icon: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-services',
  imports: [MatIcon, CommonModule],
  templateUrl: './services.html',
  styleUrls: ['./services.css'],
})
export class Services implements OnInit {
  services: Service[] = [
    {
      id: 1,
      title: 'AI Workout Plans',
      description:
        'Get personalized workout routines powered by machine learning algorithms that adapt to your fitness level, goals, and preferences.',
      icon: 'fitness_center',
      iconClass: 'workout-icon',
      features: [
        'AI-generated custom workouts',
        'Adaptive difficulty progression',
        'Exercise form guidance',
        'Equipment-based customization',
        'Real-time workout adjustments',
      ],
      pricing: {
        amount: 2999,
        period: 'month',
        note: 'Billed monthly',
      },
      ctaText: 'Start Training',
      popular: true,
      featured: true,
    },
    {
      id: 2,
      title: 'Smart Diet Planning',
      description:
        'AI-powered nutrition plans tailored to your dietary preferences, allergies, and fitness goals for optimal results.',
      icon: 'restaurant',
      iconClass: 'nutrition-icon',
      features: [
        'Personalized meal plans',
        'Macro & calorie tracking',
        'Dietary restriction support',
        'Smart grocery lists',
        'Recipe recommendations',
      ],
      pricing: {
        amount: 1999,
        period: 'month',
        note: 'Billed monthly',
      },
      ctaText: 'Plan My Diet',
    },
    {
      id: 3,
      title: 'Progress Tracking',
      description:
        'Advanced analytics and AI insights to monitor your fitness journey, track improvements, and optimize your performance.',
      icon: 'trending_up',
      iconClass: 'progress-icon',
      features: [
        'Comprehensive progress analytics',
        'Body composition tracking',
        'Performance predictions',
        'Goal achievement insights',
        'Detailed progress reports',
      ],
      pricing: {
        amount: 1599,
        period: 'month',
        note: 'Billed monthly',
      },
      ctaText: 'Track Progress',
    },
    {
      id: 4,
      title: 'AI Personal Trainer',
      description:
        '24/7 virtual personal trainer powered by AI that provides real-time guidance, motivation, and exercise corrections.',
      icon: 'psychology',
      iconClass: 'trainer-icon',
      features: [
        '24/7 AI trainer availability',
        'Real-time form correction',
        'Motivational coaching',
        'Injury prevention guidance',
        'Personalized workout modifications',
      ],
      pricing: {
        amount: 4999,
        period: 'month',
        note: 'Premium service',
      },
      ctaText: 'Meet Your Trainer',
      featured: true,
    },
    {
      id: 5,
      title: 'Group Challenges',
      description:
        'Join AI-curated fitness challenges with like-minded individuals to stay motivated and achieve your goals together.',
      icon: 'groups',
      iconClass: 'social-icon',
      features: [
        'AI-matched group challenges',
        'Social fitness tracking',
        'Team leaderboards',
        'Achievement badges',
        'Community support',
      ],
      pricing: {
        amount: 999,
        period: 'month',
        note: 'Community access',
      },
      ctaText: 'Join Challenges',
    },
    {
      id: 6,
      title: 'Wellness Coaching',
      description:
        'Holistic wellness approach combining fitness, nutrition, and mental health with AI-powered insights and recommendations.',
      icon: 'spa',
      iconClass: 'wellness-icon',
      features: [
        'Holistic wellness plans',
        'Stress management guidance',
        'Sleep optimization',
        'Mental health support',
        'Lifestyle recommendations',
      ],
      pricing: {
        amount: 3999,
        period: 'month',
        note: 'Complete wellness',
      },
      ctaText: 'Start Wellness Journey',
    },
  ];

  additionalFeatures: AdditionalFeature[] = [
    {
      icon: 'smart_toy',
      title: 'Advanced AI Technology',
      description:
        'Our cutting-edge machine learning algorithms continuously learn from your progress to provide increasingly personalized recommendations.',
    },
    {
      icon: 'devices',
      title: 'Multi-Platform Access',
      description:
        'Access your fitness plans anywhere with our mobile app, web platform, and smartwatch integration for seamless tracking.',
    },
    {
      icon: 'support_agent',
      title: '24/7 Support',
      description:
        'Get help whenever you need it with our round-the-clock customer support and AI-powered assistance.',
    },
    {
      icon: 'security',
      title: 'Data Privacy',
      description:
        'Your personal fitness data is encrypted and secure. We never share your information with third parties.',
    },
  ];

  howItWorks: HowItWorksStep[] = [
    {
      icon: 'person_add',
      title: 'Create Your Profile',
      description:
        'Tell us about your fitness goals, current level, preferences, and any limitations to get started.',
    },
    {
      icon: 'auto_awesome',
      title: 'AI Analysis',
      description:
        'Our AI analyzes your profile and creates a personalized fitness and nutrition plan tailored specifically for you.',
    },
    {
      icon: 'play_circle',
      title: 'Start Your Journey',
      description:
        'Begin your customized workout and diet plan with real-time guidance and support from our AI trainer.',
    },
    {
      icon: 'analytics',
      title: 'Track & Optimize',
      description:
        'Monitor your progress with detailed analytics while our AI continuously optimizes your plan for better results.',
    },
  ];

  constructor() {}

  ngOnInit(): void {
    this.animateOnLoad();
  }

  onServiceClick(service: Service): void {
    console.log('Service clicked:', service.title);
    // Add navigation logic or modal opening
  }

  onGetStarted(service: Service, event: Event): void {
    event.stopPropagation();
    console.log('Get Started clicked for:', service.title);
    // Add service subscription logic
    this.trackServiceInteraction('get_started', service.id);
  }

  onLearnMore(service: Service, event: Event): void {
    event.stopPropagation();
    console.log('Learn More clicked for:', service.title);
    // Add detailed service information modal or navigation
    this.trackServiceInteraction('learn_more', service.id);
  }

  onStartFreeTrialClick(): void {
    console.log('Start Free Trial clicked');
    // Add free trial signup logic
    this.trackUserAction('start_free_trial');
  }

  onContactUsClick(): void {
    console.log('Contact Us clicked');
    // Add contact form or navigation logic
    this.trackUserAction('contact_us');
  }

  private trackServiceInteraction(action: string, serviceId: number): void {
    // Analytics tracking for service interactions
    const service = this.services.find((s) => s.id === serviceId);
    if (service) {
      console.log(`Tracking: ${action} for ${service.title}`);
      // Add your analytics tracking here
    }
  }

  private trackUserAction(action: string): void {
    // Analytics tracking for user actions
    console.log(`Tracking user action: ${action}`);
    // Add your analytics tracking here
  }

  private animateOnLoad(): void {
    // Add entrance animations after component loads
    setTimeout(() => {
      const cards = document.querySelectorAll('.service-card');
      cards.forEach((card, index) => {
        const element = card as HTMLElement;
        element.style.animationDelay = `${index * 100}ms`;
        element.classList.add('animate-in');
      });
    }, 100);
  }

  // Utility methods for template
  getServicePrice(service: Service): string {
    if (!service.pricing) return 'Free';
    return `$${service.pricing.amount}/${service.pricing.period}`;
  }

  isServiceFeatured(service: Service): boolean {
    return service.featured || false;
  }

  getServiceIconClass(service: Service): string {
    return service.iconClass || 'default-icon';
  }
}
