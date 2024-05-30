package com.insure.rfq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.insure.rfq.entity.InsureList;

public interface InsureListRepository extends JpaRepository<InsureList, String> {

	@Modifying
	@Transactional
	@Query("UPDATE InsureList ins SET ins.status=false WHERE ins.insurerId=:id")
	int deleteInsureList(@Param("id") String id);

}
