package com.insure.rfq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.EmpDependentHeaderMapping;

@Repository
public interface EmpDependentHeaderMappingRepository extends JpaRepository<EmpDependentHeaderMapping, Long> {

	List<EmpDependentHeaderMapping> findByAliasName(String aliasname);

	@Query("SELECT e FROM ExcelReportHeadersMapping e where e.reportHeaders.headerId=:id")
	List<EmpDependentHeaderMapping> getBasedOnHeaderId(@Param("id") Long id);
}
