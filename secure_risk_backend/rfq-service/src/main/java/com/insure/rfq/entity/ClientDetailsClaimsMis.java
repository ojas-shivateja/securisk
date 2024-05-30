package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Entity
@Table(name = "Client_Details_Claims_Mis")
@Data
public class ClientDetailsClaimsMis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "CLAIMSID")
    private Long claimsId;

    @Column(name = "RFQID")
    private String rfqId;
    @Column(name = "POLICYNUMBER", length = 1500)
    private String PolicyNumber;
    @Column(name = "CLAIMSNUMBER", length = 1500)
    private String claimsNumber;
    @Column(name = "EMPLOYEEID", length = 1500)
    private String employeeId;
    @Column(name = "EMPLOYEENAME", length = 1500)
    private String employeeName;
    @Column(name = "RELATIONSHIP", length = 1500)
    private String relationship;
    @Column(name = "GENDER", length = 1500)
    private String gender;
    @Column(name = "AGE")
    private int age;
    @Column(name = "PATIENTNAME", length = 1500)
    private String patientName;
    @Column(name = "SUMINSURED")
    private double sumInsured;
    @Column(name = "CLAIMEDAMOUNT")
    private double claimedAmount;
    @Column(name = "PAIDAMOUNT")
    private double paidAmount;
    @Column(name = "OUTSTANDINGAMOUNT")
    private double outstandingAmount;
    @Column(name = "CLAIMSTATUS", length = 1500)
    private String claimStatus;
    @Column(name = "DATEOFCLAIM")
    private Date dateOfClaim;
    @Column(name = "CLAIMTYPE", length = 1500)
    private String claimType;
    @Column(name = "NETWORKTYPE", length = 1500)
    private String networkType;
    @Column(name = "HOSPITALNAME", length = 1500)
    private String hospitalName;
    @Column(name = "ADMISSIONDATE")
    private Date admissionDate;
    @Column(name = "DISEASE", length = 1500)
    private String disease;
    @Column(name = "DATEOFDISCHARGE")
    private Date dateOfDischarge;
    @Column(name = "MEMBERCODE", length = 1500)
    private String memberCode;
    @Column(name = "POLICYSTARTDATE")
    private Date policyStartDate;
    @Column(name = "POLICYENDDATE")
    private Date policyEndDate;
    @Column(name = "HOSPITALSTATE", length = 1500)
    private String hospitalState;
    @Column(name = "HOSPITALCITY", length = 1500)
    private String hospitalCity;

    @Column(name = "CREATEDDATE")
    private Date createdDate;
    @Column(name = "UPDATEDDATE")
    private Date updatedDate;
    @Column(name = "RECORDSTATUS")

    private String recordStatus;
    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(referencedColumnName ="cid" )
    private ClientList clientList;
}
