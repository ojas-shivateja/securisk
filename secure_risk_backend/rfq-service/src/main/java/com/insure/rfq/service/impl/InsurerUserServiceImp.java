package com.insure.rfq.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.GetAllUsersByInsurerIdDto;
import com.insure.rfq.dto.InsurerUsersDto;
import com.insure.rfq.dto.UpdateInsurerUserDto;
import com.insure.rfq.entity.InsureList;
import com.insure.rfq.entity.InsureUsers;
import com.insure.rfq.exception.InvalidInsurerUser;
import com.insure.rfq.login.entity.Location;
import com.insure.rfq.login.repository.LocationRepository;
import com.insure.rfq.repository.InsureListRepository;
import com.insure.rfq.repository.InsurerUsersRepository;
import com.insure.rfq.service.InsurerUserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InsurerUserServiceImp implements InsurerUserService {
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private InsureListRepository insureListRepository;
	@Autowired
	private InsurerUsersRepository usersRepository;
	@Autowired
	private LocationRepository locationRepository;

	@Override
	public InsurerUsersDto addUser(InsurerUsersDto dto, String insureListId) {
		InsureUsers user = new InsureUsers();
		user.setUserId(UUID.randomUUID().toString());
		user.setStatus(1);

		Optional<InsureList> insurer = insureListRepository.findById(insureListId);
		Optional<Location> location = locationRepository
				.findById(insurer.isPresent() ? insurer.get().getLocationId().getLocationId() : null);

		insureListRepository.findById(insureListId).ifPresent(user::setInsureList);
		user.setLocation(location.isPresent() ? location.get() : null);
		user.setEmail(dto.getEmail());
		user.setManagerName(dto.getManagerName());
		user.setPhoneNumber(dto.getPhoneNumber());
		usersRepository.save(user);

		InsurerUsersDto insurerUsersDto = new InsurerUsersDto();
		insurerUsersDto.setUserId(user.getUserId());
		insurerUsersDto.setEmail(dto.getEmail());
		insurerUsersDto.setLocation(dto.getLocation());
		insurerUsersDto.setManagerName(dto.getManagerName());
		insurerUsersDto.setPhoneNumber(dto.getPhoneNumber());

		return insurerUsersDto;
	}

	@Override
	public InsurerUsersDto getUserById(String id) {
		InsureUsers user = usersRepository.findById(id).orElseThrow(() -> new InvalidInsurerUser(" invalid user id "));
		return modelMapper.map(user, InsurerUsersDto.class);
	}

	@Override
	public List<GetAllUsersByInsurerIdDto> getAllUsersDto(String id) {

		return usersRepository.findAll().stream().filter(user -> user.getInsureList().getInsurerId().equals(id))
				.filter(user -> user.getStatus() == 1).map(user -> {
					GetAllUsersByInsurerIdDto getAllUsersByInsurerIdDto = new GetAllUsersByInsurerIdDto();
					getAllUsersByInsurerIdDto.setUserId(user.getUserId());
					getAllUsersByInsurerIdDto.setEmail(user.getEmail());
					getAllUsersByInsurerIdDto.setManagerName(user.getManagerName());
					getAllUsersByInsurerIdDto.setPhoneNumber(user.getPhoneNumber());
					getAllUsersByInsurerIdDto.setLocation(user.getLocation().getLocationName());
					return getAllUsersByInsurerIdDto;

				}).toList();
	}

	@Override
	public int deleteUser(String id) {
		InsureUsers user = usersRepository.findById(id).orElseThrow(() -> new InvalidInsurerUser(" invalid user"));
		return usersRepository.deleteInsureList(user.getUserId());
	}

	@Override
	public UpdateInsurerUserDto updateUser(String userId, UpdateInsurerUserDto usersDto) {
		InsureUsers user = usersRepository.findById(userId).orElseThrow(() -> new InvalidInsurerUser(" Invalid user"));
		Optional<Location> location = locationRepository.findById(Long.parseLong(usersDto.getLocation()));
		user.setEmail(usersDto.getEmail());
		user.setLocation(location.isPresent() ? location.get() : null);
		user.setManagerName(usersDto.getManagerName());
		user.setPhoneNumber(usersDto.getPhoneNumber());
		user.setStatus(user.getStatus());
		usersRepository.save(user);

		UpdateInsurerUserDto updateInsurerUserDto = new UpdateInsurerUserDto();
		updateInsurerUserDto.setEmail(usersDto.getEmail());
		updateInsurerUserDto.setLocation(location.isPresent() ? location.get().getLocationName() : null);
		updateInsurerUserDto.setManagerName(usersDto.getManagerName());
		updateInsurerUserDto.setPhoneNumber(usersDto.getPhoneNumber());

		return updateInsurerUserDto;
	}

}
