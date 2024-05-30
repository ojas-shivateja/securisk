package com.insure.rfq.service.impl;

import com.insure.rfq.dto.RfqQuoteDto;
import com.insure.rfq.entity.RfqQuote;
import com.insure.rfq.repository.RfqRepository;
import com.insure.rfq.service.RfqQuoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RfqQuoteServiceImpl implements RfqQuoteService {

    @Autowired
    private RfqRepository respository;

    @Override
    public String createRfqQuote(RfqQuoteDto quote) {
        RfqQuote rfqQuote = new RfqQuote();
        rfqQuote.setCompanyName(quote.getCompanyName());
        rfqQuote.setFirstName(quote.getFirstName());
        rfqQuote.setLastName(quote.getLastName());
        rfqQuote.setRole(quote.getRole());
        rfqQuote.setDepartment(quote.getDepartment());
        rfqQuote.setEmail(quote.getEmail());
        rfqQuote.setMobileNo(quote.getMobileNo());
        rfqQuote.setPolicyType(quote.getPolicyType());
        rfqQuote.setTotalEmployees(quote.getTotalEmployees());
        rfqQuote.setStatus(quote.getStatus());
        rfqQuote.setFamilyDefinition(quote.getFamilyDefinition());
        rfqQuote.setPinCode(quote.getPinCode());
        rfqQuote.setCity(quote.getCity());
        rfqQuote.setState(quote.getState());
        rfqQuote.setDate(quote.getDate());
        log.info("Corporate :{}", rfqQuote.getIsCorporate());
        rfqQuote.setIsCorporate(quote.getIsCorporate());
        log.info("Retail :{}", rfqQuote.getIsRetail());
        rfqQuote.setIsRetail(quote.getIsRetail());
        respository.save(rfqQuote);
        return "Created Successfully ";
    }

}
