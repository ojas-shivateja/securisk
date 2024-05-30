package com.insure.rfq.login.service;

import java.util.List;

import com.insure.rfq.login.dto.LocationLoginDto;

public interface LocationService {

	String createLocation(LocationLoginDto location);

	List<LocationLoginDto> getAllLocations();

	LocationLoginDto updateLocation(LocationLoginDto locationDto, Long id);

	int deleteLocation(Long id);

}
