package com.insure.rfq.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.ClientListEmployee_E_CashlessDisplayDto;
import com.insure.rfq.dto.ClientListEmployee_E_CashlessDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientListEmployee_E_Cashless;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientListEmployee_E_CashlessRepository;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.EmployeeRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ClientListEmployee_E_CashlessService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientListEmployee_E_CashlessServiceImpl implements ClientListEmployee_E_CashlessService {
	@Value("${file.path.coverageMain}")
	private String mainpath;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private ClientListEmployee_E_CashlessRepository eCashlessRepository;

	@Override
	public String saveClientList_E_Cashless(ClientListEmployee_E_CashlessDto dto, Long clientID, Long productId,
			Long employeeId) {
		String message = "";

		ClientListEmployee_E_Cashless entity_E_Cashless = new ClientListEmployee_E_Cashless();
		entity_E_Cashless.setHospitalization(dto.getHospitalization());
		entity_E_Cashless.setSearchNetworkHospital(dto.getSearchNetworkHospital());
		entity_E_Cashless.setPlannedDateOfAdmission(dto.getPlannedDateOfAdmission());
		entity_E_Cashless.setTreatment(dto.getTreatment());
		entity_E_Cashless.setFullNameOfYourTreatingDoctor(dto.getFullNameOfYourTreatingDoctor());
		entity_E_Cashless.setLatestInvestigationReportsOfYourDiagnosis(
				retunFilePath(dto.getLatestInvestigationReportsOfYourDiagnosis()));
		entity_E_Cashless.setLastDoctorConsultationNote(retunFilePath(dto.getLastDoctorConsultationNote()));
		entity_E_Cashless.setPatientIdentityNumber(dto.getPatientIdentityNumber());
		entity_E_Cashless.setPatientIdentityProof(retunFilePath(dto.getPatientIdentityProof()));
		entity_E_Cashless.setOtherMedicalDocuments(retunFilePath(dto.getOtherMedicalDocuments()));
		entity_E_Cashless.setMobileNumber(dto.getMobileNumber());
		entity_E_Cashless.setRoomType(dto.getRoomType());
		entity_E_Cashless.setPlanned_DateOfDischarge(dto.getPlanned_DateOfDischarge());
		entity_E_Cashless.setProsedTreatment(dto.getProsedTreatment());
		entity_E_Cashless.setOut_PatientNumber(dto.getOut_PatientNumber());
		entity_E_Cashless.setRecordStatus("ACTIVE");
		entity_E_Cashless.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		if (productId != null) {
			Product product1 = productRepository.findById(productId)
					.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
			entity_E_Cashless.setProductId(product1);
			// log.info( " product is : "+ product1 + " ===" );
		}
		if (clientID != null) {
			ClientList clientList = clientListRepository.findById(clientID)
					.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
			entity_E_Cashless.setClientListId(clientList);
			entity_E_Cashless.setRfqId(clientList.getRfqId());
		}
		if (employeeId != null) {
			log.info(" Employee id  {}: ", employeeId);
			entity_E_Cashless.setEmployeeID(String.valueOf(employeeId));
		}

		ClientListEmployee_E_Cashless save = eCashlessRepository.save(entity_E_Cashless);
		if (save != null) {
			message = "ClientListEmployee_ECashless Data is Saved Successfully";
		} else
			message = "ClientListEmployee_ECashless Data is Not saved ";

		return message;

	}

	public String retunFilePath(MultipartFile file) {
		try {
			// Generate a unique file name
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

			// Create the directory if it doesn't exist
			Path directory = Paths.get(mainpath);

			if (!Files.exists(directory)) {
				Files.createDirectories(directory);
			}

			// Save the file to the local machine
			Path filePath = Paths.get(mainpath, fileName);
			Files.copy(file.getInputStream(), filePath);

			log.info(filePath + "      -----------path ......");
			// Return the path of the saved file
			return filePath.toString();
		} catch (IOException e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}

	@Override
	public List<ClientListEmployee_E_CashlessDisplayDto> getAllClientListEmployeeECashlessDtoList() {
		return eCashlessRepository.findAll().stream().map(entity -> {
			ClientListEmployee_E_CashlessDisplayDto dto = new ClientListEmployee_E_CashlessDisplayDto();
			dto.setHospitalization(entity.getHospitalization());
			dto.setSearchNetworkHospital(entity.getSearchNetworkHospital());
			dto.setPlannedDateOfAdmission(entity.getPlannedDateOfAdmission());
			dto.setTreatment(entity.getTreatment());
			dto.setFullNameOfYourTreatingDoctor(entity.getFullNameOfYourTreatingDoctor());
			dto.setLatestInvestigationReportsOfYourDiagnosis(entity.getLatestInvestigationReportsOfYourDiagnosis());
			dto.setLastDoctorConsultationNote(entity.getLastDoctorConsultationNote());
			dto.setPatientIdentityNumber(entity.getPatientIdentityNumber());
			dto.setPatientIdentityProof(entity.getPatientIdentityProof());
			dto.setOtherMedicalDocuments(entity.getOtherMedicalDocuments());
			dto.setMobileNumber(entity.getMobileNumber());
			dto.setRoomType(entity.getRoomType());
			dto.setPlanned_DateOfDischarge(entity.getPlanned_DateOfDischarge());
			dto.setProsedTreatment(entity.getProsedTreatment());
			dto.setOut_PatientNumber(entity.getOut_PatientNumber());

			dto.setProductId(entity.getProductId().getProductId().toString());
			dto.setClientListId(String.valueOf(entity.getClientListId().getCid()));

			dto.setRfqId(entity.getRfqId());
			dto.setEmployeeID(entity.getEmployeeID()); // Assuming employeeID is a String in your DTO

			return dto;
		}).toList(); // Collect DTOs into a list
	}

}
