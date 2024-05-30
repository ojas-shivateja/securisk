package com.insure.rfq.login.service.impl;

import com.insure.rfq.login.dto.LocationLoginDto;
import com.insure.rfq.login.dto.UsersNewDtoGet;
import com.insure.rfq.login.entity.Location;
import com.insure.rfq.login.entity.UserRegisteration;
import com.insure.rfq.login.repository.LocationRepository;
import com.insure.rfq.login.repository.UserRepositiry;
import com.insure.rfq.login.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {

	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private UserRepositiry usersNewRepositiry;

	@Override
	public String createLocation(LocationLoginDto locationDto) {
		if (locationDto != null) {
			Location location = modelMapper.map(locationDto, Location.class);
			location.setStatus("ACTIVE");

			try {
				locationRepository.save(location);
				log.info("Location created successfully");
				return "Location created successfully";
			} catch (DataIntegrityViolationException ex) {
				log.warn("Location name is unique, duplicates not allowed");
				return "Location name is unique, duplicates not allowed";
			}
		}

		log.warn("Invalid input");
		return "Invalid input";
	}

	@Override
	public List<LocationLoginDto> getAllLocations() {


		String active = "ACTIVE";
		List<Location> allLocations = locationRepository.findAll().stream().filter(status -> status.getStatus().equalsIgnoreCase(active)).toList();
		List<LocationLoginDto> listOfLocations = new ArrayList<>();
		log.info("Getting all locations...");
		for (Location locationDto : allLocations) {
			LocationLoginDto dto = new LocationLoginDto();
			dto.setLocationId(locationDto.getLocationId());
			dto.setLocationName(locationDto.getLocationName());
			dto.setSno(locationDto.getSno());
			List<UserRegisteration> allUsersByLocationId = usersNewRepositiry
					.getAllUsersByLocationId(locationDto.getLocationId());
			log.debug("Processing location: {}", locationDto.getLocationName());
			List<UsersNewDtoGet> userDtos = new ArrayList<>();

			for (UserRegisteration user : allUsersByLocationId) {
				if (user.getStatus().equalsIgnoreCase(active)) {
					UsersNewDtoGet usersNewDtoGet = new UsersNewDtoGet();
					usersNewDtoGet.setBusinessType(user.getBusinessType());
					usersNewDtoGet.setCorporateName(user.getCorporateName());
					usersNewDtoGet.setAge(user.getAge());
					usersNewDtoGet.setDateOfBirth(user.getDateOfBirth());
					usersNewDtoGet.setEmployeeId(user.getEmployeeId());
					usersNewDtoGet.setDepartment(
							usersNewRepositiry.findDepartmentByUserId(user.getUserId()).getDepartmentName());
					usersNewDtoGet.setDesignation(
							usersNewRepositiry.findDesignationByUserId(user.getUserId()).getDesignationName());
					usersNewDtoGet
							.setLocation(usersNewRepositiry.findLocationByUserId(user.getUserId()).getLocationName());
					usersNewDtoGet.setEmail(user.getEmail());
					usersNewDtoGet.setFirstName(user.getFirstName());
					usersNewDtoGet.setLastName(user.getLastName());
					usersNewDtoGet.setGender(user.getGender());
					usersNewDtoGet.setPhoneNo(user.getPhoneNo());
					userDtos.add(usersNewDtoGet);
				}
			}

			dto.setUsersNewId(userDtos);
			listOfLocations.add(dto);
		}

		log.info("Returning {} locations.", listOfLocations.size());
		return listOfLocations;
	}

	@Override
	public LocationLoginDto updateLocation(LocationLoginDto locationDto, Long id) {
		log.info("Updating location with ID: {}", id);

		if (locationDto != null) {
			Location location = locationRepository.findById(id).orElseThrow();

			Location map = modelMapper.map(location, Location.class);
			map.setLocationName(locationDto.getLocationName());
			Location updatedLocation = locationRepository.save(map);

			log.info("Location updated successfully: {}", updatedLocation);

			return modelMapper.map(updatedLocation, LocationLoginDto.class);
		}

		log.warn("LocationDto is null, update failed for ID: {}", id);

		return null;
	}

	@Override
	public int deleteLocation(Long id) {
		log.info("Deleting location with ID: {}", id);

		Location location = locationRepository.findById(id).orElseThrow();
		int deletedCount = locationRepository.deleteLocation(location.getLocationId());

		log.info("Deleted {} locations with ID: {}", deletedCount, id);

		return deletedCount;
	}

}
