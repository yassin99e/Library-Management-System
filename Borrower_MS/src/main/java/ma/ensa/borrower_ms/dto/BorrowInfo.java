package ma.ensa.borrower_ms.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BorrowInfo {
    private BorrowedBookDTO  borrowedBookdto;
    private BookResponseDTO  bookResponseDTO;

}
