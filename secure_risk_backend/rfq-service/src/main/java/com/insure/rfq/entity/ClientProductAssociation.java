package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"clientId", "productId", "insurerId", "tpaId"}))
public class ClientProductAssociation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clientId", referencedColumnName = "cid")
    private ClientList clientList;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "PRODUCTID")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "insurerId", referencedColumnName = "insurerId")
    private InsureList insurer;

    @ManyToOne
    @JoinColumn(name = "tpaId", referencedColumnName = "tpa_id")
    private Tpa tpa;

    @Column(name = "policyType")
    private String policyType;

    @Column(name = "recordStatus")
    private String recordStatus;
}
