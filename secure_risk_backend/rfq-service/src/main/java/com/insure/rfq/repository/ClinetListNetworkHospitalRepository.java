package com.insure.rfq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.insure.rfq.entity.ClientListNetworkHospitalEntity;

import jakarta.transaction.Transactional;

@Repository
public interface ClinetListNetworkHospitalRepository extends JpaRepository<ClientListNetworkHospitalEntity, Long> {
	@Modifying
	@Transactional
	@Query("DELETE FROM ClientListNetworkHospitalEntity c WHERE c.clientListId.cid=:clientListId AND c.productId.productId=:productId")
	void deleteByClientListIdAndProductId(@Param("clientListId") Long clientListId, @Param("productId") Long productId);
}
