package com.insure.rfq.service.impl;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ClaimAnayalisMisReportDto;
import com.insure.rfq.dto.ClaimAnayalisReportDto;
import com.insure.rfq.payload.AgeWiseClaimAnalysis;
import com.insure.rfq.payload.ClaimTypeAnalysis;
import com.insure.rfq.payload.DiseaseWiseAnalysis;
import com.insure.rfq.payload.GenderWiseClaimReport;
import com.insure.rfq.payload.IncurredCliamRatio;
import com.insure.rfq.payload.MemberTypeAnalysis;
import com.insure.rfq.payload.RelationWiseClaimReport;
import com.insure.rfq.repository.ClientDetailsClaimsMisRepository;
import com.insure.rfq.service.ClientListClaimsMisAnalysisService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientListAnalysisClaimsMisImpl implements ClientListClaimsMisAnalysisService {
	@Autowired
	private ClientDetailsClaimsMisRepository clientDetailsClaimsMisRepository;

	@Override
	public ClaimAnayalisMisReportDto generateReport(Long clientListId, Long productId) {

		int amount = 0;
		//
		int amountrela = 0;
		//
		String child = "";
		ClaimAnayalisMisReportDto claimAnayalisReportDto = new ClaimAnayalisMisReportDto();

		MemberTypeAnalysis analysis = new MemberTypeAnalysis();
		// Get the current system date
		LocalDate currentDate = LocalDate.now();
		// Extract the month from the current date
		Month desiredMonth = currentDate.getMonth();
		clientDetailsClaimsMisRepository.findAll().stream().filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(i -> clientListId != 0 && i.getClientList() != null
						&& i.getClientList().getCid() == (clientListId))
				.filter(i -> productId != 0 && i.getProduct() != null
						&& i.getProduct().getProductId().equals(productId))
				.filter(i -> {
					// Extract month from createdDate
					LocalDate createdDate = i.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					Month month = createdDate.getMonth();
					return month == desiredMonth;
				}).forEach(c -> {
					Set<String> relations = clientDetailsClaimsMisRepository.getAllRelationData(clientListId,
							productId);
					for (String rela : relations) {
						if (rela.equalsIgnoreCase("Self") || rela.equalsIgnoreCase("Employee")) {
							analysis.setMainMemberCount(clientDetailsClaimsMisRepository
									.getCountOfMemeberBasedOnRelation(rela, clientListId, productId));
							analysis.setDependentCount(clientDetailsClaimsMisRepository
									.getCountOfMemeberTypeIsNotSelf(rela, clientListId, productId));
							analysis.setMainMemberCountAmount(
									clientDetailsClaimsMisRepository.getAmountOfMember(rela, clientListId, productId));
							analysis.setDependentCountDepedentAmount(
									clientDetailsClaimsMisRepository.getAmountDepent(rela, clientListId, productId));
							claimAnayalisReportDto.setMemberTypeAnalysis(analysis);
						}
					}
				});

		Set<String> allGenderDetails = clientDetailsClaimsMisRepository.getAllGenderDetails(clientListId, productId);
		allGenderDetails.remove(null);

		List<GenderWiseClaimReport> getAllGenderWiseDetails = new ArrayList<>();
		for (String allgender : allGenderDetails) {

			GenderWiseClaimReport claimReport = new GenderWiseClaimReport();
			if (allgender.equalsIgnoreCase("Male") || allgender.equalsIgnoreCase("M")) {
				claimReport.setGender("Male");
				claimReport.setGenderCount(
						clientDetailsClaimsMisRepository.getCountOfGenderWise(allgender, clientListId, productId));
				double totalNumber = getPercentageCountdata(allgender,
						clientDetailsClaimsMisRepository.getCountOfGenderWise(allgender, clientListId, productId),
						clientListId, productId);
				claimReport.setCountPerct(totalNumber);
				claimReport.setAmount(
						clientDetailsClaimsMisRepository.getGenderWiseAmountSum(allgender, clientListId, productId));
				double totalAmount = getPercentageAmountData(clientListId, productId,
						clientDetailsClaimsMisRepository.getGenderWiseAmountSum(allgender, clientListId, productId));
				claimReport.setAmountPerct(totalAmount);
				getAllGenderWiseDetails.add(claimReport);
			} else if (allgender.equalsIgnoreCase("Female") || allgender.equalsIgnoreCase("F")) {
				claimReport.setGender("Female");
				claimReport.setGenderCount(
						clientDetailsClaimsMisRepository.getCountOfGenderWise(allgender, clientListId, productId));
				double totalNumber = getPercentageCountdata(allgender,
						clientDetailsClaimsMisRepository.getCountOfGenderWise(allgender, clientListId, productId),
						clientListId, productId);
				claimReport.setCountPerct(totalNumber);
				claimReport.setAmount(
						clientDetailsClaimsMisRepository.getGenderWiseAmountSum(allgender, clientListId, productId));
				double totalAmount = getPercentageAmountData(clientListId, productId,
						clientDetailsClaimsMisRepository.getGenderWiseAmountSum(allgender, clientListId, productId));
				claimReport.setAmountPerct(totalAmount);
				getAllGenderWiseDetails.add(claimReport);
			}
		}

		// adding gender wise report
		claimAnayalisReportDto.setGenderWiseClaimReport(getAllGenderWiseDetails);

		// Relation Wise Claim Analysis
		Set<String> relation = clientDetailsClaimsMisRepository.getAllRelationData(clientListId, productId);
		relation.remove(null);
		List<RelationWiseClaimReport> relationWiseReport = new ArrayList<>();
		RelationWiseClaimReport relationWiseClaimReports = new RelationWiseClaimReport();

		// __--count

		for (String rela : relation) {
			RelationWiseClaimReport relationWiseClaimReport = new RelationWiseClaimReport();
			if (rela.equalsIgnoreCase("Mother")) {
				relationWiseClaimReport.setRelation("Mother");
				relationWiseClaimReport.setCount(clientDetailsClaimsMisRepository.getCountOfMemeberBasedOnRelation(rela,
						clientListId, productId));
				relationWiseClaimReport
						.setAmount(clientDetailsClaimsMisRepository.getAmountOfMember(rela, clientListId, productId));
				relationWiseReport.add(relationWiseClaimReport);
			} else if (rela.equalsIgnoreCase("Father")) {
				relationWiseClaimReport.setRelation("Father");
				relationWiseClaimReport.setCount(clientDetailsClaimsMisRepository.getCountOfMemeberBasedOnRelation(rela,
						clientListId, productId));
				relationWiseClaimReport
						.setAmount(clientDetailsClaimsMisRepository.getAmountOfMember(rela, clientListId, productId));
				relationWiseReport.add(relationWiseClaimReport);
			} else if (rela.equalsIgnoreCase("Husband") || rela.equalsIgnoreCase("Spouse")) {
				relationWiseClaimReport.setRelation("Husband");
				relationWiseClaimReport.setCount(clientDetailsClaimsMisRepository.getCountOfMemeberBasedOnRelation(rela,
						clientListId, productId));
				relationWiseClaimReport
						.setAmount(clientDetailsClaimsMisRepository.getAmountOfMember(rela, clientListId, productId));
				relationWiseReport.add(relationWiseClaimReport);
			} else if (rela.equalsIgnoreCase("wife") || rela.equalsIgnoreCase("Spouse")) {
				relationWiseClaimReport.setRelation("Spouse");
				relationWiseClaimReport.setCount(clientDetailsClaimsMisRepository.getCountOfMemeberBasedOnRelation(rela,
						clientListId, productId));
				relationWiseClaimReport
						.setAmount(clientDetailsClaimsMisRepository.getAmountOfMember(rela, clientListId, productId));
				relationWiseReport.add(relationWiseClaimReport);
			}

			else if (rela.equalsIgnoreCase("Employee") || rela.equalsIgnoreCase("Self")) {
				relationWiseClaimReport.setRelation("Self");
				relationWiseClaimReport.setCount(clientDetailsClaimsMisRepository.getCountOfMemeberBasedOnRelation(rela,
						clientListId, productId));
				relationWiseClaimReport
						.setAmount(clientDetailsClaimsMisRepository.getAmountOfMember(rela, clientListId, productId));
				relationWiseReport.add(relationWiseClaimReport);
			} else {
				// relationWiseClaimReport.setRelation("Child");
				// relationWiseClaimReport.setCount(clientDetailsClaimsMisRepository.getCountOfMemeberBasedOnRelation(rela,
				// rfqId));
				child = "child";
				amount = amount + clientDetailsClaimsMisRepository.getCountOfMemeberBasedOnRelation(rela, clientListId,
						productId);
				// relationWiseClaimReport.setAmount(clientDetailsClaimsMisRepository.getAmountOfMember(rela,
				// rfqId));
				amountrela = amountrela
						+ clientDetailsClaimsMisRepository.getAmountOfMember(rela, clientListId, productId);
				// relationWiseReport.add(relationWiseClaimReport);

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
		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(0, 10, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount0To10(
					clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(0, 10, clientListId, productId));
			log.info("{}", clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(0, 10, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount0To10(0);

		}
		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(11, 20, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount11To20(
					clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(11, 20, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount11To20(0);
		}

		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(21, 30, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount21To30(
					clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(21, 30, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount21To30(0);
		}

		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(21, 30, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount21To30(
					clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(21, 30, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount21To30(0);
		}

		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(31, 40, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount31To40(
					clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(31, 40, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount31To40(0);
		}

		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(41, 50, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount41To50(
					clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(41, 50, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount41To50(0);
		}

		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(51, 60, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount51To60(
					clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(51, 60, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount51To60(0);
		}
		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(61, 70, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount61To70(
					clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(61, 70, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount61To70(0);
		}
		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedAgeMoreThan70(clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount61To70(
					clientDetailsClaimsMisRepository.getCountOfMemberBasedAgeMoreThan70(clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount70AndAbove(0);
		}

		// amount

		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(0, 10, clientListId, productId) > 0) {

			ageWiseClaimAnalysis.setAgeCount0To10Amount(
					clientDetailsClaimsMisRepository.getAmountOfMemberBasedOnAge(0, 10, clientListId, productId));
			log.info("{}",
					clientDetailsClaimsMisRepository.getAmountOfMemberBasedOnAge(0, 10, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount0To10Amount(0.0);
		}

		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(11, 20, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount11To20Amount(
					clientDetailsClaimsMisRepository.getAmountOfMemberBasedOnAge(11, 20, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount11To20Amount(0);
		}

		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(21, 30, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount21To30Amount(
					clientDetailsClaimsMisRepository.getAmountOfMemberBasedOnAge(21, 30, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount21To30Amount(0);
		}
		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(31, 40, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount31To40Amount(
					clientDetailsClaimsMisRepository.getAmountOfMemberBasedOnAge(31, 40, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount31To40Amount(0);
		}
		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(41, 50, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount41To50Amount(
					clientDetailsClaimsMisRepository.getAmountOfMemberBasedOnAge(41, 50, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount41To50Amount(0);
		}
		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(51, 60, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount0To10Amount(
					clientDetailsClaimsMisRepository.getAmountOfMemberBasedOnAge(51, 60, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount51To60Amount(0);
		}
		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedOnAge(61, 70, clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount0To10Amount(
					clientDetailsClaimsMisRepository.getAmountOfMemberBasedOnAge(61, 70, clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount61To70Amount(0);
		}
		if (clientDetailsClaimsMisRepository.getCountOfMemberBasedAgeMoreThan70(clientListId, productId) > 0) {
			ageWiseClaimAnalysis.setAgeCount70AndAboveAmount(
					clientDetailsClaimsMisRepository.getAmountOfMemberAgeMoreThan70(clientListId, productId));
		} else {
			ageWiseClaimAnalysis.setAgeCount70AndAboveAmount(0);
		}

		claimAnayalisReportDto.setAgeWiseClaimAnalysis(ageWiseClaimAnalysis);
		// Claim Type analysis Report
		Set<String> allClaimType = clientDetailsClaimsMisRepository.getAllClaimType(clientListId, productId);
		allClaimType.remove(null);
		allClaimType.remove("");
		List<ClaimTypeAnalysis> getlaimDetails = new ArrayList<>();

		for (String claimType : allClaimType) {
			ClaimTypeAnalysis analysis1 = new ClaimTypeAnalysis();
			analysis1.setStatus(claimType);
			analysis1.setNumber(
					clientDetailsClaimsMisRepository.getCountBasedOnClaimType(claimType, clientListId, productId));
			analysis1.setAmount(
					clientDetailsClaimsMisRepository.getAmountBasedOnClaimType(claimType, clientListId, productId));
			getlaimDetails.add(analysis1);
		}

		claimAnayalisReportDto.setClaimTypeAnalysis(getlaimDetails);
		Set<String> allStatus;
		allStatus = clientDetailsClaimsMisRepository.getAllStatus(clientListId, productId);
		allStatus.remove(null);
		allStatus.remove("");
		if (allStatus.size() == 0) {
//				allStatus = clientDetailsClaimsMisRepository.getAllStatusNew(rfqId);
			allStatus.remove(null);
			allStatus.remove("");

		}
		List<IncurredCliamRatio> incurred = new ArrayList<>();

		for (String status : allStatus) {
			IncurredCliamRatio incurredCliamRatio = new IncurredCliamRatio();
			incurredCliamRatio.setStatus(status);
			incurredCliamRatio
					.setCount(clientDetailsClaimsMisRepository.getCountBasedOnStatus(status, clientListId, productId));
			incurredCliamRatio.setAmount(
					clientDetailsClaimsMisRepository.getAmountBasedOnStatus(status, clientListId, productId));
			incurred.add(incurredCliamRatio);
		}

		claimAnayalisReportDto.setIncurredCliamRatio(incurred);

		Set<String> diseaseList = clientDetailsClaimsMisRepository.getAllDisease(clientListId, productId);
		diseaseList.remove(null);
		List<DiseaseWiseAnalysis> getAllDiswaseWiseAnalysis = new ArrayList<>();

		for (String disease : diseaseList) {
			DiseaseWiseAnalysis diseaseWiseAnalysis = new DiseaseWiseAnalysis();
			diseaseWiseAnalysis.setDiseaseName(disease);
			diseaseWiseAnalysis.setDiseaseCount(
					clientDetailsClaimsMisRepository.getCountBasedOnDisease(disease, clientListId, productId));
			diseaseWiseAnalysis.setAmount(
					clientDetailsClaimsMisRepository.getAmountBasedOnDisease(disease, clientListId, productId));
			getAllDiswaseWiseAnalysis.add(diseaseWiseAnalysis);
		}
		claimAnayalisReportDto.setDiseaseWiseAnalysis(getAllDiswaseWiseAnalysis);
		return claimAnayalisReportDto;
	}

	public double getPercentageCountdata(String allgender, int count, Long clientListId, Long productId) {

		int totalCount = clientDetailsClaimsMisRepository.getAllGenderCount(clientListId, productId);

		return ((count * 100) / totalCount);
	}

	public double getPercentageAmountData(Long clientListId, Long productId, double Amount) {
		int totalAmount = clientDetailsClaimsMisRepository.getAllGenderSum(clientListId, productId);
		return ((Amount * 100)) / totalAmount;
	}

}
