package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.ProductCategoryChildDto;

public interface ProductCategoryService {
	List<ProductCategoryChildDto> findCategory();
}