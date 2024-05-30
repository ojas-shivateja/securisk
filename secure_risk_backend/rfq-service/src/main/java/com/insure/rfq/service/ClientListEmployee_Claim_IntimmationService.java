package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.ClientListEmployee_Claim_IntimmationDataDisplayDto;
import com.insure.rfq.dto.ClientListEmployee_Claim_IntimmationDto;

public interface ClientListEmployee_Claim_IntimmationService {

    String  saveClientListEmployeeClaimIntimmation(ClientListEmployee_Claim_IntimmationDto clientListEmployeeClaimIntimmationDto, Long clientListId, Long productId, Long employeeId);

    List<ClientListEmployee_Claim_IntimmationDataDisplayDto> gClientListEmployeeClaimIntimmation();
}
