package com.insure.rfq.controller;


import com.insure.rfq.dto.ClientListNetworkHospitalDataStatus;
import com.insure.rfq.dto.ClientListNetworkHospitalHeaderMappingDto;
import com.insure.rfq.service.ClientListNetworkHospitalUploadService;
import com.insure.rfq.service.ClientListNetworkHospitalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("clientList/networkDetails")
@CrossOrigin("*")
public class ClientListNetworkHospitalController {

    private ClientListNetworkHospitalUploadService clientListNetworkHospitalUploadService;

    private ClientListNetworkHospitalityService ClientListNetworkHospitalityService;

    @Autowired
    public ClientListNetworkHospitalController(ClientListNetworkHospitalUploadService clientListNetworkHospitalUploadService, ClientListNetworkHospitalityService clientListNetworkHospitalityService) {
        this.clientListNetworkHospitalUploadService = clientListNetworkHospitalUploadService;
        this.ClientListNetworkHospitalityService = clientListNetworkHospitalityService;
    }

    @PostMapping("/addHeadersForNetworkHospital")
    public ResponseEntity<?> addHeadersForNetworkHospital(@RequestBody ClientListNetworkHospitalHeaderMappingDto clientListNetworkHospitalUploadDto) {
        return new ResponseEntity<>(clientListNetworkHospitalUploadService.uploadNetworkHospital(clientListNetworkHospitalUploadDto), HttpStatus.CREATED);
    }

    @PostMapping("/headerValidationForHospital")
    public ResponseEntity<?> headerValidationEnrollment(@ModelAttribute MultipartFile multipartFile, @RequestParam String tpaName) throws IOException {
        return ResponseEntity.ok(ClientListNetworkHospitalityService.validateHeadersBasedOnTpa(multipartFile, tpaName));
    }

    @PostMapping("/getNetworkHospitalWithStatus")
    public ResponseEntity<?> getNetworkHospitalWithStatus(@RequestPart MultipartFile multipartFile, @RequestParam String tpaName) throws IOException {
        return ResponseEntity.ok(ClientListNetworkHospitalityService.validateValuesBasedOnTpa(multipartFile, tpaName));
    }

    @PostMapping("/uploadNetWorkHospital")
    public ResponseEntity<?> uploadNetWorkHospital(@RequestBody List<ClientListNetworkHospitalDataStatus> clientListMemberDetailsDataStatuses, @RequestParam Long clientListId, @RequestParam Long productId) {
        return new ResponseEntity<>(ClientListNetworkHospitalityService.uploadNetworkHospitalData(clientListMemberDetailsDataStatuses, clientListId, productId), HttpStatus.CREATED);
    }

    @GetMapping("/getAllClientListNetWorkHospital")
    public ResponseEntity<?> getAllClientListNetWorkHospitalData(@RequestParam Long clientListId, @RequestParam Long productId) {
        return ResponseEntity.ok(ClientListNetworkHospitalityService.getAllclientListEnrollmentData(clientListId, productId));
    }

    @GetMapping("/getAllNetworkHospitalListInExcel")
    public ResponseEntity<byte[]> getAllNetworkHospitalListInExcel(@RequestParam Long clientListId,
                                                                   @RequestParam Long productId) {

        byte[] networkHospitalInExcelFormat = ClientListNetworkHospitalityService.getNetworkHospitalInExcelFormat(clientListId, productId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "members_details_additionList.xlsx");

        return new ResponseEntity<>(networkHospitalInExcelFormat, headers, HttpStatus.OK);


    }
}


