package com.insure.rfq.login.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
	private String accessToken;
	private String refreshToken;
	private LocationInfo locationInfo;
	private DepartmentInfo departmentInfo;
	private List<ProductCategory> productCategory;
	private DesignationBasedOperation designationBasedOperation;
	private Long clientListId;
	private Long productId;
	private String employeeId;

	private Boolean isLogin;
	private Boolean isClientLogin;
	private Boolean isBrokerLogin;

	private String clientId;
	private String brokerId;

	private long userId;
	private int loginSession;

	private String firstName;

	private String lastName;

}
