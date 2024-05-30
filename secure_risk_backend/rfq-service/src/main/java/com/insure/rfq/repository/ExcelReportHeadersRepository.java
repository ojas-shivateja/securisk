package com.insure.rfq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.ExcelReportHeaders;

@Repository
public interface ExcelReportHeadersRepository extends JpaRepository<ExcelReportHeaders, Long> {
	
	ExcelReportHeaders findByHeaderNameAndHeaderCategory(String headerName,String headercategory);
	
	ExcelReportHeaders findByHeaderName(String headerName);
	
	List<ExcelReportHeaders> findByHeaderCategory(String headerCategory);
	
	@Query("SELECT headerName FROM ExcelReportHeaders")
	List<String> getAllHeaderNames();
	@Query("SELECT headerCategory FROM ExcelReportHeaders")
	List<String> getAllHeaderCategory();

}
