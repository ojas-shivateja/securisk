package com.insure.rfq.controller;

import com.insure.rfq.dto.*;
import com.insure.rfq.service.ClientDetailsClaimsMisService;
import com.insure.rfq.service.ClientListClaimsMisAnalysisService;
import com.insure.rfq.service.CoverageDetailsService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.insure.rfq.generator.ClientMisAnalyticsPdfGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ClientDetails/ClaimsMis")
@CrossOrigin(origins = "*")
public class ClientDetailsClaimsMisController {

	@Autowired
	private ClientDetailsClaimsMisService clientDetailsClaimsMisService;

	@Autowired
	private ClientMisAnalyticsPdfGenerator clientMisAnalyticsPdfGenerator;

	@Autowired
	private ClientListClaimsMisAnalysisService clientListClaimsMisAnalysis;

	@Autowired
	private CoverageDetailsService service;

	@PostMapping("/createClaimsMis")
	public ResponseEntity<String> createCoverage(@ModelAttribute ClaimsMisClientDetailsDto coverageDto,
			@RequestParam Long ClientId) {
		if (coverageDto != null) {
			String rfqId = String.valueOf(clientDetailsClaimsMisService.create(coverageDto, ClientId));
			return new ResponseEntity<>(rfqId, HttpStatus.CREATED);
		}
		return null;
	}

	@PostMapping("/uploadFile")
	public ResponseEntity<String> uploadCoverageDetails(
			@ModelAttribute ClientDetailsClaimsMisUploadDto coverageUploadDto, @RequestParam Long clientlistId,
			@RequestParam Long productId) {
		if (!coverageUploadDto.getFile().isEmpty()) {
			String filePath = clientDetailsClaimsMisService.uploadFileCoverage(coverageUploadDto, clientlistId,
					productId);
			return new ResponseEntity<>(filePath, HttpStatus.OK);
		}
		return new ResponseEntity<>("No File Found", HttpStatus.NOT_FOUND);
	}

	@GetMapping("/getClientListClaimsUploadData")
	public ResponseEntity<List<ClaimsMisNewDto>> getClientListClaimsUploadData(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestParam String month) {
		return ResponseEntity.ok(clientDetailsClaimsMisService.getDataWithStatus(clientListId, productId, month));
	}

	@GetMapping("/statusCounts")
	public ResponseEntity<Map<String, Long>> getStatusCounts(@RequestParam Long clientListId,
			@RequestParam Long productId) {
		Map<String, Long> statusCounts = (Map<String, Long>) clientDetailsClaimsMisService.getStatusCounts(clientListId,
				productId);
		return ResponseEntity.ok().body(statusCounts);
	}

	@GetMapping("/getAllClaimsMis")
	public ResponseEntity<List<ClaimsMisNewDto>> getAllClaimsMis(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestParam String month) {
		return new ResponseEntity<>(
				clientDetailsClaimsMisService.getAllClaimsMisByRfqId(clientListId, productId, month), HttpStatus.OK);
	}

	@PostMapping("/headerClaimsMiscValidation")
	public ResponseEntity<CovergaeHeaderValidateDto> claimsMisHeaderValidation(@RequestPart MultipartFile file,
			@RequestParam String tpaName) {
		CovergaeHeaderValidateDto validateDto = clientDetailsClaimsMisService.validateClaimsMisHeader(file, tpaName);
		return new ResponseEntity<>(validateDto, HttpStatus.OK);
	}

	@PostMapping("/getAllClaimsMisWithStatus")
	public ResponseEntity<List<ClaimsMisDataStatusValidateDto>> claimsMisDataValidation(@RequestPart MultipartFile file,
			@RequestParam String tpaName) {
		List<ClaimsMisDataStatusValidateDto> validateClaimsMisDataWithStatus = clientDetailsClaimsMisService
				.validateClaimsMisDataWithStatus(file, tpaName);
		if (!validateClaimsMisDataWithStatus.isEmpty()) {
			return new ResponseEntity<>(validateClaimsMisDataWithStatus, HttpStatus.OK);
		}
		return new ResponseEntity<>(validateClaimsMisDataWithStatus, HttpStatus.NO_CONTENT);
	}

	@GetMapping("/clientDetailsClaimsMisDetailsExport")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<byte[]> getAllMembersDetailsByExcelFormatByClientListProduct(

			@RequestParam Long clientListId, @RequestParam Long productId) {

		byte[] excelData = clientDetailsClaimsMisService.generateExcelFromData(clientListId, productId);

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

		headers.setContentDispositionFormData("attachment", "Client_Details_Claims_Mis.xlsx");

		return new ResponseEntity<>(excelData, headers, HttpStatus.OK);

	}

	@GetMapping("/clientClaimsTotalCount")
	public ClientListClaimsTotalCountDto getClientClaimsTotalCount(
			@RequestParam(name = "clientlistId") Long clientlistId, @RequestParam(name = "productId") Long productId) {
		return clientDetailsClaimsMisService.getCountByStatus(clientlistId, productId);
	}

	@GetMapping("/getClientDetailsClaimAnalysisReport")
	public ResponseEntity<byte[]> dowloadDemoGraph(@RequestParam(name = "clientList") Long clientList,
			@RequestParam(name = "product") Long product) throws IOException, DocumentException {
		byte[] chartImage = clientMisAnalyticsPdfGenerator.generatePdf(clientList, product);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "claim_analysis_report.pdf");
		return new ResponseEntity<>(chartImage, headers, HttpStatus.OK);
	}

	@GetMapping("/getClaimsForEmployeeData")
	public ResponseEntity<List<ClaimsMisNewDto>> getClaimsForEmployeeData(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestParam String employeeId) {
		return ResponseEntity
				.ok(clientDetailsClaimsMisService.getClaimsForEmployee(clientListId, productId, employeeId));
	}

	@GetMapping("/getAllClaimsForEmployeeDataListInExcel")
	public ResponseEntity<byte[]> getAllClaimsForEmployeeDataListInExcel(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestParam String employeeId) {

		byte[] claimsForEmployeeInExcelFormat = clientDetailsClaimsMisService
				.getClaimsForEmployeeInExcelFormat(clientListId, productId, employeeId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "client_Details_claim_Mis.xlsx");

		return new ResponseEntity<>(claimsForEmployeeInExcelFormat, headers, HttpStatus.OK);

	}

	@GetMapping("/getStatusCountForEmployee")
	public ResponseEntity<Map<String, Long>> getStatusCountForEmployee(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestParam String employeeId) {
		Map<String, Long> statusCounts = (Map<String, Long>) clientDetailsClaimsMisService
				.getStatusCountForEmployee(clientListId, productId, employeeId);
		return ResponseEntity.ok().body(statusCounts);
	}

	@GetMapping("/clientListGenarateAnalytics")
	public ClaimAnayalisMisReportDto getClaimsMISAnalytics(@RequestParam Long clientListId,
			@RequestParam Long productId) {
		return clientListClaimsMisAnalysis.generateReport(clientListId, productId);
	}

	@GetMapping("/getClaimTracker")
	public ResponseEntity<List<ClientListClaimsTrackerDto>> getClaimDetailsForEmployee(@RequestParam Long clientId,
			@RequestParam Long productId, @RequestParam String employeeId) {
		List<ClientListClaimsTrackerDto> claimDetails = clientDetailsClaimsMisService
				.getClaimDetailsForEmployee(clientId, productId, employeeId);

		return new ResponseEntity<>(claimDetails, HttpStatus.OK);

	}

}
