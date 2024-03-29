package com.continuum.tenant.repos.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUuid(String uuid);

	User findByUserName(String userName);

	User findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<User> findById(Long id);

	List<User> findByStatus(Boolean i);

	Optional<User> findByCustomerId(Long id);

	User findByActivationUuid(String uuid);
}
