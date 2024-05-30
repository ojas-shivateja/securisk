package com.insure.rfq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.EmpDependentHeaderDto;
import com.insure.rfq.entity.EmpDependentHeaders;
import com.insure.rfq.service.EmpDependentHeaderService;

@RestController
@RequestMapping("/rfq/header")
@CrossOrigin(origins = "*")
public class EmpDependentHeadersController {
	
	@Autowired
	private EmpDependentHeaderService headerService;
	
	@PostMapping("/addEmpDepHeader")
	private ResponseEntity<EmpDependentHeaderDto> addHeader(@RequestBody EmpDependentHeaderDto header){
		EmpDependentHeaderDto createHeaders = null;
		if(header != null) {
			createHeaders = headerService.createHeaders(header);
			return new ResponseEntity<>(createHeaders, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(createHeaders, HttpStatus.BAD_REQUEST);
	}
	@GetMapping("/viewEmpDepHeader")
	private ResponseEntity<List<EmpDependentHeaders>> viewHeader(){
		List<EmpDependentHeaders> viewAllHeaders = headerService.viewAllHeaders();
		return new ResponseEntity<List<EmpDependentHeaders>>(viewAllHeaders, HttpStatus.OK);
	}
	
	// fetching only standard headers
	@GetMapping("/getAllEmpDependentHeaders")
	private ResponseEntity<List<String>> getAllEmpDependentHeaders(){
		List<String> empDepHeaders = headerService.getAllEmpDependentHeaders();
		return new ResponseEntity<List<String>>(empDepHeaders, HttpStatus.OK);
	}
	
}
