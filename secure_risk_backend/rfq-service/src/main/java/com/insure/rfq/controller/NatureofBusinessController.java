package com.insure.rfq.controller;

import com.insure.rfq.dto.NatureofBusinessDto;
import com.insure.rfq.service.impl.NatureofBusinessServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rfq/NatureofBusiness")
@CrossOrigin(origins = "*")
@Slf4j
public class NatureofBusinessController {

    @Autowired
    private NatureofBusinessServiceImpl serviceimpl;

    @PostMapping("/saveNatureofBusiness")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String saveNatureofBusiness( @Valid @RequestBody NatureofBusinessDto dto){
        return serviceimpl.seaveNatureofBusinessData(dto);
    }

    @GetMapping("/getllNatureofBusiness")
    @ResponseStatus(value = HttpStatus.OK)
    public List<NatureofBusinessDto>  getllNatureofBusiness(){
        return  serviceimpl.getAllNatureofBusinessData();
    }

}
