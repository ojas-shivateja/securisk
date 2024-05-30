package com.insure.rfq.repository;

import com.insure.rfq.entity.ClaimsDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimsDetailsRepository extends JpaRepository<ClaimsDetails, Long> {

	Optional<ClaimsDetails> findByrfqId(String rfqId);

}
