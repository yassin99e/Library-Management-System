package ma.ensa.book_ms.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookResponseDTO {
    private Long id;
    private String title;
    private String author;
    private int availableCopies;
}
