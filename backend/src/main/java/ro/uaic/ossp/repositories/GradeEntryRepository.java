package ro.uaic.ossp.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.uaic.ossp.models.GradeEntry;

@Repository
public interface GradeEntryRepository extends JpaRepository<GradeEntry, Long> {
}
