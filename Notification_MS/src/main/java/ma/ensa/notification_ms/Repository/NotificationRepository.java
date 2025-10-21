package ma.ensa.notification_ms.Repository;

import ma.ensa.notification_ms.Entity.Notification;
import ma.ensa.notification_ms.Entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Récupérer toutes les notifications d'un utilisateur
    List<Notification> findByUserId(Long userId);

    // Récupérer les notifications non lues d'un utilisateur
    List<Notification> findByUserIdAndSeenFalse(Long userId);

    // Récupérer les notifications par type pour un utilisateur
    List<Notification> findByUserIdAndType(Long userId, NotificationType type);

    // Compter les notifications non lues pour un utilisateur
    long countByUserIdAndSeenFalse(Long userId);

    // Récupérer les notifications triées par date (plus récentes d'abord)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Récupérer les notifications non lues triées par date
    List<Notification> findByUserIdAndSeenFalseOrderByCreatedAtDesc(Long userId);

    // Marquer toutes les notifications d'un user comme lues (bulk update)
    @Modifying
    @Query("UPDATE Notification n SET n.seen = true WHERE n.userId = :userId AND n.seen = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);

    // Supprimer toutes les notifications lues d'un utilisateur
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId AND n.seen = true")
    int deleteAllReadByUserId(@Param("userId") Long userId);
}