package com.insure.rfq.service.impl;

import com.insure.rfq.dto.ClientListEmployee_Submit_Claim_User_DetailsDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientListEmployee_Submit_Claim_User_Details;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.*;
import com.insure.rfq.service.ClientListEmployee_Submit_Claim_User_DetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ClientListEmployee_Submit_Claim_User_DetailsServiceImpl
		implements ClientListEmployee_Submit_Claim_User_DetailsService {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private ClientListEmployee_Submit_Claim_User_Detailsrepository userDetailsrepository;

	@Override
	public String saveClientListEmployeeSubmitClaimUserDetailsDto(ClientListEmployee_Submit_Claim_User_DetailsDto dto,
			Long clientID, Long productId, Long employeeId) {

		ClientListEmployee_Submit_Claim_User_Details userDetails = new ClientListEmployee_Submit_Claim_User_Details();

		String message;
		userDetails.setPatientName(dto.getPatientName());
		userDetails.setEmployeeName(dto.getEmployeeName());
		userDetails.setUhid(dto.getUhid());

		userDetails.setDateOfAdmission(dto.getDateOfAdmission());
		userDetails.setDateOfDischarge(dto.getDateOfDischarge());
		userDetails.setEmployeeId(dto.getEmployeeId());
		userDetails.setEmail(dto.getEmail());
		userDetails.setMobileNumber(dto.getMobileNumber());
		userDetails.setSumInsured(dto.getSumInsured());
		userDetails.setBenificiaryName(dto.getBenificiaryName());
		userDetails.setRelationToEmployee(dto.getRelationToEmployee());
		userDetails.setClaimNumber(dto.getClaimNumber());
		userDetails.setRecordStatus("ACTIVE");
		String uiid = UUID.randomUUID().toString();
		userDetails.setUser_detailsId(uiid);
		userDetails.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		if (productId != null) {
			Product product1 = productRepository.findById(productId)
					.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
			userDetails.setProductId(product1);
		}
		if (clientID != null) {
			ClientList clientList = clientListRepository.findById(clientID)
					.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
			userDetails.setClientListId(clientList);
			userDetails.setRfqId(clientList.getRfqId());
		}
		if (employeeId != null) {
			log.info(" Employee id  {}: ", employeeId);
			userDetails.setEmployeeDataID(String.valueOf(employeeId));
		}
		ClientListEmployee_Submit_Claim_User_Details saveentity = userDetailsrepository.save(userDetails);
		if (saveentity != null) {
			message = saveentity.getUser_detailsId();

		} else
			message = "ClientListEmployee_ECashless Data is Not saved ";

		return message;

	}

	public static ClientListEmployee_Submit_Claim_User_DetailsDto mapEntityToDto(
			ClientListEmployee_Submit_Claim_User_Details entity) {
		ClientListEmployee_Submit_Claim_User_DetailsDto dto = new ClientListEmployee_Submit_Claim_User_DetailsDto();

		dto.setUser_detailsId(entity.getUser_detailsId());
		dto.setPatientName(entity.getPatientName());
		dto.setEmployeeName(entity.getEmployeeName());
		dto.setUhid(entity.getUhid());
		dto.setDateOfAdmission(entity.getDateOfAdmission());
		dto.setDateOfDischarge(entity.getDateOfDischarge());
		dto.setEmployeeId(entity.getEmployeeId());
		dto.setEmail(entity.getEmail());
		dto.setMobileNumber(entity.getMobileNumber());
		dto.setSumInsured(entity.getSumInsured());
		dto.setBenificiaryName(entity.getBenificiaryName());
		dto.setRelationToEmployee(entity.getRelationToEmployee());
		dto.setClaimNumber(entity.getClaimNumber());

		dto.setRfqId(entity.getRfqId());
		dto.setEmployeeDataID(entity.getEmployeeDataID());

		return dto;
	}

	public List<ClientListEmployee_Submit_Claim_User_DetailsDto> getAllClientListEmployeeSubmitClaimUserDetailsDtos() {
		return userDetailsrepository.findAll().stream().map(entity -> {
			return mapEntityToDto(entity);
		}).toList();
	}

}
