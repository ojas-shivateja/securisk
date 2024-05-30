package com.insure.rfq.login.repository;

import com.insure.rfq.login.entity.LoginUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginUserDetailsRepo extends JpaRepository<LoginUserDetails,Long> {

}
