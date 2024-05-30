package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.ClientListFamilyPremiumCalcuaterDto;

public interface ClientListFamilyPremiumCalcuaterService {

	 String createClientListFamilyPremiumCalcuater(ClientListFamilyPremiumCalcuaterDto  preFamilyPremium_Calcuater , Long clientID , Long produtId);
	 List<ClientListFamilyPremiumCalcuaterDto> getAllTheClientListPremiumCalcuaters(Long clientID, Long ProductId);
	ClientListFamilyPremiumCalcuaterDto getClientListPremiumCalcuaterDto(Long primary_Id);
	 String deleteClientListPremiumCalcuaterDto(Long primary_Id) ;
	public ClientListFamilyPremiumCalcuaterDto update_ClientListFamilyPremiumCalcuaterDto(ClientListFamilyPremiumCalcuaterDto dto);
	byte[] generateExcelFromData(Long clientListId, Long productId);
}
