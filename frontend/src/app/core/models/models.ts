// src/app/core/models/models.ts

// ==================== Book Models ====================
export interface BookRequest {
  title: string;
  author: string;
  availableCopies: number;
}

export interface BookResponse {
  id: number;
  title: string;
  author: string;
  availableCopies: number;
}

// ==================== User Models ====================
export interface UserRegister {
  username: string;
  email: string;
  password: string;
}

export interface UserLogin {
  email: string;
  password: string;
}

export interface UserLoginResponse {
  token: string;
  username: string;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface UserUpdate {
  username?: string;
  password?: string;
}

// ==================== Borrow Models ====================
export interface BorrowRecordResponse {
  id: number;
  bookId: number;
  borrowerId: number;
  borrowDate: string; // ISO date string
  returnDate: string | null; // ISO date string or null
}

export interface BorrowResponse {
  // Book info
  bookId: number;
  title: string;
  author: string;
  availableCopies: number;

  // Borrow info
  borrowDate: string; // ISO date string
  returnDate: string | null; // ISO date string or null
}



// ==================== Error Response ====================
export interface ErrorResponse {
  error: string;
}

export interface ValidationErrors {
  [field: string]: string;
}

// ==================== Auth State ====================
export interface AuthState {
  token: string | null;
  username: string | null;
  userId: number | null;
  role: string | null;
  isAuthenticated: boolean;
}

// ==================== Notification Models ====================
export enum NotificationType {
  BOOK_CREATED = 'BOOK_CREATED',
  BOOK_BORROWED = 'BOOK_BORROWED',
  BOOK_RETURNED = 'BOOK_RETURNED',
  BOOK_STOCK_OVER = 'BOOK_STOCK_OVER'
}

export interface Notification {
  id: number;
  userId: number;
  type: NotificationType;
  message: string;
  createdAt: string; // ISO date string
  seen: boolean;
}

