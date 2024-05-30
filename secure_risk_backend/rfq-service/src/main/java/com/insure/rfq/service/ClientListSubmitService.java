package com.insure.rfq.service;

import com.insure.rfq.entity.ClientListSubmitClaims;
import com.insure.rfq.entity.RequiredDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ClientListSubmitService {

	public ClientListSubmitClaims saveRequiredDocument(RequiredDocument documentType, String employeeId,
			MultipartFile file, String userId) throws IOException;
}
