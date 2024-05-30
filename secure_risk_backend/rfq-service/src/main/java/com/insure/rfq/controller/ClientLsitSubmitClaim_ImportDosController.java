package com.insure.rfq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.dto.Declarationandclaim_Submission_ImportantNotesDto;
import com.insure.rfq.dto.ImportantNotesDisplayDto;
import com.insure.rfq.service.ClientListSubmission_ImportantNotesService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rfq/clientLsitSubmitClaim_ImportDos")
@CrossOrigin("*")
@Slf4j
public class ClientLsitSubmitClaim_ImportDosController {

    @Autowired
    private ClientListSubmission_ImportantNotesService submissionService;


    @PostMapping("/saveImportantNotes")
    public String saveImportantNotesDisplayDto(@ModelAttribute Declarationandclaim_Submission_ImportantNotesDto dto ,@RequestParam Long clientId, @RequestParam Long productId, @RequestParam Long employeeId) {
         return submissionService.sbmitClaim_Declarationandclaim_Submission_ImportantNotesCreation(dto,  clientId, productId, employeeId);
   }
    
    @GetMapping("/getAllImportantNotesData")
    public List<ImportantNotesDisplayDto> getAllImportantNotesDisplayDto(){
    	return submissionService.getAllImportantNotesDisplayDto();
    }
}
