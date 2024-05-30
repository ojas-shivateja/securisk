package com.insure.rfq.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insure.rfq.entity.ClientDetailsClaimsMis;

import jakarta.transaction.Transactional;

public interface ClientDetailsClaimsMisRepository extends JpaRepository<ClientDetailsClaimsMis,Long> {

	
	 @Modifying
	    @Transactional
	    @Query("DELETE FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId")
	    void deleteByClientListIdAndProductId(@Param("clientListId") Long clientListId, @Param("productId") Long productId);
    @Query
    List<ClientDetailsClaimsMis> findByRfqId( String rfqId);

    @Query("SELECT c FROM ClientDetailsClaimsMis c WHERE c.rfqId=:rfqId ")
    List<ClientDetailsClaimsMis> getClaimsDetailsAfterUpload(@Param("rfqId") String rfqId);

    @Query("SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.relationship = :relation AND c.clientList.cid = :cid AND c.product.productId = :productId ")
    int getCountOfMemeberBasedOnRelation(@Param("relation") String relation, @Param("cid") Long cid, @Param("productId") Long productId);
    @Query("SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.relationship <> :relation AND c.clientList.cid = :cid AND c.product.productId = :productId")
    int getCountOfMemeberTypeIsNotSelf(@Param("relation") String relation, @Param("cid") Long cid, @Param("productId") Long productId);

    @Query("SELECT SUM(c.claimedAmount) FROM ClientDetailsClaimsMis c WHERE c.relationship = :relation AND c.clientList.cid = :cid AND c.product.productId = :productId")
        // **
    int getAmountOfMember(@Param("relation") String relation, @Param("cid") Long cid, @Param("productId") Long productId);

    @Query("SELECT SUM(c.claimedAmount) FROM ClientDetailsClaimsMis c WHERE c.relationship <> :relation AND c.clientList.cid = :cid AND c.product.productId = :productId")
    int getAmountDepent(@Param("relation") String relation, @Param("cid") Long cid, @Param("productId") Long productId);
    // Relation Wise Analysis
    @Query("SELECT c.relationship FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId  ")
    Set<String> getAllRelationData(@Param("clientListId") Long clientList, @Param("productId") Long product);

    // Gender wise Claim Analysis Report Queries
    @Query("SELECT c.gender FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId")
    Set<String> getAllGenderDetails(@Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT COUNT(c.gender) FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId ")
    int getAllGenderCount(@Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT SUM(c.claimedAmount) FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId")
    int getAllGenderSum(@Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.gender=:gender AND c.clientList.cid=:clientListId AND c.product.productId=:productId ")
    int getCountOfGenderWise(@Param("gender") String gender, @Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT (COUNT(ci.claimsId) * 100.0) / (SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c) "
            + "FROM ClientDetailsClaimsMis ci " + "WHERE ci.gender = :gender AND ci.clientList.cid=:clientListId AND ci.product.productId=:productId")
    double getPercentageOfGenderWiseCount(@Param("gender") String gender, @Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT SUM(c.claimedAmount) FROM ClientDetailsClaimsMis c WHERE c.gender= :gender AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    double getGenderWiseAmountSum(@Param("gender") String gender, @Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT (SUM(ci.claimedAmount) * 100.0) / (SELECT SUM(c.claimedAmount) FROM ClientDetailsClaimsMis c) "
            + "FROM ClientDetailsClaimsMis ci " + "WHERE ci.gender = :gender AND ci.clientList.cid=:clientListId AND ci.product.productId=:productId")
    double getPercentageOfGenderAmountPerct(@Param("gender") String gender, @Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT SUM(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId")
    double getTotalAmount(@Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId ")
    int getTotalCount(@Param("clientListId") Long clientList, @Param("productId") Long product);

    // Age Wise Claim Analysis
    @Query("SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.age BETWEEN :start AND :end AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    int getCountOfMemberBasedOnAge(@Param("start") int start, @Param("end") int end, @Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.age > 70 AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    int getCountOfMemberBasedAgeMoreThan70(@Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT SUM(c.sumInsured) FROM ClientDetailsClaimsMis c WHERE c.age BETWEEN :start AND :end AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    double getAmountOfMemberBasedOnAge(@Param("start") int start, @Param("end") int end, @Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT SUM(c.sumInsured) FROM ClientDetailsClaimsMis c WHERE c.age>70 AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    double getAmountOfMemberAgeMoreThan70(@Param("clientListId") Long clientList, @Param("productId") Long product);

    // Claim Type Analysis
    @Query("SELECT c.claimType FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId ")
    Set<String> getAllClaimType(@Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.claimType= :claimType AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    int getCountBasedOnClaimType(@Param("claimType") String claimType, @Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT SUM(c.sumInsured) FROM ClientDetailsClaimsMis c WHERE c.claimType= :claimType AND c.clientList.cid=:clientListId AND c.product.productId=:productId ")
    double getAmountBasedOnClaimType(@Param("claimType") String claimType, @Param("clientListId") Long clientList, @Param("productId") Long product);

    //
    // Incurred Claim report
    @Query("SELECT c.recordStatus FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId ")
    Set<String> getAllStatus(@Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.recordStatus=:status AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    int getCountBasedOnStatus(@Param("status") String status, @Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT SUM(c.sumInsured) FROM ClientDetailsClaimsMis c WHERE c.recordStatus=:status AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    double getAmountBasedOnStatus(@Param("status") String status, @Param("clientListId") Long clientList, @Param("productId") Long product);

    // Disease Wise Data
    @Query("SELECT c.disease FROM ClientDetailsClaimsMis c WHERE c.clientList.cid=:clientListId AND c.product.productId=:productId")
    Set<String> getAllDisease(@Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT COUNT(c.claimsId) FROM ClientDetailsClaimsMis c WHERE c.disease=:disease AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    int getCountBasedOnDisease(@Param("disease") String status, @Param("clientListId") Long clientList, @Param("productId") Long product);

    @Query("SELECT SUM(c.sumInsured) FROM ClientDetailsClaimsMis c WHERE c.disease=:disease AND c.clientList.cid=:clientListId AND c.product.productId=:productId")
    double getAmountBasedOnDisease(@Param("disease") String disease, @Param("clientListId") Long clientList, @Param("productId") Long product);
}
