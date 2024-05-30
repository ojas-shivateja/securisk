package com.insure.rfq.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.insure.rfq.entity.ClaimsMisEntity;

@Repository
public interface ClaimsMisRepository extends JpaRepository<ClaimsMisEntity, Long> {

	// After Upload Claim-Mis , the policyNumber with start and end date 
	@Query("SELECT c FROM ClaimsMisEntity c WHERE c.rfqId=:rfqId ")
	List<ClaimsMisEntity> getClaimsDetailsAfterUpload(@Param("rfqId") String rfqId);

	// CliamsMis
	List<ClaimsMisEntity> findByRfqId(String rfqId);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("UPDATE ClaimsMisEntity c SET c.recordStatus='INACTIVE' WHERE c.rfqId=:id")
	int deleteByRfqId(@Param("id") String rfqId);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("DELETE FROM ClaimsMisEntity c WHERE c.rfqId=:id")
	int hardDeleteByRfqId(@Param("id") String rfqId);

	// Dashboard
	@Query(value = "SELECT status, COUNT(*) AS status_count, SUM(CASE WHEN DATE_PART('month', dateofclaim) = DATE_PART('month', CURRENT_DATE) THEN 1 ELSE 0 END) AS current_month_count FROM claimsmisentity GROUP BY recordStatus;", nativeQuery = true)
	List<Object[]> statusCount();

	// Claims Analysis Report
	@Query("SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c WHERE c.relationship = :relation AND c.rfqId= :id ")
	int getCountOfMemeberBasedOnRelation(@Param("relation") String relation, @Param("id") String id);

	// **
	@Query("SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c WHERE c.relationship <> :relation AND c.rfqId= :id")
	int getCountOfMemeberTypeIsNotSelf(@Param("relation") String relation, @Param("id") String id);

	// **
	@Query("SELECT SUM(c.claimedAmount) FROM ClaimsMisEntity c WHERE c.relationship <> :relation AND c.rfqId= :id")
	int getAmountDepent(@Param("relation") String relation, @Param("id") String id);

	// **
	@Query("SELECT SUM(c.claimedAmount) FROM ClaimsMisEntity c WHERE c.relationship = :relation AND c.rfqId= :id")
	// **
	int getAmountOfMember(@Param("relation") String relation, @Param("id") String id);

	// **
	@Query("SELECT c.relationship FROM ClaimsMisEntity c WHERE c.rfqId= :id ")
	List<String> getAllRelation(@Param("id") String id);

	// Relation Wise Analysis
	@Query("SELECT c.relationship FROM ClaimsMisEntity c WHERE c.rfqId= :id ")
	Set<String> getAllRelationData(@Param("id") String id);

	// Gender wise Claim Analysis Report Queries
	@Query("SELECT c.gender FROM ClaimsMisEntity c WHERE c.rfqId= :id")
	Set<String> getAllGenderDetails(@Param("id") String id);

	@Query("SELECT COUNT(c.gender) FROM ClaimsMisEntity c WHERE c.rfqId= :id")
	int getAllGenderCount(@Param("id") String id);

	@Query("SELECT SUM(c.claimedAmount) FROM ClaimsMisEntity c WHERE c.rfqId= :id")
	int getAllGenderSum(@Param("id") String id);

	@Query("SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c WHERE c.gender=:gender AND c.rfqId= :id ")
	int getCountOfGenderWise(@Param("gender") String gender, @Param("id") String id);

	@Query("SELECT (COUNT(ci.claimsId) * 100.0) / (SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c) "
			+ "FROM ClaimsMisEntity ci " + "WHERE ci.gender = :gender AND ci.rfqId = :id")
	double getPercentageOfGenderWiseCount(@Param("gender") String gender, @Param("id") String id);

	@Query("SELECT SUM(c.claimedAmount) FROM ClaimsMisEntity c WHERE c.gender= :gender AND c.rfqId= :id ")
	double getGenderWiseAmountSum(@Param("gender") String gender, @Param("id") String id);

	@Query("SELECT (SUM(ci.claimedAmount) * 100.0) / (SELECT SUM(c.claimedAmount) FROM ClaimsMisEntity c) "
			+ "FROM ClaimsMisEntity ci " + "WHERE ci.gender = :gender AND ci.rfqId = :id")
	double getPercentageOfGenderAmountPerct(@Param("gender") String gender, @Param("id") String id);

	@Query("SELECT SUM(c.claimsId) FROM ClaimsMisEntity c WHERE c.rfqId= :id")
	double getTotalAmount(@Param("id") String id);

	@Query("SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c WHERE c.rfqId= :id ")
	int getTotalCount(@Param("id") String id);

	// Age Wise Claim Analysis
	@Query("SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c WHERE c.age BETWEEN :start AND :end AND c.rfqId= :id")
	int getCountOfMemberBasedOnAge(@Param("start") int start, @Param("end") int end, @Param("id") String id);

	@Query("SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c WHERE c.age > 70 AND c.rfqId= :id")
	int getCountOfMemberBasedAgeMoreThan70(@Param("id") String id);

	@Query("SELECT SUM(c.sumInsured) FROM ClaimsMisEntity c WHERE c.age BETWEEN :start AND :end AND c.rfqId= :id")
	double getAmountOfMemberBasedOnAge(@Param("start") int start, @Param("end") int end, @Param("id") String id);

	@Query("SELECT SUM(c.sumInsured) FROM ClaimsMisEntity c WHERE c.age>70 AND c.rfqId= :id")
	double getAmountOfMemberAgeMoreThan70(@Param("id") String id);

	// Claim Type Analysis
	@Query("SELECT c.claimType FROM ClaimsMisEntity c WHERE c.rfqId= :id ")
	Set<String> getAllClaimType(@Param("id") String id);

	@Query("SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c WHERE c.rfqId= :id AND c.claimType= :claimType")
	int getCountBasedOnClaimType(@Param("claimType") String claimType, @Param("id") String id);

	@Query("SELECT SUM(c.sumInsured) FROM ClaimsMisEntity c WHERE c.rfqId= :id AND c.claimType= :claimType")
	double getAmountBasedOnClaimType(@Param("claimType") String claimType, @Param("id") String id);

	//
	// Incurred Claim report
	@Query("SELECT c.recordStatus FROM ClaimsMisEntity c WHERE c.rfqId= :id ")
	Set<String> getAllStatus(@Param("id") String id);

	@Query("SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c WHERE c.rfqId= :id AND c.recordStatus=:status")
	int getCountBasedOnStatus(@Param("status") String status, @Param("id") String id);

	@Query("SELECT SUM(c.sumInsured) FROM ClaimsMisEntity c WHERE c.rfqId= :id AND c.recordStatus=:status")
	double getAmountBasedOnStatus(@Param("status") String status, @Param("id") String id);

	// Disease Wise Data
	@Query("SELECT c.disease FROM ClaimsMisEntity c WHERE c.rfqId= :id")
	Set<String> getAllDisease(@Param("id") String id);

	@Query("SELECT COUNT(c.claimsId) FROM ClaimsMisEntity c WHERE c.rfqId= :id AND c.disease=:disease")
	int getCountBasedOnDisease(@Param("disease") String status, @Param("id") String id);

	@Query("SELECT SUM(c.sumInsured) FROM ClaimsMisEntity c WHERE c.rfqId= :id AND c.disease=:disease")
	double getAmountBasedOnDisease(@Param("disease") String disease, @Param("id") String id);
}
