package com.insure.rfq.repository;

import com.insure.rfq.entity.ExcelReportHeadersMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcelReportHeadersMappingRepository extends JpaRepository<ExcelReportHeadersMapping, Long> {
	List<ExcelReportHeadersMapping> findByAliasName(String aliasname);
	
	
	@Query("SELECT e FROM ExcelReportHeadersMapping e where e.reportHeaders.headerId=:id")
	List<ExcelReportHeadersMapping> getBasedOnHeaderId(@Param("id") Long id);
}
