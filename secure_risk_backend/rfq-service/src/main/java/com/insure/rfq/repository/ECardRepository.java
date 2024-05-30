package com.insure.rfq.repository;

import com.insure.rfq.entity.ECardEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ECardRepository extends JpaRepository<ECardEntity,Long> {
	
	Optional<ECardEntity> findByEmployeeId(String employeeId);
}
