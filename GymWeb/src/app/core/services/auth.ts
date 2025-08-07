import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse } from '../../models/auth.model';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private BASE_URL = 'https://api.neelahouse.cloud/auth'; // Replace with your backend

  constructor(private http: HttpClient) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.BASE_URL}/login`, payload);
  }
}
