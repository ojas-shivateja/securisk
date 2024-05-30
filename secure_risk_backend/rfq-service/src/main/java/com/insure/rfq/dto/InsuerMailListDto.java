package com.insure.rfq.dto;

import java.util.List;

import com.insure.rfq.entity.InsureUsers;

import lombok.Data;

@Data
public class InsuerMailListDto {
	private String insurerName;
	private String location;
	private String managerName;
	private List<InsureUsers> usersList;
	private String email;
}
