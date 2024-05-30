package com.insure.rfq.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.CoverageDetailsChildValidateValuesDto;
import com.insure.rfq.dto.CoverageValidateFilenamesDto;
import com.insure.rfq.entity.CoverageValidateFilenames;

public interface CoverageValidateFilenamesService {
	List<CoverageValidateFilenames> getCoverageValidateFilenames();
	String addCoverageValidateFilenames(CoverageValidateFilenamesDto dto);
	List<CoverageDetailsChildValidateValuesDto> validateFileValues(MultipartFile file);
}
