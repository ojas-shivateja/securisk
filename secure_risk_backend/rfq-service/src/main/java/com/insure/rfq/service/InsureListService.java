package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.GetAllInsurerListDto;
import com.insure.rfq.dto.GetInsuresDropdownDto;
import com.insure.rfq.dto.InsureListDto;
import com.insure.rfq.dto.UpdateInsurerListDto;

public interface InsureListService {
	InsureListDto addListInsure(InsureListDto insureListDto);

	List<GetAllInsurerListDto> getAllInsureListDto();

	int deleteInsureListDto(String id);

	UpdateInsurerListDto updateInsureListDto(String insurerId, UpdateInsurerListDto insureListDto);

	List<String> getAllInsureListDtoName();

	List<GetInsuresDropdownDto> getAllInsuresById();
}
