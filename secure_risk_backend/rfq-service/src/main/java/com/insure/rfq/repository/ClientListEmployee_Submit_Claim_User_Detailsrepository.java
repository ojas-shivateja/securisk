package com.insure.rfq.repository;

import com.insure.rfq.entity.ClientListEmployee_Submit_Claim_User_Details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientListEmployee_Submit_Claim_User_Detailsrepository extends JpaRepository<ClientListEmployee_Submit_Claim_User_Details, Long> {

    @Query("SELECT c FROM ClientListEmployee_Submit_Claim_User_Details c WHERE c.user_detailsId = :user_detailsId")
    Optional<ClientListEmployee_Submit_Claim_User_Details> findByUser_detailsId(@Param("user_detailsId") String userId);

}
