package ma.ensa.borrower_ms.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@Builder
public class BorrowedBookDTO {
    private Long bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate; // seulement pour la 2e méthode

    public BorrowedBookDTO(Long bookId, LocalDate borrowDate) {
        this.bookId = bookId;
        this.borrowDate = borrowDate;
    }

    public BorrowedBookDTO(Long bookId, LocalDate borrowDate, LocalDate returnDate) {
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }


}
