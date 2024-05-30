package com.insure.rfq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.login.dto.DepartmentLoginDto;
import com.insure.rfq.login.service.DepatmentService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/rfq")
@CrossOrigin(origins = { "*" })
public class DepartmentController {
	@Autowired
	private DepatmentService depatmentService;

	

	@GetMapping("/getAllDepartment")
	@ResponseStatus(value = HttpStatus.OK)
	public List<DepartmentLoginDto> getAllDepartmentDetails() {
		return depatmentService.getAllDepartment();
	}

}
