package com.insure.rfq.login.service.impl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.login.dto.DesignationLoginDto;
import com.insure.rfq.login.entity.Department;
import com.insure.rfq.login.entity.Designation;
import com.insure.rfq.login.repository.DepartmentRepository;
import com.insure.rfq.login.repository.DesignationRepository;
import com.insure.rfq.login.service.DesignationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DesignationServiceImp implements DesignationService {
	@Autowired
	private DesignationRepository designationRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public DesignationLoginDto addDesignation(DesignationLoginDto designationDto, long deptId) {
		Optional<Department> findById = departmentRepository.findById(deptId);
		if (findById.isPresent()) {
			designationDto.setStatus("ACTIVE");
			designationDto.setDepartment(findById.get());
			Designation save = designationRepository.save(modelMapper.map(designationDto, Designation.class));
			log.info("Added a new designation: {}", save.getDesignationName());
			return modelMapper.map(save, DesignationLoginDto.class);
		} else {
			log.warn("Department with ID {} not found, designation not added.", deptId);
			return null;
		}
	}

	@Override
	public List<DesignationLoginDto> getAllDesiDesignation() {
		List<Designation> allDesignations = designationRepository.findAll().stream()
				.filter(obj -> obj.getStatus().equalsIgnoreCase("ACTIVE")).toList();
		;
		List<DesignationLoginDto> designationDtos = allDesignations.stream()
				.map(designation -> modelMapper.map(designation, DesignationLoginDto.class)).toList();

		log.info("Retrieved {} designations.", designationDtos.size());

		return designationDtos;
	}

	@Override
	public DesignationLoginDto getDesignationById(Long id) {
		Designation designation = designationRepository.findById(id).orElse(null);

		if (designation != null) {
			log.info("Designation with ID {} found: {}", id, designation);
			return modelMapper.map(designation, DesignationLoginDto.class);
		} else {
			log.warn("Designation with ID {} not found.", id);
			return null;
		}
	}

	@Override
	public List<String> getDesignationByDeptId(Long deptId) {
		log.info("Fetching designations for department with ID: {}", deptId);

		List<String> designations = designationRepository.getDesignationsByDepartmentId(deptId).stream()
				.map(Designation::getDesignationName).toList();

		log.debug("Designations for department with ID {}: {}", deptId, designations);

		return designations;
	}

	@Override
	public DesignationLoginDto updateDesignation(DesignationLoginDto designationDto, Long desId) {
		log.info("Updating location with ID: {}", desId);

		if (designationDto != null) {
			Designation designation = designationRepository.findById(desId).orElseThrow();

			Designation map = modelMapper.map(designation, Designation.class);
			map.setDesignationName(designationDto.getDesignationName());
			Designation updateDesignation = designationRepository.save(map);

			log.info("Location updated successfully: {}", updateDesignation);

			return modelMapper.map(updateDesignation, DesignationLoginDto.class);
		}

		log.warn("LocationDto is null, update failed for ID: {}", desId);

		return null;
	}

	@Override
	public int deleteDesignation(Long desId) {
		log.info("Deleting department with ID: {}", desId);

		Designation designation = designationRepository.findById(desId).orElseThrow();
		int deleteCount = designationRepository.deleteDesignation(designation.getId());

		log.info("Deleted {} department with ID: {}", deleteCount, desId);

		return deleteCount;
	}

}
