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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.login.dto.DepartmentLoginDto;
import com.insure.rfq.login.service.DepatmentService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/roles")
public class DepartmentControllerLogin {
	@Autowired
	private DepatmentService depatmentService;

	@PostMapping("/addDepartment")
	@ResponseStatus(value = HttpStatus.OK)
	public DepartmentLoginDto createDepartmentDetails(@RequestBody DepartmentLoginDto departmentDto) {
		log.info("{} ", departmentDto.toString());
		return depatmentService.addDepartmentDetails(departmentDto);
	}

	@GetMapping("/getAllDepartment")
	@ResponseStatus(value = HttpStatus.OK)
	public List<DepartmentLoginDto> getAllDepartmentDetails() {
		return depatmentService.getAllDepartment();
	}

	@GetMapping("/getDepartmentById/{departmentId}")
	@ResponseStatus(value = HttpStatus.OK)
	public DepartmentLoginDto getDepartmentById(@PathVariable Long departmentId) {
		return depatmentService.getDepartmentById(departmentId);
	}

	@PutMapping("/updateDepartment/{deptId}")
	public ResponseEntity<DepartmentLoginDto> updateDepartment(@RequestBody DepartmentLoginDto departmentDto,
			@PathVariable Long deptId) {
		DepartmentLoginDto updateDepartment = depatmentService.updateDepartment(departmentDto, deptId);
		return new ResponseEntity<>(updateDepartment, HttpStatus.OK);

	}

	@DeleteMapping("/deleteDepartment/{deptId}")
	public ResponseEntity<String> deleteLocation(@PathVariable Long deptId) {
		if (depatmentService.deleteDepartmen(deptId) != 0) {
			return ResponseEntity.ok("sucessfully deleted");
		} else {
			return ResponseEntity.ok("not deleted");
		}
	}

}
