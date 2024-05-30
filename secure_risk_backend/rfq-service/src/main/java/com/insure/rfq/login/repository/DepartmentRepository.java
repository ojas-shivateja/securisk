package com.insure.rfq.login.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insure.rfq.login.entity.Department;

import jakarta.transaction.Transactional;

public interface DepartmentRepository extends JpaRepository<Department,Long>{
	
	@Modifying
	@Transactional
	@Query("UPDATE Department dept SET dept.status='INACTIVE' WHERE dept.id=:deptId")
	int deleteDepartment(@Param("deptId") Long deptId);

}
