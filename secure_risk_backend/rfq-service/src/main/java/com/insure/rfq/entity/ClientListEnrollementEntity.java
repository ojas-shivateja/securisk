package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CLIENT_LIST_ENROLLMENT_ENTITY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientListEnrollementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    private String employeeId;

    private String employeeName;

    private String dateOfBirth;

    private String gender;

    private String relation;

    private double age;

    private String dateOfJoining;

    private String eCardNumber;

    private String policyCommencementDate;

    private String policyValidUpTo;

    private String baseSumInsured;

    private String topUpSumInsured;

    private String groupName;

    private String insuredCompanyName;

    @ManyToOne
    @JoinColumn(referencedColumnName = "cid")
    private ClientList clientList;

    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product product;

    private String rfqId;

    private String createdDate;

    private String updatedDate;

    private String recordStatus;



}
