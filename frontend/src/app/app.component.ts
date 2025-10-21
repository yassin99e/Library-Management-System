// src/app/app.component.ts

import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/services/auth.service';
import { NotificationService } from './core/services/notification.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthState } from './core/models/models';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: false
})
export class AppComponent implements OnInit {
  title = 'Library Management System';
  authState$: Observable<AuthState>;
  unreadCount$: Observable<number>;

  constructor(
    public authService: AuthService,
    public notificationService: NotificationService,
    private router: Router
  ) {
    this.authState$ = this.authService.authState$;
    this.unreadCount$ = this.notificationService.unreadCount$;
  }

  ngOnInit(): void {
    // Component initialization if needed
  }

  logout(): void {
    this.authService.logout();
  }

  navigateToBooks(): void {
    this.router.navigate(['/books']);
  }

  navigateToAddBook(): void {
    this.router.navigate(['/books/add']);
  }

  navigateToNotifications(): void {
    this.router.navigate(['/notifications']);
  }
}
