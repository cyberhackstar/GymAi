import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-progress',
  templateUrl: './progress.html',
  styleUrls: ['./progress.css'],
})
export class Progress implements OnInit {
  @ViewChild('progressChart', { static: false })
  progressChart!: ElementRef<HTMLCanvasElement>;

  userProfile = {
    name: 'John Doe',
    bmiHistory: [24, 23.8, 23.6, 23.5],
    dietIntake: [2200, 2100, 2000, 1950],
    workoutsCompleted: [4, 8, 12, 15],
    weeks: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
  };

  ngOnInit() {}

  ngAfterViewInit() {
    this.renderChart();
  }

  renderChart() {
    if (!this.progressChart) {
      console.error('Canvas not found!');
      return;
    }

    const ctx = this.progressChart.nativeElement.getContext('2d');
    if (!ctx) {
      console.error('Could not get canvas context!');
      return;
    }

    new Chart(ctx, {
      type: 'line',
      data: {
        labels: this.userProfile.weeks,
        datasets: [
          {
            label: 'Workouts Completed',
            data: this.userProfile.workoutsCompleted,
            borderColor: '#ff4c4c',
            backgroundColor: 'rgba(255, 76, 76, 0.2)',
            fill: true,
            tension: 0.3,
            yAxisID: 'y',
          },
          {
            label: 'BMI',
            data: this.userProfile.bmiHistory,
            borderColor: '#4cc9ff',
            backgroundColor: 'rgba(76, 201, 255, 0.2)',
            fill: true,
            tension: 0.3,
            yAxisID: 'y1',
          },
          {
            label: 'Diet Intake (Calories)',
            data: this.userProfile.dietIntake,
            borderColor: '#ffd54c',
            backgroundColor: 'rgba(255, 213, 76, 0.2)',
            fill: true,
            tension: 0.3,
            yAxisID: 'y',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { labels: { color: '#f0f0f0' } },
        },
        scales: {
          x: { ticks: { color: '#f0f0f0' } },
          y: {
            position: 'left',
            ticks: { color: '#f0f0f0' },
          },
          y1: {
            position: 'right',
            ticks: { color: '#4cc9ff' },
            grid: { drawOnChartArea: false },
          },
        },
      },
    });
  }
}
