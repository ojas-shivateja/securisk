package com.insure.rfq.login.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insure.rfq.login.entity.DesignationOperationMapping;

public interface DesignationOperationRepository extends JpaRepository<DesignationOperationMapping, Long> {
	List<DesignationOperationMapping> findByDesignationId(long id);

	@Query("SELECT d.operationId FROM  DesignationOperationMapping d WHERE d.designationId=:designationId")
	List<Long> getAllPermittedOperationByDesignationId(@Param("designationId") Long designationId);

	@Query("SELECT d.operationId FROM  DesignationOperationMapping d WHERE d.designationId=:designationId")
	List<Long> findByOperationId(@Param("designationId") long designationId);

}
