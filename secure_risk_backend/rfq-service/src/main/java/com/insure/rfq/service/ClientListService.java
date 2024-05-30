package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.ClientListChildDto;
import com.insure.rfq.dto.ClientListDto;
import com.insure.rfq.dto.GetAllClientListDto;

public interface ClientListService {

	ClientListDto createClientList(ClientListDto clientList);

	ClientListDto getClientById(long id);

	List<ClientListDto> getAllClients(int pageNo, int size, String sort);

	ClientListChildDto updateClientList(ClientListChildDto clientListDto, Long clientListid);

	String deleteClientById(Long clientId);

	List<GetAllClientListDto> getAllClientList();
}