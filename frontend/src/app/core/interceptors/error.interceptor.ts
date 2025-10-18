// src/app/core/interceptors/error.interceptor.ts

import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unexpected error occurred';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Server-side error
        switch (error.status) {
          case 400:
            // Validation errors or bad request
            if (typeof error.error === 'object' && error.error.error) {
              errorMessage = error.error.error;
            } else if (typeof error.error === 'string') {
              errorMessage = error.error;
            } else {
              errorMessage = 'Bad request';
            }
            break;
          case 401:
            errorMessage = 'Unauthorized. Please log in.';
            authService.logout();
            break;
          case 403:
            errorMessage = error.error?.error || 'Access forbidden';
            break;
          case 404:
            errorMessage = error.error?.error || 'Resource not found';
            break;
          case 409:
            errorMessage = error.error?.error || 'Conflict error';
            break;
          case 500:
            errorMessage = error.error?.error || 'Internal server error';
            break;
          default:
            errorMessage = `Error ${error.status}: ${error.message}`;
        }
      }

      // Show error notification
      snackBar.open(errorMessage, 'Close', {
        duration: 5000,
        horizontalPosition: 'end',
        verticalPosition: 'top',
        panelClass: ['error-snackbar']
      });

      return throwError(() => error);
    })
  );
};
