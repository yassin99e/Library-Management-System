package ma.ensa.book_ms.Web;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ensa.book_ms.DTO.BookRequestDTO;
import ma.ensa.book_ms.DTO.BookResponseDTO;
import ma.ensa.book_ms.Exceptions.*;
import ma.ensa.book_ms.Service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("deprecation")
    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookRequestDTO requestDTO;
    private BookResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new BookRequestDTO("Domain-Driven Design", "Eric Evans", 5);
        responseDTO = BookResponseDTO.builder()
                .id(1L)
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .availableCopies(5)
                .build();
    }

    // --- POST /api/books ---
    @Test
    void testSaveBook_Success() throws Exception {
        when(bookService.saveBook(any(BookRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Domain-Driven Design")));
    }

    @Test
    void testSaveBook_Duplicate() throws Exception {
        when(bookService.saveBook(any(BookRequestDTO.class)))
                .thenThrow(new DuplicateBookException(requestDTO.getTitle()));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("already exists")));
    }

    @Test
    void testSaveBook_InvalidData() throws Exception {
        // Title null, availableCopies < 0
        BookRequestDTO invalidRequest = new BookRequestDTO(null, "Eric Evans", -5);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/books/{id} ---
    @Test
    void testGetBookById_Success() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Domain-Driven Design")));
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        when(bookService.getBookById(99L)).thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("not found")));
    }

    // --- GET /api/books ---
    @Test
    void testGetAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Domain-Driven Design")));
    }

    // --- GET /api/books/available ---
    @Test
    void testGetAvailableBooks() throws Exception {
        when(bookService.getAvailableBooks()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].availableCopies", is(5)));
    }

    // --- PUT /api/books/{id}/increment ---
    @Test
    void testIncrementAvailableCopies_Success() throws Exception {
        BookResponseDTO updated = responseDTO.toBuilder().availableCopies(6).build();
        when(bookService.incrementAvailableCopies(1L)).thenReturn(updated);

        mockMvc.perform(put("/api/books/1/increment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies", is(6)));
    }

    @Test
    void testIncrementAvailableCopies_NotFound() throws Exception {
        when(bookService.incrementAvailableCopies(99L)).thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(put("/api/books/99/increment"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("not found")));
    }

    // --- PUT /api/books/{id}/decrement ---
    @Test
    void testDecrementAvailableCopies_Success() throws Exception {
        BookResponseDTO updated = responseDTO.toBuilder().availableCopies(4).build();
        when(bookService.decrementAvailableCopies(1L)).thenReturn(updated);

        mockMvc.perform(put("/api/books/1/decrement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies", is(4)));
    }

    @Test
    void testDecrementAvailableCopies_NoAvailableCopies() throws Exception {
        when(bookService.decrementAvailableCopies(1L)).thenThrow(new NoAvailableCopiesException(1L));

        mockMvc.perform(put("/api/books/1/decrement"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("No available copies")));
    }

    @Test
    void testDecrementAvailableCopies_NegativeCopies() throws Exception {
        when(bookService.decrementAvailableCopies(1L)).thenThrow(new NegativeCopiesException(1L));

        mockMvc.perform(put("/api/books/1/decrement"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("cannot have negative copies")));
    }



    // --- DELETE /api/books/{id} ---
    @Test
    void testDeleteBook_Success() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBook_NotFound() throws Exception {
        Mockito.doThrow(new BookNotFoundException(99L)).when(bookService).deleteBook(99L);

        mockMvc.perform(delete("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("not found")));
    }

    // --- GET /api/books/search/title ---
    @Test
    void testSearchByTitle() throws Exception {
        when(bookService.searchByTitle("domain")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/books/search/title")
                        .param("keyword", "domain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Domain-Driven Design")));
    }

    // --- GET /api/books/search/author ---
    @Test
    void testSearchByAuthor() throws Exception {
        when(bookService.searchByAuthor("evans")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/books/search/author")
                        .param("keyword", "evans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author", is("Eric Evans")));
    }
}
