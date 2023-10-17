package com.continuum.tenant.repos.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.continuum.tenant.repos.entity.Invoice;

@Repository
public interface CsvRepository extends JpaRepository<Invoice, Long> {

	List<Invoice> findByInvNo(String invNo);

	List<Invoice> findByCustomerId(String customerId);
	 
}
