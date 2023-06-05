package com.continuum.repos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.continuum.repos.entity.ReasonCode;
import com.continuum.repos.entity.Store;

@Repository
public interface ReasonCodeRepository extends JpaRepository<ReasonCode,Long >,JpaSpecificationExecutor<ReasonCode>{

	
	
	  @Query("SELECT rc FROM ReasonCode rc " +
	  "left JOIN FETCH rc.parentReasonCode prc  where rc.parentReasonCode is null and rc.store=:storeId"
	  ) List<ReasonCode> findNestedReasonCodesByStoreId(@Param("storeId") Store
	  storeId);
	 
	
	    @Query("SELECT rc FROM ReasonCode rc " +
	           "left JOIN FETCH rc.parentReasonCode prc  where rc.parentReasonCode is null and rc.store=1" )
	 List<ReasonCode> findAll();

		List<ReasonCode>  findByStore(Store store);
	}
