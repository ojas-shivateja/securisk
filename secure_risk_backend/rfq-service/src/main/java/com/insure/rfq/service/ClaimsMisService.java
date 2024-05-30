package com.insure.rfq.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.ClaimsDumpDto;
import com.insure.rfq.dto.ClaimsMisDataStatusValidateDto;
import com.insure.rfq.dto.ClaimsUploadDto;
import com.insure.rfq.dto.CovergaeHeaderValidateDto;
import com.insure.rfq.entity.ClaimsMisEntity;

public interface ClaimsMisService {
    CovergaeHeaderValidateDto validateClaimsMisHeader(MultipartFile file, String tpaName);

    List<ClaimsMisDataStatusValidateDto> validateClaimsMisDataWithStatus(MultipartFile file, String tpaName);

    List<ClaimsMisEntity> getAllClaimsMisByRfqId(String rfqId);

    List<Object[]> getRfqCounts();

    ClaimsUploadDto getClaimsAferUpload(String rfqId);

    ClaimsDumpDto getClaimsDump(String rfqId);
}
