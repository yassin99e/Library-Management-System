// src/app/app.component.ts

import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/services/auth.service';
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

  constructor(
    public authService: AuthService,
    private router: Router
  ) {
    this.authState$ = this.authService.authState$;
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
}
