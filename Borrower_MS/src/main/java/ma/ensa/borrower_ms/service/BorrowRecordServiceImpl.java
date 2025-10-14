package ma.ensa.borrower_ms.service;

import lombok.RequiredArgsConstructor;
import ma.ensa.borrower_ms.exception.*;
import ma.ensa.borrower_ms.feign.BookClient;
import ma.ensa.borrower_ms.dto.BookResponseDTO;
import ma.ensa.borrower_ms.dto.BorrowRecordResponseDTO;
import ma.ensa.borrower_ms.entity.BorrowRecord;
import ma.ensa.borrower_ms.mapper.BorrowRecordMapper;
import ma.ensa.borrower_ms.repository.BorrowRecordRepository;
import ma.ensa.borrower_ms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BookClient bookClient;
    private final BorrowRecordMapper borrowRecordMapper;


    @Override
    public BorrowRecordResponseDTO borrowBook(Long borrowerId, Long bookId) {
        // 0. Check if borrower exists
        userRepository.findById(borrowerId)
                .orElseThrow(() -> new UserNotFoundException());

        // 1. Check if borrower already borrowed the book and hasn't returned
        borrowRecordRepository.findByBorrowerIdAndBookIdAndReturnDateIsNull(borrowerId, bookId)
                .ifPresent(record -> {
                    throw new AlreadyBorrowedException(borrowerId, bookId);
                });

        // 2. Check if book exists
        BookResponseDTO book;
        try {
            book = bookClient.getBookById(bookId);
        } catch (Exception e) {
            throw new BookNotFoundException(bookId);
        }

        // 3. Check availability
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(bookId);
        }

        // 4. Decrement available copies
        bookClient.decrementAvailableCopies(bookId);

        // 5. Save record
        BorrowRecord record = BorrowRecord.builder()
                .borrower(userRepository.findById(borrowerId).get())
                .bookId(bookId)
                .borrowDate(LocalDate.from(LocalDateTime.now()))
                .build();

        BorrowRecord saved = borrowRecordRepository.save(record);

        return borrowRecordMapper.toResponseDTO(saved);
    }




    @Override
    public BorrowRecordResponseDTO returnBook(Long borrowerId, Long bookId) {
        // 1. Find borrow record
        BorrowRecord record = borrowRecordRepository
                .findByBorrowerIdAndBookIdAndReturnDateIsNull(borrowerId, bookId)
                .orElseThrow(() -> new BorrowRecordNotFoundException(borrowerId, bookId));

        // 2. Mark as returned
        record.setReturnDate(LocalDate.from(LocalDateTime.now()));
        BorrowRecord saved = borrowRecordRepository.save(record);

        // 3. Increment available copies
        bookClient.incrementAvailableCopies(bookId);

        return borrowRecordMapper.toResponseDTO(saved);
    }
}
