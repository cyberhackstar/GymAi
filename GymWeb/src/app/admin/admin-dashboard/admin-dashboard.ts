import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-dashboard',
  imports: [],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard {
  adminName: string = 'Admin';

  stats = {
    users: 128,
    subscriptions: 46,
    feedback: 19,
  };

  ngOnInit(): void {
    // You can fetch these from backend in real scenario
  }
}
