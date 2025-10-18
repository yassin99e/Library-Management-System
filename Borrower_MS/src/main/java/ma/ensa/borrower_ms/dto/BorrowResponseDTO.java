package ma.ensa.borrower_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BorrowResponseDTO {
    // Book info
    private Long bookId;
    private String title;
    private String author;
    private int availableCopies;

    // Borrow info
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
