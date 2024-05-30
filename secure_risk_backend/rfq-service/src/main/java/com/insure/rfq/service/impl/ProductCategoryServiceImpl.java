package com.insure.rfq.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ProductCategoryChildDto;
import com.insure.rfq.entity.ProductCategory;
import com.insure.rfq.repository.ProductCategoryRepository;
import com.insure.rfq.service.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	@Autowired
	private ProductCategoryRepository productCategoryRepository;
	
	@Override
	public List<ProductCategoryChildDto> findCategory() {
		
		List<ProductCategory> prodCategories = productCategoryRepository.findAll();
		
		List<ProductCategoryChildDto> allCategories = new ArrayList<>();
		for (ProductCategory prodCategory : prodCategories) {
			ProductCategoryChildDto category = new ProductCategoryChildDto();
			category.setCategoryId(prodCategory.getCategoryId());
			category.setCategoryName(prodCategory.getCategoryName());
			allCategories.add(category);
		}
		return allCategories;
	}

}