package com.insure.rfq.login.service;

import java.util.List;

import com.insure.rfq.login.dto.DepartmentLoginDto;

public interface DepatmentService {
	DepartmentLoginDto addDepartmentDetails(DepartmentLoginDto departmentDto);

	List<DepartmentLoginDto> getAllDepartment();
	
	DepartmentLoginDto getDepartmentById(Long id);
	
	int deleteDepartmen(Long deptId);

	DepartmentLoginDto updateDepartment(DepartmentLoginDto departmentDto, Long deptId);
	
	
}
