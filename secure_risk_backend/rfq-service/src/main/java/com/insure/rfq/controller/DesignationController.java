package com.insure.rfq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.login.dto.DesignationLoginDto;
import com.insure.rfq.login.service.DesignationService;

@RestController
@RequestMapping("/rfq/designation")
@CrossOrigin(origins = {"*"})
public class DesignationController {
    @Autowired
    private DesignationService designationService;


    @GetMapping("/getAllDesignation")
    @ResponseStatus(value = HttpStatus.OK)
    public List<DesignationLoginDto> getAllDesignation() {
        return designationService.getAllDesiDesignation();
    }

}
