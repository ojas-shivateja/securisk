package com.insure.rfq.login.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.exception.InvalidUser;
import com.insure.rfq.login.dto.DesignationOperationMappingDto;
import com.insure.rfq.login.dto.OperationPermittedDto;
import com.insure.rfq.login.dto.PermittedDesignationDto;
import com.insure.rfq.login.entity.Designation;
import com.insure.rfq.login.entity.DesignationOperationMapping;
import com.insure.rfq.login.entity.DesignationOperationMappingHistory;
import com.insure.rfq.login.entity.OperationTable;
import com.insure.rfq.login.repository.DesignationOperationMappingHistoryRepo;
import com.insure.rfq.login.repository.DesignationOperationRepository;
import com.insure.rfq.login.repository.DesignationRepository;
import com.insure.rfq.login.repository.OperationRepository;
import com.insure.rfq.login.service.DesignationOperationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DesignationOperationServiceImpl implements DesignationOperationService {
	@Autowired
	private OperationRepository operationRepository;
	@Autowired
	private DesignationOperationRepository designationOperationRepository;
	@Autowired
	private DesignationRepository designationRepository;
	@Autowired
	private DesignationOperationMappingHistoryRepo designationOperationMappingHistoryRepo; 

	@Override
	public DesignationOperationMappingDto createDesignationOperation(DesignationOperationMappingDto mappingDto) {
		Designation designationObj = designationRepository.findById(mappingDto.getDesignationId())
				.orElseThrow(() -> new InvalidUser(" invalid user"));
		if (mappingDto != null) {
			List<DesignationOperationMapping> findByDesignationId = designationOperationRepository
					.findByDesignationId(mappingDto.getDesignationId());
		log.info("if  {}"+findByDesignationId .size() );
		
			if (!findByDesignationId .isEmpty()) {
				Optional<DesignationOperationMapping> findAny = findByDesignationId.stream().map(obj->{return obj;}).findAny();
				
				for (DesignationOperationMapping data : findByDesignationId) {
					
					DesignationOperationMappingHistory archiveCopy = new DesignationOperationMappingHistory();
	                archiveCopy.setId(data.getId());
	                archiveCopy.setDesignationId(data.getDesignationId());
	                archiveCopy.setOperationId(data.getOperationId());
	                archiveCopy.setCreateDate(data.getCreateDate());
	                archiveCopy.setUpdatedDate(data.getUpdatedDate());
	                archiveCopy.setRemark("deleted");

	                // Save the archive copy to the history table
	                designationOperationMappingHistoryRepo.save(archiveCopy);
	                
	                
					designationOperationRepository.deleteById(data.getId());
			  }

				for (Long data : mappingDto.getMenuType()) {
					DesignationOperationMapping mapping = new DesignationOperationMapping();
					mapping.setDesignationId(designationObj.getId());
					mapping.setOperationId(data);
					mapping.setCreateDate(findAny.get().getCreateDate());
					mapping.setUpdatedDate(LocalDate.now());
					designationOperationRepository.save(mapping);
					log.info("  save succesfully with dept_id {} and permisson_id {} ", mappingDto.getDesignationId(),
							data);
				}
				return mappingDto;

			} else {
				log.info("else");
				for (Long data : mappingDto.getMenuType()) {
					DesignationOperationMapping mapping = new DesignationOperationMapping();
					mapping.setDesignationId(designationObj.getId());
					mapping.setOperationId(data);
					mapping.setCreateDate(LocalDate.now());
					designationOperationRepository.save(mapping);
					log.info("  save succesfully with dept_id {} and permisson_id {} ", mappingDto.getDesignationId(),
							data);
				}
				return mappingDto;
			}
		}
		return mappingDto; 

	}

	@Override
	public PermittedDesignationDto getAllPermissionDetailsBasedOnDesignationId(Long id) {
		List<Long> getAllOpertionId = new ArrayList<>();
		List<OperationPermittedDto> getAlloperationPermittedDto = new ArrayList<>();
		List<DesignationOperationMapping> permittedDesignation = designationOperationRepository.findByDesignationId(id);

		if (permittedDesignation.isEmpty()) {
			log.info(" if condition");
			return null;
		} else {
			log.info(" else condition");
			permittedDesignation.stream().map(obj -> getAllOpertionId.add(obj.getOperationId())).toList();
			log.info(" valid designation id with value  {}", id);
			for (Long operationId : getAllOpertionId) {
				OperationTable operationTable = operationRepository.findById(operationId).get();

				OperationPermittedDto operationPermittedDto = OperationPermittedDto.builder().id(operationTable.getId())
						.operationName(operationTable.getMenuName()).build();
				getAlloperationPermittedDto.add(operationPermittedDto);

			}

			return PermittedDesignationDto.builder().id(id).operationPermittedDto(getAlloperationPermittedDto).build();
		}

	}

	@Override
	public List<Long> getPermissonIdBasedOnDeptId(Long deptId) {

		return designationOperationRepository.getAllPermittedOperationByDesignationId(deptId);

	}

}
