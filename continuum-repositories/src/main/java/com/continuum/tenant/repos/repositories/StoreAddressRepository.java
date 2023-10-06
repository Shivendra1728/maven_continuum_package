package com.continuum.tenant.repos.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.Store;
import com.continuum.tenant.repos.entity.StoreAddress;

@Repository
public interface StoreAddressRepository extends JpaRepository<StoreAddress, Long>, JpaSpecificationExecutor<Store> {

	StoreAddress findByaddressType(String addressId);
}