package ma.ensa.book_ms.Service;

import ma.ensa.book_ms.DTO.BookRequestDTO;
import ma.ensa.book_ms.DTO.BookResponseDTO;
import ma.ensa.book_ms.Entity.Book;
import ma.ensa.book_ms.Mapper.BookMapper;
import ma.ensa.book_ms.Repository.BookRepository;
import ma.ensa.book_ms.Exceptions.BookNotFoundException;
import ma.ensa.book_ms.Exceptions.DuplicateBookException;
import ma.ensa.book_ms.Exceptions.NoAvailableCopiesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookRequestDTO bookRequestDTO;
    private BookResponseDTO bookResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        book = Book.builder()
                .id(1L)
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .availableCopies(5)
                .build();

        bookRequestDTO = BookRequestDTO.builder()
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .availableCopies(5)
                .build();

        bookResponseDTO = BookResponseDTO.builder()
                .id(1L)
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .availableCopies(5)
                .build();
    }

    @Test
    void testSaveBook_Success() {
        when(bookRepository.existsByTitleAndAuthor(bookRequestDTO.getTitle(), bookRequestDTO.getAuthor()))
                .thenReturn(false);
        when(bookMapper.toEntity(bookRequestDTO)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponseDTO(book)).thenReturn(bookResponseDTO);

        BookResponseDTO result = bookService.saveBook(bookRequestDTO);

        assertNotNull(result);
        assertEquals(bookResponseDTO.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testSaveBook_Duplicate() {
        when(bookRepository.existsByTitleAndAuthor(bookRequestDTO.getTitle(), bookRequestDTO.getAuthor()))
                .thenReturn(true);

        assertThrows(DuplicateBookException.class, () -> bookService.saveBook(bookRequestDTO));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void testGetBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponseDTO(book)).thenReturn(bookResponseDTO);

        BookResponseDTO result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Domain-Driven Design", result.getTitle());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        when(bookMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList(bookResponseDTO));

        List<BookResponseDTO> result = bookService.getAllBooks();

        assertEquals(1, result.size());
    }

    @Test
    void testGetAvailableBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        when(bookMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList(bookResponseDTO));

        List<BookResponseDTO> result = bookService.getAvailableBooks();

        assertEquals(1, result.size());
    }

    @Test
    void testIncrementAvailableCopies_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toResponseDTO(any(Book.class))).thenReturn(bookResponseDTO);

        BookResponseDTO result = bookService.incrementAvailableCopies(1L);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testDecrementAvailableCopies_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toResponseDTO(any(Book.class))).thenReturn(bookResponseDTO);

        BookResponseDTO result = bookService.decrementAvailableCopies(1L);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testDecrementAvailableCopies_NoCopies() {
        book.setAvailableCopies(0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(NoAvailableCopiesException.class, () -> bookService.decrementAvailableCopies(1L));
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> bookService.deleteBook(1L));
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void testSearchByTitle_Found() {
        when(bookRepository.findByTitleContainingIgnoreCase("Domain")).thenReturn(Arrays.asList(book));
        when(bookMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList(bookResponseDTO));

        List<BookResponseDTO> result = bookService.searchByTitle("Domain");

        assertEquals(1, result.size());
    }

    @Test
    void testSearchByTitle_NotFound() {
        when(bookRepository.findByTitleContainingIgnoreCase("Unknown")).thenReturn(Collections.emptyList());
        when(bookMapper.toResponseDTOList(anyList())).thenReturn(Collections.emptyList());

        List<BookResponseDTO> result = bookService.searchByTitle("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchByAuthor_Found() {
        when(bookRepository.findByAuthorContainingIgnoreCase("Eric")).thenReturn(Arrays.asList(book));
        when(bookMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList(bookResponseDTO));

        List<BookResponseDTO> result = bookService.searchByAuthor("Eric");

        assertEquals(1, result.size());
    }

    @Test
    void testSearchByAuthor_NotFound() {
        when(bookRepository.findByAuthorContainingIgnoreCase("Unknown")).thenReturn(Collections.emptyList());
        when(bookMapper.toResponseDTOList(anyList())).thenReturn(Collections.emptyList());

        List<BookResponseDTO> result = bookService.searchByAuthor("Unknown");

        assertTrue(result.isEmpty());
    }
}
