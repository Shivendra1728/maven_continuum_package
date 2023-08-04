package com.continuum.tenant.repos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.Product;

/**
 * @author RK
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
