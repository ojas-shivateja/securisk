package com.insure.rfq.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rfq/excel")
@CrossOrigin(origins = { "*" })
public class ExcelUploadController {

	@Value("classpath:excelTemplate/templateEmployee.xlsx")
	Resource resourceFile;

	@GetMapping("/getDownloadedTemplate")
	@Transactional(readOnly = true)
	public ResponseEntity<ByteArrayResource> downloadExcelFile() throws IOException {

		ByteArrayResource resource = new ByteArrayResource(resourceFile.getContentAsByteArray());

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RfqEmployeeDataFile.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.contentLength(resourceFile.getContentAsByteArray().length).body(resource);
	}
}
