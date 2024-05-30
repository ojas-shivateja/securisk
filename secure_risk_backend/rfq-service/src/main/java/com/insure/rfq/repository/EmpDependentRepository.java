package com.insure.rfq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.EmployeeDepedentDetailsEntity;

@Repository
public interface EmpDependentRepository extends JpaRepository<EmployeeDepedentDetailsEntity, Long> {
	java.util.List<EmployeeDepedentDetailsEntity> findByrfqId(@Param("id") String id);

	
}