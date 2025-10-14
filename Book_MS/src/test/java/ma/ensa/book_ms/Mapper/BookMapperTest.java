package ma.ensa.book_ms.Mapper;

import ma.ensa.book_ms.DTO.BookRequestDTO;
import ma.ensa.book_ms.DTO.BookResponseDTO;
import ma.ensa.book_ms.Entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    private BookMapper bookMapper;

    @BeforeEach
    void setUp() {
        bookMapper = new BookMapper();
    }

    @Test
    void testToEntity() {
        BookRequestDTO dto = new BookRequestDTO();
        dto.setTitle("Clean Code");
        dto.setAuthor("Robert Martin");
        dto.setAvailableCopies(5);

        Book book = bookMapper.toEntity(dto);

        assertNotNull(book);
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals(5, book.getAvailableCopies());
    }

    @Test
    void testToResponseDTO() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setAvailableCopies(3);

        BookResponseDTO dto = bookMapper.toResponseDTO(book);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Effective Java", dto.getTitle());
        assertEquals("Joshua Bloch", dto.getAuthor());
        assertEquals(3, dto.getAvailableCopies());
    }

    @Test
    void testToResponseDTOList() {
        Book book1 = new Book(1L, "Book A", "Author A", 2);
        Book book2 = new Book(2L, "Book B", "Author B", 5);

        List<Book> books = Arrays.asList(book1, book2);
        List<BookResponseDTO> dtos = bookMapper.toResponseDTOList(books);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Book A", dtos.get(0).getTitle());
        assertEquals("Book B", dtos.get(1).getTitle());
    }
}
