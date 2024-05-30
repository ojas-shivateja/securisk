package com.insure.rfq.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.insure.rfq.dto.ClientList_Life_PremiumCalcuaterDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientList_Per_Life_Premium_Calculator;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.exception.InvalidUser;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ClientList_Life_PremiumCalcuate_repository;
import com.insure.rfq.repository.CorporateDetailsRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ClientList_Life_PremiumCalcuaterService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientList_Life_PremiumCalcuaterServiceimpl implements ClientList_Life_PremiumCalcuaterService {

	@Autowired
	private ClientList_Life_PremiumCalcuate_repository liffecalcuaterRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private CorporateDetailsRepository corporateDetailsRepository;

	@Override
	public String deleteClientListLifePremiumCalcuaterDto(Long primary_Id) {
		ClientList_Per_Life_Premium_Calculator entitypremiumCalcuater = liffecalcuaterRepository.findById(primary_Id)
				.orElseThrow(() -> new InvalidUser("Id is not found"));
		String message = "";
		if (entitypremiumCalcuater != null) {
			entitypremiumCalcuater.setRecordStatus("INACTIVE");
			ClientList_Per_Life_Premium_Calculator entitysaved = liffecalcuaterRepository.save(entitypremiumCalcuater);
			if (entitysaved != null)
				message = "Data is Deleted";
		} else
			message = "Id is not found";

		return message;
	}

	// Get by ID
	@Override
	public ClientList_Life_PremiumCalcuaterDto getClientList_LifePremiumCalcuaterDto(Long primary_Id) {
		List<ClientList_Per_Life_Premium_Calculator> active = liffecalcuaterRepository.findAll().stream()
				.filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE")
						&& i.getLife_premiumCalcuater_id().equals(primary_Id))
				.toList();

		if (!active.isEmpty()) {
			return active.stream().map(i -> {
				ClientList_Life_PremiumCalcuaterDto dto = new ClientList_Life_PremiumCalcuaterDto();
				dto.setLife_premiumCalcuater_id(i.getLife_premiumCalcuater_id());
				dto.setAgeBandEnd(i.getAgeBandEnd());
				dto.setAgeBandStart(i.getAgeBandStart());
				dto.setSumInsured(i.getSumInsured());
				dto.setBasePremium(i.getBasePremium());
				dto.setRfqId(i.getRfqId());
				dto.setClientListId(String.valueOf(i.getClientListId().getCid()));
				dto.setProductId(String.valueOf(i.getProductId().getProductId()));
				return dto;
			}).findFirst().orElse(null); // Assuming there should be only one matching record
		} else {
			return null; // Or handle the case where no matching record is found
		}
	}

	// Create

	@Override
	public String createClientList_Life_PremiumCalcuater(ClientList_Life_PremiumCalcuaterDto preLifePremium_Calcuater,
			Long clientID, Long produtId) {
		// TODO Auto-generated method stub
		String message = "";
		ClientList_Per_Life_Premium_Calculator entityCalcuater = new ClientList_Per_Life_Premium_Calculator();
		entityCalcuater.setAgeBandStart(preLifePremium_Calcuater.getAgeBandStart());
		entityCalcuater.setAgeBandEnd(preLifePremium_Calcuater.getAgeBandEnd());
		entityCalcuater.setBasePremium(preLifePremium_Calcuater.getBasePremium());
		entityCalcuater.setSumInsured(preLifePremium_Calcuater.getSumInsured());
		entityCalcuater.setRecordStatus("ACTIVE");
		entityCalcuater.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		if (produtId != null) {
			Product product1 = productRepository.findById(produtId)
					.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
			entityCalcuater.setProductId(product1);
		}
		if (clientID != null) {
			ClientList clientList = clientListRepository.findById(clientID)
					.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
			entityCalcuater.setClientListId(clientList);
			entityCalcuater.setRfqId(clientList.getRfqId());
		}

		ClientList_Per_Life_Premium_Calculator premium_Calcuater = liffecalcuaterRepository.save(entityCalcuater);
		if (premium_Calcuater != null)
			message = "ClientList_Pre_Life_Premium_Calcuater is Saved Successfully";
		else
			message = "ClientList_Pre_Life_Premium_Calcuater is not Saved";
		return message;
	}

	// Get All Premiums
	@Override
	public List<ClientList_Life_PremiumCalcuaterDto> getAllTheClientList_Life_PremiumCalcuaters(Long clientID,
			Long productId) {

		return liffecalcuaterRepository.findAll().stream()
				.filter(status -> status.getRecordStatus().equalsIgnoreCase("ACTIVE")
						&& status.getClientListId().getCid() == clientID
						&& status.getProductId().getProductId().equals(productId))
				.map(i -> {
					ClientList_Life_PremiumCalcuaterDto dto = new ClientList_Life_PremiumCalcuaterDto();
					dto.setAgeBandEnd(i.getAgeBandEnd());
					dto.setAgeBandStart(i.getAgeBandStart());
					dto.setBasePremium(i.getBasePremium());
					dto.setSumInsured(i.getSumInsured());
					dto.setLife_premiumCalcuater_id(i.getLife_premiumCalcuater_id());
					dto.setRfqId(i.getRfqId());
					dto.setClientListId(String.valueOf(i.getClientListId().getCid()));
					dto.setProductId(String.valueOf(i.getProductId().getProductId()));
					return dto;

				}).toList();
	}

	public byte[] generateExcelFromData(Long clientListId, Long productId) {
		log.info("Service     : " + clientListId + " -----------" + productId);
		List<ClientList_Per_Life_Premium_Calculator> activeLifePremium = liffecalcuaterRepository.findAll().stream()
				.filter(filter -> filter.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(client -> clientListId != null && client.getClientListId().getCid() == clientListId)
				.filter(c -> productId != null && c.getProductId().getProductId().equals(productId)).toList();
		List<ClientList> list = activeLifePremium.stream().map(ClientList_Per_Life_Premium_Calculator::getClientListId)
				.toList();
		List<Long> list1 = list.stream().map(i -> i.getCid()).toList();
		List<Long> list2 = activeLifePremium.stream().map(c -> c.getProductId().getProductId()).toList();

		log.info("A ---->{}: ", list1 + "---- product id " + list2);
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Premium_Calculator");

			String[] headers = { "AgeBandStart", "AgeBandEnd", "SumInsured", "BasePremium" };
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				headerRow.createCell(i).setCellValue(headers[i]);
			}

			int rowNum = 1;
			for (ClientList_Per_Life_Premium_Calculator cpfc : activeLifePremium) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(cpfc.getAgeBandStart());
				row.createCell(1).setCellValue(cpfc.getAgeBandEnd());
				row.createCell(2).setCellValue(cpfc.getSumInsured());
				row.createCell(3).setCellValue(cpfc.getBasePremium());
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	public ClientList_Life_PremiumCalcuaterDto update_ClientList_Life_PremiumCalcuaterDto(
			ClientList_Life_PremiumCalcuaterDto dto) {
		// Find the record with the provided ID
		List<ClientList_Per_Life_Premium_Calculator> active = liffecalcuaterRepository.findAll().stream()
				.filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE")
						&& i.getLife_premiumCalcuater_id().equals(dto.getLife_premiumCalcuater_id()))
				.collect(Collectors.toList());

		// If the list is not empty, proceed to update the record
		if (!active.isEmpty()) {
			ClientList_Per_Life_Premium_Calculator record = active.get(0); // Assuming there's only one record
			// Update the fields of the record with the values from the DTO
			record.setAgeBandEnd(dto.getAgeBandEnd());
			record.setAgeBandStart(dto.getAgeBandStart());
			record.setSumInsured(dto.getSumInsured());
			record.setBasePremium(dto.getBasePremium());
			record.setRfqId(dto.getRfqId());
			record.setUpdatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
			// Assuming setting other fields as well

			// Save the updated record
			liffecalcuaterRepository.save(record);

			// Return the updated DTO
			return dto;
		} else {
			// If the record is not found, return null or handle the case accordingly
			return null;
		}
	}

}
