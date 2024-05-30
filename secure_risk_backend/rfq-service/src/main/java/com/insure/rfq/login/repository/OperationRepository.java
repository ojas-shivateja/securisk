package com.insure.rfq.login.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.insure.rfq.login.entity.OperationTable;

@Repository
public interface OperationRepository extends JpaRepository<OperationTable, Long> {

	Optional<OperationTable> findByMenuName(String menuName);

	@Query("SELECT op.menuName FROM OperationTable op WHERE op.menuType=:menuType")
	List<String> getAllMenuNameBasedOnMenuType(@Param("menuType") String menuType);
	
	List<OperationTable>findByMenuType(String menuType);
	@Query("SELECT op.id FROM OperationTable op WHERE op.menuType=:menuType")
	List<Long> getAllMenuNameBasedOnMenuTypeObj(@Param("menuType") String menuType);
	@Query("SELECT op.menuType FROM OperationTable op")
	Set<String>allOPerationMenuType();
}
