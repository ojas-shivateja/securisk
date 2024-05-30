package com.insure.rfq.service;

import com.insure.rfq.dto.EmpDependentHeaderDto;
import com.insure.rfq.entity.EmpDependentHeaders;

import java.util.List;

public interface EmpDependentHeaderService {
	
	EmpDependentHeaderDto createHeaders(EmpDependentHeaderDto header);
	
	List<EmpDependentHeaders> viewAllHeaders();
	
	List<String> getAllEmpDependentHeaders();
}
