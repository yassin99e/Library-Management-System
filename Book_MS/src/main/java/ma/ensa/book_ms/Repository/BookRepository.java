package ma.ensa.book_ms.Repository;

import ma.ensa.book_ms.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String keyword);
    List<Book> findByAuthorContainingIgnoreCase(String keyword);
    boolean existsByTitleAndAuthor(String title,String author);

    Optional<Book> findById(Long id);
}