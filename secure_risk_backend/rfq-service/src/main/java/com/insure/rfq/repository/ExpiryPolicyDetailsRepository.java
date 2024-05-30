package com.insure.rfq.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.ExpiryPolicyDetails;

@Repository
public interface ExpiryPolicyDetailsRepository extends JpaRepository<ExpiryPolicyDetails, UUID> {
	Optional<ExpiryPolicyDetails> findByrfqId(String rfqId);
}
