package com.insure.rfq.service.impl;

import com.insure.rfq.dto.ClientListEmployee_Submit_ClaimHospitalizationDetailsDto;

import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientListEmployee_Submit_ClaimHospitalizationDetails;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientListEmployee_Submit_ClaimHospitalizationDetailsRepository;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ClientListEmployee_Submit_ClaimHospitalizationDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ClientListEmployee_Submit_ClaimHospitalizationDetailsServiceImpl
		implements ClientListEmployee_Submit_ClaimHospitalizationDetailsService {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private ClientListEmployee_Submit_ClaimHospitalizationDetailsRepository repository;

	@Override
	public String crateClientListEmployee_Submit_ClaimHospitalizationDetails(
			ClientListEmployee_Submit_ClaimHospitalizationDetailsDto dto, Long clientID, Long productId,
			Long employeeId) {
		ClientListEmployee_Submit_ClaimHospitalizationDetails entity = new ClientListEmployee_Submit_ClaimHospitalizationDetails();
		String message;
		entity.setUser_detailsId(dto.getUser_detailsId());
		entity.setState(dto.getState());
		entity.setCity(dto.getCity());
		entity.setHospitalName(dto.getHospitalName());
		entity.setHospitalAddress(dto.getHospitalAddress());
		entity.setNatureofIllness(dto.getNatureofIllness());
		entity.setPreHospitalizationAmount(dto.getPreHospitalizationAmount());
		entity.setPostHospitalizationAmount(dto.getPostHospitalizationAmount());
		entity.setTotalAmountClaimed(dto.getTotalAmountClaimed());
		entity.setHospitalizationAmount(dto.getHospitalizationAmount());
		entity.setMedicalExpenses_BillNo(dto.getMedicalExpenses_BillNo());
		entity.setMedicalExpensesBillAmount(dto.getMedicalExpensesBillAmount());
		entity.setMedicalExpenses_BillDate(dto.getMedicalExpenses_BillDate());
		entity.setMedicalExpensesRemarks(dto.getMedicalExpensesRemarks());
		entity.setRecordStatus("ACTIVE");
		entity.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		if (productId != null) {
			Product product1 = productRepository.findById(productId)
					.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
			entity.setProductId(product1);
		}
		if (clientID != null) {
			ClientList clientList = clientListRepository.findById(clientID)
					.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
			entity.setClientListId(clientList);
			entity.setRfqId(clientList.getRfqId());
		}
		if (employeeId != null) {
			log.info(" Employee id  {}: ", employeeId);
			entity.setEmployeeDataID(String.valueOf(employeeId));
		}
		ClientListEmployee_Submit_ClaimHospitalizationDetails saveentity = repository.save(entity);
		if (saveentity != null) {
			message = saveentity.getUser_detailsId();

		} else
			message = "ClientListEmployee_Submit_ClaimHospitalizationDetails Data is Not saved ";

		return message;
	}

	@Override
	public List<ClientListEmployee_Submit_ClaimHospitalizationDetailsDto> getAllClientListEmployeeSubmitClaimHospitalizationDetailsDtos() {
		return repository.findAll().stream().map(entity -> {
			return mapEntityToDto(entity);
		}).toList();

	}

	public static ClientListEmployee_Submit_ClaimHospitalizationDetailsDto mapEntityToDto(
			ClientListEmployee_Submit_ClaimHospitalizationDetails entity) {
		ClientListEmployee_Submit_ClaimHospitalizationDetailsDto dto = new ClientListEmployee_Submit_ClaimHospitalizationDetailsDto();

		dto.setUser_detailsId(entity.getUser_detailsId());
		dto.setState(entity.getState());
		dto.setCity(entity.getCity());
		dto.setHospitalName(entity.getHospitalName());
		dto.setHospitalAddress(entity.getHospitalAddress());
		dto.setNatureofIllness(entity.getNatureofIllness());
		dto.setPreHospitalizationAmount(entity.getPreHospitalizationAmount());
		dto.setPostHospitalizationAmount(entity.getPostHospitalizationAmount());
		dto.setTotalAmountClaimed(entity.getTotalAmountClaimed());
		dto.setHospitalizationAmount(entity.getHospitalizationAmount());
		dto.setMedicalExpenses_BillNo(entity.getMedicalExpenses_BillNo());
		dto.setMedicalExpensesBillAmount(entity.getMedicalExpensesBillAmount());
		dto.setMedicalExpenses_BillDate(entity.getMedicalExpenses_BillDate());
		dto.setMedicalExpensesRemarks(entity.getMedicalExpensesRemarks());

		return dto;
	}

}
