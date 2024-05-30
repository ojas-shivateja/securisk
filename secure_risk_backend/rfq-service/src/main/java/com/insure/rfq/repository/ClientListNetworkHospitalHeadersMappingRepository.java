package com.insure.rfq.repository;

import com.insure.rfq.entity.ClientListNetworkHospitalHeadersMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientListNetworkHospitalHeadersMappingRepository extends JpaRepository<ClientListNetworkHospitalHeadersMappingEntity,Long> {

    @Query("SELECT c FROM ClientListNetworkHospitalHeadersMappingEntity c  WHERE c.tpaId.tpaName=:tpaName")
    List<ClientListNetworkHospitalHeadersMappingEntity> findByTpaName(String tpaName);
}
