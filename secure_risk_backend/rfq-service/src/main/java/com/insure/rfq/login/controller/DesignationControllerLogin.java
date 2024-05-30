package com.insure.rfq.login.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.login.dto.DesignationLoginDto;
import com.insure.rfq.login.service.DesignationService;

@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "*")
public class DesignationControllerLogin {
	@Autowired
	private DesignationService designationService;

	@PostMapping("/addDesignation")
	@ResponseStatus(value = HttpStatus.OK)
	public DesignationLoginDto addDesignation(@RequestBody DesignationLoginDto designationDto,
			@RequestParam long deptId) {

		return designationService.addDesignation(designationDto, deptId);
	}

	@GetMapping("/getAllDesignation")
	@ResponseStatus(value = HttpStatus.OK)
	public List<DesignationLoginDto> getAllDesignation() {
		return designationService.getAllDesiDesignation();
	}

	@GetMapping("/getDesignationById/{id}")
	@ResponseStatus(value = HttpStatus.OK)
	public DesignationLoginDto getDesignationById(@PathVariable Long id) {
		return designationService.getDesignationById(id);

	}

	@GetMapping("/getDesignationByDepartmentId/{departmentId}")
	@ResponseStatus(value = HttpStatus.OK)
	public List<String> getDesignationByDepartmentId(@PathVariable Long departmentId) {
		return designationService.getDesignationByDeptId(departmentId);

	}

	@PutMapping("/updateDesignation/{desId}")
	public ResponseEntity<DesignationLoginDto> updateDesgnation(@RequestBody DesignationLoginDto designationDto,
			@PathVariable Long desId) {
		DesignationLoginDto updateDesignation = designationService.updateDesignation(designationDto, desId);
		return new ResponseEntity<>(updateDesignation, HttpStatus.OK);

	}

	@DeleteMapping("/deleteDesignation/{desId}")
	public ResponseEntity<String> deleteDesignation(@PathVariable Long desId) {
		if (designationService.deleteDesignation(desId) != 0) {
			return ResponseEntity.ok("sucessfully deleted");
		} else {
			return ResponseEntity.ok("not deleted");
		}
	}

}
