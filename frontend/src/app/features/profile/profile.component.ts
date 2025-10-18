// src/app/features/profile/profile.component.ts

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { BorrowService } from '../../core/services/borrow.service';
import { AuthService } from '../../core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserResponse, BorrowResponse } from '../../core/models/models';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  standalone: false
})
export class ProfileComponent implements OnInit {
  user: UserResponse | null = null;
  updateForm: FormGroup;
  isLoading = false;
  isUpdating = false;
  hidePassword = true;

  // Borrowed books data
  currentlyBorrowedBooks: BorrowResponse[] = [];
  borrowHistory: BorrowResponse[] = [];

  // Tab selection
  selectedTab = 0;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private borrowService: BorrowService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.updateForm = this.fb.group({
      username: ['', [Validators.minLength(3)]],
      password: ['', [Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    this.loadUserProfile();
    this.loadBorrowedBooks();
    this.loadBorrowHistory();
  }

  loadUserProfile(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.snackBar.open('User not found', 'Close', { duration: 3000 });
      return;
    }

    this.isLoading = true;
    this.userService.getUserById(userId).subscribe({
      next: (user) => {
        this.user = user;
        this.updateForm.patchValue({
          username: user.username
        });
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  loadBorrowedBooks(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.borrowService.getCurrentlyBorrowedBooks(userId).subscribe({
      next: (books) => {
        this.currentlyBorrowedBooks = books;
      },
      error: (err) => {
        console.error('Error loading borrowed books:', err);
      }
    });
  }

  loadBorrowHistory(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.borrowService.getBorrowHistory(userId).subscribe({
      next: (history) => {
        this.borrowHistory = history;
      },
      error: (err) => {
        console.error('Error loading borrow history:', err);
      }
    });
  }

  onUpdateProfile(): void {
    if (this.updateForm.invalid) {
      this.updateForm.markAllAsTouched();
      return;
    }

    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    // Prepare data (only send modified fields)
    const updateData: any = {};

    if (this.updateForm.get('username')?.value &&
      this.updateForm.get('username')?.value !== this.user?.username) {
      updateData.username = this.updateForm.get('username')?.value;
    }

    if (this.updateForm.get('password')?.value) {
      updateData.password = this.updateForm.get('password')?.value;
    }

    if (Object.keys(updateData).length === 0) {
      this.snackBar.open('No changes to update', 'Close', { duration: 3000 });
      return;
    }

    this.isUpdating = true;
    this.userService.updateUser(userId, updateData).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.snackBar.open('Profile updated successfully!', 'Close', { duration: 3000 });
        this.updateForm.get('password')?.reset();
        this.isUpdating = false;
      },
      error: () => {
        this.isUpdating = false;
      }
    });
  }

  returnBook(book: BorrowResponse): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.borrowService.returnBook(userId, book.bookId).subscribe({
      next: () => {
        this.snackBar.open(`"${book.title}" returned successfully`, 'Close', {
          duration: 3000
        });

        // Reload data
        this.loadBorrowedBooks();
        this.loadBorrowHistory();
      }
    });
  }

  getErrorMessage(fieldName: string): string {
    const field = this.updateForm.get(fieldName);

    if (field?.hasError('minlength')) {
      const minLength = field.getError('minlength').requiredLength;
      return `Must be at least ${minLength} characters`;
    }

    return '';
  }
}
