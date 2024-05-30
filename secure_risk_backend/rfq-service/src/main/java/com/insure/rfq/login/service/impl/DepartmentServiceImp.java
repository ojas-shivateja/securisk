package com.insure.rfq.login.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.insure.rfq.login.dto.DepartmentLoginDto;
import com.insure.rfq.login.entity.Department;
import com.insure.rfq.login.repository.DepartmentRepository;
import com.insure.rfq.login.service.DepatmentService;

import ch.qos.logback.core.model.Model;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DepartmentServiceImp implements DepatmentService {
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private ModelMapper modelMapper;
	

	@Override
	public DepartmentLoginDto addDepartmentDetails(DepartmentLoginDto departmentDto) {
		log.info("Adding a new department: {}", departmentDto.getDepartmentName());
		departmentDto.setStatus("ACTIVE");
		Department department = modelMapper.map(departmentDto, Department.class);
		Department savedDepartment = departmentRepository.save(department);

		log.info("Department added successfully: {}", savedDepartment.getId());

		return modelMapper.map(savedDepartment, DepartmentLoginDto.class);
	}

	@Override
	public List<DepartmentLoginDto> getAllDepartment() {
		log.info("Retrieving all departments.");

		List<Department> departments = departmentRepository.findAll().stream()
				.filter(obj -> obj.getStatus().equalsIgnoreCase("ACTIVE")).toList();

		log.info("Retrieved {} departments.", departments.size());

		return departments.stream().map(department -> modelMapper.map(department, DepartmentLoginDto.class)).toList();
	}

	@Override
	public DepartmentLoginDto getDepartmentById(Long id) {
		log.info("Retrieving department with ID: {}", id);
		Department department = departmentRepository.findById(id).orElse(null);
		if (department != null) {
			log.info("Department with ID {} found.", id);
		} else {
			log.warn("Department with ID {} not found.", id);
		}

		return modelMapper.map(department, DepartmentLoginDto.class);
	}

	@Override
	public DepartmentLoginDto updateDepartment(DepartmentLoginDto departmentDto, Long deptId) {
		log.info("Updating location with ID: {}", deptId);

		if (departmentDto != null) {
			Department department = departmentRepository.findById(deptId).orElseThrow();

			Department map = modelMapper.map(department, Department.class);
			map.setDepartmentName(departmentDto.getDepartmentName());
			Department updateDepartment = departmentRepository.save(map);

			log.info("Location updated successfully: {}", updateDepartment);

			return modelMapper.map(updateDepartment, DepartmentLoginDto.class);
		}

		log.warn("LocationDto is null, update failed for ID: {}", deptId);

		return null;
	}

	@Override
	public int deleteDepartmen(Long deptId) {
		log.info("Deleting department with ID: {}", deptId);

		Department department = departmentRepository.findById(deptId).orElseThrow();
		int deleteCount = departmentRepository.deleteDepartment(department.getId());

		log.info("Deleted {} department with ID: {}", deleteCount, deptId);

		return deleteCount;
	}

}
