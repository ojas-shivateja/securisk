package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.AddProductClientDto;
import com.insure.rfq.dto.GetAllProductsByClientListIdDto;
import com.insure.rfq.dto.GetProductDropdownDto;
import com.insure.rfq.dto.ProductChildDto;
import com.insure.rfq.dto.ProductCreateDto;
import com.insure.rfq.dto.ProductDto;
import com.insure.rfq.dto.ProductIntermediaryDto;

public interface ProductService {
	ProductChildDto findProducts(Long productId);

	List<ProductChildDto> getProductsByCategory(Long ProductcategoryId);

	ProductCreateDto addProduct(ProductCreateDto product);

	AddProductClientDto createProduct(AddProductClientDto clientListProductDto, Long clientListid);

	List<ProductDto> getAllProducts(Long id);

	ProductIntermediaryDto updatePoductById(Long productId, ProductIntermediaryDto productDto);

	String deletePoductById(Long productId);

	List<GetProductDropdownDto> getAllProductsWithId();

	List<GetAllProductsByClientListIdDto> getAllProductsByClientListId(Long clientListId);

	AddProductClientDto updateClientListProduct(Long productId, Long clientListId,AddProductClientDto addProductClientDto);



	String deleteProductRelationForClientList(Long productId,Long clientListId);
}