package com.insure.rfq.controller;

import java.util.List;
import java.util.Optional;

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

import com.insure.rfq.dto.TpaDto;
import com.insure.rfq.repository.TpaRepository;
import com.insure.rfq.service.impl.TpaServiceImpl;

@RequestMapping("/tpa")
@RestController
@CrossOrigin(origins = { "*" })
public class TpaController {

	@Autowired
	private TpaServiceImpl tpaServiceImpl;

	@Autowired
	private TpaRepository tpaRepository;

	@PostMapping("/createTpa")
	public ResponseEntity<TpaDto> createTpa(@RequestBody TpaDto tpa) {
		TpaDto createTpa = tpaServiceImpl.createTpa(tpa);
		return new ResponseEntity<>(createTpa, HttpStatus.CREATED);

	}

	@GetMapping("/getAllTpa")
	public ResponseEntity<List<TpaDto>> getAllTpa() {
		List<TpaDto> findAll = tpaServiceImpl.viewAllTpa();
		return new ResponseEntity<>(findAll, HttpStatus.OK);
	}

	@PutMapping("/updateTpa/{tpaId}")
	public ResponseEntity<TpaDto> updateTpa(@RequestBody TpaDto tpaDto, @PathVariable Long tpaId) {
		TpaDto updateTpa = tpaServiceImpl.updateTpa(tpaDto, tpaId);
		return new ResponseEntity<>(updateTpa, HttpStatus.OK);
	}

	@GetMapping("/getTpaById/{tpaId}")
	public ResponseEntity<TpaDto> getTpaById(@PathVariable Long tpaId) {
		Optional<TpaDto> byId = tpaServiceImpl.getById(tpaId);
		return new ResponseEntity<>(byId.get(), HttpStatus.OK);
	}

	@GetMapping("/getTpaList")
	public ResponseEntity<List<String>> getTpaList() {
		List<String> tpaList = tpaRepository.getTpaList();
		return new ResponseEntity<>(tpaList, HttpStatus.OK);
	}

	@DeleteMapping("/deleteTpa/{id}")
	public ResponseEntity<String> deleteTpaList(@PathVariable Long id) {
		String deleteTpa = tpaServiceImpl.deleteTpa(id);
		return new ResponseEntity<>(deleteTpa, HttpStatus.OK);
	}
	@GetMapping("/getCountTpa")
	public Long getRfqCountByCount() {
		System.out.println(tpaRepository.countApplicationsByStatus());
		return tpaRepository.countApplicationsByStatus();

	}
}
