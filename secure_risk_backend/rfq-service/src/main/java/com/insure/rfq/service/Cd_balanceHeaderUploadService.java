package com.insure.rfq.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.Cd_balanceDisplayDto;
import com.insure.rfq.dto.Cd_balanceHeaderMappingDto;
import com.insure.rfq.dto.Cd_balanceHeaderStatusDto;
import com.insure.rfq.dto.Cd_balanceValueStatus;


public interface Cd_balanceHeaderUploadService {

    Cd_balanceHeaderMappingDto uploadEnrollement(Cd_balanceHeaderMappingDto clientListEnrollementUploadDto);
    Cd_balanceHeaderStatusDto validateHeaders(MultipartFile multipartFile);
//    List<Cd_balanceEntitytable> getDataformFile(MultipartFile file);
    List<Cd_balanceDisplayDto> getDataformFile(Long clientID, Long produtId);
     String saveData(List<Cd_balanceValueStatus> valueStatuses,
                           Long clientID, Long productId);
     String deleteTheCdBalancData(Long cd_balanceId);
      String updateCdBalanceData(Cd_balanceDisplayDto dto,Long cd_balanceId);
     List<Cd_balanceValueStatus> validateValuesBased(MultipartFile multipartFile ) throws IOException;
     byte[] generateExcelFromData(Long clientListId, Long productId);

}
