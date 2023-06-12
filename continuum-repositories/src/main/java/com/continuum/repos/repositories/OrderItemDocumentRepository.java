package com.continuum.repos.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.continuum.repos.entity.OrderItemDocuments;

@Repository
public interface OrderItemDocumentRepository extends JpaRepository<OrderItemDocuments, Long> {
     Optional<OrderItemDocuments> findById(Long id);
}
