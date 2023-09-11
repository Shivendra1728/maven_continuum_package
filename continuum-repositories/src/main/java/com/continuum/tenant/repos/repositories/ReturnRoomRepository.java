package com.continuum.tenant.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.AuditLog;
import com.continuum.tenant.repos.entity.ReturnRoom;

@Repository
public interface ReturnRoomRepository extends JpaRepository<ReturnRoom, Long>{

}
