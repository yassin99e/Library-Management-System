// src/app/core/guards/admin.guard.ts

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  if (authService.isAuthenticated() && authService.isAdmin()) {
    return true;
  }

  snackBar.open('Access denied: Admin only', 'Close', {
    duration: 3000,
    horizontalPosition: 'end',
    verticalPosition: 'top'
  });

  router.navigate(['/books']);
  return false;
};
