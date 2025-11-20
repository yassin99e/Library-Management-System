package ma.ensa.borrower_ms;

import ma.ensa.borrower_ms.entity.Role;
import ma.ensa.borrower_ms.entity.User;
import ma.ensa.borrower_ms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableFeignClients
@SpringBootApplication
public class BorrowerMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BorrowerMsApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 👤 Normal User
            User user = User.builder()
                    .username("yassin99e")
                    .email("yassine.benakki@gmail.com")
                    .role(Role.USER)
                    .password(passwordEncoder.encode("password123"))
                    .build();

            // 🛡️ Admin User
            User admin = User.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .role(Role.ADMIN)
                    .password(passwordEncoder.encode("admin123"))
                    .build();

            userRepository.save(user);
            userRepository.save(admin);

            System.out.println("✅ Database initialized with Normal User and Admin succesfully!!");

        };
    }


}
