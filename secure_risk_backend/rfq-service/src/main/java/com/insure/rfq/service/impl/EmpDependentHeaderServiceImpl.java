package com.insure.rfq.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.EmpDependentHeaderDto;
import com.insure.rfq.entity.EmpDependentHeaderMapping;
import com.insure.rfq.entity.EmpDependentHeaders;
import com.insure.rfq.repository.EmpDependentHeaderMappingRepository;
import com.insure.rfq.repository.EmpDependentHeaderRepository;
import com.insure.rfq.service.EmpDependentHeaderService;

@Service
public class EmpDependentHeaderServiceImpl implements EmpDependentHeaderService {

	@Autowired
	private EmpDependentHeaderRepository headersRepo;
	@Autowired
	private EmpDependentHeaderMappingRepository headerMappingRepo;
	
	private static final Logger LOG = LoggerFactory.getLogger(EmpDependentHeaderServiceImpl.class);
	
	@Override
	public EmpDependentHeaderDto createHeaders(EmpDependentHeaderDto header) {
		
		EmpDependentHeaders findByHeaderNameAndHeaderCategory = headersRepo
				.findByHeaderNameAndHeaderCategory(header.getHeaderName(), header.getHeaderCategory());
		LOG.info("findByHeaderNameAndHeaderCategory :: " + findByHeaderNameAndHeaderCategory);
		if (findByHeaderNameAndHeaderCategory == null) {
			EmpDependentHeaders empHeaders = new EmpDependentHeaders();
			empHeaders.setHeaderName(header.getHeaderName());
			empHeaders.setHeaderCategory(header.getHeaderCategory());
			empHeaders.setStatus("ACTIVE");
			empHeaders.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			headersRepo.save(empHeaders);

			EmpDependentHeaderMapping headerMapping = new EmpDependentHeaderMapping();
			headerMapping.setAliasName(header.getHeaderAliasname().getAliasName());
			headerMapping.setReportHeaders(empHeaders);
			headerMapping.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			headerMapping.setStatus("ACTIVE");
			headerMappingRepo.save(headerMapping);

			header.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			header.setStatus("ACTIVE");
			header.getHeaderAliasname().setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			header.getHeaderAliasname().setStatus("ACTIVE");
			return header;
		} else {
			EmpDependentHeaders findByHeaderName = headersRepo.findByHeaderName(header.getHeaderName());
			
			LOG.info("findByHeaderName :: "+findByHeaderName);
			EmpDependentHeaderMapping empHeadersMapping = new EmpDependentHeaderMapping();
			empHeadersMapping.setAliasName(header.getHeaderAliasname().getAliasName());
			empHeadersMapping.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			empHeadersMapping.setStatus("ACTIVE");
			empHeadersMapping.setReportHeaders(findByHeaderName);
			headerMappingRepo.save(empHeadersMapping);

			header.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			header.setStatus("ACTIVE");
			header.getHeaderAliasname().setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			header.getHeaderAliasname().setStatus("ACTIVE");
			return header;
		}
		
	}

	@Override
	public List<EmpDependentHeaders> viewAllHeaders() {
		return headersRepo.findAll();
	}

	@Override
	public List<String> getAllEmpDependentHeaders() {
		List<String> empDepHeaders = new ArrayList<>();
		List<EmpDependentHeaders> findAll = headersRepo.findAll();
		if(!findAll.isEmpty()) {
			findAll.stream().forEach(i -> empDepHeaders.add(i.getHeaderName()));
		}
		return empDepHeaders;
	}

}
