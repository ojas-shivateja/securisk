package com.insure.rfq.controller;

import com.insure.rfq.dto.ClientListEmployee_E_CashlessDisplayDto;
import com.insure.rfq.dto.ClientListEmployee_E_CashlessDto;
import com.insure.rfq.entity.ClientListEmployee_E_Cashless;
import com.insure.rfq.service.ClientListEmployee_E_CashlessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("rfq/clientListEmployee_E_Cashless")
public class ClientListEmployee_E_CashlessController {

    @Autowired
    private ClientListEmployee_E_CashlessService serviceImpl;




    @PostMapping("/saveClientListEmployee_E_Cashless")
    public String  saveClientListEmployeeECashless(@ModelAttribute ClientListEmployee_E_CashlessDto dto, @RequestParam Long clientId, @RequestParam Long productId, @RequestParam Long employeeId) {
        return serviceImpl.saveClientList_E_Cashless(dto, clientId, productId, employeeId);
    }

    @GetMapping("/getAllClientListEmployeeECashlessData")
    public List<ClientListEmployee_E_CashlessDisplayDto> getListEmployeeECashlessDisplay() {
        return serviceImpl.getAllClientListEmployeeECashlessDtoList();
    }

}
