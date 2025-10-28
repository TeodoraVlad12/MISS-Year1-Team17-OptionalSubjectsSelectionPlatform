package com.example.ossp.repositories;

import com.example.ossp.entities.TransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRequestRepository extends JpaRepository<TransferRequest, Long> {
    
    List<TransferRequest> findByStudentId(Long studentId);
    
    List<TransferRequest> findByStatus(TransferRequest.TransferStatus status);
}
