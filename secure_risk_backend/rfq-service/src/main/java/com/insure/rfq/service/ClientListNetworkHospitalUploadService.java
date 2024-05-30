package com.insure.rfq.service;

import com.insure.rfq.dto.ClientListNetworkHospitalDataStatus;
import com.insure.rfq.dto.ClientListNetworkHospitalHeaderMappingDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ClientListNetworkHospitalUploadService {

    ClientListNetworkHospitalHeaderMappingDto uploadNetworkHospital(ClientListNetworkHospitalHeaderMappingDto clientListNetworkHospitalHeaderMappingDto);




}
