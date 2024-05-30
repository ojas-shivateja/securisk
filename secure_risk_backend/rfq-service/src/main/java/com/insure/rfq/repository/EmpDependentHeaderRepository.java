package com.insure.rfq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.EmpDependentHeaders;

@Repository
public interface EmpDependentHeaderRepository extends JpaRepository<EmpDependentHeaders, Long> {

	EmpDependentHeaders findByHeaderNameAndHeaderCategory(String headerName, String headercategory);

	EmpDependentHeaders findByHeaderName(String headerName);

	List<EmpDependentHeaders> findByHeaderCategory(String headerCategory);

	@Query("SELECT headerName FROM EmpDependentHeaders")
	List<String> getAllHeaderNames();

	@Query("SELECT headerCategory FROM EmpDependentHeaders")
	List<String> getAllHeaderCategory();

}
