package ma.ensa.borrower_ms.repository;

import ma.ensa.borrower_ms.dto.BorrowedBookDTO;
import ma.ensa.borrower_ms.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    Optional<BorrowRecord> findByBorrowerIdAndBookIdAndReturnDateIsNull(Long borrowerId, Long bookId);


    @Query("SELECT new ma.ensa.borrower_ms.dto.BorrowedBookDTO(b.bookId, b.borrowDate) " +
            "FROM BorrowRecord b " +
            "WHERE b.borrower.id = :borrowerId AND b.returnDate IS NULL " +
            "ORDER BY b.borrowDate ASC")
    List<BorrowedBookDTO> findCurrentlyBorrowedBookIdsByUser(@Param("borrowerId") Long borrowerId);

    @Query("SELECT new ma.ensa.borrower_ms.dto.BorrowedBookDTO(b.bookId, b.borrowDate, b.returnDate) " +
            "FROM BorrowRecord b " +
            "WHERE b.borrower.id = :borrowerId AND b.returnDate IS NOT NULL " +
            "ORDER BY b.borrowDate ASC")
    List<BorrowedBookDTO> findReturnedBorrowedBookIdsByUser(@Param("borrowerId") Long borrowerId);

}
