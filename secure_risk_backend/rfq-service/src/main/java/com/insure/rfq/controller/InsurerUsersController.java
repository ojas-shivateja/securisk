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

import com.insure.rfq.dto.GetAllUsersByInsurerIdDto;
import com.insure.rfq.dto.InsurerUsersDto;
import com.insure.rfq.dto.UpdateInsurerUserDto;
import com.insure.rfq.service.InsurerUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rfq/users")
@CrossOrigin(origins = { "*" })
public class InsurerUsersController {
	@Autowired
	private InsurerUserService userService;

	@PostMapping("/add/{id}")
	public ResponseEntity<InsurerUsersDto> addUsers(@RequestBody InsurerUsersDto usersDto, @PathVariable String id) {
		return new ResponseEntity<>(userService.addUser(usersDto, id), HttpStatus.CREATED);
	}

	@GetMapping("/getUser/{id}")
	public ResponseEntity<InsurerUsersDto> getUserById(@PathVariable String id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable String id) {

		if (userService.deleteUser(id) != 0) {
			return ResponseEntity.ok(" sucessfully deleted");
		} else {
			return ResponseEntity.ok(" not deleted ");
		}
	}

	@PutMapping("/update/{userId}")
	public ResponseEntity<UpdateInsurerUserDto> updateUser(@PathVariable String userId,
			@Valid @RequestBody UpdateInsurerUserDto usersDto) {
		return ResponseEntity.ok(userService.updateUser(userId, usersDto));
	}

	@GetMapping("/getAllByInsureList/{id}")
	public ResponseEntity<List<GetAllUsersByInsurerIdDto>> getUserByInsureId(@PathVariable String id) {
		return ResponseEntity.ok(userService.getAllUsersDto(id));
	}
}
