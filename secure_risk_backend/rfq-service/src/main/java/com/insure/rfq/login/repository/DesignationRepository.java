package com.insure.rfq.login.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insure.rfq.login.entity.Designation;

import jakarta.transaction.Transactional;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

	@Query("SELECT designation  FROM Designation designation WHERE designation.department.id=:departmentId")
	List<Designation> getDesignationsByDepartmentId(@Param("departmentId") Long departmentId);
	
	@Modifying
	@Transactional
	@Query("UPDATE Designation des SET des.status='INACTIVE' WHERE des.id=:desId")
	int deleteDesignation(@Param("desId") Long desId);
}
