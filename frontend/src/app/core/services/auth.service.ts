// src/app/core/services/auth.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { UserLogin, UserLoginResponse, UserRegister, UserResponse, AuthState } from '../models/models';
import { Router } from '@angular/router';
import {environment} from '../../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL =`${environment.apiUrl}/borrower-ms/api/users`;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  private authStateSubject = new BehaviorSubject<AuthState>(this.getInitialAuthState());
  public authState$ = this.authStateSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  private getInitialAuthState(): AuthState {
    const token = localStorage.getItem(this.TOKEN_KEY);
    const userStr = localStorage.getItem(this.USER_KEY);

    if (token && userStr) {
      const user = JSON.parse(userStr);
      const decodedToken = this.decodeToken(token);
      return {
        token,
        username: user.username,
        userId: decodedToken.userId,
        role: decodedToken.role,
        isAuthenticated: true
      };
    }

    return {
      token: null,
      username: null,
      userId: null,
      role: null,
      isAuthenticated: false
    };
  }

  register(data: UserRegister): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.API_URL}/register`, data);
  }

  login(credentials: UserLogin): Observable<UserLoginResponse> {
    return this.http.post<UserLoginResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        this.setAuthData(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.authStateSubject.next({
      token: null,
      username: null,
      userId: null,
      role: null,
      isAuthenticated: false
    });
    this.router.navigate(['/login']);
  }

  private setAuthData(response: UserLoginResponse): void {
    const decodedToken = this.decodeToken(response.token);

    localStorage.setItem(this.TOKEN_KEY, response.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify({ username: response.username }));

    this.authStateSubject.next({
      token: response.token,
      username: response.username,
      userId: decodedToken.userId,
      role: decodedToken.role,
      isAuthenticated: true
    });
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return this.authStateSubject.value.isAuthenticated;
  }

  isAdmin(): boolean {
    return this.authStateSubject.value.role === 'ADMIN';
  }

  getCurrentUserId(): number | null {
    return this.authStateSubject.value.userId;
  }

  getCurrentUsername(): string | null {
    return this.authStateSubject.value.username;
  }

  private decodeToken(token: string): { userId: number; role: string } {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return {
        userId: parseInt(payload.sub, 10),
        role: payload.role
      };
    } catch (error) {
      console.error('Error decoding token:', error);
      return { userId: 0, role: 'USER' };
    }
  }
}
