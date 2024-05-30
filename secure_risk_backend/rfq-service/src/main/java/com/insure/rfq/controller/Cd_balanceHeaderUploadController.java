package com.insure.rfq.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.Cd_balanceDisplayDto;
import com.insure.rfq.dto.Cd_balanceHeaderMappingDto;
import com.insure.rfq.dto.Cd_balanceValueStatus;
import com.insure.rfq.service.Cd_balanceHeaderUploadService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rfq/Cd_balanceHeaderUploadController")
@Slf4j
@CrossOrigin(originPatterns = "*")
public class Cd_balanceHeaderUploadController {

	@Autowired
	private Cd_balanceHeaderUploadService cd_balanceHeaderUploadService;

	@PostMapping("/uploadheaders")
	public ResponseEntity<?> uploadEnrollement(@RequestBody Cd_balanceHeaderMappingDto cd_balanceHeaderMappingDto) {
		try {
			Cd_balanceHeaderMappingDto result = cd_balanceHeaderUploadService
					.uploadEnrollement(cd_balanceHeaderMappingDto);
			if (result != null) {
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Request body is empty or headers list is null");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
		}
	}

	@PostMapping("/Cd_balanceheaderSave")
	public ResponseEntity<?> saveData(@RequestBody List<Cd_balanceValueStatus> cd_balanceValueStatus,
			@RequestParam Long clientId, @RequestParam Long productId) {
		try {
			// Your existing saveData logic here
			return ResponseEntity
					.ok(cd_balanceHeaderUploadService.saveData(cd_balanceValueStatus, clientId, productId));
		} catch (MultipartException e) {
			// Handle MultipartException
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to parse multipart request: " + e.getMessage());
		} catch (Exception e) {
			// Handle other exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + e.getMessage());
		}
	}

	@PostMapping("/Cd_balancevaluevalidation")
	public ResponseEntity<?> uploadFile(@RequestPart MultipartFile multipartFile) throws IOException {
		return ResponseEntity.ok(cd_balanceHeaderUploadService.validateValuesBased(multipartFile));
	}

	@PostMapping("/Cd_balanceheaderValidation")
	public ResponseEntity<?> validations(@RequestPart MultipartFile file) throws IOException {
		return ResponseEntity.ok(cd_balanceHeaderUploadService.validateHeaders(file));
	}

	@GetMapping("/cd_balanceheadergetDataByClientIdAndProductId")
	public ResponseEntity<?> getData(@RequestParam Long clientId, @RequestParam Long productId) throws IOException {
		return ResponseEntity.ok(cd_balanceHeaderUploadService.getDataformFile(clientId, productId));
		// return
	}

	@PatchMapping("/updateCdBalanceData")

	public String updateCdBalanceData(@RequestBody Cd_balanceDisplayDto dto, @RequestParam Long cd_balanceId) {
		return cd_balanceHeaderUploadService.updateCdBalanceData(dto, cd_balanceId);
	}

	@DeleteMapping("/deleteCd_balanceheadergetDataById")
	@ResponseStatus(value = HttpStatus.OK)
	public String deleteCd_balanceheadergetDataById(@RequestParam Long cd_balanceId) {
		log.info("The cd_balanceId is : {} ", cd_balanceId);
		return cd_balanceHeaderUploadService.deleteTheCdBalancData(cd_balanceId);

	}

	@GetMapping("/exportExcel")
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ResponseEntity<byte[]> exportExcel(@RequestParam Long clientId, @RequestParam Long productId) {
		log.info(clientId + " -----------" + productId);
		byte[] excelData = cd_balanceHeaderUploadService.generateExcelFromData(clientId, productId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", "Cd_balancDetails.xlsx");

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).headers(headers).body(excelData);
	}

}
