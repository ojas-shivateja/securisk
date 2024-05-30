package com.insure.rfq.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insure.rfq.generator.ClaimAnalysisReport;
import com.insure.rfq.generator.Demograph;
import com.itextpdf.text.DocumentException;

@RestController
@RequestMapping("/rfq")
@CrossOrigin(origins = { "*" })
public class ReportController {

	@Autowired
	private ClaimAnalysisReport claimAnalysisReport;
	@Autowired
	private Demograph demograph;



	@GetMapping("/getclaimAnalysisReportById/{rfqId}")
	public ResponseEntity<byte[]> dowloadDemoGraph(@PathVariable String rfqId)
			throws IOException, DocumentException {
		byte[] chartImage = claimAnalysisReport.generatePdf(rfqId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "claim_analysis_report.pdf");
		return new ResponseEntity<>(chartImage, headers, HttpStatus.OK);
	}

}
