package com.insure.rfq.controller;

import com.insure.rfq.dto.ClientListEmployee_Submit_Claim_User_DetailsDto;
import com.insure.rfq.service.ClientListEmployee_Submit_Claim_User_DetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("rfq/clientListEmployee_Submit_Claim_User_Details")
public class ClientListEmployee_Submit_Claim_User_DetailsController {

    @Autowired
private ClientListEmployee_Submit_Claim_User_DetailsService service;



    @PostMapping("/saveclientListEmployee_Submit_Claim_User_DetailsDto")
public String saveClientListEmployeeSubmitClaimUserDetailsDto(@RequestBody ClientListEmployee_Submit_Claim_User_DetailsDto dto ,@RequestParam Long clientId, @RequestParam Long productId, @RequestParam Long employeeId) {
 
    return service.saveClientListEmployeeSubmitClaimUserDetailsDto(dto,clientId,productId, employeeId);
}

@GetMapping("/getllClientListEmployeeSubmitClaimUserDetailsDtos")
public List<ClientListEmployee_Submit_Claim_User_DetailsDto> getllClientListEmployeeSubmitClaimUserDetailsDtos(){
        return service.getAllClientListEmployeeSubmitClaimUserDetailsDtos();
}


}
