package com.insure.rfq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insure.rfq.entity.PolicyCoverageEntity;

public interface PolicyCoverageRepository extends JpaRepository<PolicyCoverageEntity, Long> {

	@Query("SELECT  policy FROM PolicyCoverageEntity policy WHERE policy.product.productId=:productId ")
	List<PolicyCoverageEntity> getCoveragesByProductId(@Param("productId") Long productId);

	@Query("SELECT COUNT(c) FROM PolicyCoverageEntity c WHERE c.product.productId = :productId AND c.status = 'ACTIVE' ")
	int getCoverageCount(@Param("productId") Long productId);

}
