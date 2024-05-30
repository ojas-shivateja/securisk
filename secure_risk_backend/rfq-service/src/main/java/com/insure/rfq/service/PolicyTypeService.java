package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.PolicyTypeDto;

public interface PolicyTypeService {

	List<String> getPolicyTypeById(Long id);
	
	PolicyTypeDto createPolicy(PolicyTypeDto policyTypeDto, Long id);
}
