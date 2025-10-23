/*
package ma.ensa.borrower_ms.repository;


import ma.ensa.borrower_ms.entity.Role;
import ma.ensa.borrower_ms.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;

    private User user;

    // Each time we want to execute a test method this function will execute
    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("yassin99e")
                .email("yassine.benakki@gmail.com")
                .role(Role.USER)
                .password("password123")
                .build();

        userRepository.save(user);
    }


    @Test
    void test_existsByEmail_succes(){
        boolean existsByEmail = userRepository.existsByEmail("yassine.benakki@gmail.com");
        assertTrue(existsByEmail);

    }

    @Test
    void test_existsByEmail_faillure(){
        boolean existsByEmail = userRepository.existsByEmail("zabawchtasaba@gmail.com");
        assertFalse(existsByEmail);

    }

    @Test
    void test_existsByUsername_succes(){
        boolean existsByUsername = userRepository.existsByUsername("yassin99e");
        assertTrue(existsByUsername);
    }

    @Test
    void test_existsByUsername_faillure(){
        boolean existsByUsername = userRepository.existsByUsername("eruine42e");
        assertFalse(existsByUsername);
    }


    @Test
    void test_findByEmail(){
        Optional<User> byEmail = userRepository.findByEmail("yassine.benakki@gmail.com");
        assertTrue(byEmail.isPresent());
        assertEquals(byEmail.get().getEmail(), "yassine.benakki@gmail.com");
    }


}
*/