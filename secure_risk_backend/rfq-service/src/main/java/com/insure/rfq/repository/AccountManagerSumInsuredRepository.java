package com.insure.rfq.repository;

import com.insure.rfq.entity.AccountManagerSumInsured;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountManagerSumInsuredRepository extends JpaRepository<AccountManagerSumInsured, Long> {

}
