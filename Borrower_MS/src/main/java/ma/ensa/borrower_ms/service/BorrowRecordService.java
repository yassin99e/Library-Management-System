package ma.ensa.borrower_ms.service;

import ma.ensa.borrower_ms.dto.BorrowRecordResponseDTO;

public interface BorrowRecordService {

    BorrowRecordResponseDTO borrowBook(Long borrowerId, Long bookId);

    BorrowRecordResponseDTO returnBook(Long borrowerId, Long bookId);
}
