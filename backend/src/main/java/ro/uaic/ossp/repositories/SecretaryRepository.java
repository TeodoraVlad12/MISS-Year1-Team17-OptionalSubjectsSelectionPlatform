package ro.uaic.ossp.repositories;

import ro.uaic.ossp.models.Secretary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecretaryRepository extends JpaRepository<Secretary, Long> {
    
    Optional<Secretary> findByEmail(String email);
}
