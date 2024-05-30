package com.insure.rfq.entity;

import com.insure.rfq.login.entity.Department;
import com.insure.rfq.login.entity.Designation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CLIENT_LIST_MEMBER_DETAILS")
public class ClientListMemberDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String employeeNo;

    private String name;

    private String relationShip;

    private String gender;

    private String dateOfBirth;

    private Double age;

    private Double sumInsured;

    private String email;

    private String phoneNumber;

    private String updatedStatus;

    private String deletedStatus;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Designation designation;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Department department;

    private String role;

    private String rfqId;

    private String createdDate;

    private String updatedDate;

    private String recordStatus;

    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(referencedColumnName ="cid" )
    private ClientList clientList;

}
