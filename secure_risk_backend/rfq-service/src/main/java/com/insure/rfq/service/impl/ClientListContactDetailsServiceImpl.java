package com.insure.rfq.service.impl;

import com.insure.rfq.dto.ClientListContactDetailsDto;
import com.insure.rfq.dto.ResponseDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientListContactDetails;
import com.insure.rfq.entity.ClientListRoleInfo;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.*;
import com.insure.rfq.login.entity.Designation;
import com.insure.rfq.login.repository.DesignationRepository;
import com.insure.rfq.repository.ClientListContactDetailsRepository;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ClientListRoleInfoRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ClientListContactDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ClientListContactDetailsServiceImpl implements ClientListContactDetailsService {

	@Autowired
	private ClientListContactDetailsRepository clientListContactDetailsRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private DesignationRepository designationRepository;
	@Autowired
	private ClientListRoleInfoRepository clientListRoleInfoRepository;

	@Override
	public ResponseDto createContactDetails(Long clientListId, Long productId,
			ClientListContactDetailsDto clientListContactDetailsDto) {
		ClientListContactDetails clientListContactDetails = new ClientListContactDetails();
		StringBuilder errorMessages = new StringBuilder();
		if (clientListId != null) {
			try {
				ClientList clientList = clientListRepository.findById(clientListId)
						.orElseThrow(() -> new InvalidClientList("Invalid ClientList"));
				clientListContactDetails.setClientList(clientList);
				clientListContactDetails.setRfqId(clientList.getRfqId());
			} catch (InvalidClientList e) {
				errorMessages.append("ClientList is not Found with Id : ").append(clientListId).append(" . ");
			}
		}
		if (productId != null) {
			try {
				Product product = productRepository.findById(productId)
						.orElseThrow(() -> new InvalidProduct("Invalid Product"));
				clientListContactDetails.setProduct(product);
			} catch (InvalidProduct e) {
				errorMessages.append("Product is not Found with Id : ").append(productId).append(" . ");
			}
		}
		if (clientListContactDetailsDto.getDesignation() != null) {
			try {
				Designation designation = designationRepository
						.findById(Long.parseLong(clientListContactDetailsDto.getDesignation()))
						.orElseThrow(() -> new InvalidDesignationException("Invalid DesignationId"));
				clientListContactDetails.setDesignation(designation);
			} catch (InvalidDesignationException e) {
				errorMessages.append("Designation is not Found with Id : ")
						.append(clientListContactDetailsDto.getDesignation()).append(" . ");
			}
		}
		if (clientListContactDetailsDto.getRole() != null) {
			try {
				ClientListRoleInfo clientListRoleInfo = clientListRoleInfoRepository
						.findById(Long.parseLong(clientListContactDetailsDto.getRole()))
						.orElseThrow(() -> new InvalidClientListRoleException("Invalid Role"));
				clientListContactDetails.setRoleInfo(clientListRoleInfo);
			} catch (InvalidClientListRoleException e) {
				errorMessages.append("Role is not Found with Id : ").append(clientListContactDetailsDto.getRole())
						.append(" . ");
			}
		}
		clientListContactDetails.setEmployeeId(clientListContactDetailsDto.getEmployeeId());
		clientListContactDetails.setName(clientListContactDetailsDto.getName());
		clientListContactDetails.setEmail(clientListContactDetailsDto.getEmail());
		clientListContactDetails.setPhoneNumber(clientListContactDetailsDto.getPhoneNumber());
		clientListContactDetails.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		clientListContactDetails.setRecordStatus("ACTIVE");

		if (errorMessages.length() == 0) {
			clientListContactDetailsRepository.save(clientListContactDetails);
		}
		if (errorMessages.length() > 0) {
			return new ResponseDto(errorMessages.toString());
		}
		return new ResponseDto("Created Sucessfully");
	}

	@Override
	public List<ClientListContactDetailsDto> getAllContactDetails(Long clientListId, Long productId) {
		List<ClientListContactDetailsDto> listContactDetails = clientListContactDetailsRepository.findAll().stream()
				.filter(c -> c.getRecordStatus().equalsIgnoreCase("ACTIVE") && c.getClientList() != null
						&& c.getClientList().getCid() == clientListId && c.getProduct() != null
						&& c.getProduct().getProductId().equals(productId))
				.map(c -> {
					ClientListContactDetailsDto clientListContactDetailsDto = new ClientListContactDetailsDto();
					clientListContactDetailsDto.setContactId(c.getContactId());
					clientListContactDetailsDto.setEmployeeId(c.getEmployeeId());
					clientListContactDetailsDto.setName(c.getName());
					clientListContactDetailsDto.setEmail(c.getEmail());
					clientListContactDetailsDto.setPhoneNumber(c.getPhoneNumber());
					if (c.getDesignation() != null) {
						clientListContactDetailsDto.setDesignation(c.getDesignation().getDesignationName());
					} else {
						clientListContactDetailsDto.setDesignation(null);
					}
					if (c.getRoleInfo() != null) {
						clientListContactDetailsDto.setRole(c.getRoleInfo().getRole());
					} else {
						clientListContactDetailsDto.setRole(null);
					}
					return clientListContactDetailsDto;
				}).toList();
		if (listContactDetails.isEmpty()) {
			throw new InvalidClientListContactDetailsException("No Details Available For ClientList and Product");
		}
		return listContactDetails;
	}

	@Override
	public ClientListContactDetailsDto getContactDetailsByContactId(Long contactId) {
		if (contactId != null) {
			ClientListContactDetails clientListContactDetails = clientListContactDetailsRepository.findById(contactId)
					.orElseThrow(() -> new InvalidClientListContactDetailsException("Invalid Contact Details"));
			ClientListContactDetailsDto clientListContactDetailsDto = new ClientListContactDetailsDto();
			clientListContactDetailsDto.setContactId(clientListContactDetails.getContactId());
			clientListContactDetailsDto.setEmployeeId(clientListContactDetails.getEmployeeId());
			clientListContactDetailsDto.setEmail(clientListContactDetails.getEmail());
			clientListContactDetailsDto.setName(clientListContactDetails.getName());
			clientListContactDetailsDto.setPhoneNumber(clientListContactDetails.getPhoneNumber());
			if (clientListContactDetails.getDesignation() != null) {
				clientListContactDetailsDto
						.setDesignation(clientListContactDetails.getDesignation().getDesignationName());
			} else {
				clientListContactDetailsDto.setDesignation(null);
			}
			if (clientListContactDetails.getRoleInfo() != null) {
				clientListContactDetailsDto.setRole(clientListContactDetails.getRoleInfo().getRole());
			} else {
				clientListContactDetailsDto.setRole(null);
			}
			return clientListContactDetailsDto;
		}
		return null;
	}

	@Override
	public ClientListContactDetailsDto updateContactDetailsByContactId(Long contactId,
			ClientListContactDetailsDto clientListContactDetailsDto) {
		if (contactId != null) {
			ClientListContactDetails clientListContactDetails = clientListContactDetailsRepository.findById(contactId)
					.orElseThrow(() -> new InvalidClientListContactDetailsException("Invalid ContactDetails"));
			clientListContactDetails.setEmployeeId(clientListContactDetailsDto.getEmployeeId());
			clientListContactDetails.setEmail(clientListContactDetailsDto.getEmail());
			clientListContactDetails.setName(clientListContactDetailsDto.getName());
			clientListContactDetails.setPhoneNumber(clientListContactDetailsDto.getPhoneNumber());
			if (clientListContactDetailsDto.getDesignation() != null) {
				Designation designation = designationRepository
						.findById(Long.parseLong(clientListContactDetailsDto.getDesignation()))
						.orElseThrow(() -> new InvalidDesignationException("Invalid Designation"));
				clientListContactDetails.setDesignation(designation);

			} else {
				clientListContactDetailsDto.setDesignation(null);
			}
			ClientListContactDetails savedClientListContactDetails = clientListContactDetailsRepository
					.save(clientListContactDetails);
			clientListContactDetailsDto.setEmployeeId(savedClientListContactDetails.getEmployeeId());
			clientListContactDetailsDto.setName(savedClientListContactDetails.getName());
			clientListContactDetailsDto.setEmail(savedClientListContactDetails.getEmail());
			clientListContactDetailsDto.setPhoneNumber(savedClientListContactDetails.getPhoneNumber());
			if (savedClientListContactDetails.getDesignation() != null) {
				clientListContactDetailsDto
						.setDesignation(savedClientListContactDetails.getDesignation().getDesignationName());
			} else {
				clientListContactDetailsDto.setDesignation(null);
			}
			return clientListContactDetailsDto;
		}
		return null;
	}

	@Override
	public String deleteContactDetailsByContactId(Long contactId) {
		if (contactId != null) {
			ClientListContactDetails clientListContactDetails = clientListContactDetailsRepository.findById(contactId)
					.orElseThrow(() -> new InvalidClientListContactDetailsException("Invalid Contact Details"));
			clientListContactDetails.setRecordStatus("INACTIVE");
			clientListContactDetailsRepository.save(clientListContactDetails);
			return "Deleted Successfully";
		}
		return "Invalid ContactDetails";
	}
}
