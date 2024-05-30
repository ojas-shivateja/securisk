package com.insure.rfq.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ClaimAnayalisReportDto;
import com.insure.rfq.payload.AgeWiseClaimAnalysis;
import com.insure.rfq.payload.ClaimTypeAnalysis;
import com.insure.rfq.payload.DiseaseWiseAnalysis;
import com.insure.rfq.payload.GenderWiseClaimReport;
import com.insure.rfq.payload.IncurredCliamRatio;
import com.insure.rfq.payload.MemberTypeAnalysis;
import com.insure.rfq.payload.RelationWiseClaimReport;
import com.insure.rfq.repository.ClaimsMisRepository;
import com.insure.rfq.repository.CorporateDetailsRepository;
import com.insure.rfq.service.ClaimAnalysisReportService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClaimServiceAnalysisImp implements ClaimAnalysisReportService {
	@Autowired
	private ClaimsMisRepository analysisReportRepository;

	@Autowired
	private CorporateDetailsRepository corporateDetailsRepository;

	@Override
	public ClaimAnayalisReportDto generateClaimAnalysisReportPdf(String rfqId) {

		int amount = 0;
//
		int amountrela = 0;
//
		String child = "";

		ClaimAnayalisReportDto claimAnayalisReportDto = new ClaimAnayalisReportDto();
//
		MemberTypeAnalysis analysis = new MemberTypeAnalysis();
//
		Set<String> relations = analysisReportRepository.getAllRelationData(rfqId);
		for (String rela : relations) {
			if (rela.equalsIgnoreCase("Self") || rela.equalsIgnoreCase("Employee")) {

				analysis.setMainMemberCount(analysisReportRepository.getCountOfMemeberBasedOnRelation(rela, rfqId));
				analysis.setDependentCount(analysisReportRepository.getCountOfMemeberTypeIsNotSelf(rela, rfqId));
				analysis.setMainMemberCountAmount(analysisReportRepository.getAmountOfMember(rela, rfqId));
				analysis.setDependentCountDepedentAmount(analysisReportRepository.getAmountDepent(rela, rfqId));
				claimAnayalisReportDto.setMemberTypeAnalysis(analysis);

			}
		}

		// Gender wise Claim Analyis Report

		// count
		Set<String> allGenderDetails = analysisReportRepository.getAllGenderDetails(rfqId);
		allGenderDetails.remove(null);

		List<GenderWiseClaimReport> getAllGenderWiseDetails = new ArrayList<>();
		for (String allgender : allGenderDetails) {

			GenderWiseClaimReport claimReport = new GenderWiseClaimReport();
			if (allgender.equalsIgnoreCase("Male") || allgender.equalsIgnoreCase("M")) {
				claimReport.setGender("Male");
				claimReport.setGenderCount(analysisReportRepository.getCountOfGenderWise(allgender, rfqId));
				double totalNumber = getPercentageCountdata(allgender, rfqId,
						analysisReportRepository.getCountOfGenderWise(allgender, rfqId));
				claimReport.setCountPerct(totalNumber);
				claimReport.setAmount(analysisReportRepository.getGenderWiseAmountSum(allgender, rfqId));
				double totalAmount = getPercentageAmountData(rfqId,
						analysisReportRepository.getGenderWiseAmountSum(allgender, rfqId));
				claimReport.setAmountPerct(totalAmount);
				getAllGenderWiseDetails.add(claimReport);
			} else if (allgender.equalsIgnoreCase("Female") || allgender.equalsIgnoreCase("F")) {
				claimReport.setGender("Female");
				claimReport.setGenderCount(analysisReportRepository.getCountOfGenderWise(allgender, rfqId));
				double totalNumber = getPercentageCountdata(allgender, rfqId,
						analysisReportRepository.getCountOfGenderWise(allgender, rfqId));
				claimReport.setCountPerct(totalNumber);
				claimReport.setAmount(analysisReportRepository.getGenderWiseAmountSum(allgender, rfqId));
				double totalAmount = getPercentageAmountData(rfqId,
						analysisReportRepository.getGenderWiseAmountSum(allgender, rfqId));
				claimReport.setAmountPerct(totalAmount);
				getAllGenderWiseDetails.add(claimReport);
			}
		}

		// adding gender wise report
		claimAnayalisReportDto.setGenderWiseClaimReport(getAllGenderWiseDetails);

		// Relation Wise Claim Analysis
		Set<String> relation = analysisReportRepository.getAllRelationData(rfqId);
		relation.remove(null);
		List<RelationWiseClaimReport> relationWiseReport = new ArrayList<>();
		RelationWiseClaimReport relationWiseClaimReports = new RelationWiseClaimReport();

		// __--count

		for (String rela : relation) {
			RelationWiseClaimReport relationWiseClaimReport = new RelationWiseClaimReport();
			if (rela.equalsIgnoreCase("Mother")) {
				relationWiseClaimReport.setRelation("Mother");
				relationWiseClaimReport
						.setCount(analysisReportRepository.getCountOfMemeberBasedOnRelation(rela, rfqId));
				relationWiseClaimReport.setAmount(analysisReportRepository.getAmountOfMember(rela, rfqId));
				relationWiseReport.add(relationWiseClaimReport);
			} else if (rela.equalsIgnoreCase("Father")) {
				relationWiseClaimReport.setRelation("Father");
				relationWiseClaimReport
						.setCount(analysisReportRepository.getCountOfMemeberBasedOnRelation(rela, rfqId));
				relationWiseClaimReport.setAmount(analysisReportRepository.getAmountOfMember(rela, rfqId));
				relationWiseReport.add(relationWiseClaimReport);
			} else if (rela.equalsIgnoreCase("Husband") || rela.equalsIgnoreCase("Spouse")) {
				relationWiseClaimReport.setRelation("Husband");
				relationWiseClaimReport
						.setCount(analysisReportRepository.getCountOfMemeberBasedOnRelation(rela, rfqId));
				relationWiseClaimReport.setAmount(analysisReportRepository.getAmountOfMember(rela, rfqId));
				relationWiseReport.add(relationWiseClaimReport);
			} else if (rela.equalsIgnoreCase("wife") || rela.equalsIgnoreCase("Spouse")) {
				relationWiseClaimReport.setRelation("Spouse");
				relationWiseClaimReport
						.setCount(analysisReportRepository.getCountOfMemeberBasedOnRelation(rela, rfqId));
				relationWiseClaimReport.setAmount(analysisReportRepository.getAmountOfMember(rela, rfqId));
				relationWiseReport.add(relationWiseClaimReport);
			}

			else if (rela.equalsIgnoreCase("Employee") || rela.equalsIgnoreCase("Self")) {
				relationWiseClaimReport.setRelation("Self");
				relationWiseClaimReport
						.setCount(analysisReportRepository.getCountOfMemeberBasedOnRelation(rela, rfqId));
				relationWiseClaimReport.setAmount(analysisReportRepository.getAmountOfMember(rela, rfqId));
				relationWiseReport.add(relationWiseClaimReport);
			} else {

				child = "child";
				amount = amount + analysisReportRepository.getCountOfMemeberBasedOnRelation(rela, rfqId);
				amountrela = amountrela + analysisReportRepository.getAmountOfMember(rela, rfqId);

			}

		}

		if (child.equals("child")) {
			relationWiseClaimReports.setRelation("Child");
			relationWiseClaimReports.setCount(amount);
			relationWiseClaimReports.setAmount(amountrela);
			relationWiseReport.add(relationWiseClaimReports);

		}

		claimAnayalisReportDto.setRelationWiseClaimReport(relationWiseReport);

		// Claim Analysis Based on Age
		AgeWiseClaimAnalysis ageWiseClaimAnalysis = new AgeWiseClaimAnalysis();
		// count
		if (analysisReportRepository.getCountOfMemberBasedOnAge(0, 10, rfqId) > 0) {
			ageWiseClaimAnalysis.setAgeCount0To10(analysisReportRepository.getCountOfMemberBasedOnAge(0, 10, rfqId));
			log.info("{}", analysisReportRepository.getCountOfMemberBasedOnAge(0, 10, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount0To10(0);

		}
		if (analysisReportRepository.getCountOfMemberBasedOnAge(11, 20, rfqId) > 0) {
			ageWiseClaimAnalysis.setAgeCount11To20(analysisReportRepository.getCountOfMemberBasedOnAge(11, 20, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount11To20(0);
		}

		if (analysisReportRepository.getCountOfMemberBasedOnAge(21, 30, rfqId) > 0) {
			ageWiseClaimAnalysis.setAgeCount21To30(analysisReportRepository.getCountOfMemberBasedOnAge(21, 30, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount21To30(0);
		}

		if (analysisReportRepository.getCountOfMemberBasedOnAge(21, 30, rfqId) > 0) {
			ageWiseClaimAnalysis.setAgeCount21To30(analysisReportRepository.getCountOfMemberBasedOnAge(21, 30, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount21To30(0);
		}

		if (analysisReportRepository.getCountOfMemberBasedOnAge(31, 40, rfqId) > 0) {
			ageWiseClaimAnalysis.setAgeCount31To40(analysisReportRepository.getCountOfMemberBasedOnAge(31, 40, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount31To40(0);
		}

		if (analysisReportRepository.getCountOfMemberBasedOnAge(41, 50, rfqId) > 0) {
			ageWiseClaimAnalysis.setAgeCount41To50(analysisReportRepository.getCountOfMemberBasedOnAge(41, 50, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount41To50(0);
		}

		if (analysisReportRepository.getCountOfMemberBasedOnAge(51, 60, rfqId) > 0) {
			ageWiseClaimAnalysis.setAgeCount51To60(analysisReportRepository.getCountOfMemberBasedOnAge(51, 60, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount51To60(0);
		}
		if (analysisReportRepository.getCountOfMemberBasedOnAge(61, 70, rfqId) > 0) {
			ageWiseClaimAnalysis.setAgeCount61To70(analysisReportRepository.getCountOfMemberBasedOnAge(61, 70, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount61To70(0);
		}
		if (analysisReportRepository.getCountOfMemberBasedAgeMoreThan70(rfqId) > 0) {
			ageWiseClaimAnalysis.setAgeCount61To70(analysisReportRepository.getCountOfMemberBasedAgeMoreThan70(rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount70AndAbove(0);
		}

		// amount

		if (analysisReportRepository.getCountOfMemberBasedOnAge(0, 10, rfqId) > 0) {

			ageWiseClaimAnalysis
					.setAgeCount0To10Amount(analysisReportRepository.getAmountOfMemberBasedOnAge(0, 10, rfqId));
			log.info("{}", analysisReportRepository.getAmountOfMemberBasedOnAge(0, 10, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount0To10Amount(0.0);
		}

		if (analysisReportRepository.getCountOfMemberBasedOnAge(11, 20, rfqId) > 0) {
			ageWiseClaimAnalysis
					.setAgeCount11To20Amount(analysisReportRepository.getAmountOfMemberBasedOnAge(11, 20, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount11To20Amount(0);
		}

		if (analysisReportRepository.getCountOfMemberBasedOnAge(21, 30, rfqId) > 0) {
			ageWiseClaimAnalysis
					.setAgeCount21To30Amount(analysisReportRepository.getAmountOfMemberBasedOnAge(21, 30, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount21To30Amount(0);
		}
		if (analysisReportRepository.getCountOfMemberBasedOnAge(31, 40, rfqId) > 0) {
			ageWiseClaimAnalysis
					.setAgeCount31To40Amount(analysisReportRepository.getAmountOfMemberBasedOnAge(31, 40, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount31To40Amount(0);
		}
		if (analysisReportRepository.getCountOfMemberBasedOnAge(41, 50, rfqId) > 0) {
			ageWiseClaimAnalysis
					.setAgeCount41To50Amount(analysisReportRepository.getAmountOfMemberBasedOnAge(41, 50, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount41To50Amount(0);
		}
		if (analysisReportRepository.getCountOfMemberBasedOnAge(51, 60, rfqId) > 0) {
			ageWiseClaimAnalysis
					.setAgeCount0To10Amount(analysisReportRepository.getAmountOfMemberBasedOnAge(51, 60, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount51To60Amount(0);
		}
		if (analysisReportRepository.getCountOfMemberBasedOnAge(61, 70, rfqId) > 0) {
			ageWiseClaimAnalysis
					.setAgeCount0To10Amount(analysisReportRepository.getAmountOfMemberBasedOnAge(61, 70, rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount61To70Amount(0);
		}
		if (analysisReportRepository.getCountOfMemberBasedAgeMoreThan70(rfqId) > 0) {
			ageWiseClaimAnalysis
					.setAgeCount70AndAboveAmount(analysisReportRepository.getAmountOfMemberAgeMoreThan70(rfqId));
		} else {
			ageWiseClaimAnalysis.setAgeCount70AndAboveAmount(0);
		}

		claimAnayalisReportDto.setAgeWiseClaimAnalysis(ageWiseClaimAnalysis);
		// Claim Type analysis Report
		Set<String> allClaimType = analysisReportRepository.getAllClaimType(rfqId);
		allClaimType.remove(null);
		allClaimType.remove("");
		List<ClaimTypeAnalysis> getlaimDetails = new ArrayList<>();

		for (String claimType : allClaimType) {
			ClaimTypeAnalysis analysis1 = new ClaimTypeAnalysis();
			analysis1.setStatus(claimType);
			analysis1.setNumber(analysisReportRepository.getCountBasedOnClaimType(claimType, rfqId));
			analysis1.setAmount(analysisReportRepository.getAmountBasedOnClaimType(claimType, rfqId));
			getlaimDetails.add(analysis1);
		}

		claimAnayalisReportDto.setClaimTypeAnalysis(getlaimDetails);
		Set<String> allStatus;
		allStatus = analysisReportRepository.getAllStatus(rfqId);
		allStatus.remove(null);
		allStatus.remove("");
		if (allStatus.size() == 0) {
			allStatus.remove(null);
			allStatus.remove("");

		}
		List<IncurredCliamRatio> incurred = new ArrayList<>();

		for (String status : allStatus) {
			IncurredCliamRatio incurredCliamRatio = new IncurredCliamRatio();
			incurredCliamRatio.setStatus(status);
			incurredCliamRatio.setCount(analysisReportRepository.getCountBasedOnStatus(status, rfqId));
			incurredCliamRatio.setAmount(analysisReportRepository.getAmountBasedOnStatus(status, rfqId));
			incurred.add(incurredCliamRatio);
		}

		claimAnayalisReportDto.setIncurredCliamRatio(incurred);

		Set<String> diseaseList = analysisReportRepository.getAllDisease(rfqId);
		diseaseList.remove(null);
		List<DiseaseWiseAnalysis> getAllDiswaseWiseAnalysis = new ArrayList<>();

		for (String disease : diseaseList) {
			DiseaseWiseAnalysis diseaseWiseAnalysis = new DiseaseWiseAnalysis();
			diseaseWiseAnalysis.setDiseaseName(disease);
			diseaseWiseAnalysis.setDiseaseCount(analysisReportRepository.getCountBasedOnDisease(disease, rfqId));
			diseaseWiseAnalysis.setAmount(analysisReportRepository.getAmountBasedOnDisease(disease, rfqId));
			getAllDiswaseWiseAnalysis.add(diseaseWiseAnalysis);
		}
		claimAnayalisReportDto.setDiseaseWiseAnalysis(getAllDiswaseWiseAnalysis);
		return claimAnayalisReportDto;
	}

	public double getPercentageCountdata(String allgender, String rfqid, int count) {

		int totalCount = analysisReportRepository.getAllGenderCount(rfqid);

		return ((count * 100) / totalCount);
	}

	public double getPercentageAmountData(String rfqid, double Amount) {
		int totalAmount = analysisReportRepository.getAllGenderSum(rfqid);
		return ((Amount * 100)) / totalAmount;
	}

}