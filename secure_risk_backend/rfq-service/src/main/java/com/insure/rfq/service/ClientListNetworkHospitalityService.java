package com.insure.rfq.service;

import com.insure.rfq.dto.ClientListNetworkHospitalDataStatus;
import com.insure.rfq.dto.ClientListNetworkHospitalHeadersMapping1Dto;
import com.insure.rfq.dto.GetAllClientListNetWorkHospitalDto;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ClientListNetworkHospitalityService {

    ClientListNetworkHospitalHeadersMapping1Dto validateHeadersBasedOnTpa(MultipartFile multipartFile, String tpaName) throws IOException;

    List<ClientListNetworkHospitalDataStatus> validateValuesBasedOnTpa(MultipartFile multipartFile, String tpaName) throws IOException;

    void validateBasedOnSheet(Sheet sheet, String tpaName, List<ClientListNetworkHospitalDataStatus> claimsMisValidateData);

    String uploadNetworkHospitalData(List<ClientListNetworkHospitalDataStatus> clientListMemberDetailsDataStatuses, Long clientListId, Long productId);

    List<GetAllClientListNetWorkHospitalDto> getAllclientListEnrollmentData(Long clientListId, Long productId);
    
    byte[] getNetworkHospitalInExcelFormat(Long clientListId,Long productId);
}