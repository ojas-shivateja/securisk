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

import com.insure.rfq.dto.ClientListFamilyPremiumCalcuaterDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientList_PreFamilyPremium_Calcuater;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.exception.InvalidUser;
import com.insure.rfq.repository.ClientListPremiumCalcuate_repository;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ClientListFamilyPremiumCalcuaterService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientListFamilyPremiumCalcuaterServiceimpl implements ClientListFamilyPremiumCalcuaterService {

	@Autowired
	private ClientListPremiumCalcuate_repository calcuaterRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;

	@Override
	public String createClientListFamilyPremiumCalcuater(ClientListFamilyPremiumCalcuaterDto calcuaterDto,
			Long clientId, Long productId) {
		// TODO Auto-generated method stub
		String message = "";
		ClientList_PreFamilyPremium_Calcuater entityCalcuater = new ClientList_PreFamilyPremium_Calcuater();
		entityCalcuater.setAgeBandStart(calcuaterDto.getAgeBandStart());
		entityCalcuater.setAgeBandEnd(calcuaterDto.getAgeBandEnd());
		entityCalcuater.setBasePremium(calcuaterDto.getBasePremium());
		entityCalcuater.setSumInsured(calcuaterDto.getSumInsured());
		entityCalcuater.setRecordStatus("ACTIVE");
		entityCalcuater.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		if (productId != null) {
			Product product1 = productRepository.findById(productId)
					.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
			entityCalcuater.setProductId(product1);
		}
		if (clientId != null) {
			ClientList clientList = clientListRepository.findById(clientId)
					.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
			entityCalcuater.setClientListId(clientList);
			entityCalcuater.setRfqId(clientList.getRfqId());
		}

		ClientList_PreFamilyPremium_Calcuater premium_Calcuater = calcuaterRepository.save(entityCalcuater);
		if (premium_Calcuater != null)
			message = "ClientList_PreFamilyPremium_Calcuater is Saved Successfully";
		else
			message = "ClientList_PreFamilyPremium_Calcuater is not Saved";
		return message;
	}

	@Override
	public List<ClientListFamilyPremiumCalcuaterDto> getAllTheClientListPremiumCalcuaters(Long clientID,
			Long productId) {
		// TODO Auto-generated method stub
		System.out.println(clientID + "   ----- " + productId);

		return calcuaterRepository.findAll().stream()
				.filter(status -> status.getRecordStatus().equalsIgnoreCase("ACTIVE")
						&& status.getClientListId().getCid() == clientID
						&& status.getProductId().getProductId().equals(productId))
				.map(i -> {
					ClientListFamilyPremiumCalcuaterDto dto = new ClientListFamilyPremiumCalcuaterDto();
					dto.setAgeBandEnd(i.getAgeBandEnd());
					dto.setAgeBandStart(i.getAgeBandStart());
					dto.setBasePremium(i.getBasePremium());
					dto.setSumInsured(i.getSumInsured());
					dto.setFamilypremiumCalcuater_id(i.getPremiumCalcuater_id());
					dto.setRfqId(i.getRfqId());
					dto.setClientListId(String.valueOf(i.getClientListId().getCid()));
					dto.setProductId(String.valueOf(i.getProductId().getProductId()));
					return dto;

				}).toList();
	}

	@Override
	public String deleteClientListPremiumCalcuaterDto(Long primary_Id) {
		ClientList_PreFamilyPremium_Calcuater entitypremiumCalcuater = calcuaterRepository.findById(primary_Id)
				.orElseThrow(() -> new InvalidUser("Id is not found"));
		String message = "";
		if (entitypremiumCalcuater != null) {
			entitypremiumCalcuater.setRecordStatus("INACTIVE");
			ClientList_PreFamilyPremium_Calcuater entitysaved = calcuaterRepository.save(entitypremiumCalcuater);
			if (entitysaved != null)
				message = "Data is Deleted";
		} else
			message = "Id is not found";

		return message;
	}

	@Override
	public ClientListFamilyPremiumCalcuaterDto getClientListPremiumCalcuaterDto(Long primary_Id) {
		List<ClientList_PreFamilyPremium_Calcuater> active = calcuaterRepository.findAll().stream().filter(
				i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE") && i.getPremiumCalcuater_id().equals(primary_Id))
				.collect(Collectors.toList());
		// If the list is not empty, proceed to map the entities to DTOs
		if (!active.isEmpty()) {
			return active.stream().map(i -> {
				ClientListFamilyPremiumCalcuaterDto dto = new ClientListFamilyPremiumCalcuaterDto();
				dto.setFamilypremiumCalcuater_id(i.getPremiumCalcuater_id());
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

	// public String u
	@Override
	public byte[] generateExcelFromData(Long clientListId, Long productId) {
		log.info("Service     : " + clientListId + " -----------" + productId);
		List<ClientList_PreFamilyPremium_Calcuater> familyPremiumCalcuaters = calcuaterRepository.findAll().stream()
				.filter(filter -> filter.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(client -> clientListId != null && client.getClientListId().getCid() == clientListId)
				.filter(c -> productId != null && c.getProductId().getProductId().equals(productId)).toList();
		List<ClientList> list = familyPremiumCalcuaters.stream()
				.map(ClientList_PreFamilyPremium_Calcuater::getClientListId).toList();
		List<Long> list1 = list.stream().map(i -> i.getCid()).toList();
		List<Long> list2 = familyPremiumCalcuaters.stream().map(c -> c.getProductId().getProductId()).toList();

		log.info("A ---->{}: ", list1 + "---- product id " + list2);
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Premium_Calculator");

			String[] headers = { "AgeBandStart", "AgeBandEnd", "SumInsured", "BasePremium" };
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				headerRow.createCell(i).setCellValue(headers[i]);
			}

			int rowNum = 1;
			for (ClientList_PreFamilyPremium_Calcuater cpfc : familyPremiumCalcuaters) {
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

	public ClientListFamilyPremiumCalcuaterDto update_ClientListFamilyPremiumCalcuaterDto(
			ClientListFamilyPremiumCalcuaterDto dto) {
		// Find the record with the provided ID
		List<ClientList_PreFamilyPremium_Calcuater> active = calcuaterRepository.findAll().stream()
				.filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE")
						&& i.getPremiumCalcuater_id().equals(dto.getFamilypremiumCalcuater_id()))
				.collect(Collectors.toList());

		// If the list is not empty, proceed to update the record
		if (!active.isEmpty()) {
			ClientList_PreFamilyPremium_Calcuater record = active.get(0); // Assuming there's only one record
			// Update the fields of the record with the values from the DTO
			record.setAgeBandEnd(dto.getAgeBandEnd());
			record.setAgeBandStart(dto.getAgeBandStart());
			record.setSumInsured(dto.getSumInsured());
			record.setBasePremium(dto.getBasePremium());
			record.setRfqId(dto.getRfqId());
			record.setUpdatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

			calcuaterRepository.save(record);

			return dto;
		} else {

			return null;
		}
	}

}
