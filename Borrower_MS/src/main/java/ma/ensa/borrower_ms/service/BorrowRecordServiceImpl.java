package ma.ensa.borrower_ms.service;

import lombok.RequiredArgsConstructor;
import ma.ensa.borrower_ms.dto.*;
import ma.ensa.borrower_ms.events.BookBorrowedEvent;
import ma.ensa.borrower_ms.events.BookReturnedEvent;
import ma.ensa.borrower_ms.events.BookStockOverEvent;
import ma.ensa.borrower_ms.exception.*;
import ma.ensa.borrower_ms.feign.BookClient;
import ma.ensa.borrower_ms.entity.BorrowRecord;
import ma.ensa.borrower_ms.kafka.BorrowEventProducer;
import ma.ensa.borrower_ms.mapper.BorrowRecordMapper;
import ma.ensa.borrower_ms.repository.BorrowRecordRepository;
import ma.ensa.borrower_ms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BookClient bookClient;
    private final BorrowRecordMapper borrowRecordMapper;
    private final BorrowEventProducer borrowEventProducer;


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

        // We'll implement the events here :

        // Book Borrowed event :
        BookBorrowedEvent bookBorrowedEvent = BookBorrowedEvent.builder()
                .bookId(bookId)
                .userId(borrowerId)
                .borrowDate(saved.getBorrowDate())
                .build();

        // we will send the event to the producer :
        borrowEventProducer.sendBookBorrowedEvent(bookBorrowedEvent);


        // Book Stock Over Event :
        if (book.getAvailableCopies() == 1){
            BookStockOverEvent stockOverEvent = BookStockOverEvent.builder()
                    .bookId(bookId)
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .availableCopies(book.getAvailableCopies()-1)
                    .build();
            borrowEventProducer.sendBookStockOverEvent(stockOverEvent);
        }

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

        // Implement the Book Return Event :
        BookReturnedEvent bookReturnedEvent = BookReturnedEvent.builder()
                .bookId(saved.getBookId())
                .userId(saved.getBorrower().getId())
                .borrowDate(saved.getBorrowDate())
                .returnDate(saved.getReturnDate())
                .build();

        borrowEventProducer.sendBookReturnedEvent(bookReturnedEvent);

        return borrowRecordMapper.toResponseDTO(saved);
    }


    @Override
    public List<BorrowResponseDTO> findCurrentlyBorrowedBooksByUser(Long borrowerId) {
        userRepository.findById(borrowerId)
                .orElseThrow(() -> new UserNotFoundException());

        List<BorrowedBookDTO> borrowedBookDTOS = borrowRecordRepository.findCurrentlyBorrowedBookIdsByUser(borrowerId);
        return combineBorrowedBooksWithDetails(borrowedBookDTOS);
    }

    @Override
    public List<BorrowResponseDTO> findReturnedBorrowedBookIdsByUser(Long borrowerId) {
        userRepository.findById(borrowerId)
                .orElseThrow(() -> new UserNotFoundException());

        List<BorrowedBookDTO> borrowedBookDTOS = borrowRecordRepository.findReturnedBorrowedBookIdsByUser(borrowerId);
        return combineBorrowedBooksWithDetails(borrowedBookDTOS);
    }


    private List<BorrowResponseDTO> combineBorrowedBooksWithDetails(List<BorrowedBookDTO> borrowedBooks) {
        List<Long> bookIds = borrowedBooks.stream()
                .map(BorrowedBookDTO::getBookId)
                .toList();

        if (bookIds.isEmpty()) {
            return List.of();
        }

        Map<Long, BookResponseDTO> bookMap = bookClient.getBooksByIds(bookIds).stream()
                .collect(Collectors.toMap(BookResponseDTO::getId, Function.identity()));

        return borrowedBooks.stream()
                .map(borrowedBook -> {
                    BookResponseDTO book = bookMap.get(borrowedBook.getBookId());
                    return BorrowResponseDTO.builder()
                            .bookId(book.getId())
                            .title(book.getTitle())
                            .author(book.getAuthor())
                            .availableCopies(book.getAvailableCopies())
                            .borrowDate(borrowedBook.getBorrowDate())
                            .returnDate(borrowedBook.getReturnDate())
                            .build();
                })
                .toList();
    }


}
