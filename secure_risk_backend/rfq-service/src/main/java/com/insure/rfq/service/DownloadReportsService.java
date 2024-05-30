package com.insure.rfq.service;
import jakarta.servlet.http.HttpServletResponse;
public interface DownloadReportsService {
	void downloadPdfAsZip(HttpServletResponse response,String rfqId);
}