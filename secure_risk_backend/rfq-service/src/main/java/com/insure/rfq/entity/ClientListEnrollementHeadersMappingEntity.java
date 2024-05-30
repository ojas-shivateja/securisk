package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "CLIENT_LIST_ENROLLMENT_HEADERS_MAPPING")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientListEnrollementHeadersMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    private String headerName;

    private String headerAliasName;

    private String sheetName;

    @ManyToOne
    @JoinColumn(referencedColumnName = "tpa_id")
    private Tpa tpaId;


}
