// src/app/features/books/book-list/book-list.component.ts

import { Component, OnInit } from '@angular/core';
import { BookService } from '../../../core/services/book.service';
import { BorrowService } from '../../../core/services/borrow.service';
import { AuthService } from '../../../core/services/auth.service';
import { BookResponse } from '../../../core/models/models';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

interface BookWithBorrowStatus extends BookResponse {
  isBorrowedByCurrentUser?: boolean;
}

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css'],
  standalone: false
})
export class BookListComponent implements OnInit {
  books: BookWithBorrowStatus[] = [];
  filteredBooks: BookWithBorrowStatus[] = [];
  borrowedBookIds: Set<number> = new Set();
  isLoading = false;
  isAdmin = false;
  currentUserId: number | null = null;
  searchQuery = '';
  searchType: 'title' | 'author' | 'all' = 'all';
  displayedColumns: string[] = ['title', 'author', 'availableCopies', 'actions'];

  constructor(
    private bookService: BookService,
    private borrowService: BorrowService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.currentUserId = this.authService.getCurrentUserId();

    if (!this.isAdmin && this.currentUserId) {
      this.loadCurrentlyBorrowedBooks();
    } else {
      this.loadBooks();
    }
  }

  loadCurrentlyBorrowedBooks(): void {
    if (!this.currentUserId) return;

    this.borrowService.getCurrentlyBorrowedBooks(this.currentUserId).subscribe({
      next: (borrowedBooks) => {
        this.borrowedBookIds = new Set(borrowedBooks.map(b => b.bookId));
        this.loadBooks();
      },
      error: () => {
        this.loadBooks();
      }
    });
  }

  loadBooks(): void {
    this.isLoading = true;
    this.bookService.getAllBooks().subscribe({
      next: (books) => {
        this.books = books.map(book => ({
          ...book,
          isBorrowedByCurrentUser: this.borrowedBookIds.has(book.id)
        }));
        this.filteredBooks = this.books;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    if (!this.searchQuery.trim()) {
      this.filteredBooks = this.books;
      return;
    }

    const query = this.searchQuery.toLowerCase().trim();

    if (this.searchType === 'all') {
      this.filteredBooks = this.books.filter(book =>
        book.title.toLowerCase().includes(query) ||
        book.author.toLowerCase().includes(query)
      );
    } else if (this.searchType === 'title') {
      this.bookService.searchByTitle(this.searchQuery).subscribe({
        next: (books) => {
          this.filteredBooks = books.map(book => ({
            ...book,
            isBorrowedByCurrentUser: this.borrowedBookIds.has(book.id)
          }));
        }
      });
    } else if (this.searchType === 'author') {
      this.bookService.searchByAuthor(this.searchQuery).subscribe({
        next: (books) => {
          this.filteredBooks = books.map(book => ({
            ...book,
            isBorrowedByCurrentUser: this.borrowedBookIds.has(book.id)
          }));
        }
      });
    }
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.filteredBooks = this.books;
  }

  borrowBook(book: BookWithBorrowStatus): void {
    if (!this.currentUserId) {
      this.snackBar.open('Please log in to borrow books', 'Close', { duration: 3000 });
      return;
    }

    if (book.availableCopies === 0) {
      this.snackBar.open('This book is not available', 'Close', { duration: 3000 });
      return;
    }

    if (book.isBorrowedByCurrentUser) {
      this.snackBar.open('You have already borrowed this book', 'Close', { duration: 3000 });
      return;
    }

    this.borrowService.borrowBook(this.currentUserId, book.id).subscribe({
      next: () => {
        this.snackBar.open(`Successfully borrowed "${book.title}"`, 'Close', { duration: 3000 });
        this.borrowedBookIds.add(book.id);
        this.loadBooks();
      }
    });
  }

  returnBook(book: BookWithBorrowStatus): void {
    if (!this.currentUserId) return;

    this.borrowService.returnBook(this.currentUserId, book.id).subscribe({
      next: () => {
        this.snackBar.open(`Successfully returned "${book.title}"`, 'Close', { duration: 3000 });
        this.borrowedBookIds.delete(book.id);
        this.loadBooks();
      }
    });
  }

  deleteBook(book: BookResponse): void {
    if (confirm(`Are you sure you want to delete "${book.title}"?`)) {
      this.bookService.deleteBook(book.id).subscribe({
        next: () => {
          this.snackBar.open('Book deleted successfully', 'Close', { duration: 3000 });
          this.loadBooks();
        }
      });
    }
  }

  navigateToAddBook(): void {
    this.router.navigate(['/books/add']);
  }
}
