package ma.ensa.borrower_ms.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.borrower_ms.events.BookBorrowedEvent;
import ma.ensa.borrower_ms.events.BookReturnedEvent;
import ma.ensa.borrower_ms.events.BookStockOverEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowEventProducer {

    private final KafkaTemplate<String, BookBorrowedEvent> borrowedKafkaTemplate;
    private final KafkaTemplate<String, BookReturnedEvent> returnedKafkaTemplate;
    private final KafkaTemplate<String, BookStockOverEvent> stockOverKafkaTemplate;

    private static final String BOOK_BORROWED_TOPIC = "book-borrowed-topic";
    private static final String BOOK_RETURNED_TOPIC = "book-returned-topic";
    private static final String BOOK_STOCK_OVER_TOPIC = "book-stock-over-topic";

    public void sendBookBorrowedEvent(BookBorrowedEvent event) {
        log.info("📚 Sending BookBorrowedEvent to Kafka -> {}", event);
        borrowedKafkaTemplate.send(BOOK_BORROWED_TOPIC, event);
    }

    public void sendBookReturnedEvent(BookReturnedEvent event) {
        log.info("📖 Sending BookReturnedEvent to Kafka -> {}", event);
        returnedKafkaTemplate.send(BOOK_RETURNED_TOPIC, event);
    }

    public void sendBookStockOverEvent(BookStockOverEvent event) {
        log.info("⚠️ Sending BookStockOverEvent to Kafka -> {}", event);
        stockOverKafkaTemplate.send(BOOK_STOCK_OVER_TOPIC, event);
    }
}