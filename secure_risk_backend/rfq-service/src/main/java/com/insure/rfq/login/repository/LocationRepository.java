package com.insure.rfq.login.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.insure.rfq.login.entity.Location;

import jakarta.transaction.Transactional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

	@Query("SELECT locationName FROM Location")
	List<String> findByLocation();

	@Modifying
	@Transactional
	@Query("UPDATE Location loc SET loc.status='INACTIVE' WHERE loc.locationId=:id")
	int deleteLocation(@Param("id") Long id);


	

}
