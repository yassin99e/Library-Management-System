package ma.ensa.borrower_ms.web.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ensa.borrower_ms.dto.BookResponseDTO;
import ma.ensa.borrower_ms.entity.User;
import ma.ensa.borrower_ms.repository.BorrowRecordRepository;
import ma.ensa.borrower_ms.repository.UserRepository;
import ma.ensa.borrower_ms.feign.BookClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;



import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // utilisera application-test.properties avec H2
class BorrowRecordControllerComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @MockBean
    private BookClient bookClient; // mock seulement l'appel au microservice externe

    private User testUser;
    private BookResponseDTO testBook;

    @BeforeEach
    void setUp() {
        // Clear DB before each test

        borrowRecordRepository.deleteAll();
        userRepository.deleteAll();

        // Création d'un utilisateur de test
        testUser = User.builder()
                .username("john")
                .email("john@example.com")
                .password("secret123")
                .build();
        userRepository.save(testUser);

        // Book mocké
        testBook = BookResponseDTO.builder()
                .id(10L)
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .availableCopies(5)
                .build();

        // BookClient simule les appels vers book-ms
        when(bookClient.getBookById(anyLong())).thenReturn(testBook);
        when(bookClient.decrementAvailableCopies(anyLong())).thenReturn(testBook);
        when(bookClient.incrementAvailableCopies(anyLong())).thenReturn(testBook);
    }

    @Test
    void borrowBook_ShouldReturn200_WhenSameUser() throws Exception {
        mockMvc.perform(post("/api/borrow/{borrowerId}/{bookId}", testUser.getId(), 10L)
                        .header("X-User-Id", testUser.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void borrowBook_ShouldReturn403_WhenDifferentUser() throws Exception {
        mockMvc.perform(post("/api/borrow/{borrowerId}/{bookId}", 1, 10L)
                        .header("X-User-Id", 99L))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnBook_ShouldReturn200_WhenSameUser() throws Exception {
        // First borrow
        mockMvc.perform(post("/api/borrow/{borrowerId}/{bookId}", testUser.getId(), 10L)
                        .header("X-User-Id", testUser.getId()))
                .andExpect(status().isOk());

        // Then return
        mockMvc.perform(put("/api/borrow/{borrowerId}/{bookId}/return", testUser.getId(), 10L)
                        .header("X-User-Id", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowerId").value(testUser.getId()))
                .andExpect(jsonPath("$.bookId").value(10L))
                .andExpect(jsonPath("$.returnDate").exists());
    }
}
