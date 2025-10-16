package ma.ensa.borrower_ms.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ensa.borrower_ms.dto.BorrowRecordResponseDTO;
import ma.ensa.borrower_ms.service.BorrowRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BorrowRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowRecordService borrowRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    private BorrowRecordResponseDTO borrowRecordResponse;

    @BeforeEach
    void setUp() {
        borrowRecordResponse = BorrowRecordResponseDTO.builder()
                .id(1L)
                .bookId(10L)
                .borrowerId(1L)
                .borrowDate(LocalDate.now())
                .build();
    }

    // --- borrowBook() tests ---

    @Test
    void borrowBook_ShouldReturn200_WhenSameUser() throws Exception {
        when(borrowRecordService.borrowBook(anyLong(), anyLong())).thenReturn(borrowRecordResponse);

        mockMvc.perform(post("/api/borrow/1/10")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowerId").value(1L))
                .andExpect(jsonPath("$.bookId").value(10L));

        verify(borrowRecordService).borrowBook(1L, 10L);
    }

    @Test
    void borrowBook_ShouldReturn403_WhenDifferentUser() throws Exception {
        mockMvc.perform(post("/api/borrow/2/10")
                        .header("X-User-Id", "1"))
                .andExpect(status().isForbidden());

        verify(borrowRecordService, never()).borrowBook(anyLong(), anyLong());
    }

    @Test
    void borrowBook_ShouldReturn403_WhenMissingUserHeader() throws Exception {
        mockMvc.perform(post("/api/borrow/1/10"))
                .andExpect(status().isForbidden());

        verify(borrowRecordService, never()).borrowBook(anyLong(), anyLong());
    }

    // --- returnBook() tests ---

    @Test
    void returnBook_ShouldReturn200_WhenSameUser() throws Exception {
        borrowRecordResponse.setReturnDate(LocalDate.now());
        when(borrowRecordService.returnBook(anyLong(), anyLong())).thenReturn(borrowRecordResponse);

        mockMvc.perform(put("/api/borrow/1/10/return")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowerId").value(1L))
                .andExpect(jsonPath("$.bookId").value(10L))
                .andExpect(jsonPath("$.returnDate").exists());

        verify(borrowRecordService).returnBook(1L, 10L);
    }

    @Test
    void returnBook_ShouldReturn403_WhenDifferentUser() throws Exception {
        mockMvc.perform(put("/api/borrow/2/10/return")
                        .header("X-User-Id", "1"))
                .andExpect(status().isForbidden());

        verify(borrowRecordService, never()).returnBook(anyLong(), anyLong());
    }

    @Test
    void returnBook_ShouldReturn403_WhenMissingUserHeader() throws Exception {
        mockMvc.perform(put("/api/borrow/1/10/return"))
                .andExpect(status().isForbidden());

        verify(borrowRecordService, never()).returnBook(anyLong(), anyLong());
    }
}
