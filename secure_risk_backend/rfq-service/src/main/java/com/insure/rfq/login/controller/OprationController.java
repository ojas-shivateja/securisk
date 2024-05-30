package com.insure.rfq.login.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.login.dto.OperationTableDto;
import com.insure.rfq.login.service.OperationService;

@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "*")
public class OprationController {
	@Autowired
	private OperationService operationService;

	@PostMapping("/createOperation")
	@ResponseStatus(value = HttpStatus.CREATED)
	public OperationTableDto createOperation(@RequestBody OperationTableDto operationTableDto) {
		return operationService.createOperation(operationTableDto);
	}
	
	@GetMapping("/getAllOperation")
	public List<OperationTableDto> getAllOperation(){
		return operationService.getAllOperation();
	}

}
