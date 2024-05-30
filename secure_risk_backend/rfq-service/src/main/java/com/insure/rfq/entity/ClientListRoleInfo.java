package com.insure.rfq.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "CLIENT_LIST_ROLE_INFO")
@AllArgsConstructor
@NoArgsConstructor
public class ClientListRoleInfo {

    @Id
    private Long roleId;

    private String role;
}
