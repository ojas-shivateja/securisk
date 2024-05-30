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

import com.insure.rfq.login.dto.LocationLoginDto;
import com.insure.rfq.login.service.impl.LocationServiceImpl;

@RequestMapping("/location")
@RestController
@CrossOrigin(origins = "*")
public class LocationControllerLogin {

	@Autowired
	private LocationServiceImpl locationServiceImpl;

	@PostMapping("/addLocation")
	public ResponseEntity<?> saveLocation(@RequestBody LocationLoginDto locationDto) {
		String errorMessage = locationServiceImpl.createLocation(locationDto);

		if (errorMessage == null) {
			return new ResponseEntity<>("Location created successfully", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/getAllLocation")
	@ResponseStatus(value = HttpStatus.OK)
	public List<LocationLoginDto> getAllLocations() {
		return locationServiceImpl.getAllLocations();

	}


	@PutMapping("/updateLocation/{id}")
	public ResponseEntity<LocationLoginDto> updateLocation(@RequestBody LocationLoginDto locationDto, @PathVariable Long id) {
		LocationLoginDto updateLocation = locationServiceImpl.updateLocation(locationDto, id);
		return new ResponseEntity<>(updateLocation, HttpStatus.OK);

	}

	@DeleteMapping("/deleteLocation/{id}")
	public ResponseEntity<String> deleteLocation(@PathVariable Long id) {
		if (locationServiceImpl.deleteLocation(id) != 0) {
			return ResponseEntity.ok("sucessfully deleted");
		} else {
			return ResponseEntity.ok("not deleted");
		}
	}
}
