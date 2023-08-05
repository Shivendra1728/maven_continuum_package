
package com.continuum.tenant.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.Orders;

@Repository
public interface OrderRepository  extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders>{


}
