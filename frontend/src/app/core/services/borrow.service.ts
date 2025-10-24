// src/app/core/services/borrow.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BorrowRecordResponse, BorrowResponse } from '../models/models';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BorrowService {

  private readonly API_URL =`${environment.apiUrl}/borrower-ms/api/borrow`;

  constructor(private http: HttpClient) {}

  // Borrow a book
  borrowBook(borrowerId: number, bookId: number): Observable<BorrowRecordResponse> {
    return this.http.post<BorrowRecordResponse>(
      `${this.API_URL}/${borrowerId}/${bookId}`,
      {}
    );
  }

  // Return a book
  returnBook(borrowerId: number, bookId: number): Observable<BorrowRecordResponse> {
    return this.http.put<BorrowRecordResponse>(
      `${this.API_URL}/${borrowerId}/${bookId}/return`,
      {}
    );
  }

  // Get currently borrowed books
  getCurrentlyBorrowedBooks(borrowerId: number): Observable<BorrowResponse[]> {
    return this.http.get<BorrowResponse[]>(
      `${this.API_URL}/borrowed/${borrowerId}`
    );
  }

  // Get borrow history (returned books)
  getBorrowHistory(borrowerId: number): Observable<BorrowResponse[]> {
    return this.http.get<BorrowResponse[]>(
      `${this.API_URL}/history/${borrowerId}`
    );
  }


}
