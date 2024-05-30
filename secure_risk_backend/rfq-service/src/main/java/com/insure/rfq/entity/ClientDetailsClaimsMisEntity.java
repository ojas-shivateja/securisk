package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "ClaimsMisEntityClientDetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDetailsClaimsMisEntity {


    @Id
    //@SequenceGenerator(name = "mySeq",sequenceName = "seq", allocationSize = 1)
    @GeneratedValue(generator = "mySeq", strategy = GenerationType.SEQUENCE)
    private Long claimsId;

    @Column(name = "CLAIMSMISC_FILEPATH")
    String claimsMiscFilePath;

    @Column(name = "CLAIMSTATUS", length = 1500)
    private String claimStatus;

    @Column(name = "RFQ_ID")
    private String rfqId;

    @Column(name = "CREATED_DATE")
    private Date createDate;

    @Column(name = "UPDATED_DATE")
    private Date updateDate;

    @Column(name = "RECORD_STATUS")
    private String recordStatus;

}
