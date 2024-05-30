package com.insure.rfq.service;

import com.insure.rfq.dto.PolicyTermsDto;
import com.insure.rfq.entity.PolicyTermsEntity;

import java.util.List;

public interface PolicyTermsService {

	List<PolicyTermsEntity> createPolicyTerms(PolicyTermsDto details);

	List<PolicyTermsEntity> updatePolicyTerms(PolicyTermsDto details);

	List<PolicyTermsEntity> getPolicyTermsByRfqId(String rfqId);

	String deletePolicyTermsByRfqId(String rfqId);
}
