package ma.ensa.borrower_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserLoginResponseDTO {
    private String token;
    private String username;
}
