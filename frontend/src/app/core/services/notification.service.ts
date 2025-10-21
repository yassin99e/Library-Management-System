// src/app/core/services/notification.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, interval } from 'rxjs';
import { tap, switchMap, startWith } from 'rxjs/operators';
import { Notification, NotificationType } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly API_URL = 'http://localhost:8080/notification-ms/api/notifications';

  // Observable pour le nombre de notifications non lues
  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) {
    // Polling toutes les 30 secondes pour mettre à jour le compteur
    this.startPolling();
  }

  // Démarrer le polling automatique du compteur
  private startPolling(): void {
    interval(30000) // 30 secondes
      .pipe(
        startWith(0), // Démarrer immédiatement
        switchMap(() => this.getUnreadCount())
      )
      .subscribe({
        next: (response) => {
          this.unreadCountSubject.next(response.count);
        },
        error: (err) => {
          console.error('Error fetching unread count:', err);
        }
      });
  }

  // Forcer la mise à jour du compteur
  refreshUnreadCount(): void {
    this.getUnreadCount().subscribe({
      next: (response) => {
        this.unreadCountSubject.next(response.count);
      }
    });
  }

  // Récupérer toutes les notifications
  getAllNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.API_URL);
  }

  // Récupérer les notifications non lues
  getUnreadNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.API_URL}/unread`);
  }

  // Récupérer le nombre de notifications non lues
  getUnreadCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.API_URL}/unread/count`);
  }

  // Récupérer les notifications par type
  getNotificationsByType(type: NotificationType): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.API_URL}/type/${type}`);
  }

  // Marquer une notification comme lue
  markAsRead(notificationId: number): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/${notificationId}/read`, {}).pipe(
      tap(() => this.refreshUnreadCount())
    );
  }

  // Marquer toutes les notifications comme lues
  markAllAsRead(): Observable<{ updated: number }> {
    return this.http.put<{ updated: number }>(`${this.API_URL}/read-all`, {}).pipe(
      tap(() => this.refreshUnreadCount())
    );
  }

  // Supprimer une notification
  deleteNotification(notificationId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${notificationId}`).pipe(
      tap(() => this.refreshUnreadCount())
    );
  }

  // Supprimer toutes les notifications lues
  deleteAllRead(): Observable<{ deleted: number }> {
    return this.http.delete<{ deleted: number }>(`${this.API_URL}/read`).pipe(
      tap(() => this.refreshUnreadCount())
    );
  }
}
