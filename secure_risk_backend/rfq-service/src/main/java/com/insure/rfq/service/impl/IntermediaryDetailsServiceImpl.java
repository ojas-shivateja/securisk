package com.insure.rfq.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.IntermediaryDetailsDto;
import com.insure.rfq.dto.ProductCategoryChildDto;
import com.insure.rfq.dto.ProductChildDto;
import com.insure.rfq.entity.Product;
import com.insure.rfq.entity.ProductCategory;
import com.insure.rfq.repository.PolicyCoverageRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.IntermediaryDetailsService;
import com.insure.rfq.service.ProductCategoryService;
import com.insure.rfq.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IntermediaryDetailsServiceImpl implements IntermediaryDetailsService {

	@Autowired
	private ProductCategoryService service;

	@Autowired
	private ProductService prodService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PolicyCoverageRepository policyCoverageRepository;

	@Override
	public List<IntermediaryDetailsDto> getAllIntermediaryDetails() {
		List<Product> products = productRepository.findAll();

		List<IntermediaryDetailsDto> intermediaryDetailsList = new ArrayList<>();

		for (Product product : products) {
			if (product != null && product.getStatus() != null && product.getStatus().equalsIgnoreCase("ACTIVE")) {
				IntermediaryDetailsDto intermediaryDetailsDto = new IntermediaryDetailsDto();
				intermediaryDetailsDto.setProduct(product.getProductName());

				ProductCategory productCategory = product.getProductcategory();
				if (productCategory != null) {
					intermediaryDetailsDto.setProductCategory(productCategory.getCategoryName());
				} else {
					intermediaryDetailsDto.setProductCategory("null"); // or any default value you prefer
				}
				intermediaryDetailsDto.setProductId(product.getProductId());
				int coverageCount = policyCoverageRepository.getCoverageCount(product.getProductId());
				System.out.print("Coverage Count From Get All Products" + coverageCount);
				intermediaryDetailsDto.setCoverageCount(coverageCount);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				intermediaryDetailsDto.setCreatedDate(
						product.getCreatedDate() != null ? dateFormat.format(product.getCreatedDate()) : null);
				intermediaryDetailsDto.setUpdateDate(
						product.getUpdatedDate() != null ? dateFormat.format(product.getUpdatedDate()) : null);
				intermediaryDetailsDto.setRecordStatus(product.getStatus());
				intermediaryDetailsList.add(intermediaryDetailsDto);
			}
		}

		return intermediaryDetailsList;
	}

	public List<ProductCategoryChildDto> getAllProductcategories() {
		return service.findCategory();
	}

	public List<ProductChildDto> getProductcategoryById(Long id) {
		return prodService.getProductsByCategory(id);
	}

}
