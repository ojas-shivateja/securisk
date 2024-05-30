package com.insure.rfq.repository;

import com.insure.rfq.entity.RfqQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RfqRepository extends JpaRepository<RfqQuote, Long> {

    Optional<RfqQuote> findByEmail(String email);


}
