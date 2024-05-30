package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CLIENT_LIST_DECLARATION_SUBMISSION_IMPORTANT_DOCS")
public class ClientList_Submission_ImportantNotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long declaration_submission_imported_Documents_Id;

    private String user_detailsId;
    private String declaration_submisssion_Id;


    private String proof_id_Type; // select the type of the Account;
    private String id_proof;           // select the proof NAME WE HAVE TO GIVE
    private String id_proofUpload;//  we have to upload the proof

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
    @Column(name = "RECORD_STATUS")
    private String recordStatus;

    private  String employeeDataID;

    @Column(name = "UPDATEDDATE")
    private String updatedDate;

}
