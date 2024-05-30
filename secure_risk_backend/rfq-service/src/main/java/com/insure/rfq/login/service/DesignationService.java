package com.insure.rfq.login.service;

import java.util.List;

import com.insure.rfq.login.dto.DesignationLoginDto;

public interface DesignationService {

	DesignationLoginDto addDesignation(DesignationLoginDto designationDto, long deptId);

	List<DesignationLoginDto> getAllDesiDesignation();

	DesignationLoginDto getDesignationById(Long id);

	List<String> getDesignationByDeptId(Long deptId);

	int deleteDesignation(Long desId);

	DesignationLoginDto updateDesignation(DesignationLoginDto designationDto, Long desId);

}
