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
public class BookBorrowedEvent {
    private Long bookId;
    private Long userId;
    private LocalDate borrowDate;

    @Override
    public String toString() {
        return "User " + userId + " borrowed book ID " + bookId + " on " + borrowDate;
    }


}
