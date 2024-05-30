package com.insure.rfq.service;

import com.insure.rfq.dto.DisplayAllEndorsementDto;
import com.insure.rfq.dto.EndorsementDto;
import com.insure.rfq.dto.UpdateEndorsementDetailsDto;
import com.insure.rfq.entity.EndorsementEntity;

import java.io.IOException;
import java.util.List;

public interface EndorsementService {

    EndorsementDto createEndorsement(EndorsementDto endorsementDto,Long clientListId,Long poductId);

    List<DisplayAllEndorsementDto> getAllEndorsement(Long clientlistId, Long productId) ;
    
    
    DisplayAllEndorsementDto getById(Long endorsementId);

   public String updateEndorsementById(UpdateEndorsementDetailsDto dto, Long id);

    String deleteEndorsementById(Long id);
    
    String clearAllEndorsements();

    String getFileExtension(String filePath);

    byte [] downloadEndrosementDocumentByEndrosementId(Long endorsementId) throws IOException;


    List<EndorsementEntity> getAllEndorsements(Long clientlistId,Long productId);
    byte[] getFileDataById(Long endorsementId);
}
