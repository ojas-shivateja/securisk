package com.insure.rfq.service;

import com.insure.rfq.dto.ClientListEmployee_Submit_ClaimHospitalizationDetailsDto;

import java.util.List;

public interface ClientListEmployee_Submit_ClaimHospitalizationDetailsService {
    String crateClientListEmployee_Submit_ClaimHospitalizationDetails
            (ClientListEmployee_Submit_ClaimHospitalizationDetailsDto dto ,Long clientID, Long productId,Long employeeId) ;
 List<ClientListEmployee_Submit_ClaimHospitalizationDetailsDto> getAllClientListEmployeeSubmitClaimHospitalizationDetailsDtos();
}
