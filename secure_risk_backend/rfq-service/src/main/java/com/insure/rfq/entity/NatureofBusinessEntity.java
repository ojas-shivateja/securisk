package com.insure.rfq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name =  "NatureofBusiness")
public class NatureofBusinessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long natureofBusinessId;
    @Column(name = "nameofNatureofBusiness")
    private String nameofNatureofBusiness;
    @Column(name = "RECORDSTATUS")
    private String recordStatus;

    @Column(name = "CREATEDDATE")
    private String createdDate;

}
