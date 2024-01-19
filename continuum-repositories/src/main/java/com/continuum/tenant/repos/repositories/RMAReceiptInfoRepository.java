package com.continuum.tenant.repos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.continuum.tenant.repos.entity.RMAReceiptInfo;

public interface RMAReceiptInfoRepository extends JpaRepository<RMAReceiptInfo, Long> {
	List<RMAReceiptInfo> findByStatus(String status);

	List<RMAReceiptInfo> findByStatusAndRetryCountLessThan(String status, Integer retryCount);
}
