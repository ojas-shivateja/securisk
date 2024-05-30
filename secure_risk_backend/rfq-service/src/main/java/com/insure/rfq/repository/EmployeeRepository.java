package com.insure.rfq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.insure.rfq.entity.EmployeeDepedentDetailsEntity;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeDepedentDetailsEntity, Long> {

	

    List<EmployeeDepedentDetailsEntity>findByEmployeeId(String employeeId);
	@Query(value = "SELECT emp.rfqId FROM EmployeeDepedentDetailsEntity emp WHERE emp.rfqId=:id ")
	List<String> getByrfqId(@Param("id") String id);


	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("UPDATE EmployeeDepedentDetailsEntity emp SET emp.recordStatus='INACTIVE' WHERE emp.rfqId=:id")
	int deleteByRfqId(@Param("id") String rfqId);

	List<EmployeeDepedentDetailsEntity> findByRfqId(String rfqId);


	@Query("SELECT emp.relationship FROM EmployeeDepedentDetailsEntity emp WHERE emp.rfqId=:id")
	Optional<List<String>> findAllRelationShipByRfqId(@Param("id") String rfqId);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("DELETE FROM EmployeeDepedentDetailsEntity emp WHERE emp.rfqId=:id")
	int hardDeleteByRfqId(@Param("id") String rfqId);

}
