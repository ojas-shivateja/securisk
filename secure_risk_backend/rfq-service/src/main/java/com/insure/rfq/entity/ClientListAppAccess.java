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
@Table(name = "CLIENT_LIST_APP_ACCESS")
public class ClientListAppAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appAccessId;

    private String employeeId;

    private String employeeName;

    private String relationship;

    private String  email;

    private String phoneNumber;

    private String password;

    private String Age;

    private String dateOfBirth;

    private String appAccessStatus;

    private String gender;

    private String sumInsured;

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
