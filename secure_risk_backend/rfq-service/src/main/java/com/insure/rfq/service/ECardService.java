package com.insure.rfq.service;

import com.insure.rfq.dto.DisplayAllECardDto;
import com.insure.rfq.dto.ECardDto;
import com.insure.rfq.dto.UpdateECardDto;
import com.insure.rfq.entity.ECardEntity;

import java.io.IOException;
import java.util.List;

public interface ECardService {

    ECardDto create(ECardDto eCardDto, Long clientListId, Long productId);
    public List<DisplayAllECardDto> getAllECard(Long clientlistId, Long productId);
    public DisplayAllECardDto getById(Long eCardId);
    public String deleteECardById(Long id);
    public String updateECardById(UpdateECardDto dto, Long id);
    public String getFileExtension(String filePath);
    public byte[] downloadClientDetialsDocumentByClientDetialsId(Long eCardId) throws IOException;

    public List<ECardEntity> getAllECardDownload(Long clientlistId, Long productId);
    public byte[] getFileDataById(Long eCardId);

    public List<DisplayAllECardDto> getAllECardbyId(Long clientlistId, Long productId, String employeeId);

    byte[] downloadClientDetialsDocumentByEmployeeId(String employeeId) throws IOException;

    void uploadEcardsDocument(ECardDto fileName, Long clientListId, Long productId) throws IOException;


}
