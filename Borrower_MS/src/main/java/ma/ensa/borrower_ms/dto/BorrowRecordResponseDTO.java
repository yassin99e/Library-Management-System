package ma.ensa.borrower_ms.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecordResponseDTO {
    private Long id;
    private Long bookId;
    private Long borrowerId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
