package com.insure.rfq.service;

import java.io.IOException;
import java.util.List;

import com.insure.rfq.dto.*;
import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.CoverageDetailsChildValidateValuesDto;
import com.insure.rfq.dto.CoverageDetailsDto;
import com.insure.rfq.dto.CoverageRemarksUploadDto;
import com.insure.rfq.dto.CoverageUploadDto;
import com.insure.rfq.dto.DownloadTemplateAttachementDto;
import com.insure.rfq.dto.EmpDepdentValidationDto;
import com.insure.rfq.entity.CoverageDetailsEntity;
import com.insure.rfq.entity.EmployeeDepedentDetailsEntity;
import com.insure.rfq.payload.DataToEmail;
import com.itextpdf.text.DocumentException;

public interface CoverageDetailsService {

	String createCoverageDetails(CoverageDetailsDto details);

	CoverageDetailsEntity updateCoverageDetails(String rfqId, CoverageDetailsDto coverageDetailsDto);

	public byte[] getEmployeeData();

	public byte[] getIrdaData() throws IOException;

	String saveEmployeesFromExcel(CoverageUploadDto coverageUploadDto);

	List<EmployeeDepedentDetailsEntity> getAllEmployeeDepedentDataByRfqId(String rfqId);

	String uploadFileCoverage(CoverageUploadDto coverageUploadDto);

	EmpDepdentValidationDto validateUploadedFileNames(MultipartFile file);

	byte[] downloadMandateLetter(String rfqId) throws IOException;

	String sendEmailAlongPreparedAttachment(DataToEmail dataToEmail) throws IOException, DocumentException;

	CoverageDetailsEntity getCoverageByRfqId(String rfqId);

	DownloadTemplateAttachementDto sendEmailAlongWithDownloadTEmplate(
			DownloadTemplateAttachementDto downloadTemplateAttachementDto);

	byte[] downloadClaimMISC(String rfqId) throws IOException;

	List<CoverageRemarksUploadDto> getAllRemarks(
			List<CoverageDetailsChildValidateValuesDto> coverageDetailsChildValidateValuesDtos);

}
