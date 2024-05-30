package com.insure.rfq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.insure.rfq.entity.IntermediaryDetails;

public interface IntermediaryDetailsRepository extends JpaRepository<IntermediaryDetails, Long> {

	
	@Query(value = "SELECT COUNT(*) FROM INTERMEDIARYDETAILS;", nativeQuery = true)
	Long countApplicationsByStatus();
}
