package com.insure.rfq.service;

import com.insure.rfq.dto.ClientListContactDetailsDto;
import com.insure.rfq.dto.ResponseDto;

import java.util.List;

public interface ClientListContactDetailsService {

    ResponseDto createContactDetails(Long clientListId,Long productId,ClientListContactDetailsDto clientListContactDetailsDto);

    List<ClientListContactDetailsDto> getAllContactDetails(Long clientListId, Long productId);

    ClientListContactDetailsDto getContactDetailsByContactId(Long contactId);

    ClientListContactDetailsDto updateContactDetailsByContactId(Long contactId,ClientListContactDetailsDto clientListContactDetailsDto);

    String  deleteContactDetailsByContactId(Long contactId);
}
