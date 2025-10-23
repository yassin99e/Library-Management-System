/*
package ma.ensa.borrower_ms.service;

import ma.ensa.borrower_ms.dto.BookResponseDTO;
import ma.ensa.borrower_ms.dto.BorrowRecordResponseDTO;
import ma.ensa.borrower_ms.entity.BorrowRecord;
import ma.ensa.borrower_ms.entity.User;
import ma.ensa.borrower_ms.exception.*;
import ma.ensa.borrower_ms.feign.BookClient;
import ma.ensa.borrower_ms.mapper.BorrowRecordMapper;
import ma.ensa.borrower_ms.repository.BorrowRecordRepository;
import ma.ensa.borrower_ms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowRecordServiceImplTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookClient bookClient;

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @InjectMocks
    private BorrowRecordServiceImpl borrowRecordService;

    private User borrower;
    private BookResponseDTO availableBook;
    private BookResponseDTO unavailableBook;
    private BorrowRecord borrowRecord;
    private BorrowRecordResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        borrower = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .build();

        availableBook = BookResponseDTO.builder()
                .id(2L)
                .title("Clean Code")
                .availableCopies(3)
                .build();

        unavailableBook = availableBook.toBuilder()
                .availableCopies(0)
                .build();

        borrowRecord = BorrowRecord.builder()
                .id(10L)
                .borrower(borrower)
                .bookId(2L)
                .borrowDate(LocalDate.now())
                .build();

        responseDTO = BorrowRecordResponseDTO.builder()
                .id(10L)
                .bookId(2L)
                .borrowerId(1L)
                .borrowDate(LocalDate.now())
                .build();
    }

    // --- borrowBook tests ---

    @Test
    void borrowBook_ShouldSaveRecord_WhenAllValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(borrowRecordRepository.findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 2L))
                .thenReturn(Optional.empty());
        when(bookClient.getBookById(2L)).thenReturn(availableBook);
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(borrowRecord);
        when(borrowRecordMapper.toResponseDTO(any())).thenReturn(responseDTO);

        BorrowRecordResponseDTO result = borrowRecordService.borrowBook(1L, 2L);

        assertNotNull(result);
        assertEquals(2L, result.getBookId());
        verify(bookClient).decrementAvailableCopies(2L);
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
    }

    @Test
    void borrowBook_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> borrowRecordService.borrowBook(1L, 2L));
        verifyNoInteractions(bookClient);
    }

    @Test
    void borrowBook_ShouldThrow_WhenAlreadyBorrowed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(borrowRecordRepository.findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 2L))
                .thenReturn(Optional.of(borrowRecord));

        assertThrows(AlreadyBorrowedException.class, () -> borrowRecordService.borrowBook(1L, 2L));
        verifyNoInteractions(bookClient);
    }

    @Test
    void borrowBook_ShouldThrow_WhenBookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(borrowRecordRepository.findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 2L))
                .thenReturn(Optional.empty());
        when(bookClient.getBookById(2L)).thenThrow(new RuntimeException("not found"));

        assertThrows(BookNotFoundException.class, () -> borrowRecordService.borrowBook(1L, 2L));
    }

    @Test
    void borrowBook_ShouldThrow_WhenBookUnavailable() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(borrowRecordRepository.findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 2L))
                .thenReturn(Optional.empty());
        when(bookClient.getBookById(2L)).thenReturn(unavailableBook);

        assertThrows(BookNotAvailableException.class, () -> borrowRecordService.borrowBook(1L, 2L));
        verify(bookClient, never()).decrementAvailableCopies(anyLong());
    }

    // --- returnBook tests ---

    @Test
    void returnBook_ShouldMarkAsReturned_WhenValid() {
        when(borrowRecordRepository.findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 2L))
                .thenReturn(Optional.of(borrowRecord));
        when(borrowRecordRepository.save(any())).thenReturn(borrowRecord);
        when(borrowRecordMapper.toResponseDTO(any())).thenReturn(responseDTO);

        BorrowRecordResponseDTO result = borrowRecordService.returnBook(1L, 2L);

        assertNotNull(result);
        assertEquals(2L, result.getBookId());
        verify(bookClient).incrementAvailableCopies(2L);
    }

    @Test
    void returnBook_ShouldThrow_WhenBorrowRecordNotFound() {
        when(borrowRecordRepository.findByBorrowerIdAndBookIdAndReturnDateIsNull(1L, 2L))
                .thenReturn(Optional.empty());

        assertThrows(BorrowRecordNotFoundException.class, () -> borrowRecordService.returnBook(1L, 2L));
        verify(bookClient, never()).incrementAvailableCopies(anyLong());
    }
}


 */