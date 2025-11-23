package ro.uaic.ossp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.uaic.ossp.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMatriculationNumber(String matriculationNumber);
    boolean existsByEmail(String email);
    boolean existsByMatriculationNumber(String matriculationNumber);
}
