import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
// import { Router, RouterOutlet } from '@angular/router';

interface WorkoutSlot {
  day: string;
  time: string;
  title: string;
  trainer: string;
  category: string; // fitness, workout, motivation
}

@Component({
  selector: 'app-workout-plan',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './workout-plan.html',
  styleUrl: './workout-plan.css',
})
export class WorkoutPlan implements OnInit {
  workoutSchedule: { [time: string]: { [day: string]: WorkoutSlot | null } } =
    {};
  days: string[] = [
    'Monday',
    'Tuesday',
    'Wednesday',
    'Thursday',
    'Friday',
    'Saturday',
    'Sunday',
  ];
  times: string[] = [
    '6.00am - 8.00am',
    '10.00am - 12.00am',
    '5.00pm - 7.00pm',
    '7.00pm - 9.00pm',
  ];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadWorkoutPlan();
  }

  loadWorkoutPlan(): void {
    // Replace this mock with actual WGER API call and parsing
    const mockData: WorkoutSlot[] = [
      {
        day: 'Monday',
        time: '6.00am - 8.00am',
        title: 'Weight Loss',
        trainer: 'Alex Doe',
        category: 'workout',
      },
      {
        day: 'Tuesday',
        time: '10.00am - 12.00am',
        title: 'Cardio',
        trainer: 'Jane Smith',
        category: 'fitness',
      },
      {
        day: 'Wednesday',
        time: '5.00pm - 7.00pm',
        title: 'Yoga',
        trainer: 'Yogi Joe',
        category: 'workout',
      },
      // Add more entries...
    ];

    this.times.forEach((time) => {
      this.workoutSchedule[time] = {};
      this.days.forEach((day) => {
        this.workoutSchedule[time][day] = null;
      });
    });

    mockData.forEach((slot) => {
      if (this.workoutSchedule[slot.time]) {
        this.workoutSchedule[slot.time][slot.day] = slot;
      }
    });
  }

  getCategoryClass(category: string): string {
    if (!category) return 'ts-meta';
    return `ts-meta ${category}`;
  }
}
