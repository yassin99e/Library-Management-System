package ma.ensa.borrower_ms.web.Component;


import ma.ensa.borrower_ms.dto.*;
import ma.ensa.borrower_ms.entity.User;
import ma.ensa.borrower_ms.entity.Role;
import ma.ensa.borrower_ms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerComponentTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder ;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // --- REGISTER ---
    @Test
    void registerUser_ShouldReturn201() {
        UserRegisterDTO registerDTO = UserRegisterDTO.builder()
                .username("john")
                .email("john@example.com")
                .password("secret123")
                .build();

        ResponseEntity<UserResponseDTO> response = restTemplate.postForEntity(
                "/api/users/register", registerDTO, UserResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("john");
        assertThat(userRepository.existsByUsername("john")).isTrue();
    }

    // --- LOGIN ---
    @Test
    void loginUser_ShouldReturn200() {
        // On enregistre d'abord l'utilisateur dans la base
        User user = User.builder()
                .username("john")
                .email("john@example.com")
                .password(passwordEncoder.encode("secret123"))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("john@example.com")
                .password("secret123")
                .build();

        ResponseEntity<UserLoginResponseDTO> response =
                restTemplate.postForEntity("/api/users/login", loginDTO, UserLoginResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("john");
        assertThat(response.getBody().getToken()).isNotEmpty();
    }

    // --- GET ALL USERS (ADMIN only) ---
    @Test
    void getAllUsers_ShouldReturn200_WhenAdmin() {
        userRepository.save(User.builder().username("john").email("john@example.com").password("123").role(Role.USER).build());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "ADMIN");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserResponseDTO[]> response =
                restTemplate.exchange("/api/users", HttpMethod.GET, entity, UserResponseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getUsername()).isEqualTo("john");
    }

    @Test
    void getAllUsers_ShouldReturn403_WhenNotAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "USER");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange("/api/users", HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // --- GET USER BY ID ---
    @Test
    void getUserById_ShouldReturn200_WhenAdmin() {
        User user = userRepository.save(User.builder().username("john").email("john@example.com").password("123").role(Role.USER).build());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "ADMIN");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserResponseDTO> response =
                restTemplate.exchange("/api/users/" + user.getId(), HttpMethod.GET, entity, UserResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().getUsername()).isEqualTo("john");
    }

    @Test
    void getUserById_ShouldReturn200_WhenSameUser() {
        User user = userRepository.save(User.builder().username("john").email("john@example.com").password("123").role(Role.USER).build());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "USER");
        headers.set("X-User-Id", user.getId().toString());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserResponseDTO> response =
                restTemplate.exchange("/api/users/" + user.getId(), HttpMethod.GET, entity, UserResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getUserById_ShouldReturn403_WhenDifferentUserAndNotAdmin() {
        User user = userRepository.save(User.builder().username("john").email("john@example.com").password("123").role(Role.USER).build());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "USER");
        headers.set("X-User-Id", "999");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange("/api/users/" + user.getId(), HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // --- DELETE USER ---
    @Test
    void deleteUser_ShouldReturn204_WhenAdmin() {
        User user = userRepository.save(User.builder().username("john").email("john@example.com").password("123").role(Role.USER).build());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "ADMIN");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response =
                restTemplate.exchange("/api/users/" + user.getId(), HttpMethod.DELETE, entity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThat(userRepository.existsById(user.getId())).isFalse();
    }

    @Test
    void deleteUser_ShouldReturn403_WhenDifferentUserAndNotAdmin() {
        User user = userRepository.save(User.builder().username("john").email("john@example.com").password("123").role(Role.USER).build());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "USER");
        headers.set("X-User-Id", "999");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange("/api/users/" + user.getId(), HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
