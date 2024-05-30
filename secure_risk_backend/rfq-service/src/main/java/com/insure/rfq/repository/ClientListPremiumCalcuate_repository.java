package com.insure.rfq.repository;

import com.insure.rfq.entity.ClientList_PreFamilyPremium_Calcuater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientListPremiumCalcuate_repository
		extends JpaRepository<ClientList_PreFamilyPremium_Calcuater, Long> {


}
