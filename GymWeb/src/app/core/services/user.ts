import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserProfile } from '../../models/user-profile.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserProfileService {
  private apiUrl = environment.userUrl; // Change port if user-service runs elsewhere

  constructor(private http: HttpClient) {}

  // Save or update profile
  saveProfile(profile: UserProfile): Observable<UserProfile> {
    return this.http.post<UserProfile>(`${this.apiUrl}/profile`, profile);
  }

  // Get the logged-in user's profile
  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/profile`);
  }

  // Check if profile is completed
  isProfileCompleted(): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/profile/check`);
  }
}
