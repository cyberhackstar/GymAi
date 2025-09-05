import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface ProgressData {
  calories: { consumed: number; target: number };
  water: { consumed: number; target: number };
  steps: { current: number; target: number };
  workout: { completed: number; target: number };
  sleep: { hours: number; target: number };
  weight: { current: number; target: number };
}

interface DailyActivity {
  id: string;
  type: 'meal' | 'water' | 'exercise' | 'sleep' | 'steps';
  title: string;
  value: number;
  unit: string;
  time: string;
  icon: string;
  calories?: number;
}

interface QuickAddInput {
  type: 'calories' | 'water' | 'steps' | 'workout' | 'weight';
  value: number;
  description?: string;
}

interface MotivationMessage {
  type: 'success' | 'warning' | 'info' | 'celebration';
  message: string;
  icon: string;
}

interface UserStats {
  streak: number;
  totalPoints: number;
  level: number;
  nextLevelPoints: number;
}

@Component({
  selector: 'app-progress-tracker',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './progress-tracker.html',
  styleUrls: ['./progress-tracker.css'],
})
export class ProgressTracker implements OnInit {
  private apiUrl = 'http://localhost:3000/api'; // Backend API URL

  currentDate: Date = new Date();
  selectedDate: string = '';

  // Quick add inputs
  quickAddValues: { [key: string]: number } = {
    calories: 0,
    water: 1,
    steps: 0,
    workout: 0,
    weight: 0,
  };

  // Motivation system
  currentMotivation: MotivationMessage | null = null;
  showMotivation = false;

  // User stats for gamification
  userStats: UserStats = {
    streak: 7,
    totalPoints: 1250,
    level: 5,
    nextLevelPoints: 1500,
  };

  // Daily goals completion
  dailyGoalsCompleted = 0;
  totalDailyGoals = 6;

  progressData: ProgressData = {
    calories: { consumed: 1850, target: 2200 },
    water: { consumed: 6, target: 8 },
    steps: { current: 8500, target: 10000 },
    workout: { completed: 45, target: 60 },
    sleep: { hours: 7.5, target: 8 },
    weight: { current: 72.5, target: 70 },
  };

  dailyActivities: DailyActivity[] = [
    {
      id: '1',
      type: 'meal',
      title: 'Breakfast',
      value: 450,
      unit: 'kcal',
      time: '08:30 AM',
      icon: '🍳',
      calories: 450,
    },
    {
      id: '2',
      type: 'water',
      title: 'Water Glass',
      value: 250,
      unit: 'ml',
      time: '09:15 AM',
      icon: '💧',
    },
    {
      id: '3',
      type: 'exercise',
      title: 'Morning Run',
      value: 30,
      unit: 'min',
      time: '10:00 AM',
      icon: '🏃‍♂️',
    },
    {
      id: '4',
      type: 'meal',
      title: 'Lunch',
      value: 650,
      unit: 'kcal',
      time: '01:30 PM',
      icon: '🥗',
      calories: 650,
    },
    {
      id: '5',
      type: 'water',
      title: 'Water Bottle',
      value: 500,
      unit: 'ml',
      time: '02:45 PM',
      icon: '💧',
    },
    {
      id: '6',
      type: 'exercise',
      title: 'Gym Workout',
      value: 45,
      unit: 'min',
      time: '06:00 PM',
      icon: '💪',
    },
    {
      id: '7',
      type: 'steps',
      title: 'Evening Walk',
      value: 2000,
      unit: 'steps',
      time: '07:30 PM',
      icon: '🚶‍♂️',
    },
  ];

  weeklyStats = [
    { day: 'Mon', calories: 2100, water: 8, steps: 9500 },
    { day: 'Tue', calories: 1950, water: 7, steps: 8800 },
    { day: 'Wed', calories: 2200, water: 8, steps: 10200 },
    { day: 'Thu', calories: 1850, water: 6, steps: 8500 },
    { day: 'Fri', calories: 2050, water: 7, steps: 9200 },
    { day: 'Sat', calories: 2300, water: 9, steps: 11000 },
    { day: 'Sun', calories: 1900, water: 6, steps: 7800 },
  ];

  quickActions = [
    { title: 'Add Meal', icon: '🍽️', action: 'addMeal', color: '#ff4c4c' },
    { title: 'Log Water', icon: '💧', action: 'addWater', color: '#4c9aff' },
    {
      title: 'Add Exercise',
      icon: '🏋️',
      action: 'addExercise',
      color: '#00ff88',
    },
    { title: 'Track Sleep', icon: '😴', action: 'addSleep', color: '#ff9f40' },
  ];

  achievements = [
    {
      title: 'Week Warrior',
      description: '7 days streak',
      icon: '🔥',
      unlocked: true,
    },
    {
      title: 'Hydration Hero',
      description: 'Daily water goal for 5 days',
      icon: '💧',
      unlocked: true,
    },
    {
      title: 'Fitness Fanatic',
      description: '10 workouts completed',
      icon: '💪',
      unlocked: false,
    },
    {
      title: 'Early Bird',
      description: 'Morning workouts for 7 days',
      icon: '🌅',
      unlocked: false,
    },
  ];

  motivationalMessages = {
    calories: {
      success: [
        {
          type: 'celebration',
          message: "🎉 Great job! You're fueling your body perfectly!",
          icon: '🔥',
        },
        {
          type: 'success',
          message: "Amazing! You've hit your calorie target like a champion!",
          icon: '⭐',
        },
      ],
      warning: [
        {
          type: 'warning',
          message: 'Almost there! Just a few more calories to reach your goal!',
          icon: '💪',
        },
        {
          type: 'info',
          message: "You're doing great! Keep pushing towards your target!",
          icon: '🎯',
        },
      ],
    },
    water: {
      success: [
        {
          type: 'celebration',
          message: '💧 Hydration hero! Your body is thanking you!',
          icon: '🏆',
        },
        {
          type: 'success',
          message: "Excellent! You're glowing with proper hydration!",
          icon: '✨',
        },
      ],
      warning: [
        {
          type: 'warning',
          message: 'Time to drink up! Your body needs more water!',
          icon: '💧',
        },
        {
          type: 'info',
          message: 'Stay hydrated, stay amazing! A few more glasses to go!',
          icon: '🌊',
        },
      ],
    },
    steps: {
      success: [
        {
          type: 'celebration',
          message: "🚀 Step master! You've crushed your daily goal!",
          icon: '👟',
        },
        {
          type: 'success',
          message: 'Incredible! Every step is a step towards a healthier you!',
          icon: '🎖️',
        },
      ],
      warning: [
        {
          type: 'warning',
          message: "Let's get moving! Your daily steps are calling!",
          icon: '🏃‍♂️',
        },
        {
          type: 'info',
          message: "You're on the right track! Keep those steps coming!",
          icon: '📈',
        },
      ],
    },
    workout: {
      success: [
        {
          type: 'celebration',
          message: "💪 Workout warrior! You're unstoppable today!",
          icon: '🔥',
        },
        {
          type: 'success',
          message: 'Amazing dedication! Your future self will thank you!',
          icon: '⚡',
        },
      ],
      warning: [
        {
          type: 'warning',
          message: 'Time to sweat! Your body is ready for action!',
          icon: '🏋️‍♂️',
        },
        {
          type: 'info',
          message:
            "You've got this! A little more exercise to reach your goal!",
          icon: '💯',
        },
      ],
    },
  };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.selectedDate = this.formatDate(this.currentDate);
    this.loadProgressData();
    this.calculateDailyGoals();
    this.animateProgressBars();
  }

  // Backend API methods
  async loadProgressData(): Promise<void> {
    try {
      // In real app, replace with actual API call
      // const response = await this.http.get(`${this.apiUrl}/progress/${this.selectedDate}`).toPromise();
      // this.progressData = response.data;
      console.log('Loading progress data from backend...');
    } catch (error) {
      console.error('Error loading progress data:', error);
    }
  }

  async saveProgressData(): Promise<void> {
    try {
      // In real app, replace with actual API call
      // await this.http.post(`${this.apiUrl}/progress`, {
      //   date: this.selectedDate,
      //   data: this.progressData
      // }).toPromise();
      console.log('Saving progress data to backend...', this.progressData);
    } catch (error) {
      console.error('Error saving progress data:', error);
    }
  }

  async addActivity(activity: DailyActivity): Promise<void> {
    try {
      // In real app, replace with actual API call
      // await this.http.post(`${this.apiUrl}/activities`, activity).toPromise();
      console.log('Adding activity to backend...', activity);
    } catch (error) {
      console.error('Error adding activity:', error);
    }
  }

  // Quick add functionality
  quickAddProgress(
    type: 'calories' | 'water' | 'steps' | 'workout' | 'weight'
  ): void {
    const value = this.quickAddValues[type];
    if (value <= 0 && type !== 'weight') return;

    let activityTitle = '';
    let activityIcon = '';
    let unit = '';

    switch (type) {
      case 'calories':
        this.progressData.calories.consumed += value;
        activityTitle = `Added ${value} calories`;
        activityIcon = '🍽️';
        unit = 'kcal';
        break;
      case 'water':
        this.progressData.water.consumed += value;
        activityTitle = `Drank ${value} glass${value > 1 ? 'es' : ''}`;
        activityIcon = '💧';
        unit = 'glasses';
        break;
      case 'steps':
        this.progressData.steps.current += value;
        activityTitle = `Walked ${value} steps`;
        activityIcon = '🚶‍♂️';
        unit = 'steps';
        break;
      case 'workout':
        this.progressData.workout.completed += value;
        activityTitle = `Worked out ${value} minutes`;
        activityIcon = '💪';
        unit = 'min';
        break;
      case 'weight':
        this.progressData.weight.current = value;
        activityTitle = `Updated weight`;
        activityIcon = '⚖️';
        unit = 'kg';
        break;
    }

    // Add activity to list
    if (type !== 'weight') {
      this.addActivityToList(
        type === 'steps'
          ? 'steps'
          : type === 'workout'
          ? 'exercise'
          : type === 'calories'
          ? 'meal'
          : 'water',
        activityTitle,
        value,
        unit,
        activityIcon
      );
    }

    // Show motivation message
    this.showMotivationMessage(type);

    // Update user stats
    this.updateUserStats(type, value);

    // Calculate daily goals
    this.calculateDailyGoals();

    // Save to backend
    this.saveProgressData();

    // Reset quick add value
    if (type !== 'weight') {
      this.quickAddValues[type] = type === 'water' ? 1 : 0;
    }

    // Animate progress bars
    this.animateProgressBars();
  }

  private addActivityToList(
    type: DailyActivity['type'],
    title: string,
    value: number,
    unit: string,
    icon: string
  ): void {
    const now = new Date();
    const timeString = now.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true,
    });

    const newActivity: DailyActivity = {
      id: Date.now().toString(),
      type,
      title,
      value,
      unit,
      time: timeString,
      icon,
      calories: type === 'meal' ? value : undefined,
    };

    this.dailyActivities.unshift(newActivity);
    this.addActivity(newActivity);
  }

  private showMotivationMessage(
    type: 'calories' | 'water' | 'steps' | 'workout' | 'weight'
  ): void {
    if (type === 'weight') return;

    let current = 0;
    const target = this.progressData[type].target;

    if ('consumed' in this.progressData[type]) {
      current = (this.progressData[type] as { consumed: number }).consumed;
    } else if ('current' in this.progressData[type]) {
      current = (this.progressData[type] as { current: number }).current;
    } else if ('completed' in this.progressData[type]) {
      current = (this.progressData[type] as { completed: number }).completed;
    }

    const percentage = (current / target) * 100;

    const messages =
      percentage >= 100
        ? this.motivationalMessages[
            type as keyof typeof this.motivationalMessages
          ].success
        : this.motivationalMessages[
            type as keyof typeof this.motivationalMessages
          ].warning;

    const randomMessage = messages[Math.floor(Math.random() * messages.length)];

    this.currentMotivation = randomMessage as MotivationMessage;
    this.showMotivation = true;

    // Hide after 4 seconds
    setTimeout(() => {
      this.showMotivation = false;
    }, 4000);
  }

  private updateUserStats(type: string, value: number): void {
    // Award points based on action
    let points = 0;
    switch (type) {
      case 'calories':
        points = Math.floor(value / 100) * 5;
        break;
      case 'water':
        points = value * 10;
        break;
      case 'steps':
        points = Math.floor(value / 1000) * 15;
        break;
      case 'workout':
        points = value * 2;
        break;
    }

    this.userStats.totalPoints += points;

    // Level up check
    if (this.userStats.totalPoints >= this.userStats.nextLevelPoints) {
      this.userStats.level++;
      this.userStats.nextLevelPoints += 500;
      this.showLevelUpMessage();
    }
  }

  private showLevelUpMessage(): void {
    this.currentMotivation = {
      type: 'celebration',
      message: `🎉 LEVEL UP! You're now level ${this.userStats.level}! You're amazing!`,
      icon: '🏆',
    };
    this.showMotivation = true;

    setTimeout(() => {
      this.showMotivation = false;
    }, 6000);
  }

  private calculateDailyGoals(): void {
    let completed = 0;

    if (
      this.progressData.calories.consumed >= this.progressData.calories.target
    )
      completed++;
    if (this.progressData.water.consumed >= this.progressData.water.target)
      completed++;
    if (this.progressData.steps.current >= this.progressData.steps.target)
      completed++;
    if (this.progressData.workout.completed >= this.progressData.workout.target)
      completed++;
    if (this.progressData.sleep.hours >= this.progressData.sleep.target)
      completed++;
    if (this.progressData.weight.current <= this.progressData.weight.target)
      completed++;

    this.dailyGoalsCompleted = completed;
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  // Enhanced progress calculation with motivation
  getProgressPercentage(current: number, target: number): number {
    return Math.min((current / target) * 100, 100);
  }

  getProgressStatus(current: number, target: number): string {
    const percentage = (current / target) * 100;
    if (percentage >= 100) return 'completed';
    if (percentage >= 75) return 'almost';
    if (percentage >= 50) return 'halfway';
    return 'start';
  }

  getProgressEmoji(current: number, target: number): string {
    const percentage = (current / target) * 100;
    if (percentage >= 100) return '🎉';
    if (percentage >= 75) return '🔥';
    if (percentage >= 50) return '💪';
    return '🎯';
  }

  getProgressColor(percentage: number): string {
    if (percentage >= 100) return '#00ff88';
    if (percentage >= 75) return '#ff9f40';
    if (percentage >= 50) return '#4c9aff';
    return '#ff4c4c';
  }

  // Gamification helpers
  getLevelProgress(): number {
    const currentLevelPoints = this.userStats.nextLevelPoints - 500;
    const progress = this.userStats.totalPoints - currentLevelPoints;
    return (progress / 500) * 100;
  }

  getStreakEmoji(): string {
    if (this.userStats.streak >= 30) return '🏆';
    if (this.userStats.streak >= 14) return '🔥';
    if (this.userStats.streak >= 7) return '⚡';
    return '💪';
  }

  getDailyGoalsPercentage(): number {
    return (this.dailyGoalsCompleted / this.totalDailyGoals) * 100;
  }

  quickAction(action: string): void {
    // Legacy support for existing quick actions
    switch (action) {
      case 'addMeal':
        this.quickAddValues['calories'] = 300;
        this.quickAddProgress('calories');
        break;
      case 'addWater':
        this.quickAddProgress('water');
        break;
      case 'addExercise':
        this.quickAddValues['workout'] = 15;
        this.quickAddProgress('workout');
        break;
      case 'addSleep':
        this.trackSleep();
        break;
    }
  }

  private trackSleep(): void {
    // In real app, this would open a sleep tracking modal
    console.log('Track sleep functionality');
  }

  private animateProgressBars(): void {
    setTimeout(() => {
      const progressBars = document.querySelectorAll('.progress-fill');
      progressBars.forEach((bar) => {
        bar.classList.add('animate-progress');
      });
    }, 100);
  }

  changeDate(direction: 'prev' | 'next'): void {
    const currentDate = new Date(this.selectedDate);
    if (direction === 'prev') {
      currentDate.setDate(currentDate.getDate() - 1);
    } else {
      currentDate.setDate(currentDate.getDate() + 1);
    }
    this.selectedDate = this.formatDate(currentDate);
    this.currentDate = currentDate;
  }

  getTodayString(): string {
    const today = new Date();
    return today.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }
}
