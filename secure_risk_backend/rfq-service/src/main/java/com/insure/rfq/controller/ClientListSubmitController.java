package com.insure.rfq.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.entity.RequiredDocument;
import com.insure.rfq.service.ClientListSubmitService;



@RestController
@RequestMapping("/client")
@CrossOrigin(origins = "*")
public class ClientListSubmitController {
    @Autowired
    private ClientListSubmitService requiredDocumentService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("documentType") RequiredDocument documentType,
                                             @RequestParam("employeeId") String employeeId,
                                             @ModelAttribute("file") MultipartFile file,
                                             
                                             @RequestParam String UserID) {
        try {
            requiredDocumentService.saveRequiredDocument(documentType, employeeId, file,UserID);
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
