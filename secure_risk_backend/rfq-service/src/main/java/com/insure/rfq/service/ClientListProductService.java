package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.ClientListProductDto;

public interface ClientListProductService {

	ClientListProductDto createProduct(ClientListProductDto clientListProductDto,Long id);
	List<ClientListProductDto> getAllProducts(Long id);
}
