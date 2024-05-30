package com.insure.rfq.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.ClientListEmployee_Claim_IntimmationDataDisplayDto;
import com.insure.rfq.dto.ClientListEmployee_Claim_IntimmationDto;
import com.insure.rfq.generator.Claim_IntimmationPDfGenerator;
import com.insure.rfq.service.ClientListEmployee_Claim_IntimmationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("rfq/ClientListEmployee_Claim_Intimmation")
public class ClientListEmployee_Claim_IntimmationController {


    private final ClientListEmployee_Claim_IntimmationService serviceImpl;
    private final Claim_IntimmationPDfGenerator claimIntimationPDfGenerator;

    @Autowired
    public ClientListEmployee_Claim_IntimmationController(ClientListEmployee_Claim_IntimmationService serviceImpl,
                                                         Claim_IntimmationPDfGenerator claimIntimationPDfGenerator) {
        this.serviceImpl = serviceImpl;
        this.claimIntimationPDfGenerator = claimIntimationPDfGenerator;
    }



    @PostMapping("/saveClientListEmployee_Claim_Intimmation")
    public  String saveData(@RequestBody ClientListEmployee_Claim_IntimmationDto  dto, @RequestParam Long clientListId, @RequestParam Long productId, @RequestParam Long employeeId){
        log.info("Dto is {} : clientlistid {} :  productId{} : employeeId{} ",dto , clientListId ,productId ,employeeId);
        return  serviceImpl.saveClientListEmployeeClaimIntimmation(dto, clientListId, productId, employeeId);

    }

    @GetMapping("/getAllClientListEmployee_Claim_Intimmation")
    public List<ClientListEmployee_Claim_IntimmationDataDisplayDto> getAllClientListEmployeeClaimIntimmations(){
        return  serviceImpl.gClientListEmployeeClaimIntimmation();
    }

    @GetMapping("/claimIntimationpdf/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] generatePdf = claimIntimationPDfGenerator.generatePdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, " attachment;filename:data.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(generatePdf);

    }

    @PostMapping("/claimIntimationSendEmail")
    public String generateAndEmailPDF(@RequestParam Long id, @RequestBody List<String> emailRecipients) {
        claimIntimationPDfGenerator.generateAndSendPDF(id, emailRecipients);
        return "PDF generated and emailed successfully.";
    }



}
