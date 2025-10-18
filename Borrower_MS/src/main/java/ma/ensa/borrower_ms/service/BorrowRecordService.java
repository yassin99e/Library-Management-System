package ma.ensa.borrower_ms.service;

import ma.ensa.borrower_ms.dto.BookResponseDTO;
import ma.ensa.borrower_ms.dto.BorrowInfo;
import ma.ensa.borrower_ms.dto.BorrowRecordResponseDTO;
import ma.ensa.borrower_ms.dto.BorrowResponseDTO;

import java.util.List;

public interface BorrowRecordService {

    BorrowRecordResponseDTO borrowBook(Long borrowerId, Long bookId);

    BorrowRecordResponseDTO returnBook(Long borrowerId, Long bookId);

    List<BorrowResponseDTO> findCurrentlyBorrowedBooksByUser(Long borrowerId);

    List<BorrowResponseDTO> findReturnedBorrowedBookIdsByUser(Long borrowerId);




}
