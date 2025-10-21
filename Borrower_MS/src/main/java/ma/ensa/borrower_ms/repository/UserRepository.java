package ma.ensa.borrower_ms.repository;

import ma.ensa.borrower_ms.entity.Role;
import ma.ensa.borrower_ms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);



    // Get IDs for a specific role
    @Query("SELECT u.id FROM User u WHERE u.role = :role")
    List<Long> findIdsByRole(@Param("role") Role role);

}