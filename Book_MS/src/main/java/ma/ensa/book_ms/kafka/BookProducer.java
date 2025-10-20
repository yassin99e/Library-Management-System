package ma.ensa.book_ms.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.book_ms.events.BookCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookProducer {

    private final KafkaTemplate<String, BookCreatedEvent> kafkaTemplate;

    private static final String TOPIC = "book-created-topic";

    public void sendBookCreatedEvent(BookCreatedEvent event) {
        log.info("📘 Sending BookCreatedEvent to Kafka -> {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
