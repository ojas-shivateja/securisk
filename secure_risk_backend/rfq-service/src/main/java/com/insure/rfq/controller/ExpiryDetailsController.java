package com.insure.rfq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.ExpiryDetailsDto;
import com.insure.rfq.entity.ExpiryPolicyDetails;
import com.insure.rfq.service.ExpiryDetailsService;

@RestController
@RequestMapping("/rfq/expiryDetails")
@CrossOrigin(origins = "*")
public class ExpiryDetailsController {

	@Autowired
	private ExpiryDetailsService service;

	@PostMapping("/createExpiryDetails")
	public ResponseEntity<String> createExpiryDetails(@RequestBody ExpiryDetailsDto expiryDetailsDto) {
		if (expiryDetailsDto != null) {
			String rfqId = service.createExpiryDetails(expiryDetailsDto);
			return new ResponseEntity<>(rfqId, HttpStatus.CREATED);
		}
		return new ResponseEntity<>("No Details Found", HttpStatus.BAD_REQUEST);
	}

	@PutMapping("/updateExpiryDetails/{id}")
	public ResponseEntity<ExpiryPolicyDetails> updateExpiryDetails(@RequestBody ExpiryDetailsDto detailsDto,
			@PathVariable String id) {
		return ResponseEntity.ok(service.updateExpiryDetails(detailsDto, id));
	}

	@GetMapping("/getExpiryDetailsById")
	public ResponseEntity<ExpiryPolicyDetails> getExpiryDetailsById(@RequestParam String rfqId) {
		return new ResponseEntity<>(service.getExpiryDetailsRfqById(rfqId), HttpStatus.OK);
	}
}
