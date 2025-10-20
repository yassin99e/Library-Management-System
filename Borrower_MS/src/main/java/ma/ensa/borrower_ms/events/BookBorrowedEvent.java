package ma.ensa.borrower_ms.events;

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
}
