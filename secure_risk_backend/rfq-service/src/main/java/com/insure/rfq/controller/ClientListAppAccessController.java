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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.ClientListAppAccessStatusDto;
import com.insure.rfq.dto.ClientLoginDto;
import com.insure.rfq.dto.GetAllAppAccessDto;
import com.insure.rfq.dto.UpdateAppAccessDto;
import com.insure.rfq.service.ClientListAppAccessService;

@RestController
@RequestMapping("/clientList/appAccess")
@CrossOrigin(origins = "*")
public class ClientListAppAccessController {
	@Autowired
	private ClientListAppAccessService clientListAppAccessService;

	@PostMapping("/uploadAccessData")
	private ResponseEntity<?> uploadAccessData(@ModelAttribute MultipartFile multipartFile,
			@RequestParam Long clientListId, @RequestParam Long productId) throws IOException {
		return new ResponseEntity<>(
				clientListAppAccessService.uploadAppAccessExcel(multipartFile, clientListId, productId),
				HttpStatus.CREATED);
	}

	@PostMapping("/sendLoginCredentialsByEmails")
	public ResponseEntity<String> sendEmail(@RequestBody List<String> employeeEmails) {
		if (clientListAppAccessService.sendLoginCredentials(employeeEmails)) {
			return ResponseEntity.ok("Emails sent successfully");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send emails");
		}
	}

	@GetMapping("/getAllAppAccess")
	public ResponseEntity<List<GetAllAppAccessDto>> getAllAppAccess(@RequestParam Long clientListId,
			@RequestParam Long productId) {
		return ResponseEntity.ok(clientListAppAccessService.getAllAppAccessDto(clientListId, productId));
	}

	@GetMapping("/getAppAccessByAppAccessId")
	public ResponseEntity<GetAllAppAccessDto> getAppAccessByAppAccessId(@RequestParam Long appAccessId) {
		return ResponseEntity.ok(clientListAppAccessService.getAllAppAccessDtoById(appAccessId));
	}

	@PatchMapping("/updateAppAccessByAppAccessId")
	public ResponseEntity<UpdateAppAccessDto> updateAppAccessByAppAccessId(@RequestParam Long appAccessId,
			@RequestBody UpdateAppAccessDto updateAppAccessDto) {
		return ResponseEntity.ok(clientListAppAccessService.updateAppAccessDtoById(appAccessId, updateAppAccessDto));
	}

	@PostMapping("/clientLogin")
	public ResponseEntity<ClientLoginDto> clientLogin(@RequestParam String username, @RequestParam String password) {
		return ResponseEntity.ok(clientListAppAccessService.authenticate(username, password));
	}
	
	@PutMapping("/ChangeAppAccessStatus")
    public ResponseEntity<String> changeAppAccessStatus(@RequestBody List<ClientListAppAccessStatusDto> clientListAppAccessStatusDto) {
        return ResponseEntity.ok(clientListAppAccessService.changeAppAccessStatus(clientListAppAccessStatusDto));
    }

    @DeleteMapping("/deleteAppAccessById")
    public ResponseEntity<?> deleteAppAccessById(@RequestParam Long appAccessId) {
        return ResponseEntity.ok(clientListAppAccessService.deleteAppAccessById(appAccessId));
    }
    
    @DeleteMapping("/clearAllAppAccess")
    public ResponseEntity<?> clearAllAppAccess() {
        return ResponseEntity.ok(clientListAppAccessService.clearAllAppAccess());
    }
    
    
    @GetMapping("/getAppAccessDownloadedTemplate")
    public ResponseEntity<?> getActiveListDownloadedTemplate() throws IOException {
        byte[] excelBytes = clientListAppAccessService.downloadAppAccessTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        headers.setContentDispositionFormData("attachment", "appAccess.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
