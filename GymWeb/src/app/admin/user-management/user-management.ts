import { Component } from '@angular/core';
import { UserProfile } from '../../models/user-profile.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-management',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.html',
  styleUrls: ['./user-management.css'],
})
export class UserManagement {
  users: UserProfile[] = [
    {
      userId: 1,
      name: 'John Doe',
      email: 'john@example.com',
      age: 28,
      height: 180,
      weight: 78,
      gender: 'Male',
      goal: 'Muscle Gain',
      activityLevel: 'High',
      preference: 'Weight Training',
    },
    {
      userId: 2,
      name: 'Sarah Lee',
      email: 'sarah@example.com',
      age: 25,
      height: 165,
      weight: 60,
      gender: 'Female',
      goal: 'Weight Loss',
      activityLevel: 'Medium',
      preference: 'Cardio',
    },
    {
      userId: 3,
      name: 'Alex Kim',
      email: 'alex@example.com',
      age: 31,
      height: 175,
      weight: 72,
      gender: 'Male',
      goal: 'Endurance',
      activityLevel: 'Low',
      preference: 'Yoga',
    },
  ];

  viewProfile(user: UserProfile) {
    alert(`Viewing profile of ${user.name}`);
  }

  suspendUser(user: UserProfile) {
    alert(`Suspended user: ${user.name}`);
  }
}
