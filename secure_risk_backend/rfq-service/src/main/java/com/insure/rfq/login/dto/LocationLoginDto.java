package com.insure.rfq.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationLoginDto {

	private Long locationId;
	private String sno;
	private String locationName;
	private List<UsersNewDtoGet> usersNewId;

}
