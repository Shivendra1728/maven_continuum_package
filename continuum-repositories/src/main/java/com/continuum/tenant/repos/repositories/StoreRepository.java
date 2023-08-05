package com.continuum.tenant.repos.repositories;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.Orders;
import com.continuum.tenant.repos.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {

	Store findById(Store storeId);

	

 

}