package com.insure.rfq.dto;

import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListEmployee_E_CashlessDto {

    private String hospitalization;
    private String searchNetworkHospital;
    private String plannedDateOfAdmission;
    private String treatment;
    private String fullNameOfYourTreatingDoctor;
    private MultipartFile latestInvestigationReportsOfYourDiagnosis;   // fileuploading
    private MultipartFile lastDoctorConsultationNote;    // fileuploading
    private String patientIdentityNumber;
    private MultipartFile patientIdentityProof;     // fileuploading
    private MultipartFile otherMedicalDocuments;    //fileuploading
    private String mobileNumber;
    private String roomType;
    private String planned_DateOfDischarge;
    private String prosedTreatment;
    private String out_PatientNumber;


    private String rfqId;


    private Product productId;

    private ClientList clientListId;

    private String employeeID;
}
