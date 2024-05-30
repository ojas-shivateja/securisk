package com.insure.rfq.service.impl;

import com.insure.rfq.dto.PolicyTermsChildDto;
import com.insure.rfq.dto.PolicyTermsDto;
import com.insure.rfq.entity.PolicyTermsEntity;
import com.insure.rfq.exception.InvalidUser;
import com.insure.rfq.repository.PolicyTermsRepository;
import com.insure.rfq.service.PolicyTermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PolicyTermsServiceImpl implements PolicyTermsService {

	@Autowired
	private PolicyTermsRepository policyTermsRepo;

	@Override
	public List<PolicyTermsEntity> createPolicyTerms(PolicyTermsDto details) {
		// Find existing policy terms entities by RFQ ID
		Optional<List<PolicyTermsEntity>> findByRfqId = policyTermsRepo.findByRfqId(details.getRfqId());

		// If existing entities are found, delete them
		findByRfqId.ifPresent(policyTermsEntities -> {
			for (PolicyTermsEntity entity : policyTermsEntities) {
				policyTermsRepo.delete(entity);
			}
		});

		List<PolicyTermsEntity> policyEntites = new ArrayList<>();
		PolicyTermsEntity entity = null;
		if (details != null) {
			for (PolicyTermsChildDto childDto : details.getPolicyDetails()) {
				entity = new PolicyTermsEntity();
				entity.setRfqId(details.getRfqId());
				entity.setCoverageName(childDto.getCoverageName());
				entity.setRemark(childDto.getRemark());
				entity.setCreateDate(new Date());
				entity.setRecordStatus("ACTIVE");
				policyTermsRepo.save(entity);
				policyEntites.add(entity);
			}
			return policyEntites;
		}
		return policyEntites;

	}

	@Override
	public List<PolicyTermsEntity> updatePolicyTerms(PolicyTermsDto details) {
		PolicyTermsEntity policyTermsEntity = null;
		if (details != null) {
			for (PolicyTermsChildDto policyTermsChildDto : details.getPolicyDetails()) {
				Optional<PolicyTermsEntity> findById = policyTermsRepo.findById(policyTermsChildDto.getPolicyTermId());
				if (findById.isPresent()) {
					policyTermsEntity = findById.get();
					policyTermsEntity.setCoverageName(policyTermsChildDto.getCoverageName());
					policyTermsEntity.setCreateDate(policyTermsEntity.getCreateDate());
					policyTermsEntity.setRecordStatus(policyTermsEntity.getRecordStatus());
					policyTermsEntity.setRemark(policyTermsChildDto.getRemark());
					policyTermsEntity.setUpdateDate(new Date());
					policyTermsRepo.save(policyTermsEntity);
				}
			}
			Optional<List<PolicyTermsEntity>> findByRfqId = policyTermsRepo.findByRfqId(details.getRfqId());
			if (!findByRfqId.get().isEmpty()) {
				return findByRfqId.get();
			}
		}
		return null;
	}

	@Override
	public List<PolicyTermsEntity> getPolicyTermsByRfqId(String rfqId) {
		List<PolicyTermsEntity> list = policyTermsRepo.getPolicyTermsByRfqId(rfqId).get();
		return list.stream().filter(policy -> policy.getRecordStatus().equalsIgnoreCase("ACTIVE")).toList();
	}

	@Override
	public String deletePolicyTermsByRfqId(String rfqId) {
		List<PolicyTermsEntity> list = policyTermsRepo.findByRfqId(rfqId)
				.orElseThrow(() -> new InvalidUser("Policy terms with Id is not found"));
		for (PolicyTermsEntity policyTermsEntity : list) {
			policyTermsEntity.setRecordStatus("INACTIVE");
			policyTermsRepo.save(policyTermsEntity);
		}
		return "Deleted Successfully";
	}

}
