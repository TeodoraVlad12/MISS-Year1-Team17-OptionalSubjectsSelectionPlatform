package ro.uaic.ossp.repositories;

import ro.uaic.ossp.models.TransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.uaic.ossp.models.enums.TransferStatus;

import java.util.List;

@Repository
public interface TransferRequestRepository extends JpaRepository<TransferRequest, Long> {

    List<TransferRequest> findByUserId(Long userId);

    List<TransferRequest> findByStatus(TransferStatus status);
}
