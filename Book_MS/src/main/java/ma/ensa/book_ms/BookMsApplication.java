package ma.ensa.book_ms;

import ma.ensa.book_ms.Entity.Book;
import ma.ensa.book_ms.Repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookMsApplication.class, args);
    }

    @Bean
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
                            .author("Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides")
                            .availableCopies(4)
                            .build()
            );


            System.out.println("✅ Database initialized with sample books");
        };
    }
}