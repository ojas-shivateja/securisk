package com.insure.rfq.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insure.rfq.login.entity.UserLogin;
public interface UserLoginRepo extends JpaRepository<UserLogin, Long> {

	Optional<UserLogin> findByEmail(String email);
	boolean existsByEmail(String email);
}
