package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ClientList_ECardDetails")
public class ECardEntity {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long eCardId;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "RFQ_ID")
    private String rfqId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(referencedColumnName = "cid")
    private  ClientList clientList;

    @Column(name = "CREATED_DATE")
    private String createDate;

    @Column(name = "UPDATED_DATE")
    private String updateDate;

    @Column(name = "RECORD_STATUS")
    private String recordStatus;
    
    @Column(name="EmployeeID")
    private String employeeId;



}
