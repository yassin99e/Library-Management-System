package ma.ensa.notification_ms.KafkaConsumers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.notification_ms.Entity.Notification;
import ma.ensa.notification_ms.Entity.NotificationType;
import ma.ensa.notification_ms.Events.BookStockOverEvent;
import ma.ensa.notification_ms.Repository.NotificationRepository;
import ma.ensa.notification_ms.feign.UserClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookStockOverConsumer {

    private final NotificationRepository notificationRepository;

    private final UserClient userClient;

    @KafkaListener(topics = "book-stock-over-topic", groupId = "notification-group")
    public void consume(BookStockOverEvent event) {
        log.info("📩 Received BookStockOverEvent: {}", event);

        List<Long> adminIds = userClient.GetAdminIds();

        adminIds.forEach(adminId -> {
            Notification notif = Notification.builder()
                    .type(NotificationType.BOOK_STOCK_OVER)
                    .message(event.toString())
                    .createdAt(LocalDateTime.now())
                    .seen(false)
                    .userId(adminId)
                    .build();

            notificationRepository.save(notif);
        });

    }
}
