package com.insure.rfq.service.impl;

import com.insure.rfq.dto.CoverageDetailsDto;
import com.insure.rfq.entity.*;
import com.insure.rfq.generator.AgeBindingReportPdfGenerator;
import com.insure.rfq.generator.ClaimAnalysisReport;
import com.insure.rfq.generator.CoverageDetailsPdfGenerator;
import com.insure.rfq.generator.IrdaPdfGenerator;
import com.insure.rfq.repository.*;
import com.insure.rfq.service.CorporateDetailsService;
import com.insure.rfq.service.DownloadReportsService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class DownloadReportsServiceImpl implements DownloadReportsService {
	@Autowired
	private IrdaPdfGenerator irdaPdfGenerator;
	@Autowired
	private ExpiryPolicyDetailsRepository expiryPolicyDetailsRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private CoverageDetailsRepository cdEBRepo;
	@Autowired
	private CorporateDetailsRepository corporateDetailsRepo;
	@Autowired
	private CoverageDetailsPdfGenerator coverageDetailsPdfGenerator;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private CoverageDetailsRepository coverageDetailsRepository;
	@Autowired
	private AgeBindingReportPdfGenerator ageBindingReportPdfGenerator;
	@Autowired
	private CoverageDetailsServiceImpl coverageDetailsServiceImpl;
	@Autowired
	private CorporateDetailsService corporateDetailsService;
	@Autowired
	private ClaimAnalysisReport claimAnalysisReport;
	@Autowired
	private EmpDependentRepository dependentRepository;
	@Autowired
	private PolicyTermsRepository policyTermsRepository;
	@Autowired
	private ClaimsMisRepository claimsMisRepository;
	@Autowired
	private ClaimsDetailsRepository claimsDetailsRepository;

	@Override
	public void downloadPdfAsZip(HttpServletResponse response, String rfqId) {
		Map<byte[], String> pdfFiles = new HashMap();
		Map<byte[], String> pdfFilesXls = new HashMap<>();

		Optional<CoverageDetailsEntity> coverageDetails = coverageDetailsRepository.findByRfqId(rfqId);
		Optional<CorporateDetailsEntity> corporateDetailsEntity = corporateDetailsRepo.findByRfqId(rfqId);
		List<EmployeeDepedentDetailsEntity> dependentRepositoryByrfqId = dependentRepository.findByrfqId(rfqId);
		Optional<List<PolicyTermsEntity>> policyTermsRepositoryByRfqId = policyTermsRepository.findByRfqId(rfqId);
		List<ClaimsMisEntity> claimsMisRepositoryByRfqId = claimsMisRepository.findByRfqId(rfqId);
		CoverageDetailsEntity coverageDetails1 = cdEBRepo.findByRfqId(rfqId).orElse(null);
		List<String> employeeRelations = employeeRepository.findAllRelationShipByRfqId(rfqId).orElse(null);
		CorporateDetailsEntity corporateDetailsEntity2 = corporateDetailsRepo.findByRfqId(rfqId).orElse(null);

		if (!corporateDetailsEntity.isEmpty()) {
			Optional<ExpiryPolicyDetails> findByrfqId = expiryPolicyDetailsRepository.findByrfqId(rfqId);
			ExpiryPolicyDetails expiryDetails = findByrfqId.orElse(null);
			ClaimsDetails byrfqId = claimsDetailsRepository.findByrfqId(rfqId).orElse(null);

			try {
				if (coverageDetails1.getRfqId() != null
//						&& dependentRepositoryByrfqId.contains(rfqId)
						&& coverageDetails1.getEmpDepDataFilePath() != null
						&& !coverageDetails1.getEmpDepDataFilePath().trim().isEmpty()) {
					byte[] ageBindingReportPdf = ageBindingReportPdfGenerator
							.generatePdf(modelMapper.map(coverageDetails.orElse(null), CoverageDetailsDto.class));
					if (ageBindingReportPdf.length > 0) {
						log.info("Age Binding Report length form download Zip :{}", ageBindingReportPdf.length);
						pdfFiles.put(ageBindingReportPdf, "ageBindingReport.pdf");
					}
				}
				byte[] coverageDetailsPdf = coverageDetailsPdfGenerator.generateCoverageDetails(rfqId);
				if (coverageDetailsPdf.length > 0) {
					log.info("Coverage Details Report length form download Zip :{}", coverageDetailsPdf.length);
					pdfFiles.put(coverageDetailsPdf, "coverageDetailsSummary.pdf");
				}

				if (corporateDetailsEntity2.getRfqId() != null && coverageDetails1.getRfqId() != null
						&& !employeeRelations.isEmpty()) {
					byte[] irdaPdf = irdaPdfGenerator.generateEmployeeDataReport(corporateDetailsEntity.get(),
							expiryDetails, byrfqId, coverageDetails1, employeeRelations);
					if (irdaPdf.length > 0) {
						log.info("IRDA Report length form download Zip :{}", irdaPdf.length);
						pdfFiles.put(irdaPdf, "irdaReport.pdf");
					}
				}

				if (coverageDetails1.getRfqId() != null && coverageDetails1.getMandateLetterFilePath() != null
						&& !coverageDetails1.getMandateLetterFilePath().trim().isEmpty()) {
					byte[] mandateLetter2 = coverageDetailsServiceImpl.downloadMandateLetter(rfqId);
					if (mandateLetter2.length > 0) {
						log.info("Mandate Letter length form download Zip :{}", mandateLetter2.length);
						pdfFiles.put(mandateLetter2, "mandateLetter.pdf");
					}
				}
				if (coverageDetails1.getRfqId() != null && coverageDetails1.getClaimsMiscFilePath() != null
						&& !coverageDetails1.getClaimsMiscFilePath().trim().isEmpty()) {
					byte[] downloadClaimMISC = coverageDetailsServiceImpl.downloadClaimMISC(rfqId);
					if (downloadClaimMISC.length > 0) {
						log.info("Claim MISC Report length form download Zip :{}", downloadClaimMISC.length);
						pdfFilesXls.put(downloadClaimMISC, "claimMISC.xlsx");
					}
				}
				if (coverageDetails1.getRfqId() != null && coverageDetails1.getClaimsMiscFilePath() != null
						&& !coverageDetails1.getClaimsMiscFilePath().trim().isEmpty()) {
					byte[] clamisAnalysis = claimAnalysisReport.generatePdf(rfqId);
					if (clamisAnalysis.length > 0) {
						log.info("Claim Analysis Report length form download Zip :{}", clamisAnalysis.length);
						pdfFiles.put(clamisAnalysis, "clamisAnalysis.pdf");
					}
				}
				if (coverageDetails1.getRfqId() != null && coverageDetails.get().getEmpDepDataFilePath() != null
						&& !coverageDetails1.getEmpDepDataFilePath().trim().isEmpty()) {
					log.info("Employee Dependent Path :{}", coverageDetails.get().getEmpDepDataFilePath());
					byte[] employeeDataXls = corporateDetailsService.getEmployeeData(rfqId);
					if (employeeDataXls.length > 0) {
						log.info("Employee Dependent Report length form download Zip :{}", employeeDataXls.length);
						pdfFilesXls.put(employeeDataXls, "employeeData.xlsx");
					}
				}

				log.info("Total No.of Files attached as Zip :{}", pdfFilesXls.size());
			} catch (IOException | DocumentException e) {
				log.error("Error generating PDF files", e);
			}

			log.info("Before Zip");
			if (!pdfFiles.isEmpty() || !pdfFilesXls.isEmpty()) {
				try {
					File zipFile = File.createTempFile("specifiedPdfFiles", ".zip");
					try (FileOutputStream fos = new FileOutputStream(zipFile);
							ZipOutputStream zos = new ZipOutputStream(fos)) {

						for (Map.Entry<byte[], String> entry : pdfFiles.entrySet()) {
							addBytesToZip(entry.getKey(), entry.getValue(), zos);
						}
						log.info("PDF Size is :{}", pdfFiles.size());
						for (Map.Entry<byte[], String> map : pdfFilesXls.entrySet()) {
							addBytesToZipXls(map.getKey(), map.getValue(), zos);
						}

						log.info("Excel Size is :{}", pdfFiles.size());
					}

					try (FileInputStream fis = new FileInputStream(zipFile);
							OutputStream os = response.getOutputStream()) {
						response.setHeader("Content-Disposition", "attachment; filename=downloadReports.zip");
						response.setContentType("application/zip");
						IOUtils.copy(fis, os);
						os.flush();
					}

					zipFile.delete();
				} catch (IOException e) {
					log.error("Error creating or writing to the zip file", e);
				}
			}
		}
	}

	private static void addBytesToZip(byte[] data, String entryName, ZipOutputStream zos) throws IOException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
			ZipEntry zipEntry = new ZipEntry(entryName);
			zos.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = bis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}

			zos.closeEntry();
		}
	}

	private static void addBytesToZipXls(byte[] data, String entryName, ZipOutputStream zos) throws IOException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
			ZipEntry zipEntry = new ZipEntry(entryName);
			zos.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = bis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}

			zos.closeEntry();
		}
	}
}