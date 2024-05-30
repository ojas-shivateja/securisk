package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.GetAllUsersByInsurerIdDto;
import com.insure.rfq.dto.InsurerUsersDto;
import com.insure.rfq.dto.UpdateInsurerUserDto;

public interface InsurerUserService {
	InsurerUsersDto addUser(InsurerUsersDto usersDto, String insureListId);

	InsurerUsersDto getUserById(String id);

	List<GetAllUsersByInsurerIdDto> getAllUsersDto(String id);

	int deleteUser(String id);

	UpdateInsurerUserDto updateUser(String id, UpdateInsurerUserDto usersDto);
}
