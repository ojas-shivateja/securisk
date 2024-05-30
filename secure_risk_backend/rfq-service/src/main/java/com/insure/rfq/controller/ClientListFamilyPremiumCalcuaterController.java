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

import com.insure.rfq.dto.ClientListFamilyPremiumCalcuaterDto;
import com.insure.rfq.service.ClientListFamilyPremiumCalcuaterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rfq/PreFamilyPremium_Calcuater")
@CrossOrigin(origins = "*")
public class ClientListFamilyPremiumCalcuaterController {

    @Autowired
    private ClientListFamilyPremiumCalcuaterService premiumCalcuaterServiceimpl;

    @PostMapping("/saveClientList_PreFamilyPremium_Calcuater")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String SaveClientList_PreFamilyPremium_Calcuater(
            @RequestBody ClientListFamilyPremiumCalcuaterDto premiumCalcuaterDto, @RequestParam Long clientId,
            @RequestParam Long productId) {
        log.info(premiumCalcuaterDto + "  " + clientId + " ===" + productId);

        return premiumCalcuaterServiceimpl.createClientListFamilyPremiumCalcuater(premiumCalcuaterDto, clientId,
                productId);
    }

    @GetMapping("/getAllPremiumCalcuaters")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ClientListFamilyPremiumCalcuaterDto> getAllPremiumCalcuaterDtos(@RequestParam Long clientId,
                                                                                @RequestParam Long productId) {
        return premiumCalcuaterServiceimpl.getAllTheClientListPremiumCalcuaters(clientId, productId);
    }

    @DeleteMapping("/deleteClientList_PreFamilyPremium_Calcuater")
    @ResponseStatus(value = HttpStatus.OK)
    public String deleteClientList_PreFamilyPremium_Calcuater(@RequestParam Long primary_Id) {
        return premiumCalcuaterServiceimpl.deleteClientListPremiumCalcuaterDto(primary_Id);

    }

    @PatchMapping("/updateClientListFamilyPremiumCalcuater")
    @ResponseStatus(value = HttpStatus.OK)
    public  ClientListFamilyPremiumCalcuaterDto updateClientListFamilyPremiumCalcuaterDto(@RequestBody ClientListFamilyPremiumCalcuaterDto dto) {
        return premiumCalcuaterServiceimpl.update_ClientListFamilyPremiumCalcuaterDto( dto);
    }
    @GetMapping("/getClientList_PreFamilyPremium_Calcuater")
    @ResponseStatus(value = HttpStatus.OK)
    public ClientListFamilyPremiumCalcuaterDto getClientList_PreFamilyPremium_Calcuater(@RequestParam Long primary_Id) {
        return premiumCalcuaterServiceimpl.getClientListPremiumCalcuaterDto(primary_Id);

    }

    @GetMapping("/exportExcel")
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<byte[]> exportExcel(@RequestParam Long clientId,
                                              @RequestParam Long productId) {
        log.info(clientId + " -----------" + productId);
        byte[] excelData = premiumCalcuaterServiceimpl.generateExcelFromData(clientId, productId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Premium_Calculator.xlsx");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(excelData);
    }


}
