package ma.ensa.borrower_ms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;   // nullable
    private String email;  // nullable
    private String role;
}