package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ClientList_MyDetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyDetailsEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mydetailId;

   @Column(name = "DetailName")
    private String detailName;

   @Column(name = "FileName")
    private String fileName;

   @Column(name = "Rfq_Id")
    private String rfqId;


    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(referencedColumnName = "cid")
    private ClientList clientList;

    @Column(name = "CREATED_DATE")
    private String createDate;

    @Column(name = "UPDATED_DATE")
    private String updateDate;

    @Column(name = "RECORD_STATUS")
    private String recordStatus;

}
