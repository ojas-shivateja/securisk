package com.insure.rfq.service;

import com.insure.rfq.dto.AccountManagerSumInsuredDisplayDto;
import com.insure.rfq.dto.AccountManagerSumInsuredDto;

import java.io.IOException;
import java.util.List;

public interface AccountManagerSumInsuredService {

	String  createAccountManagerSumInsured(AccountManagerSumInsuredDto sumInsuredDto, Long clientListId, Long poductId);

	AccountManagerSumInsuredDisplayDto upadateAccountManagerSumInsured(AccountManagerSumInsuredDto sumInsuredDto, Long id);

	List<AccountManagerSumInsuredDisplayDto> getAllSumInsuredDetails(Long clientlistId, Long productId);

	String deleteSumInsuredDetailsById(Long id);

	byte[] downloadSumInsuredDocumentBySumInsuredId(Long sumInsuredId) throws IOException;
}
