package com.insure.rfq.repository;

import com.insure.rfq.entity.ClientDetailsClaimsMis;
import com.insure.rfq.entity.ClientDetailsClaimsMisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientDetailsClaimsMisNewRepo extends JpaRepository<ClientDetailsClaimsMisEntity,Long> {


    List<ClientDetailsClaimsMis> findByRfqId(String rfqId);
}
