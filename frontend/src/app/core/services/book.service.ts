// src/app/core/services/book.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookRequest, BookResponse } from '../models/models';
import {environment} from '../../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class BookService {
  private readonly API_URL =`${environment.apiUrl}/book-ms/api/books`;
  constructor(private http: HttpClient) {}

  // Create book (ADMIN only)
  createBook(book: BookRequest): Observable<BookResponse> {
    return this.http.post<BookResponse>(this.API_URL, book);
  }

  // Get all books
  getAllBooks(): Observable<BookResponse[]> {
    return this.http.get<BookResponse[]>(this.API_URL);
  }

  // Get available books
  getAvailableBooks(): Observable<BookResponse[]> {
    return this.http.get<BookResponse[]>(`${this.API_URL}/available`);
  }

  // Get book by ID
  getBookById(id: number): Observable<BookResponse> {
    return this.http.get<BookResponse>(`${this.API_URL}/${id}`);
  }

  // Delete book (ADMIN only)
  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  // Search by title
  searchByTitle(keyword: string): Observable<BookResponse[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<BookResponse[]>(`${this.API_URL}/search/title`, { params });
  }

  // Search by author
  searchByAuthor(keyword: string): Observable<BookResponse[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<BookResponse[]>(`${this.API_URL}/search/author`, { params });
  }
}
