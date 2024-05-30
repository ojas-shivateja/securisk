package com.insure.rfq.service.impl;

import com.insure.rfq.dto.DisplayAllInsurerDetails;
import com.insure.rfq.dto.InsurerBankDetailsDto;
import com.insure.rfq.dto.ResponseDto;
import com.insure.rfq.dto.UpdateInsurerBankDetailsDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.InsurerBankDetails;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvaildEndorsementException;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.InsurerBankDetailsRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.InsurerBankDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class InsurerBankDetailsServiceImpl implements InsurerBankDetailsService {


    @Autowired

    private InsurerBankDetailsRepository insurerBankDetailsRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ClientListRepository clientListRepository;
    @Override
    public ResponseDto createInsurerBank(InsurerBankDetailsDto insurerBankDetailsDto, Long clientListId, Long productId)

    {
        StringBuilder errorMessages=new StringBuilder();
        InsurerBankDetails insurerBankDetails=new InsurerBankDetails();
        insurerBankDetails.setBankName(insurerBankDetailsDto.getBankName());
        insurerBankDetails.setBranch(insurerBankDetailsDto.getBranch());
        insurerBankDetails.setIfscCode(insurerBankDetailsDto.getIfscCode());
        insurerBankDetails.setLocation(insurerBankDetailsDto.getLocation());
        insurerBankDetails.setAccountNumber(insurerBankDetailsDto.getAccountNumber());
        insurerBankDetails.setAccountHolderNumber(insurerBankDetailsDto.getAccountHolderNumber());

        if (clientListId!=null) {
            ClientList clientList = clientListRepository.findById(clientListId).orElseThrow(() -> new InvalidClientList("ClientList Not Found"));
            insurerBankDetails.setRfqId(clientList.getRfqId());
        }
        if (clientListId != null) {
            try {
                ClientList clientList = clientListRepository.findById(clientListId)
                        .orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
                insurerBankDetails.setClientList(clientList);
            } catch (InvalidClientList e) {
            }
        }
        if (productId != null) {
            try {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new InvalidProduct("Product is not Found"));
                insurerBankDetails.setProduct(product);
            } catch (InvalidProduct e) {
            }
        }
        insurerBankDetails.setCreateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        insurerBankDetails.setRecordStatus("ACTIVE");
        if(errorMessages.length()==0) {
            insurerBankDetailsRepository.save(insurerBankDetails);
        }

        if (errorMessages.length() >  0) {
            return new ResponseDto(errorMessages.toString());
        }

        return new ResponseDto("Created Sucessfully");
    }

    @Override
    public List<DisplayAllInsurerDetails> getAllInsurerDetails(long clientlistId, long productId) {
        List<DisplayAllInsurerDetails> result= insurerBankDetailsRepository
                .findAll().stream().filter(i->i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
                .filter(i->clientlistId!=0  &&  i.getClientList().getCid()==clientlistId).
                filter(i->productId!=0  && i.getProduct().getProductId().equals(productId)).map(i->{

                    DisplayAllInsurerDetails displayAllInsurerDetails= new DisplayAllInsurerDetails();
                    displayAllInsurerDetails.setInsurerId(i.getInsurerId());
                    displayAllInsurerDetails.setBranch(i.getBranch());
                    displayAllInsurerDetails.setBankName(i.getBankName());
                    displayAllInsurerDetails.setLocation(i.getLocation());
                    displayAllInsurerDetails.setAccountNumber(i.getAccountNumber());
                    displayAllInsurerDetails.setAccountHolderNumber(i.getAccountHolderNumber());
                    displayAllInsurerDetails.setIfscCode(i.getIfscCode());

                    return displayAllInsurerDetails;
                }).toList();

        if (result.isEmpty()) {
            String errorMessage = "No insurer data found for clientlistId=" + clientlistId + " and productId=" + productId;
            throw new InvalidClientList(errorMessage);
        }

        return result;
    }

    @Override
    public String deleteInsurer(Long id) {


        InsurerBankDetails entity = insurerBankDetailsRepository.findById(id).orElseThrow(() -> new InvaildEndorsementException("Not Found"));
        entity.setRecordStatus("IN ACTIVE");

        InsurerBankDetails insurerBankDetails = insurerBankDetailsRepository.save(entity);
        String message = "";
        if (insurerBankDetails != null) {
            message = "delete successful";
        }

        return message;
    }

    public byte[] generateExcelFromData(Long clientListId, Long productId) {
        List<InsurerBankDetails> insurerBankDetails = insurerBankDetailsRepository.findAll().
                stream().filter(filter -> filter.getRecordStatus().equalsIgnoreCase("ACTIVE"))
                .filter(client -> clientListId != null && client.getClientList().getCid()
                       == clientListId)
                .filter(c -> productId != null && c.getProduct().getProductId().equals(productId)).toList();
        log.info("Service     : " +clientListId + " -----------" + productId);


        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("InsurerBankDetails");

            String[] headers = {"AccountHolderNumber", "AccountNumber", "IfscCode", "Branch","location"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (InsurerBankDetails cpfc : insurerBankDetails) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(cpfc.getAccountHolderNumber());
                row.createCell(1).setCellValue(cpfc.getAccountNumber());
                row.createCell(2).setCellValue(cpfc.getIfscCode());
                row.createCell(3).setCellValue(cpfc.getBranch());
                row.createCell(4).setCellValue(cpfc.getLocation());


            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public String updateInsurerById(UpdateInsurerBankDetailsDto dto, Long id) {
        InsurerBankDetails entity = insurerBankDetailsRepository.findById(id).orElseThrow(() -> new InvaildEndorsementException("not found"));

entity.setBankName(dto.getBankName());
entity.setBranch(dto.getBranch());
entity.setLocation(dto.getLocation());
entity.setIfscCode(dto.getIfscCode());
entity.setAccountNumber(dto.getAccountNumber());
entity.setAccountHolderNumber(dto.getAccountHolderNumber());
        entity.setUpdateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        InsurerBankDetails endorsementEntity = insurerBankDetailsRepository.save(entity);
        String message = "";
        if (endorsementEntity != null) {
            message = "updated successful";
        }
        return message;
    }

    @Override


    public DisplayAllInsurerDetails getById(Long insurerId) {

        InsurerBankDetails e = insurerBankDetailsRepository.findById(insurerId).orElseThrow(() -> new InvaildEndorsementException("Invalid Endorsement"));

        DisplayAllInsurerDetails displayAllinsurerIdDto = new DisplayAllInsurerDetails();
        displayAllinsurerIdDto.setInsurerId(e.getInsurerId());
        displayAllinsurerIdDto.setBankName(e.getBankName());
        displayAllinsurerIdDto.setBranch(e.getBranch());
        displayAllinsurerIdDto.setAccountNumber(e.getAccountNumber());
        displayAllinsurerIdDto.setAccountHolderNumber(e.getAccountHolderNumber());
        displayAllinsurerIdDto.setIfscCode(e.getIfscCode());
        displayAllinsurerIdDto.setLocation(e.getLocation());



        return displayAllinsurerIdDto;

    }

}
