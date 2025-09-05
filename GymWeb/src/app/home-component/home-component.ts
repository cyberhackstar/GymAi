import {
  Component,
  OnInit,
  HostListener,
  AfterViewInit,
  OnDestroy,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';

interface Benefit {
  icon: string;
  title: string;
  description: string;
  delay: string;
}

interface Plan {
  title: string;
  price: number;
  originalPrice?: number;
  label: string;
  features: string[];
  popular?: boolean;
  badge?: string;
  color: string;
}

interface Stat {
  value: string;
  label: string;
  icon: string;
  color: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, NgOptimizedImage],
  templateUrl: './home-component.html',
  styleUrls: ['./home-component.css'],
})
export class HomeComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('statsSection', { static: false }) statsSection!: ElementRef;

  isLoading: boolean = true;

  constructor(private router: Router) {}

  isMobile = window.innerWidth <= 768;
  isScrolled = false;
  private observer!: IntersectionObserver;

  stats: Stat[] = [
    {
      value: '10K+',
      label: 'Active Users',
      icon: 'fas fa-users',
      color: '#d11d1dff',
    },
    {
      value: '1M+',
      label: 'Workouts Generated',
      icon: 'fas fa-dumbbell',
      color: '#d11d1dff',
    },
    {
      value: '95%',
      label: 'Success Rate',
      icon: 'fas fa-chart-line',
      color: '#d11d1dff',
    },
    {
      value: '24/7',
      label: 'Support',
      icon: 'fas fa-headset',
      color: '#d11d1dff',
    },
  ];

  benefits: Benefit[] = [
    {
      icon: 'fas fa-brain',
      title: 'AI-Powered Workouts',
      description:
        'Custom routines tailored to your goals and progress, powered by machine learning algorithms that adapt to your performance.',
      delay: '0s',
    },
    {
      icon: 'fas fa-utensils',
      title: 'Smart Nutrition Plans',
      description:
        'Get personalized meal plans that align with your fitness needs, dietary preferences, and lifestyle requirements.',
      delay: '0.2s',
    },
    {
      icon: 'fas fa-chart-line',
      title: 'Advanced Analytics',
      description:
        'Track, analyze, and improve with real-time data visualizations, progress insights, and performance metrics.',
      delay: '0.4s',
    },
    {
      icon: 'fas fa-shield-alt',
      title: 'Secure & Private',
      description:
        'Your data is encrypted with military-grade security, and your privacy is our absolute top priority.',
      delay: '0.6s',
    },
    {
      icon: 'fas fa-mobile-alt',
      title: 'Cross-Platform',
      description:
        'Access your workouts anywhere with our responsive web app and native mobile applications.',
      delay: '0.8s',
    },
    {
      icon: 'fas fa-trophy',
      title: 'Goal Achievement',
      description:
        'Set and crush your fitness goals with AI-guided coaching and milestone tracking systems.',
      delay: '1s',
    },
  ];

  plans: Plan[] = [
    {
      title: 'Starter',
      price: 999,
      originalPrice: 2999,
      label: 'Perfect for beginners',
      features: [
        'AI-based workout plans',
        'Basic nutrition tracking',
        'Progress analytics',
        'Mobile app access',
        'Community support',
      ],
      color: '#4ade80',
    },
    {
      title: 'Pro',
      price: 1999,
      originalPrice: 3999,
      label: 'Most popular choice',
      features: [
        'Advanced AI customization',
        'Smart meal suggestions',
        'Detailed progress reports',
        'Wearable integration',
        'Priority support',
        'Custom macros tracking',
      ],
      popular: true,
      badge: 'BEST VALUE',
      color: '#ff4c4c',
    },
    {
      title: 'Elite',
      price: 2999,
      originalPrice: 5999,
      label: 'For serious transformation',
      features: [
        'Real-time AI coaching',
        'Personal nutrition coach',
        'Advanced biometrics',
        '1-on-1 video sessions',
        '24/7 premium support',
        'Custom workout videos',
        'Supplement recommendations',
      ],
      color: '#8b5cf6',
    },
  ];

  @HostListener('window:resize')
  onResize() {
    this.isMobile = window.innerWidth <= 768;
  }

  @HostListener('window:scroll')
  onScroll() {
    this.isScrolled = window.scrollY > 100;
  }

  ngOnInit(): void {
    // Initialize intersection observer
    this.setupIntersectionObserver();
  }

  ngAfterViewInit(): void {
    // Small delay to ensure DOM is ready
    setTimeout(() => {
      this.observeElements();
    }, 100);
  }

  ngOnDestroy(): void {
    if (this.observer) {
      this.observer.disconnect();
    }
  }

  private setupIntersectionObserver(): void {
    this.observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('animate-in');

            // Special handling for stats items with staggered animation
            if (entry.target.classList.contains('stats-grid')) {
              this.animateStatsItems();
            }
          }
        });
      },
      {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px', // Start animation slightly before element is fully visible
      }
    );
  }

  private observeElements(): void {
    // Get all elements that should animate
    const elementsToObserve = document.querySelectorAll('.animate-on-scroll');

    elementsToObserve.forEach((element) => {
      this.observer.observe(element);
    });
  }

  private animateStatsItems(): void {
    // Add staggered animation to individual stat items
    const statItems = document.querySelectorAll('.stat-item');
    statItems.forEach((item, index) => {
      setTimeout(() => {
        item.classList.add('animate-in');
      }, index * 200); // 200ms delay between each item
    });
  }

  scrollToSection(sectionId: string): void {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  onGetStarted(): void {
    // Navigate to registration
    this.router.navigate(['/register']);
    // console.log('Get Started clicked');
  }

  onLearnMore(): void {
    // Navigate to learn more page
    this.router.navigate(['/learn-more']);
    // console.log('Learn More clicked');
  }

  onEnrollPlan(planTitle: string): void {
    // Handle plan enrollment
    console.log('Enrolling in plan:', planTitle);
  }
}
