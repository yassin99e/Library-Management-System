// src/app/core/services/user.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse, UserUpdate } from '../models/models';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly API_URL =`${environment.apiUrl}/borrower-ms/api/users`;

  constructor(private http: HttpClient) {}

  // Get all users (ADMIN only)
  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(this.API_URL);
  }

  // Get user by ID (ADMIN or same user)
  getUserById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.API_URL}/${id}`);
  }

  // Update user (ADMIN or same user)
  updateUser(id: number, data: UserUpdate): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.API_URL}/${id}`, data);
  }

  // Delete user (ADMIN or same user)
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
