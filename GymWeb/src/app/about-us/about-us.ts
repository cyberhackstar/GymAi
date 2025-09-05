import { NgOptimizedImage } from '@angular/common';
import {
  Component,
  OnInit,
  OnDestroy,
  ElementRef,
  AfterViewInit,
} from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-about-us',
  standalone: true,
  imports: [NgOptimizedImage, RouterModule],
  templateUrl: './about-us.html',
  styleUrl: './about-us.css',
})
export class AboutUs implements OnInit, OnDestroy, AfterViewInit {
  isVisible = false;
  founderImage = 'founder_mqtmq4.webp';

  private intersectionObserver?: IntersectionObserver;
  private animationElements: NodeListOf<Element> | null = null;

  constructor(private elementRef: ElementRef) {}

  ngOnInit(): void {
    // Trigger initial animation
    setTimeout(() => {
      this.isVisible = true;
    }, 300);
  }

  ngAfterViewInit(): void {
    this.initializeScrollAnimations();
    this.initializeCounterAnimations();
  }

  ngOnDestroy(): void {
    if (this.intersectionObserver) {
      this.intersectionObserver.disconnect();
    }
  }

  private initializeScrollAnimations(): void {
    this.animationElements =
      this.elementRef.nativeElement.querySelectorAll('.animate-on-scroll');

    this.intersectionObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('animate-in');
          }
        });
      },
      {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px',
      }
    );

    // Observe all animation elements
    if (this.animationElements) {
      this.animationElements.forEach((element) => {
        this.intersectionObserver?.observe(element);
      });
    }

    // Observe individual stat items for staggered animation
    const statItems =
      this.elementRef.nativeElement.querySelectorAll('.stat-item');
    statItems.forEach((item: Element, index: number) => {
      this.intersectionObserver?.observe(item);
    });

    // Observe value cards for staggered animation
    const valueCards =
      this.elementRef.nativeElement.querySelectorAll('.value-card');
    valueCards.forEach((card: Element, index: number) => {
      this.intersectionObserver?.observe(card);
    });
  }

  private initializeCounterAnimations(): void {
    const counterElements =
      this.elementRef.nativeElement.querySelectorAll('.stat-value');

    const animateCounter = (
      element: HTMLElement,
      target: number,
      suffix: string = ''
    ) => {
      let current = 0;
      const increment = target / 100;
      const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
          element.textContent = target + suffix;
          clearInterval(timer);
        } else {
          element.textContent = Math.floor(current) + suffix;
        }
      }, 20);
    };

    // Counter observer
    const counterObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const element = entry.target as HTMLElement;
            const text = element.textContent || '';

            if (text.includes('K+')) {
              const num = parseInt(text.replace('K+', ''));
              animateCounter(element, num, 'K+');
            } else if (text.includes('M+')) {
              const num = parseInt(text.replace('M+', ''));
              animateCounter(element, num, 'M+');
            } else if (text.includes('%')) {
              const num = parseInt(text.replace('%', ''));
              animateCounter(element, num, '%');
            } else if (text.includes('+')) {
              const num = parseInt(text.replace('+', ''));
              animateCounter(element, num, '+');
            }

            counterObserver.unobserve(element);
          }
        });
      },
      { threshold: 0.7 }
    );

    counterElements.forEach((element: Element) => {
      counterObserver.observe(element);
    });
  }

  // Method to handle CTA button clicks
  onStartJourney(): void {
    console.log('Starting fitness journey...');
    // Add your navigation logic here
  }

  onWatchDemo(): void {
    console.log('Opening demo...');
    // Add your demo logic here
  }

  // Method to handle social link clicks
  onSocialClick(platform: string, url: string): void {
    if (url !== '#') {
      window.open(url, '_blank', 'noopener,noreferrer');
    }
    console.log(`Opening ${platform} profile`);
  }
}
