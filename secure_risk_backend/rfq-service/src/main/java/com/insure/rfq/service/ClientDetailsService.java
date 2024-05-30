package com.insure.rfq.service;


import com.insure.rfq.dto.ClientDetailsDto;
import com.insure.rfq.dto.DisplayAllClientDetailsDto;
import com.insure.rfq.dto.UpdateClientDetailsDto;
import com.insure.rfq.entity.ClientDetailsEntity;

import java.io.IOException;
import java.util.List;

public interface ClientDetailsService {

    public ClientDetailsDto createClientDetails(ClientDetailsDto clientDetailsDto, Long clientListId, Long productId);

    List<DisplayAllClientDetailsDto> getAllClientDetails(Long clientlistId, Long productId) ;


    public String updateClientDetails(UpdateClientDetailsDto dto, Long id);
    String cleareAllClientDetails();
    String deleteClientDetailsById(Long id);
    public byte[] downloadClientDetialsDocumentByClientDetialsId(Long clientDetailsId) throws IOException;
    public List<ClientDetailsEntity> getAllClientDetailsDownload(Long clientlistId,Long productId);
    public byte[] getFileDataById(Long clientDetailsId);
    public DisplayAllClientDetailsDto getById(Long clientDetailsId);
    public String getFileExtension(String filePath);
}
