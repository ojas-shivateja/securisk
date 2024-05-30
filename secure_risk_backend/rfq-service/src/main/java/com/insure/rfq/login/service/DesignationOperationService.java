package com.insure.rfq.login.service;

import java.util.List;

import com.insure.rfq.login.dto.DesignationOperationMappingDto;
import com.insure.rfq.login.dto.PermittedDesignationDto;

public interface DesignationOperationService {

	DesignationOperationMappingDto createDesignationOperation(DesignationOperationMappingDto mappingDto);
	PermittedDesignationDto getAllPermissionDetailsBasedOnDesignationId(Long id);
	List<Long> getPermissonIdBasedOnDeptId(Long deptId);
}
