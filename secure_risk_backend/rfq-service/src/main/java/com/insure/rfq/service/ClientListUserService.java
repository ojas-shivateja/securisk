package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.AddClientListUserDto;
import com.insure.rfq.dto.ClientListUserDto;

public interface ClientListUserService {

	AddClientListUserDto createUser(AddClientListUserDto clientListUserDto, Long id);

	List<ClientListUserDto> getAllUsers(Long id);

	ClientListUserDto updateUserById(Long uid, ClientListUserDto clientListUserDto);

	String deleteClientUserById(Long clientUserId);
}
