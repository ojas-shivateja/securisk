package com.insure.rfq.repository;

import com.insure.rfq.entity.ClientListEnrollementHeadersMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientListEnrollementHeadersMappingRepository extends JpaRepository<ClientListEnrollementHeadersMappingEntity,Long> {
    @Query("SELECT c FROM ClientListEnrollementHeadersMappingEntity c  WHERE c.tpaId.tpaName=:tpaName")
    List<ClientListEnrollementHeadersMappingEntity> findByTpaName(String tpaName);
}
