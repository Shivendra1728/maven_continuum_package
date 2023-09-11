package com.continuum.tenant.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.continuum.tenant.repos.entity.OrderAddress;

public interface OrderAddressrepository extends JpaRepository<OrderAddress, Long> {

}
