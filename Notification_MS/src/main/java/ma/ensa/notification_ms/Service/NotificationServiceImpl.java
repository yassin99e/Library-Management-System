package ma.ensa.notification_ms.Service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.notification_ms.Entity.Notification;
import ma.ensa.notification_ms.Entity.NotificationType;
import ma.ensa.notification_ms.Repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        log.info("Fetching all notifications for user {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        log.info("Fetching unread notifications for user {}", userId);
        return notificationRepository.findByUserIdAndSeenFalseOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Notification> getNotificationsByType(Long userId, NotificationType type) {
        log.info("Fetching notifications of type {} for user {}", type, userId);
        return notificationRepository.findByUserIdAndType(userId, type);
    }

    @Override
    public long countUnreadNotifications(Long userId) {
        long count = notificationRepository.countByUserIdAndSeenFalse(userId);
        log.info("User {} has {} unread notifications", userId, count);
        return count;
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.info("Marking notification {} as read for user {}", notificationId, userId);

        notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId)) // Security check
                .ifPresentOrElse(
                        notification -> {
                            notification.setSeen(true);
                            notificationRepository.save(notification);
                            log.info("✅ Notification {} marked as read", notificationId);
                        },
                        () -> log.warn("⚠️ Notification {} not found or doesn't belong to user {}",
                                notificationId, userId)
                );
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user {}", userId);
        int updated = notificationRepository.markAllAsReadByUserId(userId);
        log.info("✅ Marked {} notifications as read for user {}", updated, userId);
        return updated;
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        log.info("Deleting notification {} for user {}", notificationId, userId);

        notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId)) // Security check
                .ifPresentOrElse(
                        notification -> {
                            notificationRepository.delete(notification);
                            log.info("✅ Notification {} deleted", notificationId);
                        },
                        () -> log.warn("⚠️ Notification {} not found or doesn't belong to user {}",
                                notificationId, userId)
                );
    }

    @Override
    @Transactional
    public int deleteAllReadNotifications(Long userId) {
        log.info("Deleting all read notifications for user {}", userId);
        int deleted = notificationRepository.deleteAllReadByUserId(userId);
        log.info("✅ Deleted {} read notifications for user {}", deleted, userId);
        return deleted;
    }
}