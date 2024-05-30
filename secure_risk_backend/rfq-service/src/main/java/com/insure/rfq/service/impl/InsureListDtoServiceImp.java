package com.insure.rfq.service.impl;

import com.insure.rfq.dto.*;
import com.insure.rfq.entity.InsureList;
import com.insure.rfq.entity.InsureUsers;
import com.insure.rfq.exception.InvalidInsurer;
import com.insure.rfq.login.entity.Location;
import com.insure.rfq.login.repository.LocationRepository;
import com.insure.rfq.repository.InsureListRepository;
import com.insure.rfq.repository.InsurerUsersRepository;
import com.insure.rfq.service.InsureListService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class InsureListDtoServiceImp implements InsureListService {
	@Autowired
	private InsureListRepository insureListRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private InsurerUsersRepository usersRepository;

	@Override
	public InsureListDto addListInsure(InsureListDto insureListDto) {
		InsureList insureList = new InsureList();
		insureList.setInsurerId(UUID.randomUUID().toString());
		insureList.setInsurerName(insureListDto.getInsurerName());
		Optional<Location> location = locationRepository.findById(Long.parseLong(insureListDto.getLocation()));
		insureList.setLocationId(location.isPresent() ? location.get() : null);
		insureList.setStatus(true);
		InsureUsers user = new InsureUsers();
		user.setUserId(UUID.randomUUID().toString());
		user.setEmail(insureListDto.getEmail());
		user.setManagerName(insureListDto.getManagerName());
		user.setPhoneNumber(Long.parseLong(insureListDto.getPhoneNumber()));
		user.setLocation(location.isPresent() ? location.get() : null);
		user.setStatus(1);
		user.setInsureList(insureList);
		insureListRepository.save(insureList);
		usersRepository.save(user);
		InsureListDto listDto = new InsureListDto();
		listDto.setEmail(insureListDto.getEmail());
		listDto.setInsurerName(insureListDto.getInsurerName());
		listDto.setLocation(location.get().getLocationName());
		listDto.setManagerName(insureListDto.getManagerName());
		listDto.setPhoneNumber(insureListDto.getPhoneNumber());
		return listDto;
	}

	@Override
	public List<GetAllInsurerListDto> getAllInsureListDto() {
		List<InsureList> insurers = insureListRepository.findAll();

		return insurers.stream().filter(InsureList::isStatus).map(insurer -> {
			GetAllInsurerListDto allInsurerListDto = new GetAllInsurerListDto();
			allInsurerListDto.setInsurerId(insurer.getInsurerId());
			allInsurerListDto.setInsurerName(insurer.getInsurerName());

			Optional<Location> locations = locationRepository.findById(insurer.getLocationId().getLocationId());
			allInsurerListDto.setLocation(locations.isPresent() ? locations.get().getLocationName() : null);

			List<InsureUsers> users = usersRepository.getAllUsersByInsurerId(insurer.getInsurerId());

			List<GetAllUsersByInsurerIdDto> allUsersByInsurerIdDtos = users.stream()
					.filter(user -> user.getStatus() == 1).map(user -> {
						GetAllUsersByInsurerIdDto usersByInsurerIdDto = new GetAllUsersByInsurerIdDto();
						usersByInsurerIdDto.setUserId(user.getUserId());
						usersByInsurerIdDto.setEmail(user.getEmail());
						usersByInsurerIdDto.setManagerName(user.getManagerName());
						usersByInsurerIdDto.setPhoneNumber(user.getPhoneNumber());
						usersByInsurerIdDto.setLocation(user.getLocation().getLocationName());
						return usersByInsurerIdDto;
					}).toList();

			allInsurerListDto.setListOfUsers(allUsersByInsurerIdDtos);

			return allInsurerListDto;
		}).toList();
	}

	@Override
	public int deleteInsureListDto(String id) {
		InsureList insureList = insureListRepository.findById(id)
				.orElseThrow(() -> new InvalidInsurer("Invalid Insurer Id"));
		return insureListRepository.deleteInsureList(insureList.getInsurerId());
	}

	@Override
	public UpdateInsurerListDto updateInsureListDto(String insurerId, UpdateInsurerListDto insureListDto) {
		InsureList insureList = insureListRepository.findById(insurerId)
				.orElseThrow(() -> new InvalidInsurer("Invalid id: " + insurerId));

		Optional<Location> location = locationRepository.findById(Long.parseLong(insureListDto.getLocation()));
		insureList.setInsurerName(insureListDto.getInsurerName());
		insureList.setLocationId(location.isPresent() ? location.get() : null);
		insureListRepository.save(insureList);

		UpdateInsurerListDto updatedDto = new UpdateInsurerListDto();
		updatedDto.setInsurerName(insureList.getInsurerName());
		updatedDto.setLocation(location.get().getLocationName());

		return updatedDto;
	}

	@Override
	public List<String> getAllInsureListDtoName() {
		List<InsureListDto> listInsureListDto = insureListRepository.findAll().stream()
				.filter(obj -> obj.isStatus() == true).map(u -> modelMapper.map(u, InsureListDto.class)).toList();
		return listInsureListDto.stream().map(e -> e.getInsurerName()).toList();
	}

	@Override
	public List<GetInsuresDropdownDto> getAllInsuresById() {
		List<InsureList> insures = insureListRepository.findAll();
		List<GetInsuresDropdownDto> getInsuresWithIdDtos = new ArrayList<>();
		for (InsureList getInsuresWithIdDto : insures) {
			if (getInsuresWithIdDto.isStatus()) {
				GetInsuresDropdownDto insuresWithIdDto = new GetInsuresDropdownDto();
				insuresWithIdDto.setInsurerId(getInsuresWithIdDto.getInsurerId());
				insuresWithIdDto.setInsurerName(getInsuresWithIdDto.getInsurerName());
				getInsuresWithIdDtos.add(insuresWithIdDto);
			}
		}
		return getInsuresWithIdDtos;
	}

}
