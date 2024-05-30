package com.insure.rfq.repository;

import com.insure.rfq.entity.EndorsementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EndorsementRepository extends JpaRepository<EndorsementEntity,Long> {



}
