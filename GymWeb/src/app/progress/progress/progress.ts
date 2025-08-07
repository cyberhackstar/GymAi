import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-progress',
  imports: [CommonModule],
  templateUrl: './progress.html',
  styleUrl: './progress.css',
})
export class Progress implements OnInit {
  weightLogs: { date: string; weight: number }[] = [];
  completedWorkouts: number = 0;
  dietCompliance: string = '80%';

  ngOnInit(): void {
    this.fetchProgress();
  }

  fetchProgress() {
    // Replace this with actual API calls later
    this.weightLogs = [
      { date: '2025-08-01', weight: 70 },
      { date: '2025-08-05', weight: 69.5 },
      { date: '2025-08-07', weight: 69 },
    ];

    this.completedWorkouts = 12;
    this.dietCompliance = '85%';
  }
}
