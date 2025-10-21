package ma.ensa.book_ms;

import ma.ensa.book_ms.Entity.Book;
import ma.ensa.book_ms.Repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class BookMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookMsApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner initDatabase(BookRepository bookRepository) {
        return args -> {

            // Insert valid books
            bookRepository.save(
                    Book.builder()
                            .title("Effective Java")
                            .author("Joshua Bloch")
                            .availableCopies(5)
                            .build()
            );

            bookRepository.save(
                    Book.builder()
                            .title("Clean Code")
                            .author("Robert C. Martin")
                            .availableCopies(3)
                            .build()
            );

            bookRepository.save(
                    Book.builder()
                            .title("Design Patterns")
                            .author("Adelwahab Naji")
                            .availableCopies(20)
                            .build()
            );

            bookRepository.save(
                    Book.builder()
                            .title("Generative AI")
                            .author("Mohammed Youssfi")
                            .availableCopies(10)
                            .build()
            );


            System.out.println("✅ Database initialized with sample books");
        };
    }

}