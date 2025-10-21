package ma.ensa.notification_ms.Web;



import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.notification_ms.Entity.Notification;
import ma.ensa.notification_ms.Entity.NotificationType;
import ma.ensa.notification_ms.Service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /api/notifications
     * Récupérer toutes les notifications de l'utilisateur connecté
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("📬 User {} requesting all notifications", userId);
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/unread
     * Récupérer uniquement les notifications non lues
     */
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("📬 User {} requesting unread notifications", userId);
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/unread/count
     * Compter les notifications non lues (pour le badge)
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("🔢 User {} requesting unread count", userId);
        long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * GET /api/notifications/type/{type}
     * Récupérer les notifications par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(
            @PathVariable NotificationType type,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("📬 User {} requesting notifications of type {}", userId, type);
        List<Notification> notifications = notificationService.getNotificationsByType(userId, type);
        return ResponseEntity.ok(notifications);
    }

    /**
     * PUT /api/notifications/{id}/read
     * Marquer une notification comme lue
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("✅ User {} marking notification {} as read", userId, id);
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/notifications/read-all
     * Marquer toutes les notifications comme lues
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("✅ User {} marking all notifications as read", userId);
        int updated = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("updated", updated));
    }

    /**
     * DELETE /api/notifications/{id}
     * Supprimer une notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("🗑️ User {} deleting notification {}", userId, id);
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/notifications/read
     * Supprimer toutes les notifications lues
     */
    @DeleteMapping("/read")
    public ResponseEntity<Map<String, Integer>> deleteAllReadNotifications(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("🗑️ User {} deleting all read notifications", userId);
        int deleted = notificationService.deleteAllReadNotifications(userId);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }

    /**
     * Helper method : Extraire userId depuis le header JWT (ajouté par API Gateway)
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            throw new IllegalArgumentException("User ID header is missing");
        }
        return Long.parseLong(userIdHeader);
    }
}