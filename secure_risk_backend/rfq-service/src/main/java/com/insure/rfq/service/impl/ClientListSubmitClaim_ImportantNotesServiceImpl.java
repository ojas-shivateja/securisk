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

import com.insure.rfq.dto.Declarationandclaim_Submission_ImportantNotesDto;
import com.insure.rfq.dto.ImportantNotesDisplayDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientListEmployee_Submit_Claim_User_Details;
import com.insure.rfq.entity.ClientList_Submission_ImportantNotes;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ClientList_Submission_ImportantNotesRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ClientListSubmission_ImportantNotesService;

import lombok.extern.slf4j.Slf4j;

@Service

@Slf4j
public class ClientListSubmitClaim_ImportantNotesServiceImpl implements ClientListSubmission_ImportantNotesService {

	@Value("${file.path.coverageMain}")
	private String mainpath;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;

	@Autowired
	private ClientList_Submission_ImportantNotesRepository imortantnotesrepository;

	@Override
	public String sbmitClaim_Declarationandclaim_Submission_ImportantNotesCreation(
			Declarationandclaim_Submission_ImportantNotesDto dto, Long clientId, Long productId, Long employeeId) {
		String message;
		ClientList_Submission_ImportantNotes entity = new ClientList_Submission_ImportantNotes();
		/* proof_id_Type; id_proof; id_proofUpload */
		entity.setId_proof(dto.getId_proof());
		entity.setProof_id_Type(dto.getProof_id_Type());
		entity.setId_proofUpload(retunFilePath(dto.getId_proofUpload()));
		entity.setRecordStatus("ACTIVE");

		entity.setUser_detailsId(dto.getUser_detailsId());
		String uiid = UUID.randomUUID().toString();
		entity.setDeclaration_submisssion_Id(uiid);
		entity.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		if (productId != null) {
			Product product1 = productRepository.findById(productId)
					.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
			entity.setProductId(product1);
			// log.info( " product is : "+ product1 + " ===" );
		}
		if (clientId != null) {
			ClientList clientList = clientListRepository.findById(clientId)
					.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
			entity.setClientListId(clientList);
			entity.setRfqId(clientList.getRfqId());
		}
		if (employeeId != null) {
			// employeeRepository.findByEmployeeId(String.valueOf(employeeId));
			// log.info(" Employee id {}: ", employeeId);
			entity.setEmployeeDataID(String.valueOf(employeeId));
		}
		ClientList_Submission_ImportantNotes savedEntity = imortantnotesrepository.save(entity);
		if (savedEntity != null) {
			message = savedEntity.getUser_detailsId();
		} else
			message = "ClientList_Submission_ImportantNotes Data is Not saved ";

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

	public ClientListEmployee_Submit_Claim_User_Details mapDtoToEntity(ImportantNotesDisplayDto dto) {
		ClientListEmployee_Submit_Claim_User_Details entity = new ClientListEmployee_Submit_Claim_User_Details();
		// Map the fields from DTO to entity
		entity.setPatientName(dto.getProof_id_Type());
		entity.setEmployeeName(dto.getId_proof());
		// Map other fields as needed
		return entity;
	}

	public ImportantNotesDisplayDto mapEntityToDto(ClientList_Submission_ImportantNotes entity) {
		ImportantNotesDisplayDto dto = new ImportantNotesDisplayDto();
		dto.setId(entity.getDeclaration_submission_imported_Documents_Id());
		dto.setUser_detailsId(entity.getUser_detailsId());
		// Map the fields from entity to DTO
		dto.setProof_id_Type(entity.getProof_id_Type());
		dto.setId_proof(entity.getId_proof());
		dto.setId_proofUpload(entity.getId_proofUpload());
		// Map other fields as needed
		return dto;
	}

	@Override
	public List<ImportantNotesDisplayDto> getAllImportantNotesDisplayDto() {
		// TODO Auto-generated method stub
		return imortantnotesrepository.findAll().stream().map(i -> {
			return mapEntityToDto(i);
		}).toList();
	}
}
