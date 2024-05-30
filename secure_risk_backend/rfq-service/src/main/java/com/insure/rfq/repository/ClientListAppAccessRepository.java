package com.insure.rfq.repository;

import com.insure.rfq.entity.ClientListAppAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientListAppAccessRepository extends JpaRepository<ClientListAppAccess,Long> {
    Optional<ClientListAppAccess> findByEmail(String email);
}

