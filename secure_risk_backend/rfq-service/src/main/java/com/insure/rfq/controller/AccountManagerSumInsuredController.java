package com.insure.rfq.controller;

import java.util.List;

import com.insure.rfq.dto.AccountManagerSumInsuredDisplayDto;
import com.insure.rfq.dto.AccountManagerSumInsuredDto;
import com.insure.rfq.service.AccountManagerSumInsuredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rfq/accountManagerSumInsured")
@CrossOrigin("*")
@Slf4j
public class AccountManagerSumInsuredController {

	@Autowired
	private AccountManagerSumInsuredService sumInsuredservice;

	@PostMapping("/createAccountManagerSumInsuredDetails")
	@ResponseStatus(HttpStatus.CREATED)
	public String  createAccountManagerSumInsuredDetails(
			@ModelAttribute AccountManagerSumInsuredDto sumInsuredDisplayDto, @RequestParam Long clientListId,
			@RequestParam Long productId) {
		log.info("Suminsured Data {} : client ID {} : productId {} : ", sumInsuredDisplayDto, clientListId, productId);
		return sumInsuredservice.createAccountManagerSumInsured(sumInsuredDisplayDto,
				clientListId, productId);


	}

	@GetMapping("/getAllAccountManagerSumInsuredDetails")
	@ResponseStatus(HttpStatus.OK)
	public List<AccountManagerSumInsuredDisplayDto> getAllAccountManagerSumInsuredetails(@RequestParam Long clientListId,
			@RequestParam Long productId) {
		return sumInsuredservice.getAllSumInsuredDetails(clientListId, productId);
	}
	@PatchMapping("/updateAccountManagerSumInsuredDetails")
	@ResponseStatus(			HttpStatus.OK)
	public AccountManagerSumInsuredDisplayDto updateAccountManagerSumInsuredDetails(
			@ModelAttribute AccountManagerSumInsuredDto sumInsuredDisplayDto,	@RequestParam Long sumInsuredId) {
		log.info("Suminsured Data {} : client ID {} : productId {} : ", sumInsuredDisplayDto, sumInsuredId);
		return sumInsuredservice.upadateAccountManagerSumInsured(sumInsuredDisplayDto,
				sumInsuredId);


	}



	@DeleteMapping("/deleteSumInsured/{id}")
	@ResponseStatus(HttpStatus.OK)
	public String deleteSumInsuredDetails(@PathVariable Long id) {
		return sumInsuredservice.deleteSumInsuredDetailsById(id);
	}

}
