package com.insure.rfq.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="DeclarationAndClaimSubmission")
public class ClientListSubmitClaims {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long submittedId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequiredDocument documentType;

    @Column(name = "fileName")
    private String fileName;

    @Lob
    @Column(nullable = false)
    private byte[] fileData;

   
    @Column(name = "CREATED_DATE")
    private String createDate;

    @Column(name = "UPDATED_DATE")
    private String updateDate;

    @Column(name = "RECORD_STATUS")
    private String recordStatus;

    @Column(name="EmployeeID")
    private String employeeId;

    @Column(name="USER_ID")
    private String userDetailsId;



}
