package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "CD_BALANCE_HEADERS_MAPPING")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cd_balanceHeadersMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    private String headerName;

    private String headerAliasName;

    private String sheetName;

    private  Long sheetId;



}
