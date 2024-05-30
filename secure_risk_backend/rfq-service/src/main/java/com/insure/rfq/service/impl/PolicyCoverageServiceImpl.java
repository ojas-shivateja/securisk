package com.insure.rfq.service.impl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.PolicyCoverageDto;
import com.insure.rfq.entity.PolicyCoverageEntity;
import com.insure.rfq.entity.Product;
import com.insure.rfq.repository.PolicyCoverageRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.PolicyCoverageService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PolicyCoverageServiceImpl implements PolicyCoverageService {

	@Autowired
	private PolicyCoverageRepository policyCoverageRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ModelMapper mapper;

	@Override
	@Transactional
	public PolicyCoverageDto createCoverageByProductId(Long productId, PolicyCoverageDto policyCoverageDto) {
		Optional<Product> optionalProduct = productRepository.findById(productId);
		if (optionalProduct.isPresent()) {
			Product product = optionalProduct.get();

			PolicyCoverageEntity coverageEntity = mapper.map(policyCoverageDto, PolicyCoverageEntity.class);
			coverageEntity.setProduct(product);
			coverageEntity.setStatus("ACTIVE");
			policyCoverageRepository.save(coverageEntity);

			return mapper.map(coverageEntity, PolicyCoverageDto.class);
		}
		return null;
	}

	@Override
	public List<PolicyCoverageDto> getCoveragesByProductId(Long productId) {
		return policyCoverageRepository.getCoveragesByProductId(productId).stream()
				.filter(coverage -> coverage.getStatus().equalsIgnoreCase("ACTIVE")).map(coverage -> {
					PolicyCoverageDto coverageDto = new PolicyCoverageDto();
					coverageDto.setCoverageId(coverage.getId());
					coverageDto.setCoverage(coverage.getCoverage());
					return coverageDto;
				}).toList();
	}

	@Override
	public PolicyCoverageDto updateCoveragesByProductId(Long policyCoverageId, PolicyCoverageDto policyCoverageDto) {
		Optional<PolicyCoverageEntity> policyCoverage = policyCoverageRepository.findById(policyCoverageId);
		log.info("Coverage From Update Coverage", policyCoverage);
		policyCoverage.get().setCoverage(policyCoverageDto.getCoverage());
		policyCoverageRepository.save(policyCoverage.get());
		return mapper.map(policyCoverage, PolicyCoverageDto.class);

	}

	@Override
	public String deleteCoveragesByProductId(Long policyCoverageId) {
		Optional<PolicyCoverageEntity> coverage = policyCoverageRepository.findById(policyCoverageId);
		log.info("Coverage From Delete Coverage", coverage);
		coverage.get().setStatus("INACTIVE");
		policyCoverageRepository.save(coverage.get());
		return "Deleted Successfully";
	}

}
