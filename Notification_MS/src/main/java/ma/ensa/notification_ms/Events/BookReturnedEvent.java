package ma.ensa.notification_ms.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookReturnedEvent {
    private Long bookId;
    private Long userId;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    @Override
    public String toString() {
        return "User " + userId + " returned book ID " + bookId +
                " on " + returnDate + "which was borrowed at "+borrowDate;
    }
}