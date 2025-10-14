package ma.ensa.borrower_ms.dto;

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
