package com.continuum.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.repos.entity.OrderItemDocuments;

@Repository
public interface FileUploadRepository extends JpaRepository<OrderItemDocuments, Long> {

}
