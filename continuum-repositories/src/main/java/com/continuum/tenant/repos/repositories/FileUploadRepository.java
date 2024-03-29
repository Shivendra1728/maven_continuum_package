package com.continuum.tenant.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.OrderItemDocuments;

@Repository
public interface FileUploadRepository extends JpaRepository<OrderItemDocuments, Long> {

}
