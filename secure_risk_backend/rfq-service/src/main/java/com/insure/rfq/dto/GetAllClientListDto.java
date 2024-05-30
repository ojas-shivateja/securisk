package com.insure.rfq.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class GetAllClientListDto {

	private Long clientId;
	private int sNO;
	private String clientName;
	private List<GetAllProductsByClientListIdDto> listOfProducts;
	private List<GetAllClientListUserByClientListIdDto> listOfUsers;
	private String location;
	private String status;
}
