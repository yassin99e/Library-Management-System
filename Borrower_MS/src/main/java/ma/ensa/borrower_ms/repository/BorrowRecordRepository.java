package ma.ensa.borrower_ms.repository;

import ma.ensa.borrower_ms.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    Optional<BorrowRecord> findByBorrowerIdAndBookIdAndReturnDateIsNull(Long borrowerId, Long bookId);


}
