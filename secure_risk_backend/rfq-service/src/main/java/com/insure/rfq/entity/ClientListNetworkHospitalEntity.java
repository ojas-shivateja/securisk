package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Client_List_Network_Hospital_Entity")
public class ClientListNetworkHospitalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long hospitalId;

    @Column(name = "HOSPITALNAME")
    private String hospitalName;

    @Column(name = "ADDRESS",length = 1500)
    private String Address;

    @Column(name = "CITY",length = 1500)
    private String city;

    @Column(name = "STATE",length = 1500)
    private String state;

    @Column(name = "PINCODE")
    private Long pinCode;

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
