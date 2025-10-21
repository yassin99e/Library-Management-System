package ma.ensa.notification_ms.Service;

import ma.ensa.notification_ms.Entity.Notification;
import ma.ensa.notification_ms.Entity.NotificationType;

import java.util.List;

public interface NotificationService {

    // Récupérer toutes les notifications d'un utilisateur
    List<Notification> getUserNotifications(Long userId);

    // Récupérer les notifications non lues
    List<Notification> getUnreadNotifications(Long userId);

    // Récupérer les notifications par type
    List<Notification> getNotificationsByType(Long userId, NotificationType type);

    // Compter les notifications non lues
    long countUnreadNotifications(Long userId);

    // Marquer une notification comme lue
    void markAsRead(Long notificationId, Long userId);

    // Marquer toutes les notifications comme lues
    int markAllAsRead(Long userId);

    // Supprimer une notification
    void deleteNotification(Long notificationId, Long userId);

    // Supprimer toutes les notifications lues
    int deleteAllReadNotifications(Long userId);
}