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

import com.insure.rfq.dto.ExcelReportHeadersDto;
import com.insure.rfq.entity.ExcelReportHeaders;
import com.insure.rfq.service.ExcelReportHeadersService;

@RestController
@RequestMapping("/rfq/header")
@CrossOrigin(origins = { "*" })
public class ExcelReportHeadersController {
	
	@Autowired
	private ExcelReportHeadersService headerService;
	
	@PostMapping("/addHeader")
	private ResponseEntity<ExcelReportHeadersDto> addHeader(@RequestBody ExcelReportHeadersDto header){
		ExcelReportHeadersDto createHeaders = null;
		if(header != null) {
			createHeaders = headerService.createHeaders(header);
			return new ResponseEntity(createHeaders, HttpStatus.CREATED);
		}
		return new ResponseEntity(createHeaders, HttpStatus.BAD_REQUEST);
	}
	@GetMapping("/viewHeader")
	private ResponseEntity<ExcelReportHeaders> viewHeader(){
		List<ExcelReportHeaders> viewAllHeaders = headerService.viewAllHeaders();
		return new ResponseEntity(viewAllHeaders, HttpStatus.OK);
	}
}
