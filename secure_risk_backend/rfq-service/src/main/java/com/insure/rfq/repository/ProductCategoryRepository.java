package com.insure.rfq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
	
	
	ProductCategory findBycategoryId(Long categoryId);
	
	ProductCategory findByCategoryName(String categoryName);
}