// src/app/features/notifications/notifications.component.ts

import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../core/services/notification.service';
import { Notification, NotificationType } from '../../core/models/models';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css'],
  standalone: false
})
export class NotificationsComponent implements OnInit {
  notifications: Notification[] = [];
  filteredNotifications: Notification[] = [];
  isLoading = false;
  selectedFilter: 'all' | 'unread' = 'all';
  selectedType: NotificationType | 'all' = 'all';

  // Pour le template
  notificationTypes = Object.values(NotificationType);

  constructor(
    private notificationService: NotificationService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.isLoading = true;

    if (this.selectedFilter === 'unread') {
      this.notificationService.getUnreadNotifications().subscribe({
        next: (notifications) => {
          this.notifications = notifications;
          this.applyTypeFilter();
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
    } else {
      this.notificationService.getAllNotifications().subscribe({
        next: (notifications) => {
          this.notifications = notifications;
          this.applyTypeFilter();
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
    }
  }

  applyTypeFilter(): void {
    if (this.selectedType === 'all') {
      this.filteredNotifications = this.notifications;
    } else {
      this.filteredNotifications = this.notifications.filter(
        n => n.type === this.selectedType
      );
    }
  }

  onFilterChange(): void {
    this.loadNotifications();
  }

  onTypeFilterChange(): void {
    this.applyTypeFilter();
  }

  markAsRead(notification: Notification): void {
    if (notification.seen) return;

    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        notification.seen = true;
        this.snackBar.open('Marked as read', 'Close', { duration: 2000 });
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: (response) => {
        this.snackBar.open(`${response.updated} notifications marked as read`, 'Close', {
          duration: 3000
        });
        this.loadNotifications();
      }
    });
  }

  deleteNotification(notification: Notification): void {
    if (confirm('Delete this notification?')) {
      this.notificationService.deleteNotification(notification.id).subscribe({
        next: () => {
          this.snackBar.open('Notification deleted', 'Close', { duration: 2000 });
          this.loadNotifications();
        }
      });
    }
  }

  deleteAllRead(): void {
    if (confirm('Delete all read notifications?')) {
      this.notificationService.deleteAllRead().subscribe({
        next: (response) => {
          this.snackBar.open(`${response.deleted} notifications deleted`, 'Close', {
            duration: 3000
          });
          this.loadNotifications();
        }
      });
    }
  }

  getNotificationIcon(type: NotificationType): string {
    switch (type) {
      case NotificationType.BOOK_CREATED:
        return 'library_add';
      case NotificationType.BOOK_BORROWED:
        return 'book';
      case NotificationType.BOOK_RETURNED:
        return 'assignment_return';
      case NotificationType.BOOK_STOCK_OVER:
        return 'warning';
      default:
        return 'notifications';
    }
  }

  getNotificationColor(type: NotificationType): string {
    switch (type) {
      case NotificationType.BOOK_CREATED:
        return 'primary';
      case NotificationType.BOOK_BORROWED:
        return 'accent';
      case NotificationType.BOOK_RETURNED:
        return 'primary';
      case NotificationType.BOOK_STOCK_OVER:
        return 'warn';
      default:
        return 'primary';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} min ago`;
    if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    return date.toLocaleDateString();
  }
}
