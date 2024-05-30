package com.insure.rfq.service;

import java.io.IOException;
import java.util.List;

import com.insure.rfq.dto.GetClientListPolicyDto;
import com.insure.rfq.dto.PolicyDto;

public interface PolicyService {

	 String createPolicyData(PolicyDto policyDto ,Long clientID ,Long produtId) ;

	 List<GetClientListPolicyDto> getAllPolicyEntities();
	 byte[] downloadpolicyCopyPath(Long clientId, Long productId) throws IOException;
	 GetClientListPolicyDto getByProductAndClientId(Long clientId, Long productId);
	 byte[] downloadpptpath(Long clientId, Long productId) throws IOException;
}