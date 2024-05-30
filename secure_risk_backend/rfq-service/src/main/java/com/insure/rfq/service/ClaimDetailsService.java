package com.insure.rfq.service;

import com.insure.rfq.dto.ClaimDetailsDto;
import com.insure.rfq.entity.ClaimsDetails;

public interface ClaimDetailsService {
	String createClaimDetails(ClaimDetailsDto claimDetailsDto);
	ClaimsDetails updateClaimDetails(ClaimDetailsDto claimDetailsDto,String id);
}
