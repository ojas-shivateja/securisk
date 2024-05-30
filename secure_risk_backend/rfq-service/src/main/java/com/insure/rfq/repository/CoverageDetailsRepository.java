package com.insure.rfq.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.CoverageDetailsEntity;

@Repository
public interface CoverageDetailsRepository extends JpaRepository<CoverageDetailsEntity, Long> {
	Optional<CoverageDetailsEntity> findByRfqId(String rfqId);

	

}
