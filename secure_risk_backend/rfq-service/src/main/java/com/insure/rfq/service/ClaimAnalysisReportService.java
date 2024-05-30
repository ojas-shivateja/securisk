package com.insure.rfq.service;

import com.insure.rfq.dto.ClaimAnayalisReportDto;

public interface ClaimAnalysisReportService {
  

ClaimAnayalisReportDto generateClaimAnalysisReportPdf(String rfqId);
}
