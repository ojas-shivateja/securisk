package com.insure.rfq.repository;

import com.insure.rfq.entity.ClientList_Per_Life_Premium_Calculator;
import com.insure.rfq.entity.ClientList_PreFamilyPremium_Calcuater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientList_Life_PremiumCalcuate_repository
		extends JpaRepository<ClientList_Per_Life_Premium_Calculator, Long> {

}
