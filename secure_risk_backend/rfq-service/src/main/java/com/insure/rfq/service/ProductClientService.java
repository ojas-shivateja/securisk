package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.ProductClientDto;

public interface ProductClientService {
	List<ProductClientDto> getProductsBasedonClientId(Long clientId);

}
