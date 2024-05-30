package com.insure.rfq.service;

import java.io.IOException;
import java.util.List;

import com.insure.rfq.dto.*;
import com.insure.rfq.entity.CorporateDetailsEntity;

public interface CorporateDetailsService {

	String createRFQ(CorporateDetailsDto dto);

	UpdateCorporateDetailsDto updateRFQ(String rfqIdValue, UpdateCorporateDetailsDto dto);

	RFQCompleteDetailsDto getRfqById(String rfqId);

	String submitRfq(String rfqId, CorporateDetailsDto corpoarCorporateDetailsDto);

	List<AllRFQDetailsDto> getAllRFQs();

	void sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentFileName);

	String sendEmailWithAttachments(InsureListDto insureListDto);

	public byte[] getEmployeeData(String id);

	public byte[] getIrdaData(String rfqId) throws IOException;
	
	DashBoardDto getCooperateDetailsInfoBasedOn_Month_Year(String month,int year);
	
	List<GetRfqCountWithLocationDto> getApplicationStatusCountByLocation(Long locationId);

}
