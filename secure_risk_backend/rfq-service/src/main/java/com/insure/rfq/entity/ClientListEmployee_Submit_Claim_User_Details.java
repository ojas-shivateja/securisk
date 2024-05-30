package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name =  "ClientListEmployee_Submit_Claim_User_Details")
public class ClientListEmployee_Submit_Claim_User_Details {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private  Long ClientListEmployee_Submit_Claim_User_DetailsId;


    private String user_detailsId;

    private String patientName;
    private String employeeName;
    private String uhid;
    private String dateOfAdmission;
    private String dateOfDischarge;
    private String employeeId;
    private String email;
    private String mobileNumber;
    private String sumInsured;
    private String benificiaryName;
    private String RelationToEmployee;
    private String claimNumber;
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
    private String employeeDataID;  //fetch the data form employee table
    @Column(name = "CREATEDDATE")
    private String createdDate;
    @Column(name = "UPDATEDDATE")
    private String updatedDate;

}
