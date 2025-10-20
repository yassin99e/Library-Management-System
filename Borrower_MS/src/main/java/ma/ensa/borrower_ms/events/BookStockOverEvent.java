package ma.ensa.borrower_ms.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookStockOverEvent {
    private Long bookId;
    private String title;
    private String author;
    private int availableCopies;
}
