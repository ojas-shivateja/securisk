package com.insure.rfq.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.PolicyEntity;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, Long> {

	@Query("SELECT p FROM PolicyEntity p WHERE p.clientListId.cid = :clientId AND p.productId.id=:productId ")
	Optional<PolicyEntity> findByClientAndProductId(@Param("clientId") Long clientId,
			@Param("productId") Long productId);

}
