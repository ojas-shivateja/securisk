package com.insure.rfq.service;

import com.insure.rfq.dto.ExpiryDetailsDto;
import com.insure.rfq.entity.ExpiryPolicyDetails;

public interface ExpiryDetailsService {
	String createExpiryDetails(ExpiryDetailsDto expiryDetailsDto);

	ExpiryPolicyDetails updateExpiryDetails(ExpiryDetailsDto expiryDetailsDto, String id);

	ExpiryPolicyDetails getExpiryDetailsRfqById(String rfqId);
}