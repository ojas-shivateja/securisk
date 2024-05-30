package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ClientListEmployee_ClaimIntimmation")
public class ClientListEmployee_ClaimIntimmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long clientListEmployee_Claim_Intimmation;

    private String patient_Name;
    private String relationToEmployee;
    private String employeeName;
    private String emailId;
    private String hospital;
    private String doctorName;
    private String uhid;
    private String employeeId;
    private String mobileNumber;
    private String reasonForAdmission;
    private String dateOfHospitalisation;
    private String other_Details;
    private  String  sumInsured;
    private String reasonforClaim;
    @Column(name = "RECORDSTATUS")
    private String recordStatus;
    @Column(name = "CREATEDDATE")
    private String createdDate;
    @Column(name = "UPDATEDDATE")
    private String updatedDate;
    @Column(name = "RFQID")
    private String rfqId;
    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product productId;
    @ManyToOne
    @JoinColumn(referencedColumnName = "cid")
    private ClientList clientListId;
    private String employeeDataID;

}
