import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
} from '../../models/auth.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private BASE_URL = environment.authUrl; // Matches backend controller mapping

  constructor(private http: HttpClient) {}

  /**
   * Calls backend login endpoint
   */
  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.BASE_URL}/login`, payload);
  }

  /**
   * Calls backend register endpoint
   */
  register(payload: RegisterRequest): Observable<any> {
    return this.http.post<any>(`${this.BASE_URL}/register`, payload);
  }
}
