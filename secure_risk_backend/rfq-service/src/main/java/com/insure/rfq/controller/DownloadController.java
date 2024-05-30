package com.insure.rfq.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.ClientListCoverageDetailsDto;
import com.insure.rfq.generator.RFQReportPDfGenerator;
import com.insure.rfq.generator.SubmitClaimPDFGenerator;
import com.insure.rfq.repository.EmployeeRepository;
import com.insure.rfq.service.CorporateDetailsService;
import com.insure.rfq.service.DownloadService;
import com.itextpdf.text.DocumentException;

@RestController
@RequestMapping("/rfq/download")
@CrossOrigin(value = { "*" })
public class DownloadController {
	@Autowired
	private RFQReportPDfGenerator pDfGenerator;

	@Autowired
	private DownloadService downloadService;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private CorporateDetailsService service;
	@Autowired
	private SubmitClaimPDFGenerator pdfGenerator;

	@GetMapping("/pdf/{id}")
	public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
		byte[] generatePdf = pDfGenerator.generatePdf(id);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, " attachment;filename:data.pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(generatePdf);

	}

	@GetMapping("/coveragePdf/{rfqId}")
	public ResponseEntity<byte[]> downloadCoveragePdf(@PathVariable String rfqId)
			throws IOException, DocumentException {
		byte[] generatePdf = downloadService.generateCoverageDetails(rfqId);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, " attachment;filename:data.pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(generatePdf);
	}

	@GetMapping("/employeeData")
	public ResponseEntity<Resource> downloadEmpDataPdf(@RequestParam String id) {

		byte[] downloadClaimMisc = service.getEmployeeData(id);
		ByteArrayResource resource = new ByteArrayResource(downloadClaimMisc);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.add(HttpHeaders.CONTENT_DISPOSITION, " attachment;filename:empdata.xlsx");
		return ResponseEntity.ok().headers(headers).contentLength(downloadClaimMisc.length).body(resource);
	}

	@GetMapping("/irda")
	public ResponseEntity<byte[]> downloadIrdaPdf(@RequestParam String rfqId) throws IOException {
		byte[] irdaData = service.getIrdaData(rfqId);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, " attachment;filename:empdata.pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(irdaData);
	}

	@PostMapping("/clientListCoverage")
	public ResponseEntity<byte[]> downloadClientListCoveragePdf(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestBody List<ClientListCoverageDetailsDto> clientListCoverageDetailsDto)
			throws IOException, DocumentException {
		byte[] generatePdf = downloadService.generateClientListCoverageDetails(clientListId, productId,
				clientListCoverageDetailsDto);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, " attachment;filename:data.pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(generatePdf);
	}

	@GetMapping("/generatePdfForSubmitClaims")
	public ResponseEntity<byte[]> generateClaimPdf(@RequestParam String id) {
		try {
			byte[] pdfBytes = pdfGenerator.generatePDF(id);

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=claim_" + id + ".pdf");
			headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

			return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/generateAndEmailPDF")
	public String generateAndEmailPDF(@RequestParam String id, @RequestBody List<String> emailRecipients) {
		pdfGenerator.generateAndSendPDF(id, emailRecipients);
		return "PDF generated and emailed successfully.";
	}
}
