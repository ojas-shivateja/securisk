package com.insure.rfq.login.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.login.dto.OperationTableDto;
import com.insure.rfq.login.entity.DesignationOperationMapping;
import com.insure.rfq.login.entity.OperationTable;
import com.insure.rfq.login.repository.DesignationOperationRepository;
import com.insure.rfq.login.repository.OperationRepository;
import com.insure.rfq.login.service.OperationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OperationServiceImple implements OperationService {

	@Autowired
	private DesignationOperationRepository designationOperationRepository;

	@Autowired
	private OperationRepository operationRepository;
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public OperationTableDto createOperation(OperationTableDto operationTableDto) {

		OperationTable save = operationRepository.save(modelMapper.map(operationTableDto, OperationTable.class));

		return modelMapper.map(save, OperationTableDto.class);
	}

	@Override
	public Map<String, List<String>> getPermissonBasedOnDeptId(Long id) {

		List<Long> listOfPermissionId = new ArrayList<>();
		Map<String, List<String>> permissionMap = new HashMap<>();
		for (DesignationOperationMapping operationMapping : designationOperationRepository.findByDesignationId(id)) {
			listOfPermissionId.add(operationMapping.getOperationId());
		}
		log.info(listOfPermissionId.toString());
		for (Long permissionId : listOfPermissionId) {
			Optional<OperationTable> operationObj = operationRepository.findById(permissionId);
			List<String> allMenuNameBasedOnMenyType = operationRepository
					.getAllMenuNameBasedOnMenuType(operationObj.get().getMenuType());
			log.info(allMenuNameBasedOnMenyType.toString());
			permissionMap.put(operationObj.get().getMenuType(), allMenuNameBasedOnMenyType);
			log.info(permissionMap.toString());
		}
		log.info(permissionMap.toString() + "  styo");
		return permissionMap;
	}

	@Override
	public List<OperationTableDto> getAllOperation() {
		log.info("Fetching all operations.");

		List<OperationTableDto> operations = operationRepository.findAll().stream()
				.map(obj -> modelMapper.map(obj, OperationTableDto.class)).toList();

		log.debug("Fetched {} operations.", operations.size());

		return operations;
	}

}
