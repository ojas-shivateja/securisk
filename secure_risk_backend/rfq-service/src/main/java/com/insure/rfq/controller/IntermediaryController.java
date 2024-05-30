package com.insure.rfq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.IntermediaryDetailsDto;
import com.insure.rfq.dto.PolicyCoverageDto;
import com.insure.rfq.repository.IntermediaryDetailsRepository;
import com.insure.rfq.service.IntermediaryDetailsService;
import com.insure.rfq.service.PolicyCoverageService;

@RestController
@RequestMapping("/rfq/intermediatrydetails")
@CrossOrigin(origins = { "*" })
public class IntermediaryController {
	@Autowired
	private IntermediaryDetailsService intermediaryDetailsService;

	@Autowired
	private PolicyCoverageService policyCoverageService;
	@Autowired
	private IntermediaryDetailsRepository detailsRepository;

	@GetMapping("/getAllIntermediaryDetails")
	private ResponseEntity<?> allIntermediaryDetails() {
		List<IntermediaryDetailsDto> allIntermediaryDetails = intermediaryDetailsService.getAllIntermediaryDetails();
		if (allIntermediaryDetails != null) {
			return new ResponseEntity<>(allIntermediaryDetails, HttpStatus.OK);
		}
		return new ResponseEntity<>("Invalid Policy Terms Data", HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/createCoverageByProductId/{productId}")
	@ResponseStatus(value = HttpStatus.CREATED)
	public PolicyCoverageDto createCoverageByProductId(@PathVariable Long productId,
			@RequestBody PolicyCoverageDto policyCoverageDto) {
		if (policyCoverageDto != null) {
			return policyCoverageService.createCoverageByProductId(productId, policyCoverageDto);
		}
		return null;
	}

	@GetMapping("/getCoveragesByProductId/{productId}")
	@ResponseStatus(value = HttpStatus.OK)
	public List<PolicyCoverageDto> getCoveragesByProductId(@PathVariable Long productId) {
		return policyCoverageService.getCoveragesByProductId(productId);

	}

	@GetMapping("/count")
	public Long getRfqCountByCount() {
		return detailsRepository.countApplicationsByStatus();

	}

	@PatchMapping("/updateCoverage/{coverageId}")
	@ResponseStatus(value = HttpStatus.OK)
	public PolicyCoverageDto upadateCoveragesByProductId(@PathVariable Long coverageId,
			@RequestBody PolicyCoverageDto coverageDto) {
		return policyCoverageService.updateCoveragesByProductId(coverageId, coverageDto);

	}

	@DeleteMapping("/deleteCoverage/{coverageId}")
	@ResponseStatus(value = HttpStatus.OK)
	public String deleteCoverageByProductId(@PathVariable Long coverageId) {
		return policyCoverageService.deleteCoveragesByProductId(coverageId);
	}

}
