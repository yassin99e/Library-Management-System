package ma.ensa.book_ms.Web.Component;

import ma.ensa.book_ms.DTO.BookRequestDTO;
import ma.ensa.book_ms.DTO.BookResponseDTO;
import ma.ensa.book_ms.Entity.Book;
import ma.ensa.book_ms.Repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookControllerComponentTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    // --- POST /api/books ---
    @Test
    void testSaveBook_Success() {
        BookRequestDTO request = new BookRequestDTO("Domain-Driven Design", "Eric Evans", 5);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "ADMIN");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BookRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<BookResponseDTO> response = restTemplate
                .postForEntity("/api/books", entity, BookResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertEquals("Domain-Driven Design", response.getBody().getTitle());
        assertThat(bookRepository.existsByTitleAndAuthor("Domain-Driven Design", "Eric Evans")).isTrue();
    }

    @Test
    void testSaveBook_ForbiddenForNonAdmin() {
        BookRequestDTO request = new BookRequestDTO("Clean Code", "Robert Martin", 3);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "USER");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BookRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/books", entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertThat(response.getBody()).contains("Access denied: Only ADMINs can add new books.");
    }

    @Test
    void testSaveBook_InvalidData() {
        BookRequestDTO invalid = new BookRequestDTO(null, "Eric Evans", -1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "ADMIN");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BookRequestDTO> entity = new HttpEntity<>(invalid, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/books", entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // --- GET /api/books/{id} ---
    @Test
    void testGetBookById_Success() {
        Book saved = bookRepository.save(new Book(null,"Clean Architecture", "Robert Martin", 5));

        ResponseEntity<BookResponseDTO> response =
                restTemplate.getForEntity("/api/books/" + saved.getId(), BookResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().getTitle()).isEqualTo("Clean Architecture");
    }

    @Test
    void testGetBookById_NotFound() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/books/9999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- GET /api/books ---
    @Test
    void testGetAllBooks() {
        bookRepository.saveAll(List.of(
                new Book(null, "Book 1", "Author 1", 2),
                new Book(null, "Book 2", "Author 2", 3)
        ));

        ResponseEntity<BookResponseDTO[]> response =
                restTemplate.getForEntity("/api/books", BookResponseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(2);
    }

    // --- GET /api/books/available ---
    @Test
    void testGetAvailableBooks() {
        bookRepository.save(new Book(null, "Book Available", "Author A",4 ));

        ResponseEntity<BookResponseDTO[]> response =
                restTemplate.getForEntity("/api/books/available", BookResponseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()[0].getAvailableCopies()).isEqualTo(4);
    }

    // --- PUT /api/books/{id}/increment ---
    @Test
    void testIncrementAvailableCopies_Success() {
        Book saved = bookRepository.save(new Book(null, "Spring Boot", "Josh Long", 3));

        ResponseEntity<BookResponseDTO> response =
                restTemplate.exchange("/api/books/" + saved.getId() + "/increment",
                        HttpMethod.PUT, null, BookResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().getAvailableCopies()).isEqualTo(4);
    }

    @Test
    void testIncrementAvailableCopies_NotFound() {
        ResponseEntity<String> response =
                restTemplate.exchange("/api/books/999/increment",
                        HttpMethod.PUT, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- PUT /api/books/{id}/decrement ---
    @Test
    void testDecrementAvailableCopies_Success() {
        Book saved = bookRepository.save(new Book(null, "DDD", "Eric Evans", 3));

        ResponseEntity<BookResponseDTO> response =
                restTemplate.exchange("/api/books/" + saved.getId() + "/decrement",
                        HttpMethod.PUT, null, BookResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().getAvailableCopies()).isEqualTo(2);
    }

    // --- DELETE /api/books/{id} ---
    @Test
    void testDeleteBook_Success() {
        Book saved = bookRepository.save(new Book(null, "Delete Me", "Someone", 2));

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "ADMIN");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response =
                restTemplate.exchange("/api/books/" + saved.getId(),
                        HttpMethod.DELETE, entity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThat(bookRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void testDeleteBook_ForbiddenForNonAdmin() {
        Book saved = bookRepository.save(new Book(null, "Should Not Delete", "User", 1));

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "USER");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange("/api/books/" + saved.getId(),
                        HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // --- SEARCH ---
    @Test
    void testSearchByTitle() {
        bookRepository.save(new Book(null, "Domain Driven Design", "Eric Evans", 3));

        ResponseEntity<BookResponseDTO[]> response =
                restTemplate.getForEntity("/api/books/search/title?keyword=domain", BookResponseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()[0].getTitle()).containsIgnoringCase("domain");
    }

    @Test
    void testSearchByAuthor() {
        bookRepository.save(new Book(null, "Book A", "Robert Martin", 2));

        ResponseEntity<BookResponseDTO[]> response =
                restTemplate.getForEntity("/api/books/search/author?keyword=robert", BookResponseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()[0].getAuthor()).contains("Robert");
    }
}
