package com.insure.rfq.entity;

import com.insure.rfq.login.entity.Designation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="CLIENT_LIST_CONTACT_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListContactDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    private String employeeId;

    private String name;

    private String email;

    private String phoneNumber;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Designation designation;

    @ManyToOne
    @JoinColumn(referencedColumnName = "roleId")
    private ClientListRoleInfo RoleInfo;

    private String recordStatus;

    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(referencedColumnName = "cid")
    private ClientList clientList;

    private String rfqId;

    private String createdDate;

    private String updatedDate;
}
