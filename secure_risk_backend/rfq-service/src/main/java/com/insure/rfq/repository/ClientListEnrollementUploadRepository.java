package com.insure.rfq.repository;

import com.insure.rfq.entity.ClientListEnrollementHeadersMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientListEnrollementUploadRepository extends JpaRepository<ClientListEnrollementHeadersMappingEntity,Long> {
}
