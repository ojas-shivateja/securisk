package com.insure.rfq.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.service.DownloadReportsService;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/dowloadZip")
public class DowloadReportsController {

	@Autowired
	private DownloadReportsService pdfService;

	@GetMapping("/reports")
	public void downloadPdfAsZip(HttpServletResponse response, @RequestParam String rfqId) {
		pdfService.downloadPdfAsZip(response,rfqId);
	}
}
