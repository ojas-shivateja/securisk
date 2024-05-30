package com.insure.rfq.service;

import com.insure.rfq.dto.ClientListCoverageDetailsDto;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;

public interface DownloadService  {

    byte[] generateCoverageDetails(String rfqId) throws IOException, DocumentException;

    byte[] generateClientListCoverageDetails(Long productId, Long clientId, List<ClientListCoverageDetailsDto> clientListCoverageDetailsDto) throws IOException, DocumentException;
}
