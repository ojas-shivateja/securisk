package com.insure.rfq.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.AddClientListUserDto;
import com.insure.rfq.dto.ClientListUserDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientListUser;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidClientListUser;
import com.insure.rfq.login.entity.Designation;
import com.insure.rfq.login.repository.DesignationRepository;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ClientListUserRepository;
import com.insure.rfq.service.ClientListUserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientListUserServiceImpl implements ClientListUserService {

	@Autowired
	private ClientListUserRepository clientListUserRepository;

	@Autowired
	private ClientListRepository clientListRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private DesignationRepository designationRepository;

	@Override
	public AddClientListUserDto createUser(AddClientListUserDto clientListUserDto, Long id) {
		ClientList clientList = clientListRepository.findById(id)
				.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
		log.info("Client List From Create Client List User", clientList);
		Optional<Designation> designation = designationRepository
				.findById(Long.parseLong(clientListUserDto.getDesignationId()));
		log.info("Designation From Create Client List User", designation);
		ClientListUser clientListUser = new ClientListUser();
		clientListUser.setCreatedDate(LocalDateTime.now());
		clientListUser.setEmployeeId(clientListUserDto.getEmployeeId());
		clientListUser.setDesignationId(designation.get());
		clientListUser.setName(clientListUserDto.getName());
		clientListUser.setClientList(clientList);
		clientListUser.setMailId(clientListUserDto.getMailId());
		clientListUser.setPhoneNo(clientListUserDto.getPhoneNo());
		clientListUser.setStatus("ACTIVE");
		clientListUserRepository.save(clientListUser);
		return modelMapper.map(clientListUser, AddClientListUserDto.class);
	}

	@Override
	public List<ClientListUserDto> getAllUsers(Long id) {
		return clientListUserRepository.findAll().stream()
				.filter(users -> users.getClientList() != null && users.getClientList().getCid() == id
						&& users.getStatus().equalsIgnoreCase("ACTIVE"))
				.map(user -> modelMapper.map(user, ClientListUserDto.class)).toList();
	}

	@Override
	public ClientListUserDto updateUserById(Long uid, ClientListUserDto clientListUserDto) {
		ClientListUser clientListUser = clientListUserRepository.findById(uid)
				.orElseThrow(() -> new InvalidClientListUser("User is Not Present"));
		log.info("Client List User From Update Client List User", clientListUser);
		Optional<Designation> designation = designationRepository.findById(clientListUser.getDesignationId().getId());
		log.info("Designation From Update Client List User", designation);
		if (clientListUser != null) {
			clientListUser.setEmployeeId(clientListUserDto.getEmployeeId());
			clientListUser.setName(clientListUserDto.getName());
			clientListUser.setMailId(clientListUserDto.getMailId());
			clientListUser.setPhoneNo(clientListUserDto.getPhoneNo());
			clientListUser.setUpdatedDate(LocalDateTime.now());
			clientListUser.setDesignationId(designation.get());
			clientListUserRepository.save(clientListUser);
		}
		return modelMapper.map(clientListUser, ClientListUserDto.class);

	}

	@Override
	public String deleteClientUserById(Long clientUserId) {
		ClientListUser clientListUser = clientListUserRepository.findById(clientUserId).get();
		clientListUser.setStatus("INACTIVE");
		clientListUserRepository.save(clientListUser);

		return "User Deleted Successfully";

	}

}
