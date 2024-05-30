package com.insure.rfq.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.ClientListAppAccessStatusDto;
import com.insure.rfq.dto.ClientLoginDto;
import com.insure.rfq.dto.GetAllAppAccessDto;
import com.insure.rfq.dto.UpdateAppAccessDto;

public interface ClientListAppAccessService {

    String uploadAppAccessExcel(MultipartFile multipartFile,Long clientListId, Long productId) throws IOException;

    boolean sendLoginCredentials(List<String> employeeEmails);

    List<GetAllAppAccessDto> getAllAppAccessDto(Long clientListId, Long productId);

    GetAllAppAccessDto getAllAppAccessDtoById(Long appAccessId);

    UpdateAppAccessDto updateAppAccessDtoById(Long appAccessId, UpdateAppAccessDto updateAppAccessDto);

    ClientLoginDto authenticate(String username, String password);
    
    String changeAppAccessStatus(List<ClientListAppAccessStatusDto> clientListAppAccessStatusDto);

    String deleteAppAccessById(Long appAccessId);
    String clearAllAppAccess();
    
    byte[] downloadAppAccessTemplate() throws IOException;

}
