package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.PolicyCoverageDto;

public interface PolicyCoverageService {

	PolicyCoverageDto createCoverageByProductId(Long productId, PolicyCoverageDto policyCoverageDto);

	List<PolicyCoverageDto> getCoveragesByProductId(Long productId);

	PolicyCoverageDto updateCoveragesByProductId(Long policyCoverageId, PolicyCoverageDto policyCoverageDto);

	String deleteCoveragesByProductId(Long policyCoverageId);
}
