package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ClientListInsurerBankDetails")
public class InsurerBankDetails {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long  insurerId;

    @Column(name="bankName")
    private String bankName;
    @Column(name="branch")
    private String branch;
    @Column(name="location")
    private String location;
    @Column(name="ifscCode")
    private String ifscCode;
    @Column(name="accountNumber")
    private Long accountNumber;
    @Column(name="accountHolderNumber")
    private String accountHolderNumber;

    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product product;


    @Column(name = "RFQ_ID")
    private String rfqId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "cid")
    private  ClientList clientList;

    @Column(name = "CREATED_DATE")
    private String createDate;

    @Column(name = "UPDATED_DATE")
    private String updateDate;

    @Column(name = "RECORD_STATUS")
    private String recordStatus;
}
