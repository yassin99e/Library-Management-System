package ma.ensa.borrower_ms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    private String username;   // nullable

    private String password;
}