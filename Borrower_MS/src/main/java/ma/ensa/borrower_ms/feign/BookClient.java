package ma.ensa.borrower_ms.feign;


import ma.ensa.borrower_ms.dto.BookResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "book-ms")  // registered name in Eureka
public interface BookClient {

    @GetMapping("/api/books/{id}")
    BookResponseDTO getBookById(@PathVariable Long id);

    @PutMapping("/api/books/{id}/increment")
    BookResponseDTO incrementAvailableCopies(@PathVariable Long id);

    @PutMapping("/api/books/{id}/decrement")
    BookResponseDTO decrementAvailableCopies(@PathVariable Long id);

    @GetMapping("/api/books/by-ids")
    List<BookResponseDTO> getBooksByIds(@RequestParam List<Long> ids);


}

