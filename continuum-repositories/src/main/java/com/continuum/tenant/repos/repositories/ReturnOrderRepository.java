package com.continuum.tenant.repos.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnType;
import com.continuum.tenant.repos.entity.User;

@Repository
public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long>, JpaSpecificationExecutor<ReturnOrder> {

	List<ReturnOrder> findByrmaOrderNo(String rmaOrderNo);

	List<ReturnOrder> findByUserId(Long id);

	Optional<ReturnOrder> findByRmaOrderNo(String rmaNo);

	ReturnOrder findFirstByRmaOrderNoStartingWithOrderByRmaOrderNoDesc(String string);

	ReturnOrder findTopByOrderByIdDesc();

	List<ReturnOrder> findByIsSalesRepLinkedFalse();
	
	List<ReturnDTO> findProjectedBy();

    interface ReturnDTO {
        String getRmaOrderNo();
        Date getCreatedDate();
        Customer getCustomer();
        User getUser();
        ReturnType getReturnType();
        Date getNextActivityDate();
        String getStatus();
    }
	
}
