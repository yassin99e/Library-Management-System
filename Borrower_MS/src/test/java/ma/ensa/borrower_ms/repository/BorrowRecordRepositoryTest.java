package ma.ensa.borrower_ms.repository;

import ma.ensa.borrower_ms.entity.BorrowRecord;
import ma.ensa.borrower_ms.entity.User;
import ma.ensa.borrower_ms.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class BorrowRecordRepositoryTest {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private BorrowRecord borrowRecord;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur pour le borrower
        user = User.builder()
                .username("yassin99e")
                .email("yassine.benakki@gmail.com")
                .role(Role.USER)
                .password("password123")
                .build();
        userRepository.save(user);

        // Créer un enregistrement de prêt
        borrowRecord = BorrowRecord.builder()
                .borrower(user)
                .bookId(1L)
                .borrowDate(LocalDate.now())
                .returnDate(null)
                .build();
        borrowRecordRepository.save(borrowRecord);
    }

    @Test
    void test_findByBorrowerIdAndBookIdAndReturnDateIsNull_success() {
        Optional<BorrowRecord> result = borrowRecordRepository
                .findByBorrowerIdAndBookIdAndReturnDateIsNull(user.getId(), 1L);

        assertTrue(result.isPresent());
        assertEquals(result.get().getBorrower().getId(), user.getId());
        assertEquals(result.get().getBookId(), 1L);
        assertNull(result.get().getReturnDate());
    }

    @Test
    void test_findByBorrowerIdAndBookIdAndReturnDateIsNull_failure() {
        Optional<BorrowRecord> result = borrowRecordRepository
                .findByBorrowerIdAndBookIdAndReturnDateIsNull(user.getId(), 999L);

        assertFalse(result.isPresent());
    }
}
