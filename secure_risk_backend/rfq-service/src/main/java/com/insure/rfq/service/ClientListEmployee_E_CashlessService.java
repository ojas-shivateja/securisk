package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.ClientListEmployee_E_CashlessDisplayDto;
import com.insure.rfq.dto.ClientListEmployee_E_CashlessDto;

public interface ClientListEmployee_E_CashlessService {

    //  public String saveClientListEmployee_E_Cashless(ClientListEmployee_E_CashlessDto clientListEmployee_E_CashlessDto ,Long clientId , Long productId);
    public List<ClientListEmployee_E_CashlessDisplayDto> getAllClientListEmployeeECashlessDtoList();
     public String saveClientList_E_Cashless(ClientListEmployee_E_CashlessDto clientListEmployee_E_CashlessDto ,Long clientId , Long productId, Long employeeId);
}
