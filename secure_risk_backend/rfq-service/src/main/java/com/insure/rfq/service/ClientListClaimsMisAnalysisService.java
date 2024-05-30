package com.insure.rfq.service;

import com.insure.rfq.dto.ClaimAnayalisMisReportDto;
import com.insure.rfq.dto.ClaimAnayalisReportDto;

public interface ClientListClaimsMisAnalysisService {
	ClaimAnayalisMisReportDto generateReport(Long clientListId, Long productId);

}
