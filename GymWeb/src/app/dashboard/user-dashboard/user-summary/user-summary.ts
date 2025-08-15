import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-user-summary',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-summary.html',
  styleUrls: ['./user-summary.css'],
})
export class UserSummary {
  user = {
    name: 'John Doe',
    gender: 'female',
    bmi: 23.5,
    calories: 2200,
    weight: 72.3,
    goalWeight: 68.0,
    height: 175,
    water: 2.5,
    waterGoal: 3,
    level: 'Intermediate',
    profileImage: '',
  };

  displayCalories = 0;
  displayWeight = 0;
  displayGoalWeight = 0;
  displayWater = 0;
  waterOffset = 188.4;
  waterAnimDuration = 1000;
  waveLevel = 0; // percentage bottom position

  ngOnInit(): void {
    this.animateNumber('displayCalories', this.user.calories, 1000, 0);
    this.animateNumber('displayWeight', this.user.weight, 1000, 1);
    this.animateNumber('displayGoalWeight', this.user.goalWeight, 1000, 1);
    this.animateNumber('displayWater', this.user.water, 1000, 1, () => {
      this.syncWaterFill();
    });
  }

  get waterPercentage(): number {
    return Math.min(this.user.water / this.user.waterGoal, 1);
  }

  get waterStroke(): string {
    const circumference = 188.4;
    return `${this.waterPercentage * circumference} ${circumference}`;
  }

  private syncWaterFill() {
    const circumference = 188.4;
    const targetOffset = circumference * (1 - this.waterPercentage);
    const startOffset = circumference;
    const startLevel = 0;
    const targetLevel = this.waterPercentage * 100;
    const duration = this.waterAnimDuration;
    const startTime = performance.now();

    const animate = (time: number) => {
      const progress = Math.min((time - startTime) / duration, 1);
      this.waterOffset = startOffset - (startOffset - targetOffset) * progress;
      this.waveLevel = startLevel + (targetLevel - startLevel) * progress;
      if (progress < 1) requestAnimationFrame(animate);
    };
    requestAnimationFrame(animate);
  }

  private animateNumber(
    property: keyof UserSummary,
    endValue: number,
    duration = 800,
    decimals = 0,
    callback?: () => void
  ) {
    const frameRate = 1000 / 60;
    const steps = Math.max(1, Math.round(duration / frameRate));
    let current = 0;
    const stepValue = endValue / steps;
    let step = 0;
    const id = setInterval(() => {
      step++;
      current += stepValue;
      if (step >= steps) {
        (this as any)[property] = parseFloat(endValue.toFixed(decimals));
        clearInterval(id);
        if (callback) callback();
      } else {
        (this as any)[property] = parseFloat(current.toFixed(decimals));
      }
    }, frameRate);
  }

  isMale() {
    return this.user.gender === 'male';
  }
  isFemale() {
    return this.user.gender === 'female';
  }

  getBmiStatus(): string {
    if (this.user.bmi < 18.5) return 'Underweight';
    if (this.user.bmi < 24.9) return 'Healthy';
    if (this.user.bmi < 29.9) return 'Overweight';
    return 'Obese';
  }
}
