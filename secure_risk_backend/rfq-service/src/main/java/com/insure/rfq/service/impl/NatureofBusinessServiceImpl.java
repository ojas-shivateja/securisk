package com.insure.rfq.service.impl;

import com.insure.rfq.dto.NatureofBusinessDto;
import com.insure.rfq.entity.NatureofBusinessEntity;
import com.insure.rfq.repository.NatureofBusinessrepository;
import com.insure.rfq.service.NatureofBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class NatureofBusinessServiceImpl implements NatureofBusinessService {
    @Autowired
    private NatureofBusinessrepository natureofBusinessRepository;


    @Override
    public String seaveNatureofBusinessData(NatureofBusinessDto natureofBusinessDto) {

        if (natureofBusinessDto != null) {

            NatureofBusinessEntity entity = new NatureofBusinessEntity();
            entity.setNameofNatureofBusiness(natureofBusinessDto.getNameofNatureofBusiness());
            entity.setRecordStatus("ACTIVE");
            entity.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));


            natureofBusinessRepository.save(entity);


            return "Nature of Business created successfully.";

        } else
            return "Failed to Create Nature of Business ";

    }


    @Override
    public List<NatureofBusinessDto> getAllNatureofBusinessData() {
        return natureofBusinessRepository.findAll().stream().map(i -> {
            NatureofBusinessDto dto = new NatureofBusinessDto();
            dto.setNameofNatureofBusiness(i.getNameofNatureofBusiness());
            return dto;
        }).toList();

    }
}
