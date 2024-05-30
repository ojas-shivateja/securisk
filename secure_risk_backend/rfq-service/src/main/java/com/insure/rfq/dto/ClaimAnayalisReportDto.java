package com.insure.rfq.dto;

import java.util.List;

import com.insure.rfq.payload.AgeWiseClaimAnalysis;
import com.insure.rfq.payload.ClaimTypeAnalysis;
import com.insure.rfq.payload.DiseaseWiseAnalysis;
import com.insure.rfq.payload.GenderWiseClaimReport;
import com.insure.rfq.payload.IncurredCliamRatio;
import com.insure.rfq.payload.MemberTypeAnalysis;
import com.insure.rfq.payload.RelationWiseClaimReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimAnayalisReportDto {
	private MemberTypeAnalysis memberTypeAnalysis;
	
	private List<GenderWiseClaimReport> genderWiseClaimReport;
	private List<RelationWiseClaimReport> relationWiseClaimReport;
	private AgeWiseClaimAnalysis ageWiseClaimAnalysis;
	private List<ClaimTypeAnalysis> claimTypeAnalysis;
	private List<IncurredCliamRatio> incurredCliamRatio;
	private List<DiseaseWiseAnalysis> diseaseWiseAnalysis;

}
