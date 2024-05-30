package com.insure.rfq.service;


import com.insure.rfq.dto.ClientListEmployee_Submit_Claim_User_DetailsDto;

import java.util.List;

public interface ClientListEmployee_Submit_Claim_User_DetailsService {

     String saveClientListEmployeeSubmitClaimUserDetailsDto(ClientListEmployee_Submit_Claim_User_DetailsDto dto, Long clientID, Long productId,Long employeeId) ;
List<ClientListEmployee_Submit_Claim_User_DetailsDto>  getAllClientListEmployeeSubmitClaimUserDetailsDtos();

    }
