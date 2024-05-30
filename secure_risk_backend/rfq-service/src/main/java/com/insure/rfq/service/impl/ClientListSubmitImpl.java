package com.insure.rfq.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.entity.ClientListSubmitClaims;
import com.insure.rfq.entity.RequiredDocument;
import com.insure.rfq.repository.ClientListSubmitClaimsRepository;
import com.insure.rfq.service.ClientListSubmitService;

@Service
public class ClientListSubmitImpl implements ClientListSubmitService {

	@Autowired

	private ClientListSubmitClaimsRepository clientListSubmitClaimsRepository;

	@Override

	public ClientListSubmitClaims saveRequiredDocument(RequiredDocument documentType, String employeeId,
			MultipartFile file, String UserID) throws IOException {

		ClientListSubmitClaims requiredDocumentEntity = new ClientListSubmitClaims();
//       

		requiredDocumentEntity.setDocumentType(documentType);
		requiredDocumentEntity.setEmployeeId(employeeId);
		requiredDocumentEntity.setFileName(file.getOriginalFilename());

		requiredDocumentEntity.setUserDetailsId(UserID);

		requiredDocumentEntity.setCreateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		requiredDocumentEntity.setRecordStatus("ACTIVE");
		try {
			requiredDocumentEntity.setFileData(file.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		ClientListSubmitClaims save = clientListSubmitClaimsRepository.save(requiredDocumentEntity);
		return save;

	}
}
