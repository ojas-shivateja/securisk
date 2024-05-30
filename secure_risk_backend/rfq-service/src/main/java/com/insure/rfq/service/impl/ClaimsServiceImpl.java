package com.insure.rfq.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ClaimDetailsDto;
import com.insure.rfq.entity.ClaimsDetails;
import com.insure.rfq.exception.InvalidUser;
import com.insure.rfq.repository.ClaimsDetailsRepository;
import com.insure.rfq.service.ClaimDetailsService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClaimsServiceImpl implements ClaimDetailsService {

	@Autowired
	private ClaimsDetailsRepository claimsRepo;

	@Override
	public String createClaimDetails(ClaimDetailsDto claimDetailsDto) {
		// Check if there's an existing record with the same RFQ ID
		Optional<ClaimsDetails> findByrfqId = claimsRepo.findByrfqId(claimDetailsDto.getRfqId());

		// If an existing record is found, delete it
		findByrfqId.ifPresent(claimsRepo::delete);
		if (claimDetailsDto != null) {
			ClaimsDetails claimsDetails = new ClaimsDetails();
			claimsDetails.setRfqId(claimDetailsDto.getRfqId());
			claimsDetails.setClaimOutstandingCashless(claimDetailsDto.getClaimOutstandingCashless());
			claimsDetails.setClaimOutstandingReimbursement(claimDetailsDto.getClaimOutstandingReimbursement());
			claimsDetails.setClaimPaidCashless(claimDetailsDto.getClaimPaidCashless());
			claimsDetails.setClaimPaidReimbursement(claimDetailsDto.getClaimPaidReimbursement());
			claimsDetails.setCorporateBufferAmount(claimDetailsDto.getCorporateBufferAmount());
			claimsDetails.setCorporateBufferClaimOutstandingCashless(
					claimDetailsDto.getCorporateBufferClaimOutstandingCashless());
			claimsDetails.setCorporateBufferClaimOutstandingReimbursement(
					claimDetailsDto.getCorporateBufferClaimOutstandingReimbursement());
			claimsDetails.setCorporateBufferClaimPaidCashless(claimDetailsDto.getCorporateBufferClaimPaidCashless());
			claimsDetails.setCorporateBufferClaimPaidReimbursement(
					claimDetailsDto.getCorporateBufferClaimPaidReimbursement());
			claimsDetails.setMaxCasesNo(claimDetailsDto.getMaxCasesNo());
			claimsDetails.setMaxSumInsured(claimDetailsDto.getMaxSumInsured());
			claimsDetails.setOpdClaimOutstandingCashless(claimDetailsDto.getOpdClaimOutstandingCashless());
			claimsDetails.setOpdClaimOutstandingReimbursement(claimDetailsDto.getOpdClaimOutstandingReimbursement());
			claimsDetails.setOpdClaimPaidCashless(claimDetailsDto.getOpdClaimPaidCashless());
			claimsDetails.setOpdClaimPaidReimbursement(claimDetailsDto.getOpdClaimPaidReimbursement());
			claimsDetails.setPerFamilyLimit(claimDetailsDto.getPerFamilyLimit());

			// Non-GHI products
			claimsDetails.setPolicyperiod(claimDetailsDto.getPolicyPeriod());
			claimsDetails.setPremiumPaid(claimDetailsDto.getPremiumPaid());
			claimsDetails.setTotalSumInsured(claimDetailsDto.getTotalSumInsured());
			claimsDetails.setClaimsNo(claimDetailsDto.getClaimsNo());
			claimsDetails.setClaimedAmount(claimDetailsDto.getClaimedAmount());
			claimsDetails.setSettledAmount(claimDetailsDto.getSettledAmount());
			claimsDetails.setPendingAmount(claimDetailsDto.getPendingAmount());

			try {
				claimsDetails.setCreateDate(new SimpleDateFormat("yyyy-MM-dd")
						.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			claimsDetails.setRecordStatus("ACTIVE");
			claimsRepo.save(claimsDetails);
			log.info("rfq service :: " + claimsDetails.getRfqId());
			return claimsDetails.getRfqId();
		}
		return null;
	}

	@Override
	public ClaimsDetails updateClaimDetails(ClaimDetailsDto claimDetailsDto, String id) {
		ClaimsDetails claimsDetails = claimsRepo.findByrfqId(id).orElseThrow(() -> new InvalidUser("Invalid RFQ ID"));



		claimsDetails.setClaimOutstandingCashless(claimDetailsDto.getClaimOutstandingCashless());
		claimsDetails.setClaimOutstandingReimbursement(claimDetailsDto.getClaimOutstandingReimbursement());
		claimsDetails.setClaimPaidCashless(claimDetailsDto.getClaimPaidCashless());
		claimsDetails.setClaimPaidReimbursement(claimDetailsDto.getClaimPaidReimbursement());
		claimsDetails.setCorporateBufferAmount(claimDetailsDto.getCorporateBufferAmount());
		claimsDetails.setCorporateBufferClaimOutstandingCashless(
				claimDetailsDto.getCorporateBufferClaimOutstandingCashless());
		claimsDetails.setCorporateBufferClaimOutstandingReimbursement(
				claimDetailsDto.getCorporateBufferClaimOutstandingReimbursement());
		claimsDetails.setCorporateBufferClaimPaidCashless(claimDetailsDto.getCorporateBufferClaimPaidCashless());
		claimsDetails
				.setCorporateBufferClaimPaidReimbursement(claimDetailsDto.getCorporateBufferClaimPaidReimbursement());
		claimsDetails.setMaxCasesNo(claimDetailsDto.getMaxCasesNo());
		claimsDetails.setMaxSumInsured(claimDetailsDto.getMaxSumInsured());
		claimsDetails.setOpdClaimOutstandingCashless(claimDetailsDto.getOpdClaimOutstandingCashless());
		claimsDetails.setOpdClaimOutstandingReimbursement(claimDetailsDto.getOpdClaimOutstandingReimbursement());
		claimsDetails.setOpdClaimPaidCashless(claimDetailsDto.getOpdClaimPaidCashless());
		claimsDetails.setOpdClaimPaidReimbursement(claimDetailsDto.getOpdClaimPaidReimbursement());
		claimsDetails.setPerFamilyLimit(claimDetailsDto.getPerFamilyLimit());

		// Non-GHI products
		claimsDetails.setPolicyperiod(claimDetailsDto.getPolicyPeriod());
		claimsDetails.setPremiumPaid(claimDetailsDto.getPremiumPaid());
		claimsDetails.setTotalSumInsured(claimDetailsDto.getTotalSumInsured());
		claimsDetails.setClaimsNo(claimDetailsDto.getClaimsNo());
		claimsDetails.setClaimedAmount(claimDetailsDto.getClaimedAmount());
		claimsDetails.setSettledAmount(claimDetailsDto.getSettledAmount());
		claimsDetails.setPendingAmount(claimDetailsDto.getPendingAmount());

		try {
			claimsDetails.setUpdateDate(
					new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		claimsDetails.setCreateDate(claimsDetails.getCreateDate());
		claimsDetails.setRecordStatus(claimsDetails.getRecordStatus());

		claimsRepo.save(claimsDetails);

		return claimsDetails;

	}

}
