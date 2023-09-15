package com.continuum.tenant.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.RmaInvoiceInfo;

public interface RmaInvoiceInfoRepository extends JpaRepository<RmaInvoiceInfo, Long> {
	RmaInvoiceInfo findById(ReturnOrder returnOrder);
}
