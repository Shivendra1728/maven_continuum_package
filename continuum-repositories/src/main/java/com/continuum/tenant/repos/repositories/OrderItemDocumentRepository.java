package com.continuum.tenant.repos.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.OrderItemDocuments;

@Repository
public interface OrderItemDocumentRepository extends JpaRepository<OrderItemDocuments, Long> {

}
