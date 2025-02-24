package com.insure.rfq.controller;

import com.insure.rfq.dto.RfqQuoteDto;
import com.insure.rfq.service.RfqQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/RfqQuote")
public class RfqQuoteController {

    @Autowired
    private RfqQuoteService quoteServiceImpl;

    @PostMapping("/createRfqQuote")
    public ResponseEntity<String> createRfqQuote(@RequestBody RfqQuoteDto quoteDto) {
        String createRfqQuote = quoteServiceImpl.createRfqQuote(quoteDto);
        return new ResponseEntity<>(createRfqQuote, HttpStatus.CREATED);
    }

}
