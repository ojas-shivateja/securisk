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
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.GetAllInsurerListDto;
import com.insure.rfq.dto.GetInsuresDropdownDto;
import com.insure.rfq.dto.InsureListDto;
import com.insure.rfq.dto.UpdateInsurerListDto;
import com.insure.rfq.service.InsureListService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rfq/intermediatrydetails")
@CrossOrigin(origins = { "*" })
public class InsureListController {
	@Autowired
	private InsureListService insureListService;

	@PostMapping("/add")
	public ResponseEntity<InsureListDto> addInsureList(@Valid @RequestBody InsureListDto dto) {
		return new ResponseEntity<>(insureListService.addListInsure(dto), HttpStatus.CREATED);
	}

	@GetMapping("/getClientListWithUserEmails")
	public ResponseEntity<List<GetAllInsurerListDto>> getAllInsureList() {
		return ResponseEntity.ok(insureListService.getAllInsureListDto());
	}

	@PutMapping("/update/{insurerId}")
	public ResponseEntity<UpdateInsurerListDto> upadteInsureList(@Valid @RequestBody UpdateInsurerListDto insureDto,
			@PathVariable String insurerId) {
		return ResponseEntity.ok(insureListService.updateInsureListDto(insurerId, insureDto));
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteInsureList(@PathVariable String id) {
		if (insureListService.deleteInsureListDto(id) != 0) {
			return ResponseEntity.ok(" succesfully deleted ");
		} else {
			return ResponseEntity.ok(" not  deleted ");
		}
	}

	@GetMapping("/getAllInsurerListByName")
	public ResponseEntity<List<String>> getAllInsurerList() {
		return ResponseEntity.ok(insureListService.getAllInsureListDtoName());
	}

	@GetMapping("/getAllInsurers")
	public ResponseEntity<List<GetInsuresDropdownDto>> getAllInsurersWithInsurerId() {
		List<GetInsuresDropdownDto> allInsuresById = insureListService.getAllInsuresById();
		return new ResponseEntity<>(allInsuresById, HttpStatus.OK);
	}
}
