package com.insure.rfq.login.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.login.dto.DesignationOperationMappingDto;
import com.insure.rfq.login.dto.PermittedDesignationDto;
import com.insure.rfq.login.service.DesignationOperationService;
import com.insure.rfq.login.service.OperationService;

@RestController
@CrossOrigin("*")
@RequestMapping("/login")
public class DesignationOperationController {
	@Autowired
	private DesignationOperationService designationOperationService;
	@Autowired
	private OperationService operationService;

	@PostMapping("/roles/create")
	@ResponseStatus(value = HttpStatus.CREATED)
	public DesignationOperationMappingDto createDesignationOperationMapping(
			@RequestBody DesignationOperationMappingDto designationOperationMappingDto) {
		return designationOperationService.createDesignationOperation(designationOperationMappingDto);
	}
	@GetMapping("/roles/getAllPermission")
	@ResponseStatus(value=HttpStatus.OK)
    public Map<String,List<String>>getPermissionData(@RequestParam("id") long id )
    {
	
		return  operationService.getPermissonBasedOnDeptId(id);
    	
    }
	@GetMapping("/roles/getAllPermissionsBasedOnDesignationId")
	@ResponseStatus(value=HttpStatus.OK)
	public PermittedDesignationDto getAllPermissionBadesOnDesignationId(@RequestParam("desigId") Long desigId)
	{
		
		return designationOperationService.getAllPermissionDetailsBasedOnDesignationId(desigId);
		
	}
	@GetMapping("/roles/getAllPermittedOperationIdsByDesignationId")
	@ResponseStatus(value = HttpStatus.OK)
	public List<Long> getAllPermittedOperationIdsByDesignationId(@RequestParam Long designationId) {
		return designationOperationService.getPermissonIdBasedOnDeptId(designationId);
	}
}
