import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  userName: string = 'User'; // Replace with actual user data from a service in future

  // Placeholder navigation logic
  navigateTo(feature: string) {
    console.log(`Navigating to ${feature}...`);
  }
}
