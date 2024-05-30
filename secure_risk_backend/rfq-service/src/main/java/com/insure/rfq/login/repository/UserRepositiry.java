package com.insure.rfq.login.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insure.rfq.login.entity.Department;
import com.insure.rfq.login.entity.Designation;
import com.insure.rfq.login.entity.Location;
import com.insure.rfq.login.entity.UserRegisteration;

import jakarta.transaction.Transactional;

public interface UserRepositiry extends JpaRepository<UserRegisteration, Long> {

	@Query("SELECT u FROM UserRegisteration u WHERE u.location.locationId=:locationId")
	List<UserRegisteration> getAllUsersByLocationId(@Param("locationId") Long locationId);

	Optional<UserRegisteration> findByEmail(String email);

	@Modifying
	@Transactional
	@Query("UPDATE UserRegisteration u SET u.status='INACTIVE' WHERE u.userId=:id")
	int deleteUser(@Param("id") Long id);

	@Query("SELECT u.location   FROM UserRegisteration u WHERE u.userId=:id")
	Location findLocationByUserId(@Param("id") Long id);

	@Query("SELECT u.designation FROM UserRegisteration u WHERE u.userId=:id")
	Designation findDesignationByUserId(@Param("id") Long id);

	@Query("SELECT u.department FROM UserRegisteration u WHERE u.userId=:id")
	Department findDepartmentByUserId(@Param("id") Long id);
	
	Optional<UserRegisteration> findByEmailAndOtp(String email, String otp);

}
