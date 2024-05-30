package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.ExcelReportHeadersDto;
import com.insure.rfq.entity.ExcelReportHeaders;

public interface ExcelReportHeadersService {
	ExcelReportHeadersDto createHeaders(ExcelReportHeadersDto header);
	List<ExcelReportHeaders> viewAllHeaders();
}
