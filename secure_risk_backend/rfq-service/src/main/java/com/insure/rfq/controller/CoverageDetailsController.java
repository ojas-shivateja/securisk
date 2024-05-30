package com.insure.rfq.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.ClaimsMisDataStatusValidateDto;
import com.insure.rfq.dto.ClaimsUploadDto;
import com.insure.rfq.dto.CoverageDetailsChildValidateValuesDto;
import com.insure.rfq.dto.CoverageDetailsDto;
import com.insure.rfq.dto.CoverageUploadDto;
import com.insure.rfq.dto.CoverageValidateFilenamesDto;
import com.insure.rfq.dto.CovergaeHeaderValidateDto;
import com.insure.rfq.dto.DownloadTemplateAttachementDto;
import com.insure.rfq.dto.EmpDepdentValidationDto;
import com.insure.rfq.entity.ClaimsMisEntity;
import com.insure.rfq.entity.CoverageDetailsEntity;
import com.insure.rfq.entity.CoverageValidateFilenames;
import com.insure.rfq.entity.EmployeeDepedentDetailsEntity;
import com.insure.rfq.payload.DataToEmail;
import com.insure.rfq.service.ClaimsMisService;
import com.insure.rfq.service.CoverageDetailsService;
import com.insure.rfq.service.CoverageValidateFilenamesService;
import com.itextpdf.text.DocumentException;

@RestController
@RequestMapping("/rfq/coverage")
@CrossOrigin(origins = "*")
public class CoverageDetailsController {

	@Autowired
	private CoverageDetailsService service;
	@Autowired
	private CoverageValidateFilenamesService fileNamesService;
	@Autowired
	private ClaimsMisService claimsMisService;

	@PostMapping("/createCoverage")
	public ResponseEntity<String> createCoverage(@RequestBody CoverageDetailsDto coverageDto) {
		if (coverageDto != null) {
			String rfqId = service.createCoverageDetails(coverageDto);
			return new ResponseEntity<>(rfqId, HttpStatus.CREATED);
		}
		return null;
	}

	@PutMapping("/updateCovergae")
	public ResponseEntity<CoverageDetailsEntity> updateCoverageDetails(@RequestParam("rfqId") String rfqId,
			@RequestBody CoverageDetailsDto coverageDto) {
		if (coverageDto != null) {
			CoverageDetailsEntity updateCoverageDetails = service.updateCoverageDetails(rfqId, coverageDto);
			return new ResponseEntity<>(updateCoverageDetails, HttpStatus.OK);
		}
		return null;
	}

	@GetMapping("/fileNames")
	public ResponseEntity<List<CoverageValidateFilenames>> getFilenames() {
		return new ResponseEntity<>(fileNamesService.getCoverageValidateFilenames(), HttpStatus.CREATED);
	}

	@PostMapping("/addFileNames")
	public ResponseEntity<?> addFilenames(@RequestBody CoverageValidateFilenamesDto filenamesDto) {
		return new ResponseEntity<>(fileNamesService.addCoverageValidateFilenames(filenamesDto), HttpStatus.CREATED);
	}

	@GetMapping("/employeeData/download")
	public ResponseEntity<byte[]> downloadPdf() {
		byte[] employeeData = service.getEmployeeData();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, " attachment;filename:empdata.pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(employeeData);
	}

	@GetMapping("/employeeData/irda")
	public ResponseEntity<byte[]> downloadIrdaPdf() throws IOException {
		byte[] irdaData = service.getIrdaData();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, " attachment;filename:empdata.pdf");
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(irdaData);
	}

	@PostMapping("/uploadFile")
	public ResponseEntity<String> uploadCoverageDetails(@ModelAttribute CoverageUploadDto coverageUploadDto) {
		if (!coverageUploadDto.getFile().isEmpty()) {
			String filePath = service.uploadFileCoverage(coverageUploadDto);
			return new ResponseEntity<>(filePath, HttpStatus.OK);
		}
		return new ResponseEntity<>("No File Found", HttpStatus.NOT_FOUND);
	}

	@GetMapping("/getAllEmpDepData")
	public ResponseEntity<List<EmployeeDepedentDetailsEntity>> getAllEmpDepData(@RequestParam("rfqId") String rfqId) {
		System.out.println("rfqId controller :: " + rfqId);
		return new ResponseEntity<>(service.getAllEmployeeDepedentDataByRfqId(rfqId), HttpStatus.OK);
	}

	@PostMapping("/headerValidation")
	public ResponseEntity<EmpDepdentValidationDto> empDepHeaderValidation(@RequestPart MultipartFile file) {
		EmpDepdentValidationDto validateUploadedFileNames = service.validateUploadedFileNames(file);
		return new ResponseEntity<>(validateUploadedFileNames, HttpStatus.OK);
	}

	@PostMapping("/getAllEmpDepDataWithStatus")
	public ResponseEntity<List<CoverageDetailsChildValidateValuesDto>> empDepDataValidation(
			@RequestPart MultipartFile file) {
		return new ResponseEntity<>(fileNamesService.validateFileValues(file), HttpStatus.OK);
	}

	@PostMapping("/sendEmail")
	public ResponseEntity<String> sendEmail(@RequestBody DataToEmail dataToEmail)

			throws FileNotFoundException, IOException, DocumentException {

		if (dataToEmail.getTo().size() == 0 || dataToEmail.getDocumentList().size() == 0) {

			return new ResponseEntity<String>("no data selected", HttpStatus.NO_CONTENT);
		} else {

			return ResponseEntity.ok(service.sendEmailAlongPreparedAttachment(dataToEmail));
		}

	}

	@GetMapping("/downloadMandateLetter/{rfqId}")
	public ResponseEntity<byte[]> downloadMandateLetter(@PathVariable String rfqId)
			throws FileNotFoundException, IOException {
		byte[] downloadMandateLetter = service.downloadMandateLetter(rfqId);

		if (downloadMandateLetter != null && downloadMandateLetter.length > 0) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", "mandate_letter.pdf");
			return new ResponseEntity<>(downloadMandateLetter, headers, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/getCoverageDetailsById")
	public ResponseEntity<CoverageDetailsEntity> getCoverageDetailsById(@RequestParam String rfqId) {
		return new ResponseEntity<>(service.getCoverageByRfqId(rfqId), HttpStatus.OK);
	}

	@PostMapping("/headerClaimsMiscValidation")
	public ResponseEntity<CovergaeHeaderValidateDto> claimsMisHeaderValidation(@RequestPart MultipartFile file,
			@RequestParam String tpaName) {
		CovergaeHeaderValidateDto validateDto = claimsMisService.validateClaimsMisHeader(file, tpaName);
		return new ResponseEntity<>(validateDto, HttpStatus.OK);
	}

	@PostMapping("/getAllClaimsMisWithStatus")
	public ResponseEntity<List<ClaimsMisDataStatusValidateDto>> claimsMisDataValidation(@RequestPart MultipartFile file,
			@RequestParam String tpaName) {
		List<ClaimsMisDataStatusValidateDto> validateClaimsMisDataWithStatus = claimsMisService
				.validateClaimsMisDataWithStatus(file, tpaName);
		if (!validateClaimsMisDataWithStatus.isEmpty()) {
			return new ResponseEntity<>(validateClaimsMisDataWithStatus, HttpStatus.OK);
		}
		return new ResponseEntity<>(validateClaimsMisDataWithStatus, HttpStatus.NO_CONTENT);
	}

	@GetMapping("/getAllClaimsMis")
	public ResponseEntity<List<ClaimsMisEntity>> getAllClaimsMis(@RequestParam("rfqId") String rfqId) {
		return new ResponseEntity<>(claimsMisService.getAllClaimsMisByRfqId(rfqId), HttpStatus.OK);
	}

	@PostMapping("/sendEmailWithDownloadTemplateAttachement")
	public ResponseEntity<String> sendEmailWithDownloadTemplateAttachement(
			@RequestBody DownloadTemplateAttachementDto downloadTemplateAttachementDto) {
		DownloadTemplateAttachementDto sendEmailAlongWithDownloadTEmplate = service
				.sendEmailAlongWithDownloadTEmplate(downloadTemplateAttachementDto);

		if (sendEmailAlongWithDownloadTEmplate != null) {
			return new ResponseEntity<>("email Sent ", HttpStatus.OK);
		} else {
			return new ResponseEntity<>(" Opps some issue Try again after some time", HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/downloadclaimMis/{rfqId}")
	public ResponseEntity<byte[]> downloadclaimMisc(@PathVariable String rfqId)
			throws FileNotFoundException, IOException {
		byte[] downloadClaimMisc = service.downloadClaimMISC(rfqId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", "claim_mis.xls");
		return new ResponseEntity<>(downloadClaimMisc, headers, HttpStatus.OK);

	}

	@GetMapping("/getClaimsCount")
	public List<Object[]> getRfqCounts() {
		List<Object[]> results = claimsMisService.getRfqCounts();
		return results;
	}

	@GetMapping("/getClaimsDetailsAfterUpload")
	@ResponseStatus(value = HttpStatus.OK)
	public ClaimsUploadDto getClaimsDetailsAfterUpload(@RequestParam String rfqId) {
		return claimsMisService.getClaimsAferUpload(rfqId);
	}

	@GetMapping("/getAllRemarks")
	public ResponseEntity<?> getAllRemarks(
			@RequestBody List<CoverageDetailsChildValidateValuesDto> coverageDetailsChildValidateValuesDtos) {
		return ResponseEntity.ok(service.getAllRemarks(coverageDetailsChildValidateValuesDtos));
	}
}
