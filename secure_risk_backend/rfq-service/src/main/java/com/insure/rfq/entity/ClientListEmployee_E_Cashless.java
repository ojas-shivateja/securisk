package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ClientLis_tEmployee_E_Cashless")
public class ClientListEmployee_E_Cashless {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long clientListEmployee_E_Cashless_id;

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
    @Column(name = "RECORDSTATUS")
    private String recordStatus;
    @Column(name = "RFQID")
    private String rfqId;
    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product productId;
    @ManyToOne
    @JoinColumn(referencedColumnName = "cid")
    private ClientList clientListId;
    private String employeeID;
    @Column(name = "CREATEDDATE")
    private String createdDate;
    @Column(name = "UPDATEDDATE")
    private String updatedDate;
}
