package ma.ensa.book_ms.Repository;

import ma.ensa.book_ms.Entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book book;

    // Each time we want to execute a test method this function will execute
    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("Domain-Driven Design");
        book.setAuthor("Eric Evans");
        book.setAvailableCopies(4);
        bookRepository.save(book);
    }

    // tester la recherche d'un Book par son titre
    @Test
    void testFindByTitleContainingIgnoreCase() {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase("domain");
        assertFalse(books.isEmpty());
        assertEquals("Domain-Driven Design", books.get(0).getTitle());
    }

    @Test
    void testFindByTitleContainingIgnoreCaseNotFound() {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase("nonexistent");
        assertTrue(books.isEmpty(), "Expected empty list when title does not match");
    }

    // tester la recherche d'un Book par son auteur
    @Test
    void testFindByAuthorContainingIgnoreCase() {
        List<Book> books = bookRepository.findByAuthorContainingIgnoreCase("eric");
        assertFalse(books.isEmpty());
        assertEquals("Eric Evans", books.get(0).getAuthor());
    }

    @Test
    void testFindByAuthorContainingIgnoreCaseNotFound() {
        List<Book> books = bookRepository.findByAuthorContainingIgnoreCase("nonexistent");
        assertTrue(books.isEmpty(), "Expected empty list when title does not match");
    }


    // tester l'existence d'un Book par son auteur et titre
    @Test
    void testExistsByTitleAndAuthor() {
        boolean exists = bookRepository.existsByTitleAndAuthor("Domain-Driven Design", "Eric Evans");
        assertTrue(exists);

        boolean notExists = bookRepository.existsByTitleAndAuthor("Some Book", "Some Author");
        assertFalse(notExists);
    }

    // tester trouver un book par son id sachant qu'il existe
    @Test
    void testFindByIdSuccess() {
        Optional<Book> found = bookRepository.findById(book.getId());
        assertTrue(found.isPresent());
        assertEquals("Domain-Driven Design", found.get().getTitle());
    }

    // tester trouver un book par son id sachant qu'il n'existe pas
    @Test
    void testFindByIdNotFound() {
        Optional<Book> found = bookRepository.findById(999L);
        assertTrue(found.isEmpty()); // no exception, just empty Optional
    }
}
