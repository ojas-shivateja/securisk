package com.insure.rfq.service;

import com.insure.rfq.dto.DisplayAllInsurerDetails;
import com.insure.rfq.dto.InsurerBankDetailsDto;
import com.insure.rfq.dto.ResponseDto;
import com.insure.rfq.dto.UpdateInsurerBankDetailsDto;

import java.util.List;

public interface InsurerBankDetailsService {

    ResponseDto createInsurerBank(InsurerBankDetailsDto insurerBankDetailsDto,Long clientListId, Long productId );

List<DisplayAllInsurerDetails> getAllInsurerDetails(long clientlistId,long productId);

    String deleteInsurer(Long id);
    public byte[] generateExcelFromData(Long clientListId, Long productId);

    public String updateInsurerById(UpdateInsurerBankDetailsDto dto, Long id);
    public DisplayAllInsurerDetails getById(Long insurerId);




}
