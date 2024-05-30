package com.insure.rfq.service;

import com.insure.rfq.dto.NatureofBusinessDto;

import java.util.List;

public interface NatureofBusinessService {

    String  seaveNatureofBusinessData(NatureofBusinessDto natureofBusinessDto);
   List<NatureofBusinessDto> getAllNatureofBusinessData();

}
