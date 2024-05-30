package com.insure.rfq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insure.rfq.entity.ClientListUser;

public interface ClientListUserRepository extends JpaRepository<ClientListUser, Long> {

	@Query("SELECT  user FROM ClientListUser user WHERE user.clientList.cid = :clientListId")
	List<ClientListUser> findByClientList(@Param("clientListId") Long clientListId);
}
