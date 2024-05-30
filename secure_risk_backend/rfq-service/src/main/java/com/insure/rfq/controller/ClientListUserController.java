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

import com.insure.rfq.dto.AddClientListUserDto;
import com.insure.rfq.dto.ClientListUserDto;
import com.insure.rfq.service.impl.ClientListUserServiceImpl;

import jakarta.validation.Valid;

@RequestMapping("/clientList")
@RestController
@CrossOrigin(origins = "*")
public class ClientListUserController {

	@Autowired
	private ClientListUserServiceImpl clientListUserServiceImpl;

	@PostMapping("/addUser/{id}")
	public ResponseEntity<AddClientListUserDto> createClientListUser(
			@Valid @RequestBody AddClientListUserDto clientListUserDto, @PathVariable Long id) {
		AddClientListUserDto createUser = clientListUserServiceImpl.createUser(clientListUserDto, id);
		return new ResponseEntity<>(createUser, HttpStatus.CREATED);
	}

	@GetMapping("/getAllUsers/{id}")
	public ResponseEntity<List<ClientListUserDto>> getAllUsers(@PathVariable Long id) {
		List<ClientListUserDto> allUsers = clientListUserServiceImpl.getAllUsers(id);
		return new ResponseEntity<>(allUsers, HttpStatus.OK);
	}

	@DeleteMapping("/deleteUserByClientListId/{id}")
	public ResponseEntity<String> deleteUserByClientListId(@PathVariable Long id) {
		String deleteClientUserById = clientListUserServiceImpl.deleteClientUserById(id);
		return new ResponseEntity<>(deleteClientUserById, HttpStatus.OK);
	}

	@PutMapping("/updateClientUserById/{uid}")
	public ResponseEntity<ClientListUserDto> updateClientUserById(@PathVariable Long uid,
			@RequestBody ClientListUserDto clientListUserDto) {
		if (clientListUserDto != null) {
			ClientListUserDto updateUserById = clientListUserServiceImpl.updateUserById(uid, clientListUserDto);
			return new ResponseEntity<>(updateUserById, HttpStatus.OK);
		}
		return null;

	}
}
