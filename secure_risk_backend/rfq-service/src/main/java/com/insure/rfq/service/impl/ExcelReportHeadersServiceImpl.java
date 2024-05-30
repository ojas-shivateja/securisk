package com.insure.rfq.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ExcelReportHeadersDto;
import com.insure.rfq.entity.ExcelReportHeaders;
import com.insure.rfq.entity.ExcelReportHeadersMapping;
import com.insure.rfq.repository.ExcelReportHeadersMappingRepository;
import com.insure.rfq.repository.ExcelReportHeadersRepository;
import com.insure.rfq.service.ExcelReportHeadersService;

@Service
public class ExcelReportHeadersServiceImpl implements ExcelReportHeadersService {

	@Autowired
	private ExcelReportHeadersRepository headersRepo;
	@Autowired
	private ExcelReportHeadersMappingRepository headerMappingRepo;
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public ExcelReportHeadersDto createHeaders(ExcelReportHeadersDto header) {


		ExcelReportHeaders findByHeaderNameAndHeaderCategory = headersRepo
				.findByHeaderNameAndHeaderCategory(header.getHeaderName(), header.getHeaderCategory());
		System.out.println("findByHeaderNameAndHeaderCategory :: " + findByHeaderNameAndHeaderCategory);
		String active = "ACTIVE";
		if (findByHeaderNameAndHeaderCategory == null) {
			ExcelReportHeaders excelHeaders = new ExcelReportHeaders();
			excelHeaders.setHeaderName(header.getHeaderName());
			excelHeaders.setHeaderCategory(header.getHeaderCategory());
			excelHeaders.setStatus(active);
			excelHeaders.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			headersRepo.save(excelHeaders);

			ExcelReportHeadersMapping headerMapping = new ExcelReportHeadersMapping();
			headerMapping.setAliasName(header.getHeaderAliasname().getAliasName());
			headerMapping.setReportHeaders(excelHeaders);
			headerMapping.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			headerMapping.setStatus(active);
			headerMappingRepo.save(headerMapping);

			header.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			header.setStatus(active);
			header.getHeaderAliasname().setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			header.getHeaderAliasname().setStatus(active);
			return header;
		} else {
			ExcelReportHeaders findByHeaderName = headersRepo.findByHeaderName(header.getHeaderName());

			ExcelReportHeadersMapping excelReportHeadersMapping = new ExcelReportHeadersMapping();
			excelReportHeadersMapping.setAliasName(header.getHeaderAliasname().getAliasName());
			excelReportHeadersMapping.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			excelReportHeadersMapping.setStatus(active);
			excelReportHeadersMapping.setReportHeaders(findByHeaderName);
			ExcelReportHeadersMapping validateHeaderMapping = headerMappingRepo.save(excelReportHeadersMapping);


			header.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			header.setStatus(active);
			header.getHeaderAliasname().setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			header.getHeaderAliasname().setStatus(active);
			return header;
		}
	}

	@Override
	public List<ExcelReportHeaders> viewAllHeaders() {
		return headersRepo.findAll();
	}

}
