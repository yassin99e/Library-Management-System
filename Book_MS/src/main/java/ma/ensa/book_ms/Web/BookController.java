package ma.ensa.book_ms.Web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ma.ensa.book_ms.DTO.BookRequestDTO;
import ma.ensa.book_ms.DTO.BookResponseDTO;
import ma.ensa.book_ms.Service.BookService;
import ma.ensa.book_ms.Exceptions.ForbiddenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ✅ Only ADMIN can create a book
    @PostMapping
    public ResponseEntity<BookResponseDTO> saveBook(@Valid @RequestBody BookRequestDTO bookRequestDTO,
                                                    HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ForbiddenException("Access denied: Only ADMINs can add new books.");
        }
        BookResponseDTO savedBook = bookService.saveBook(bookRequestDTO);
        return ResponseEntity.status(201).body(savedBook);
    }

    // ✅ Public (authenticated users)
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/available")
    public ResponseEntity<List<BookResponseDTO>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    @PutMapping("/{id}/increment")
    public ResponseEntity<BookResponseDTO> incrementAvailableCopies(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.incrementAvailableCopies(id));
    }

    @PutMapping("/{id}/decrement")
    public ResponseEntity<BookResponseDTO> decrementAvailableCopies(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.decrementAvailableCopies(id));
    }

    // ✅ Only ADMIN can delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ForbiddenException("Access denied: Only ADMINs can delete books.");
        }
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<BookResponseDTO>> searchByTitle(@RequestParam String keyword) {
        return ResponseEntity.ok(bookService.searchByTitle(keyword));
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<BookResponseDTO>> searchByAuthor(@RequestParam String keyword) {
        return ResponseEntity.ok(bookService.searchByAuthor(keyword));
    }
}
