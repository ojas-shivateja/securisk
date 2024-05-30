package com.insure.rfq.repository;

import com.insure.rfq.entity.ClientListEmployee_Submit_ClaimHospitalizationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientListEmployee_Submit_ClaimHospitalizationDetailsRepository extends JpaRepository<ClientListEmployee_Submit_ClaimHospitalizationDetails, Long> {

    @Query("SELECT c FROM ClientListEmployee_Submit_ClaimHospitalizationDetails c WHERE c.user_detailsId = :user_detailsId")
    Optional<ClientListEmployee_Submit_ClaimHospitalizationDetails> findByUser_detailsId(@Param("user_detailsId") String userId);
}
