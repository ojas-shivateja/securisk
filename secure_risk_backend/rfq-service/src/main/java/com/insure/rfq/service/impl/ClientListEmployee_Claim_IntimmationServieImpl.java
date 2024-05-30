package com.insure.rfq.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ClientListEmployee_Claim_IntimmationDataDisplayDto;
import com.insure.rfq.dto.ClientListEmployee_Claim_IntimmationDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientListEmployee_ClaimIntimmation;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientListEmployee_Claim_Intimmationrepository;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ClientListEmployee_Claim_IntimmationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClientListEmployee_Claim_IntimmationServieImpl implements ClientListEmployee_Claim_IntimmationService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private ClientListEmployee_Claim_Intimmationrepository claimIntimmationrepository;

	@Override
	public String saveClientListEmployeeClaimIntimmation(ClientListEmployee_Claim_IntimmationDto dto, Long clientId,
			Long productId, Long employeeId) {
		log.info("ClientListEmployee_Claim_Intimmation {} \n clientId{}  \n ProductId  :{} ", dto, clientId, productId);
		String message = "";
		ClientListEmployee_ClaimIntimmation entityclaim_Intimmation = new ClientListEmployee_ClaimIntimmation();

		entityclaim_Intimmation.setPatient_Name(dto.getPatient_Name());
		entityclaim_Intimmation.setRelationToEmployee(dto.getRelationToEmployee());
		entityclaim_Intimmation.setEmployeeName(dto.getEmployeeName());
		entityclaim_Intimmation.setEmailId(dto.getEmailId());
		entityclaim_Intimmation.setHospital(dto.getHospital());
		entityclaim_Intimmation.setDoctorName(dto.getDoctorName());
		entityclaim_Intimmation.setUhid(dto.getUhid());
		entityclaim_Intimmation.setEmployeeId(dto.getEmployeeId());
		entityclaim_Intimmation.setMobileNumber(dto.getMobileNumber());
		entityclaim_Intimmation.setReasonForAdmission(dto.getReasonForAdmission());
		entityclaim_Intimmation.setDateOfHospitalisation(dto.getDateOfHospitalisation());
		entityclaim_Intimmation.setOther_Details(dto.getOther_Details());
		entityclaim_Intimmation.setRecordStatus("ACTIVE");
		entityclaim_Intimmation.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		entityclaim_Intimmation.setSumInsured(dto.getSumInsured());
		entityclaim_Intimmation.setReasonforClaim(dto.getReasonforClaim());
		if (productId != null) {
			Product product1 = productRepository.findById(productId)
					.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
			entityclaim_Intimmation.setProductId(product1);
		}
		if (clientId != null) {
			ClientList clientList = clientListRepository.findById(clientId)
					.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
			entityclaim_Intimmation.setClientListId(clientList);
			entityclaim_Intimmation.setRfqId(clientList.getRfqId());
		}
		if (employeeId != null) {
			log.info(" Employee id  {}: ", employeeId);
			entityclaim_Intimmation.setEmployeeDataID(String.valueOf((employeeId)));
		}

		ClientListEmployee_ClaimIntimmation save = claimIntimmationrepository.save(entityclaim_Intimmation);
		if (save != null) {
			message = save.getClientListEmployee_Claim_Intimmation().toString();
		} else
			message = "ClientListEmployee_Claim_Intimmation Data is Not saved ";

		return message;

	}

	@Override
	public List<ClientListEmployee_Claim_IntimmationDataDisplayDto> gClientListEmployeeClaimIntimmation() {
		return claimIntimmationrepository.findAll().stream().map(entity -> {
			ClientListEmployee_Claim_IntimmationDataDisplayDto dto = new ClientListEmployee_Claim_IntimmationDataDisplayDto();
			dto.setClientListEmployee_Claim_Intimmation(entity.getClientListEmployee_Claim_Intimmation());
			dto.setPatient_Name(entity.getPatient_Name());
			dto.setRelationToEmployee(entity.getRelationToEmployee());
			dto.setEmployeeName(entity.getEmployeeName());
			dto.setEmailId(entity.getEmailId());
			dto.setHospital(entity.getHospital());
			dto.setDoctorName(entity.getDoctorName());
			dto.setUhid(entity.getUhid());
			dto.setEmployeeId(entity.getEmployeeId());
			dto.setMobileNumber(entity.getMobileNumber());
			dto.setReasonForAdmission(entity.getReasonForAdmission());
			dto.setDateOfHospitalisation(entity.getDateOfHospitalisation());
			dto.setOther_Details(entity.getOther_Details());
			// Handle null values for Product and ClientList entities
			dto.setProductId(entity.getProductId().getProductId().toString());
			dto.setClientListId(String.valueOf(entity.getClientListId().getCid()));
			dto.setSumInsured(entity.getSumInsured());
			dto.setReasonforClaim(entity.getReasonforClaim());
			dto.setRfqId(entity.getRfqId());
			dto.setEmployeeDataID(entity.getEmployeeDataID()); // Assuming employeeID is a String in your DTO

			return dto;
		}).toList();
	}
}
