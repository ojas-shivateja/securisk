package com.insure.rfq.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllInsurerListDto {

	private String insurerId;
	private String insurerName;
	private String location;
	private List<GetAllUsersByInsurerIdDto> listOfUsers;
}
