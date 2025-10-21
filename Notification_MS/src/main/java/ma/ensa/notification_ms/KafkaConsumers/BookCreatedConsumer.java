package ma.ensa.notification_ms.KafkaConsumers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.notification_ms.Entity.Notification;
import ma.ensa.notification_ms.Entity.NotificationType;
import ma.ensa.notification_ms.Events.BookCreatedEvent;
import ma.ensa.notification_ms.Repository.NotificationRepository;
import ma.ensa.notification_ms.feign.UserClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookCreatedConsumer {

    private final NotificationRepository notificationRepository;

    private final UserClient  userClient;

    @KafkaListener(topics = "book-created-topic", groupId = "notification-group")
    public void consume(BookCreatedEvent event) {
        log.info("📩 Received BookCreatedEvent: {}", event);

        List<Long> ids = userClient.GetUsersIds();


        ids.forEach(id -> {
            Notification notif = Notification.builder()
                    .type(NotificationType.BOOK_CREATED)
                    .message(event.toString())
                    .createdAt(LocalDateTime.now())
                    .seen(false)
                    .userId(id)
                    .build();

            notificationRepository.save(notif);
        });

        log.info("✅ Created {} notifications for new book", ids.size());


    }
}

