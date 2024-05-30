package com.insure.rfq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.PolicyTermsEntity;

import jakarta.transaction.Transactional;

@Repository
public interface PolicyTermsRepository extends JpaRepository<PolicyTermsEntity, UUID> {
	Optional<List<PolicyTermsEntity>> findByRfqId(String rfqId);

	@Query("SELECT COUNT(coverageName) FROM PolicyTermsEntity WHERE rfqId = :rfqId")
	int getCoverageCount(@Param("rfqId") String rfqId);

	@Query("SELECT p FROM PolicyTermsEntity p WHERE p.rfqId = :rfqId")
	Optional<List<PolicyTermsEntity>> getPolicyTermsByRfqId(@Param("rfqId") String rfqId);

	@Modifying
	@Transactional
	@Query("UPDATE PolicyTermsEntity p  SET  p.recordStatus = 'INACTIVE' WHERE p.rfqId=:rfqId")
	int deletePolicyTermsByRfqId(@Param("rfqId") String rfqId);
}
