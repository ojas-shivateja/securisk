package com.insure.rfq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListEmployee_E_CashlessDisplayDto {

    private String hospitalization;
    private String searchNetworkHospital;
    private String plannedDateOfAdmission;
    private String treatment;
    private String fullNameOfYourTreatingDoctor;
    private String latestInvestigationReportsOfYourDiagnosis;   // fileuploading
    private String lastDoctorConsultationNote;    // fileuploading
    private String patientIdentityNumber;
    private String patientIdentityProof;     // fileuploading
    private String otherMedicalDocuments;    //fileuploading
    private String mobileNumber;
    private String roomType;
    private String planned_DateOfDischarge;
    private String prosedTreatment;
    private String out_PatientNumber;


    private String rfqId;


    private String  productId;

    private String clientListId;

    private String employeeID;
}
