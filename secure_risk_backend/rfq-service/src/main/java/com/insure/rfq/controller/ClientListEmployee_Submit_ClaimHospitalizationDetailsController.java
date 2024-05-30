package com.insure.rfq.controller;

import com.insure.rfq.dto.ClientListEmployee_Submit_ClaimHospitalizationDetailsDto;
import com.insure.rfq.service.ClientListEmployee_Submit_ClaimHospitalizationDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rfq/submit_ClaimHospitalizationDetailsController")
@CrossOrigin("*")

public class ClientListEmployee_Submit_ClaimHospitalizationDetailsController {


    @Autowired
    private ClientListEmployee_Submit_ClaimHospitalizationDetailsService serviceimpl;

    

    @PostMapping("/createSubmit_ClaimHospitalizationDetails")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String createClientListEmployee_Submit_ClaimHospitalizationDetails(@RequestBody ClientListEmployee_Submit_ClaimHospitalizationDetailsDto dto, Long clientID, Long productId, Long employeeId) {
        return serviceimpl.crateClientListEmployee_Submit_ClaimHospitalizationDetails(dto, clientID, productId, employeeId);
    }

    @GetMapping("/getAllSubmit_ClaimHospitalizationDetails")
    public List<ClientListEmployee_Submit_ClaimHospitalizationDetailsDto> getAllClientListEmployeeSubmitClaimHospitalizationDetailsDtos(){
        return  serviceimpl.getAllClientListEmployeeSubmitClaimHospitalizationDetailsDtos();
    }

}

