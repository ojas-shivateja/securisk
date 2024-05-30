package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Client_Policy")
public class PolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "POLICYNAME")
    private String policyName;

    @Column(name = "POLICYNUMBER")
    private String policyNumber;

    @Column(name = "POLICYSTARTDATE")
    private String policyStartDate;

    @Column(name = "POLICYENDDATE")
    private String policyEndDate;

    @Column(name = "POLICYCOPYPATH")
    private String policyCopyPath;
    
    @Column(name="policyType")
    private String policyType;

    @Column(name = "PPTPATH")
    private String PPTPath;

    // --------- PolicyPresentation
    @Column(name = "INSURANCEBROKER")
    private String insuranceBroker;

    @Column(name = "INSURANCECOMPANY")
    private String insuranceCompany;

    @Column(name = "NAMEOFTHETPA")
    private String nameOfTheTPA;

    @Column(name = "INCEPTION_PREMIUM")
    private Double inception_Premium;

    @Column(name = "TILLDATEPREMIUM")
    private String tillDatePremium;

    @Column(name = "FAMILYDEFINATION")
    private List<String> familyDefination;

    @Column(name = "SUMINSURED")
    private List<Double> sumInsured;

    @Column(name = "RECORDSTATUS")
    private String recordStatus;
    // _________________ ID's

    @Column(name = "RFQID")
    private String rfqId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product productId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "cid")
    private ClientList clientListId;

    @Column(name = "CREATEDDATE")
    private String createdDate;
    @Column(name = "UPDATEDDATE")
    private String updatedDate;
}