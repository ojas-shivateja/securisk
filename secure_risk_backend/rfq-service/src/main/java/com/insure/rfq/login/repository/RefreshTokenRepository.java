package com.insure.rfq.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insure.rfq.login.entity.RefreshToken;
import com.insure.rfq.login.entity.UserRegisteration;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

	Optional<RefreshToken> findByToken(String refreshToken);

	RefreshToken findByUsersNew(UserRegisteration userNew);

}
