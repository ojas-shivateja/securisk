package com.insure.rfq.controller;

import com.insure.rfq.dto.ClaimDetailsDto;
import com.insure.rfq.dto.ClaimsDumpDto;
import com.insure.rfq.entity.ClaimsDetails;
import com.insure.rfq.repository.ClaimsDetailsRepository;
import com.insure.rfq.service.ClaimDetailsService;
import com.insure.rfq.service.ClaimsMisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rfq/claimDetails")
@CrossOrigin(origins = "*")
public class ClaimsDetailsController {

    @Autowired
    private ClaimDetailsService claimDetailsService;
    @Autowired
    private ClaimsDetailsRepository repo;
    @Autowired
    private ClaimsMisService claimsMisService;


    @PostMapping("/createClaimDetails")
    public ResponseEntity<String> createClaimDetails(@RequestBody ClaimDetailsDto claimDetailsDto) {
        if (claimDetailsDto != null) {
            String rfqId = claimDetailsService.createClaimDetails(claimDetailsDto);
            return new ResponseEntity<>(rfqId, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("No Details Found", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/claim")
    public List<ClaimsDetails> getAll() {
        return repo.findAll();
    }

    @PutMapping("/updateClaimDetails/{id}")
    public ResponseEntity<ClaimsDetails> updateCliamsDetails(@RequestBody ClaimDetailsDto claimDetailsDto,
                                                             @PathVariable String id) {
        return ResponseEntity.ok(claimDetailsService.updateClaimDetails(claimDetailsDto, id));
    }

    @GetMapping("/getClaimDetailsById/{rfqId}")
    public ResponseEntity<ClaimsDetails> getClaimDetailsById(@PathVariable String rfqId) {
        return new ResponseEntity<>(repo.findByrfqId(rfqId).get(), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/getClaimsDump/{rfqId}")
    public ResponseEntity<?> getAllClaimsDump(@PathVariable String rfqId) {
        ClaimsDumpDto claimsDump = claimsMisService.getClaimsDump(rfqId);
        return ResponseEntity.ok(claimsDump);
    }

}


