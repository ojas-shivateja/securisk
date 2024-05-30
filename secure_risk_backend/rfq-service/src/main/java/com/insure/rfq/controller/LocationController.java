package com.insure.rfq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.login.dto.LocationLoginDto;
import com.insure.rfq.login.service.impl.LocationServiceImpl;

@RequestMapping("/location")
@RestController
@CrossOrigin(origins = { "*" })
public class LocationController {

	@Autowired
	private LocationServiceImpl locationServiceImpl;

	@GetMapping("/getAllLocations")
	@ResponseStatus(value = HttpStatus.OK)
	public List<LocationLoginDto> getAllLocations() {
		return locationServiceImpl.getAllLocations();

	}
	

}
