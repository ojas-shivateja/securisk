package com.insure.rfq.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.DisplayAllEndorsementDto;
import com.insure.rfq.dto.EndorsementDto;
import com.insure.rfq.dto.UpdateEndorsementDetailsDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.EndorsementEntity;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.EntityNotFoundException;
import com.insure.rfq.exception.InvaildEndorsementException;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.EndorsementRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.EndorsementService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EndorsementServiceImpl implements EndorsementService {

	@Autowired
	private EndorsementRepository endorsementRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;
	@Value("${file.path.coverageMain}")
	private String mainpath;

	@Override

	public EndorsementDto createEndorsement(EndorsementDto endorsementDto, Long clientListId, Long productId) {
		EndorsementEntity entity = new EndorsementEntity();
		entity.setEndorsementName(endorsementDto.getEndorsementName());
		String filepath = retunFilePath(endorsementDto.getFileName());
		entity.setFileName(filepath);
		if (clientListId != null) {
			ClientList clientList = clientListRepository.findById(clientListId)
					.orElseThrow(() -> new InvalidClientList("ClientList Not Found"));
			entity.setRfqId(clientList.getRfqId());
		}
		if (clientListId != null) {
			try {
				ClientList clientList = clientListRepository.findById(clientListId)
						.orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
				entity.setClientList(clientList);
			} catch (InvalidClientList e) {
			}
		}
		if (productId != null) {
			try {
				Product product = productRepository.findById(productId)
						.orElseThrow(() -> new InvalidProduct("Product is not Found"));
				entity.setProduct(product);
			} catch (InvalidProduct e) {
			}
		}
		entity.setCreateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		entity.setRecordStatus("ACTIVE");

		EndorsementEntity endorsementEntity = endorsementRepository.save(entity);
		endorsementDto.setEndorsementId(endorsementEntity.getEndorsementId());



		return endorsementDto;
	}

	public String retunFilePath(MultipartFile file) {
		try {
			// Generate a unique file name
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

			// Create the directory if it doesn't exist
			Path directory = Paths.get(mainpath);

			if (!Files.exists(directory)) {
				Files.createDirectories(directory);
			}

			// Save the file to the local machine
			Path filePath = Paths.get(mainpath, fileName);
			Files.copy(file.getInputStream(), filePath);

			// Return the path of the saved file
			return filePath.toString();
		} catch (IOException e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}

	@Override
	public List<DisplayAllEndorsementDto> getAllEndorsement(Long clientlistId, Long productId) {

		return endorsementRepository.findAll().stream().filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(i -> clientlistId != 0 && i.getClientList().getCid() == clientlistId)
				.filter(i -> productId != 0 && i.getProduct().getProductId().equals(productId)).map(i -> {
					DisplayAllEndorsementDto displayAllEndorsementDto = new DisplayAllEndorsementDto();
					displayAllEndorsementDto.setEndorsementId(i.getEndorsementId());
					displayAllEndorsementDto.setEndorsementName(i.getEndorsementName());
					String[] fileNameParts = i.getFileName().split("_");

					if (fileNameParts.length > 1) {
						displayAllEndorsementDto.setFileName(fileNameParts[1]);
					} else {
						// If there is no underscore, set the full fileName
						displayAllEndorsementDto.setFileName(i.getFileName());
					}

					return displayAllEndorsementDto;
				}).toList();
	}

	@Override
	public DisplayAllEndorsementDto getById(Long endorsementId) {

		EndorsementEntity e = endorsementRepository.findById(endorsementId)
				.orElseThrow(() -> new InvaildEndorsementException("Invalid Endorsement"));

		DisplayAllEndorsementDto displayAllEndorsementDto = new DisplayAllEndorsementDto();
		displayAllEndorsementDto.setEndorsementId(e.getEndorsementId());
		displayAllEndorsementDto.setEndorsementName(e.getEndorsementName());
		String[] fileNameParts = e.getFileName().split("_");
		if (fileNameParts.length > 1) {
			// Joining the remaining parts after the first underscore
			String remainingFileName = String.join("_", Arrays.copyOfRange(fileNameParts, 1, fileNameParts.length));
			displayAllEndorsementDto.setFileName(remainingFileName);
		} else {
			// If there is no underscore, set the full fileName
			displayAllEndorsementDto.setFileName(e.getFileName());
		}

		return displayAllEndorsementDto;
	}

	@Override
	public String updateEndorsementById(UpdateEndorsementDetailsDto dto, Long id) {
		EndorsementEntity entity = endorsementRepository.findById(id)
				.orElseThrow(() -> new InvaildEndorsementException("not found"));
		String filePath = retunFilePath(dto.getFileName());
		entity.setFileName(filePath);
		entity.setEndorsementName(dto.getEndorsementName());
		entity.setUpdateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		EndorsementEntity endorsementEntity = endorsementRepository.save(entity);
		String message = "";
		if (endorsementEntity != null) {
			message = "updated successful";
		}
		return message;
	}

	@Override
	public String deleteEndorsementById(Long id) {

		EndorsementEntity entity = endorsementRepository.findById(id)
				.orElseThrow(() -> new InvaildEndorsementException("Not Found"));
		entity.setRecordStatus("IN ACTIVE");

		EndorsementEntity endorsementEntity = endorsementRepository.save(entity);
		String message = "";
		if (endorsementEntity != null) {
			message = "delete successful";
		}

		return message;
	}

	@Override
	public byte[] downloadEndrosementDocumentByEndrosementId(Long endorsementId) throws IOException {
		try {
			if (endorsementId != null) {
				EndorsementEntity endorsement = endorsementRepository.findById(endorsementId)
						.orElseThrow(() -> new InvaildEndorsementException("Invalid Endorsement"));
				Path path = Paths.get(endorsement.getFileName());
				return Files.readAllBytes(path);
			}
		} catch (IOException e) {
			// Handle IOException appropriately, e.g., log the error
			e.printStackTrace();
			throw new InvaildEndorsementException("Error reading file");
		}

		return null;
	}

	@Override
	public String getFileExtension(String filePath) {
		int lastDotIndex = filePath.lastIndexOf('.');
		if (lastDotIndex > 0) {
			return filePath.substring(lastDotIndex + 1);
		}
		return null;

	}

	public List<EndorsementEntity> getAllEndorsements(Long clientlistId, Long productId) {
		List<EndorsementEntity> result = endorsementRepository.findAll().stream()
				.filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(i -> clientlistId != null && i.getClientList().getCid() == (clientlistId))
				.filter(i -> productId != null && i.getProduct().getProductId().equals(productId))
				.toList();

		if (result.isEmpty()) {
			String errorMessage = "No endorsement data found for clientListId=" + clientlistId + " and productId="
					+ productId;
			throw new InvalidClientList(errorMessage);
		}

		return result;
	}

	public byte[] getFileDataById(Long endorsementId) {
		EndorsementEntity endorsement = endorsementRepository.findById(endorsementId)
				.orElseThrow(() -> new EntityNotFoundException("Endorsement not found with id: " + endorsementId));
		EndorsementDto endorsementDto = new EndorsementDto();
		String fileDataAsString = endorsement.getFileName();

		byte[] bytes = convertStringToByteArray(fileDataAsString);

		endorsementDto.setFileData(bytes);

		return endorsementDto.getFileData();

		// Assuming the file data is stored directly in the entity
		// Replace this with your actual method or field
	}

	private byte[] convertStringToByteArray(String data) {
		// Implement the logic to convert the string to a byte array
		// This could be based on encoding or other requirements of your application
		return data.getBytes(StandardCharsets.UTF_8); // Change the charset based on your needs
	}

	@Override
	public String clearAllEndorsements() {
		endorsementRepository.deleteAll();
		return "All endorsements are deleted successfully";
	}

}