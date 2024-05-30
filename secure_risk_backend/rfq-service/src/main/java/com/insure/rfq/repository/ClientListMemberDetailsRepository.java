    package com.insure.rfq.repository;

    import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insure.rfq.entity.ClientListMemberDetails;

import jakarta.transaction.Transactional;

    public interface ClientListMemberDetailsRepository extends JpaRepository<ClientListMemberDetails,Long> {

    	 @Transactional
         @Modifying
         @Query("DELETE FROM ClientListMemberDetails c WHERE c.clientList.cid=:clientList AND c.product.productId=:productId")
         int deleteClientListMemberDetails(@Param("clientList") Long clientList ,@Param("productId") Long productId);
    }
