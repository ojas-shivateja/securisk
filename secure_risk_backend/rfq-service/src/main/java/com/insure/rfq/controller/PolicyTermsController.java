package com.insure.rfq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.PolicyTermsDto;
import com.insure.rfq.entity.PolicyTermsEntity;
import com.insure.rfq.service.PolicyTermsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rfq/policyTerms")
@CrossOrigin(origins = "*")
public class PolicyTermsController {

	@Autowired
	private PolicyTermsService service;

	@PostMapping("/createPolicyTerms")
	public ResponseEntity<?> createPolicyTerms(@Valid @RequestBody PolicyTermsDto terms) {
		if (terms != null) {
			List<PolicyTermsEntity> createPolicyTerms = service.createPolicyTerms(terms);
			return new ResponseEntity<>(createPolicyTerms, HttpStatus.CREATED);
		}
		return new ResponseEntity<>("Invalid Policy Terms Data", HttpStatus.BAD_REQUEST);
	}

	@PutMapping("/updatePolicyTerms")
	public ResponseEntity<?> updatePolicyTerms(@Valid @RequestBody PolicyTermsDto terms) {
		if (terms != null) {
			List<PolicyTermsEntity> updatePolicyTerms = service.updatePolicyTerms(terms);
			return new ResponseEntity<>(updatePolicyTerms, HttpStatus.OK);
		}
		return new ResponseEntity<>("Invalid Policy Terms Data", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/getPolicyTermsById")
	public ResponseEntity<List<PolicyTermsEntity>> getPolicyTermsById(@RequestParam String rfqId) {
		List<PolicyTermsEntity> findByRfqId = service.getPolicyTermsByRfqId(rfqId);
		return new ResponseEntity<>(findByRfqId, HttpStatus.OK);
	}

	@DeleteMapping("/deletePolicyTermsById/{rfqId}")
	public ResponseEntity<String> deletePolicyTermsById(@PathVariable String rfqId) {
		String deletePolicyTermsByRfqId = service.deletePolicyTermsByRfqId(rfqId);
		return new ResponseEntity<>(deletePolicyTermsByRfqId, HttpStatus.OK);
	}


}
