package ma.ensa.book_ms.Service;

import ma.ensa.book_ms.DTO.BookRequestDTO;
import ma.ensa.book_ms.DTO.BookResponseDTO;
import ma.ensa.book_ms.Entity.Book;
import ma.ensa.book_ms.Mapper.BookMapper;
import ma.ensa.book_ms.Repository.BookRepository;
import ma.ensa.book_ms.Exceptions.BookNotFoundException;
import ma.ensa.book_ms.Exceptions.DuplicateBookException;
import ma.ensa.book_ms.Exceptions.NoAvailableCopiesException;
import ma.ensa.book_ms.events.BookCreatedEvent;
import ma.ensa.book_ms.kafka.BookProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookProducer bookProducer;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper,BookProducer bookProducer) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookProducer = bookProducer;
    }

    @Override
    public BookResponseDTO saveBook(BookRequestDTO bookRequestDTO) {
        if (bookRepository.existsByTitleAndAuthor(bookRequestDTO.getTitle(), bookRequestDTO.getAuthor())) {
            throw new DuplicateBookException(bookRequestDTO.getTitle());
        }
        Book book = bookMapper.toEntity(bookRequestDTO);
        Book saved = bookRepository.save(book);

        // publish event to Kafka
        BookCreatedEvent event = BookCreatedEvent.builder().
                title(saved.getTitle()).
                author(saved.getAuthor())
                .availableCopies(saved.getAvailableCopies())
                .build();
        bookProducer.sendBookCreatedEvent(event);

        return bookMapper.toResponseDTO(saved);
    }

    @Override
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return bookMapper.toResponseDTO(book);
    }

    @Override
    public List<BookResponseDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return bookMapper.toResponseDTOList(books);
    }

    @Override
    public List<BookResponseDTO> getAvailableBooks() {
        List<Book> books = bookRepository.findAll()
                .stream()
                .filter(b -> b.getAvailableCopies() > 0)
                .toList();
        return bookMapper.toResponseDTOList(books);
    }

    @Override
    public BookResponseDTO incrementAvailableCopies(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return bookMapper.toResponseDTO(bookRepository.save(book));
    }

    @Override
    public BookResponseDTO decrementAvailableCopies(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        if (book.getAvailableCopies() == 0) {
            throw new NoAvailableCopiesException(id);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return bookMapper.toResponseDTO(bookRepository.save(book));
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookResponseDTO> searchByTitle(String keyword) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(keyword);
        return bookMapper.toResponseDTOList(books);
    }

    @Override
    public List<BookResponseDTO> searchByAuthor(String keyword) {
        List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(keyword);
        return bookMapper.toResponseDTOList(books);
    }

    @Override
    public List<BookResponseDTO> getBooksByIds(List<Long> ids) {
        return bookMapper.toResponseDTOList(bookRepository.findByIdIn(ids));
    }
}
