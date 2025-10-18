package ma.ensa.book_ms.Service;

import ma.ensa.book_ms.DTO.BookRequestDTO;
import ma.ensa.book_ms.DTO.BookResponseDTO;
import java.lang.Long;

import java.util.List;

public interface BookService {

    BookResponseDTO saveBook(BookRequestDTO bookRequestDTO);

    BookResponseDTO getBookById(Long id);

    List<BookResponseDTO> getAllBooks();

    List<BookResponseDTO> getAvailableBooks();

    BookResponseDTO incrementAvailableCopies(Long id);

    BookResponseDTO decrementAvailableCopies(Long id);

    void deleteBook(Long id);

    List<BookResponseDTO> searchByTitle(String keyword);

    List<BookResponseDTO> searchByAuthor(String keyword);

    List<BookResponseDTO> getBooksByIds(List<Long> ids);

}
