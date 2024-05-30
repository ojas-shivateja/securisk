package com.insure.rfq.service.impl;

import com.insure.rfq.dto.ExpiryDetailsDto;
import com.insure.rfq.entity.ExpiryPolicyDetails;
import com.insure.rfq.exception.InvalidExpiryDetails;
import com.insure.rfq.repository.ExpiryPolicyDetailsRepository;
import com.insure.rfq.service.ExpiryDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class ExpiryServiceImpl implements ExpiryDetailsService {

	@Autowired
	private ExpiryPolicyDetailsRepository expiryDetailsRepo;

	@Override
	public String createExpiryDetails(ExpiryDetailsDto expiryDetailsDto) {
		// Check if there's an existing record with the same RFQ ID
		Optional<ExpiryPolicyDetails> findByrfqId = expiryDetailsRepo.findByrfqId(expiryDetailsDto.getRfqId());

		// If an existing record is found, delete it
		findByrfqId.ifPresent(expiryDetailsRepo::delete);
		if (expiryDetailsDto != null) {
			ExpiryPolicyDetails expiryPolicyDetails = new ExpiryPolicyDetails();
			expiryPolicyDetails.setRfqId(expiryDetailsDto.getRfqId());
			expiryPolicyDetails.setActiveYears(expiryDetailsDto.getActiveYears());
			expiryPolicyDetails.setAdditionalRelationShip(expiryDetailsDto.getAdditionalRelationShip());
			expiryPolicyDetails.setAdditionPremium(expiryDetailsDto.getAdditionPremium());
			expiryPolicyDetails.setAdditions(expiryDetailsDto.getAdditions());
			expiryPolicyDetails.setDeletionPremium(expiryDetailsDto.getDeletionPremium());
			expiryPolicyDetails.setDeletions(expiryDetailsDto.getDeletions());
			expiryPolicyDetails.setDependentMember(expiryDetailsDto.getDependentMember());
			expiryPolicyDetails.setMembersNoInceptionForDependents(expiryDetailsDto.getMembersNoInceptionForDependents());
			expiryPolicyDetails.setAdditionsForDependents(expiryDetailsDto.getAdditionsForDependents());
			expiryPolicyDetails.setDeletionsForDependents(expiryDetailsDto.getDeletionsForDependents());
			expiryPolicyDetails.setTotalMembersForDependents(expiryDetailsDto.getTotalMembersForDependents());
			expiryPolicyDetails.setEndPeriod(expiryDetailsDto.getEndPeriod());
			expiryPolicyDetails.setFamiliesNum(expiryDetailsDto.getFamiliesNum());
			expiryPolicyDetails.setFamilyDefination(expiryDetailsDto.getFamilyDefination());
			expiryPolicyDetails.setFamilyDefinationRevision(expiryDetailsDto.getFamilyDefinationRevision());
			expiryPolicyDetails.setMembersNoInception(expiryDetailsDto.getMembersNoInception());
			expiryPolicyDetails.setMembersNum(expiryDetailsDto.getMembersNum());
			expiryPolicyDetails.setPolicyNumber(expiryDetailsDto.getPolicyNumber());
			expiryPolicyDetails.setPolicyType(expiryDetailsDto.getPolicyType());
			expiryPolicyDetails.setPremium(expiryDetailsDto.getPremium());
			expiryPolicyDetails.setPremiumPaidInception(expiryDetailsDto.getPremiumPaidInception());
			expiryPolicyDetails.setStartPeriod(expiryDetailsDto.getStartPeriod());
			expiryPolicyDetails.setTotalMembers(expiryDetailsDto.getTotalMembers());

			expiryPolicyDetails.setCreateDate(new SimpleDateFormat().format(new Date()));
			expiryPolicyDetails.setRecordStatus("ACTIVE");
			expiryDetailsRepo.save(expiryPolicyDetails);
			return expiryPolicyDetails.getRfqId();
		}
		return null;

	}

	@Override
	public ExpiryPolicyDetails updateExpiryDetails(ExpiryDetailsDto expiryDetailsDto, String id) {
		ExpiryPolicyDetails expiryPolicyDetails = expiryDetailsRepo.findByrfqId(id)
				.orElseThrow(() -> new InvalidExpiryDetails("invalid expiration  id "));
		expiryPolicyDetails.setActiveYears(expiryDetailsDto.getActiveYears());
		expiryPolicyDetails.setAdditionalRelationShip(expiryDetailsDto.getAdditionalRelationShip());
		expiryPolicyDetails.setAdditionPremium(expiryDetailsDto.getAdditionPremium());
		expiryPolicyDetails.setAdditions(expiryDetailsDto.getAdditions());
		expiryPolicyDetails.setDeletionPremium(expiryDetailsDto.getDeletionPremium());
		expiryPolicyDetails.setDeletions(expiryDetailsDto.getDeletions());
		expiryPolicyDetails.setDependentMember(expiryDetailsDto.getDependentMember());
		expiryPolicyDetails.setMembersNoInceptionForDependents(expiryDetailsDto.getMembersNoInceptionForDependents());
		expiryPolicyDetails.setAdditionsForDependents(expiryDetailsDto.getAdditionsForDependents());
		expiryPolicyDetails.setDeletionsForDependents(expiryDetailsDto.getDeletionsForDependents());
		expiryPolicyDetails.setTotalMembersForDependents(expiryDetailsDto.getTotalMembersForDependents());
		expiryPolicyDetails.setEndPeriod(expiryDetailsDto.getEndPeriod());
		expiryPolicyDetails.setFamiliesNum(expiryDetailsDto.getFamiliesNum());
		expiryPolicyDetails.setFamilyDefination(expiryDetailsDto.getFamilyDefination());
		expiryPolicyDetails.setFamilyDefinationRevision(expiryDetailsDto.getFamilyDefinationRevision());
		expiryPolicyDetails.setMembersNoInception(expiryDetailsDto.getMembersNoInception());
		expiryPolicyDetails.setMembersNum(expiryDetailsDto.getMembersNum());
		expiryPolicyDetails.setPolicyNumber(expiryDetailsDto.getPolicyNumber());
		expiryPolicyDetails.setPolicyType(expiryDetailsDto.getPolicyType());
		expiryPolicyDetails.setPremium(expiryDetailsDto.getPremium());
		expiryPolicyDetails.setPremiumPaidInception(expiryDetailsDto.getPremiumPaidInception());
		expiryPolicyDetails.setStartPeriod(expiryDetailsDto.getStartPeriod());
		expiryPolicyDetails.setTotalMembers(expiryDetailsDto.getTotalMembers());
		expiryPolicyDetails.setCreateDate(expiryPolicyDetails.getCreateDate());
		expiryPolicyDetails.setRecordStatus(expiryPolicyDetails.getRecordStatus());
		expiryPolicyDetails.setUpdateDate(new SimpleDateFormat().format(new Date()));

		return expiryDetailsRepo.save(expiryPolicyDetails);
	}

	@Override
	public ExpiryPolicyDetails getExpiryDetailsRfqById(String rfqId) {
		Optional<ExpiryPolicyDetails> findByrfqId = expiryDetailsRepo.findByrfqId(rfqId);
		return findByrfqId.get();
	}

}
