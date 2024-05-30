package com.insure.rfq.service;

import java.util.List;

import com.insure.rfq.dto.DisplayAllMyDetailsDto;
import com.insure.rfq.dto.MyDetailsDto;
import com.insure.rfq.dto.UpdateMyDetailsDto;
import com.insure.rfq.entity.MyDetailsEntity;

public interface MyDetailsService {

	MyDetailsDto createMyDetails(MyDetailsDto myDetailsDto, Long clientListId, Long productId);

	List<DisplayAllMyDetailsDto> getAllMyDetail(Long clientListId, Long productId);

	DisplayAllMyDetailsDto getById(Long mydetailId);

	public String updateMyDetailsById(UpdateMyDetailsDto updateMyDetailsDto, Long Id);

	String deleteMyDetailsById(Long id);

	String getFileExtension(String filePath);

	List<MyDetailsEntity> getAllMyDetails(Long clientListId, Long productId);

	byte[] getFileDataById(Long mydetailId);

}
