package com.insure.rfq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.ClientList_Life_PremiumCalcuaterDto;
import com.insure.rfq.service.ClientList_Life_PremiumCalcuaterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rfq/Pre_Life_Premium_Calcuater")
@CrossOrigin(origins = "*")
public class ClientList_Life_PremiumCalcuaterController {

    @Autowired
    private ClientList_Life_PremiumCalcuaterService service;

    @GetMapping("/hai")
    public String hai() {
        return "HAi to all";
    }


    @PostMapping("/saveClientList_Pre_LifePremium_Calcuater")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String SaveClientList_Life_Premium_Calcuater(
            @RequestBody ClientList_Life_PremiumCalcuaterDto premiumCalcuaterDto, @RequestParam Long clientId,
            @RequestParam Long productId) {
        log.info(premiumCalcuaterDto + "  " + clientId + " ===" + productId);

        return service.createClientList_Life_PremiumCalcuater(premiumCalcuaterDto, clientId,
                productId);
    }

    @GetMapping("/getAll_Life_PremiumCalcuaters")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ClientList_Life_PremiumCalcuaterDto> getAll_Life_PremiumCalcuaterDtos(@RequestParam Long clientId,
                                                                                      @RequestParam Long productId) {

        return service.getAllTheClientList_Life_PremiumCalcuaters(clientId, productId);
    }

    @DeleteMapping("/deleteClientList_Pre_Life_Premium_Calcuater")
    @ResponseStatus(value = HttpStatus.OK)
    public String deleteClientList_Pre_Life_Premium_Calcuater(@RequestParam Long primary_Id) {
        return service.deleteClientListLifePremiumCalcuaterDto(primary_Id);

    }

   @PatchMapping("/updateClientList_Life_PremiumCalcuaterDto")
    @ResponseStatus(value = HttpStatus.OK)
    public ClientList_Life_PremiumCalcuaterDto updateClientListFamilyPremiumCalcuaterDto(@RequestBody ClientList_Life_PremiumCalcuaterDto dto) {
        return service.update_ClientList_Life_PremiumCalcuaterDto( dto);
    }
    @GetMapping("/getClientList_Pre_Life_Premium_Calcuater")
    @ResponseStatus(value = HttpStatus.OK)
    public ClientList_Life_PremiumCalcuaterDto getClientList_PreFamilyPremium_Calcuater(@RequestParam Long primary_Id) {
        return service.getClientList_LifePremiumCalcuaterDto(primary_Id);
    }

    @GetMapping("/exportExcel")
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<byte[]> exportExcel(@RequestParam Long clientId,
                                              @RequestParam Long productId) {
        log.info(clientId + " -----------" + productId);
        byte[] excelData = service.generateExcelFromData(clientId, productId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Premium_Life_Calculator.xlsx");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(excelData);
    }


}
