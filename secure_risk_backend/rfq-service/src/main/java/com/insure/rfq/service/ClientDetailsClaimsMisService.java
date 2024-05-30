package com.insure.rfq.service;

import com.insure.rfq.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClientDetailsClaimsMisService {


    public MultipartFile create(ClaimsMisClientDetailsDto clientDetailsDto, Long clientlistId);

    CovergaeHeaderValidateDto validateClaimsMisHeader(MultipartFile file, String tpaName);

    List<ClaimsMisDataStatusValidateDto> validateClaimsMisDataWithStatus(MultipartFile file, String tpaName);

    List<ClaimsMisNewDto> getAllClaimsMisByRfqId(Long clientlistId, Long productId, String month);

    List<Object[]> getRfqCounts();

    ClaimsUploadDto getClaimsAferUpload(String rfqId);

    ClaimsDumpDto getClaimsDump(String rfqId);

    List<ClaimsMisNewDto> getDataWithStatus(Long clientlistId, Long productId, String month);

    Object getStatusCounts(Long clientlistId, Long productId);

    String uploadFileCoverage(ClientDetailsClaimsMisUploadDto coverageUploadDto, Long clientlistId, Long productId);

    public byte[] generateExcelFromData(Long clientListId, Long productId);

    ClientListClaimsTotalCountDto getCountByStatus(Long clientlistId, Long productId);

    List<ClaimsMisNewDto> getClaimsForEmployee(Long clientlistId, Long productId, String employeeId);

    byte[] getClaimsForEmployeeInExcelFormat(Long clientlistId, Long productId, String employeeId);

    Object getStatusCountForEmployee(Long clientlistId, Long productId,String employeeId);
    
    List<ClientListClaimsTrackerDto> getClaimDetailsForEmployee(Long clientId, Long productId, String employeeId);


}
