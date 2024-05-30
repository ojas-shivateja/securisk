package com.insure.rfq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.insure.rfq.entity.InsureUsers;

public interface InsurerUsersRepository extends JpaRepository<InsureUsers, String> {
	@Modifying
	@Transactional
	@Query("UPDATE InsureUsers u SET u.status=0 WHERE u.userId=:id")
	int deleteInsureList(@Param("id") String id);

	@Query("SELECT u FROM InsureUsers u  WHERE u.email=:email")
	InsureUsers findUserByEmail(@Param("email") String email);

	@Query("SELECT  u FROM InsureUsers u WHERE u.insureList.insurerId=:insurerId")
	List<InsureUsers> getAllUsersByInsurerId(@Param("insurerId") String insurerId);
}
