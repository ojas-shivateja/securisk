package com.insure.rfq.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name =  "Submit_ClaimHospitalizationDetails")
@NoArgsConstructor
@AllArgsConstructor
public class ClientListEmployee_Submit_ClaimHospitalizationDetails {
    //Hospitalization Details
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private  Long ClientListEmployee_ClaimHospitalizationDetails;

    private String user_detailsId;
    private String state;
    private String city;
    private String hospitalName;
    private String hospitalAddress;
    private String natureofIllness;
    private String preHospitalizationAmount ;
    private String postHospitalizationAmount ;
    private String totalAmountClaimed ;
    private String hospitalizationAmount;
//Medical Expenses Details


    private List<String> medicalExpenses_BillNo;

    private List<Double> medicalExpensesBillAmount;
    private List<String> medicalExpenses_BillDate;

    private List<String> medicalExpensesRemarks;

    @Column(name = "RECORDSTATUS")
    private String recordStatus;
    @Column(name = "RFQID")
    private String rfqId;
    @ManyToOne
    @JoinColumn(referencedColumnName = "productId")
    private Product productId;
    @ManyToOne
    @JoinColumn(referencedColumnName = "cid")
    private ClientList clientListId;
    private String employeeDataID;  //fetch the data form employee table
    @Column(name = "CREATEDDATE")
    private String createdDate;
    @Column(name = "UPDATEDDATE")
    private String updatedDate;

}
