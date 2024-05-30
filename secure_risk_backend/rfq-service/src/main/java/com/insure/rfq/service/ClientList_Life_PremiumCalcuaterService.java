package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.ClientList_Life_PremiumCalcuaterDto;

public interface ClientList_Life_PremiumCalcuaterService {

	 String createClientList_Life_PremiumCalcuater(ClientList_Life_PremiumCalcuaterDto preLifePremium_Calcuater , Long clientID , Long produtId);
	 List<ClientList_Life_PremiumCalcuaterDto> getAllTheClientList_Life_PremiumCalcuaters(Long clientID, Long ProductId);
	ClientList_Life_PremiumCalcuaterDto getClientList_LifePremiumCalcuaterDto(Long primary_Id);
	 String deleteClientListLifePremiumCalcuaterDto(Long primary_Id);
	 ClientList_Life_PremiumCalcuaterDto update_ClientList_Life_PremiumCalcuaterDto(ClientList_Life_PremiumCalcuaterDto dto);
	byte[] generateExcelFromData(Long clientListId, Long productId);
}
