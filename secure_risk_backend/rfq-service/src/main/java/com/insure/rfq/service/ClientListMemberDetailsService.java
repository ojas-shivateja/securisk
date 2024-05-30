package com.insure.rfq.service;

import com.insure.rfq.dto.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ClientListMemberDetailsService {

    ResponseDto createClientListMembersDetails(Long clientListId, Long productId, AddClientListMemberDetailsDto clientListMemberDetailsDto);

    List<GetAllClientListMembersDetailsDto> getAllClientListMembersDetails(Long clientListId, Long productId,String month);

    byte[] getActiveListInExcelFormat(Long clientListId,Long productId);

    byte[] getAdditionListInExcelFormat(Long clientListId,Long productId);

    byte[] getDeletedListInExcelFormat(Long clientListId,Long productId);

    byte[] getCorrectionListInExcelFormat(Long clientListId,Long productId);

    byte[] getEnrollmentListInExcelFormat(Long clientListId,Long productId);

    byte[] getPendingListInExcelFormat(Long clientListId,Long productId);

    AddClientListMemberDetailsDto updateClientListMemberDetails(Long memberDetailsId,AddClientListMemberDetailsDto clientListMemberDetailsDto);

    String deleteClientListMemberDetails(Long memberDetailsId);

    AddClientListMemberDetailsDto getClaimsMemberDetailsByClaimsMemberId(Long memberDetailsId);

    List<GetClientListMemberDetailsActiveListCountDto> getAllActiveListForMemberDetailsByClientListProduct(Long clientListId, Long productId,String month);

    List<GetAllClientListMembersDetailsDto> getAllAdditionListForMemberDetailsByClientListProduct(Long clientListId, Long productId,String month);

    List<GetAllClientListMembersDetailsDto> getAllDeletedListForMemberDetailsByClientListProduct(Long clientListId, Long productId,String month);

    List<GetAllClientListMembersDetailsDto> getAllCorrectionsListForMemberDetailsByClientListProduct(Long clientListId, Long productId,String month);

    byte[] getActiveListTemplate() throws IOException;

    byte[] getAdditionListTemplate() throws IOException;

    byte[] getDeleteListTemplate() throws IOException;

    byte[] getCorrectionListTemplate() throws IOException;

    byte[] getEnrollementListTemplate() throws IOException;

    byte[] getPendingListTemplate() throws IOException;

//    String uploadExcelFile(ClientListEnrollmentUploadDto clientListEnrollmentUploadDto,Long clientListId,Long productId);

    ClientListEnrollmentHeaderDto validateHeadersBasedOnTpa(MultipartFile multipartFile,String tpaName) throws IOException;

    List<ClientListMemberDetailsDataStatus> validateValuesBasedOnTpa(MultipartFile multipartFile,String tpaName) throws IOException;

    void validateBasedOnSheet(Sheet sheet, String tpaName,List<ClientListMemberDetailsDataStatus> claimsMisValidateData);

    String uploadEnrollmentData(List<ClientListMemberDetailsDataStatus> clientListMemberDetailsDataStatuses , Long clientListId , Long productId);

    List<GetAllClientListEnrollmentDto> getAllclientListEnrollmentData(Long productId,Long clientListId,String month);
    
    List<GetAllClientListPendingListDto> getAllClientPendingListData(Long productId,Long clientListId);
    
    List<GetAllClientListMembersDetailsDto> getAllClientListMembersDetailsForEmployee(Long clientListId, Long productId,String employeeId);

    byte[] downloadMembersDetailsForEmployeeInExcelFormat(Long clientListId,Long productId,String employeeId);
    
    String validateEmployeeDetails(List<CoverageDetailsChildValidateValuesDto>  coverageDetailsChildValidateValuesDtos, Long clientListId,Long productId);

  
}
