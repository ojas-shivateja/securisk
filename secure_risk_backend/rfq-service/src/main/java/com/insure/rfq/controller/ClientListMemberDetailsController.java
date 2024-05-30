package com.insure.rfq.controller;

import com.insure.rfq.dto.*;
import com.insure.rfq.service.ClientListEnrollementUploadService;
import com.insure.rfq.service.ClientListMemberDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("clientList/memberDetails")
@CrossOrigin(origins = "*")
public class ClientListMemberDetailsController {
	@Autowired
	private ClientListMemberDetailsService clientListMemberDetailsService;

	@Autowired
	private ClientListEnrollementUploadService clientListEnrollementUploadService;

	public ClientListMemberDetailsController(ClientListMemberDetailsService clientListMemberDetailsService) {
		this.clientListMemberDetailsService = clientListMemberDetailsService;
	}

	@PostMapping("/createMemberDetails")
	public ResponseEntity<?> createMemberDetails(@Valid @RequestParam Long clientListId, @RequestParam Long productId,
			@RequestBody AddClientListMemberDetailsDto clientListMemberDetailsDto) {
		try {
			ResponseDto clientListMembersDetails = clientListMemberDetailsService
					.createClientListMembersDetails(clientListId, productId, clientListMemberDetailsDto);
			// Check if the response contains an error message
			if (clientListMembersDetails.getMessage().contains("ClientList is not Found")
					|| clientListMembersDetails.getMessage().contains("Product is not Found")
					|| clientListMembersDetails.getMessage().contains("Department is not Found")
					|| clientListMembersDetails.getMessage().contains("Designation is not Found")) {
				// Return a BAD_REQUEST status if there are error messages
				return new ResponseEntity<>(clientListMembersDetails, HttpStatus.BAD_REQUEST);
			}
			// If there are no error messages, return a CREATED status
			return new ResponseEntity<>(clientListMembersDetails, HttpStatus.CREATED);
		} catch (Exception e) {
			ResponseDto errorResponse = new ResponseDto(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	@GetMapping("/getAllMembersDetailsForMasterList")
	public ResponseEntity<?> getAllMembersDetailsForMasterList(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestParam String month) {
		try {
			List<GetAllClientListMembersDetailsDto> allClientListMembersDetails = clientListMemberDetailsService
					.getAllClientListMembersDetails(clientListId, productId, month);
			return ResponseEntity.ok(allClientListMembersDetails);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/getAllMemberDetailsForActiveList")
	public ResponseEntity<?> getAllMemberDetailsForActiveList(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestParam String month) {
		return ResponseEntity.ok(clientListMemberDetailsService
				.getAllActiveListForMemberDetailsByClientListProduct(clientListId, productId, month));
	}

	@PatchMapping("/updateMemberDetails")
	@ResponseStatus(value = HttpStatus.OK)
	public AddClientListMemberDetailsDto updateMemberDetails(@RequestParam Long memberDetailsId,
			@RequestBody AddClientListMemberDetailsDto clientListMemberDetailsDto) {
		return clientListMemberDetailsService.updateClientListMemberDetails(memberDetailsId,
				clientListMemberDetailsDto);
	}

	@DeleteMapping("/deleteMemberDetails")
	@ResponseStatus(value = HttpStatus.OK)
	public String deleteMemberDetails(@RequestParam Long memberDetailsId) {
		return clientListMemberDetailsService.deleteClientListMemberDetails(memberDetailsId);
	}

	@GetMapping("/getAllActiveListInExcel")
	public ResponseEntity<byte[]> getAllActiveListInExcel(@RequestParam Long clientListId,
			@RequestParam Long productId) {

		byte[] excelData = clientListMemberDetailsService.getActiveListInExcelFormat(clientListId, productId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "members_details_activeList.xlsx");

		return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
	}

	@GetMapping("/getAllAdditionListInExcel")
	public ResponseEntity<byte[]> getAllAdditionListInExcel(@RequestParam Long clientListId,
			@RequestParam Long productId) {

		byte[] excelData = clientListMemberDetailsService.getAdditionListInExcelFormat(clientListId, productId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "members_details_additionList.xlsx");

		return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
	}

	@GetMapping("/getAllCorrectionListInExcel")
	public ResponseEntity<byte[]> getAllCorrectionListInExcel(@RequestParam Long clientListId,
			@RequestParam Long productId) {

		byte[] excelData = clientListMemberDetailsService.getCorrectionListInExcelFormat(clientListId, productId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "members_details_correctionList.xlsx");

		return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
	}

	@GetMapping("/getAllDeletedListInExcel")
	public ResponseEntity<byte[]> getAllDeletedListInExcel(@RequestParam Long clientListId,
			@RequestParam Long productId) {

		byte[] excelData = clientListMemberDetailsService.getDeletedListInExcelFormat(clientListId, productId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "members_details_deletedList.xlsx");

		return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
	}

	@GetMapping("/getAllEnrollementListInExcel")
	public ResponseEntity<byte[]> getAllEnrollementListInExcel(@RequestParam Long clientListId,
			@RequestParam Long productId) {

		byte[] excelData = clientListMemberDetailsService.getEnrollmentListInExcelFormat(clientListId, productId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "members_details_enrollementList.xlsx");

		return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
	}

	@GetMapping("/getAllPendingListInExcel")
	public ResponseEntity<byte[]> getAllPendingListInExcel(@RequestParam Long clientListId,
			@RequestParam Long productId) {

		byte[] excelData = clientListMemberDetailsService.getPendingListInExcelFormat(clientListId, productId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "members_details_pendingList.xlsx");

		return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
	}

	@GetMapping("/getMemberDetailsByClaimId")
	public ResponseEntity<?> getMemberDetailsByClaimId(@RequestParam Long memberDetailsId) {
		return ResponseEntity
				.ok(clientListMemberDetailsService.getClaimsMemberDetailsByClaimsMemberId(memberDetailsId));
	}

	@GetMapping("/getAllDeletedList")
	public ResponseEntity<?> getAllDeletedList(@RequestParam Long clientListId, @RequestParam Long productId,
			String month) {
		return ResponseEntity.ok(clientListMemberDetailsService
				.getAllDeletedListForMemberDetailsByClientListProduct(clientListId, productId, month));
	}

	@GetMapping("/getAllCorrectionsList")
	public ResponseEntity<?> getAllCorrectionsList(@RequestParam Long clientListId, @RequestParam Long productId,
			@RequestParam String month) {
		return ResponseEntity.ok(clientListMemberDetailsService
				.getAllCorrectionsListForMemberDetailsByClientListProduct(clientListId, productId, month));
	}

	@GetMapping("/getAllAdditionList")
	public ResponseEntity<?> getAllAdditionList(@RequestParam Long clientListId, @RequestParam Long productId,
			@RequestParam String month) {
		return ResponseEntity.ok(clientListMemberDetailsService
				.getAllAdditionListForMemberDetailsByClientListProduct(clientListId, productId, month));
	}

	@GetMapping("/getActiveListDownloadedTemplate")
	public ResponseEntity<?> getActiveListDownloadedTemplate() throws IOException {
		byte[] excelBytes = clientListMemberDetailsService.getActiveListTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		headers.setContentDispositionFormData("attachment", "activeList.xlsx");

		return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
	}

	@GetMapping("/getAdditionListDownloadedTemplate")
	public ResponseEntity<?> getAdditionListDownloadedTemplate() throws IOException {
		byte[] excelBytes = clientListMemberDetailsService.getAdditionListTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		headers.setContentDispositionFormData("attachment", "additionList.xlsx");

		return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
	}

	@GetMapping("/getCorrectionListDownloadedTemplate")
	public ResponseEntity<?> getCorrectionListDownloadedTemplate() throws IOException {
		byte[] excelBytes = clientListMemberDetailsService.getCorrectionListTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		headers.setContentDispositionFormData("attachment", "correctionList.xlsx");

		return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
	}

	@GetMapping("/getDeletedListDownloadedTemplate")
	public ResponseEntity<?> getDeletedListDownloadedTemplate() throws IOException {
		byte[] excelBytes = clientListMemberDetailsService.getDeleteListTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		headers.setContentDispositionFormData("attachment", "deletedList.xlsx");

		return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
	}

	@GetMapping("/getEnrollementListDownloadedTemplate")
	public ResponseEntity<?> getEnrollementListDownloadedTemplate() throws IOException {
		byte[] excelBytes = clientListMemberDetailsService.getEnrollementListTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		headers.setContentDispositionFormData("attachment", "enrollementList.xlsx");

		return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
	}

	@GetMapping("/getPendingListDownloadedTemplate")
	public ResponseEntity<?> getPendingListDownloadedTemplate() throws IOException {
		byte[] excelBytes = clientListMemberDetailsService.getPendingListTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		headers.setContentDispositionFormData("attachment", "pendingList.xlsx");

		return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
	}

	@PostMapping("/addHeadersForEnrollement")
	public ResponseEntity<?> addHeadersForEnrollement(
			@RequestBody ClientListEnrollementHeaderMappingDto clientListEnrollementUploadDto) {
		return new ResponseEntity<>(
				clientListEnrollementUploadService.uploadEnrollement(clientListEnrollementUploadDto),
				HttpStatus.CREATED);
	}

	@PostMapping("/headerValidationEnrollment")
	public ResponseEntity<?> headerValidationEnrollment(@ModelAttribute MultipartFile multipartFile,
			@RequestParam String tpaName) throws IOException {
		return ResponseEntity.ok(clientListMemberDetailsService.validateHeadersBasedOnTpa(multipartFile, tpaName));
	}

	@PostMapping("/getEnrollmentWithStatus")
	public ResponseEntity<?> getEnrollmentWithStatus(@RequestPart MultipartFile multipartFile,
			@RequestParam String tpaName) throws IOException {
		return ResponseEntity.ok(clientListMemberDetailsService.validateValuesBasedOnTpa(multipartFile, tpaName));
	}

	@PostMapping("/uploadEnrollementData")
	public ResponseEntity<?> uploadEnrollement(
			@RequestBody List<ClientListMemberDetailsDataStatus> clientListMemberDetailsDataStatuses,
			@RequestParam Long clientListId, @RequestParam Long productId) {
		return new ResponseEntity<>(clientListMemberDetailsService.uploadEnrollmentData(
				clientListMemberDetailsDataStatuses, clientListId, productId), HttpStatus.CREATED);
	}

	@GetMapping("/getAllclientListEnrollmentData")
	public ResponseEntity<?> getAllclientListEnrollmentData(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestParam String month) {
		return ResponseEntity
				.ok(clientListMemberDetailsService.getAllclientListEnrollmentData(clientListId, productId, month));
	}

	@GetMapping("/getAllPendingList")
	public ResponseEntity<?> getAllPendingList(@RequestParam Long productId, @RequestParam Long clientListId) {
		return ResponseEntity.ok(clientListMemberDetailsService.getAllClientPendingListData(productId, clientListId));
	}

	@GetMapping("/getMembersDetailsForEmployee")
	public ResponseEntity<?> getMembersDetailsForEmployee(@RequestParam Long clientListId, @RequestParam Long productId,
			@RequestParam String employeeId) {
		return ResponseEntity.ok(clientListMemberDetailsService.getAllClientListMembersDetailsForEmployee(clientListId,
				productId, employeeId));
	}

	@GetMapping("/getMembersDetailsForEmployeeInExcel")
	public ResponseEntity<?> getMembersDetailsForEmployeeInExcel(@RequestParam Long clientListId,
			@RequestParam Long productId, @RequestParam String employeeId) {
		byte[] excelData = clientListMemberDetailsService.downloadMembersDetailsForEmployeeInExcelFormat(clientListId,
				productId, employeeId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "Employee_Details.xlsx");

		return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
	}

	@PostMapping("/uploadMembersDetails")
	public ResponseEntity<String> uploadMembersDetails(
			@RequestBody List<CoverageDetailsChildValidateValuesDto> clientListMemberDetailsDataStatuses,
			@RequestParam Long clientListId, @RequestParam Long productId) {
		return new ResponseEntity<>(clientListMemberDetailsService.validateEmployeeDetails(
				clientListMemberDetailsDataStatuses, clientListId, productId), HttpStatus.CREATED);
	}
}
