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

import com.insure.rfq.dto.ClientDetailsDto;
import com.insure.rfq.dto.DisplayAllClientDetailsDto;
import com.insure.rfq.dto.EndorsementDto;
import com.insure.rfq.dto.UpdateClientDetailsDto;
import com.insure.rfq.entity.ClientDetailsEntity;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.EntityNotFoundException;
import com.insure.rfq.exception.InvaildEndorsementException;
import com.insure.rfq.exception.InvalidClientDetailsException;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientDetailsRepository;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ClientDetailsService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientDetailsServiceImpl implements ClientDetailsService {

	@Autowired
	private ClientDetailsRepository clientDetailsRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;
	@Value("${file.path.coverageMain}")
	private String mainpath;

	@Override
	public ClientDetailsDto createClientDetails(ClientDetailsDto endorsementDto, Long clientListId, Long productId) {
		ClientDetailsEntity entity = new ClientDetailsEntity();
		entity.setClientDetailsName(endorsementDto.getClientDetailsName());
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

		ClientDetailsEntity endorsementEntity = clientDetailsRepository.save(entity);
		endorsementDto.setClientDetailsId(endorsementEntity.getClientDetailsId());

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

	public List<DisplayAllClientDetailsDto> getAllClientDetails(Long clientlistId, Long productId) {

		List<DisplayAllClientDetailsDto> result = clientDetailsRepository.findAll().stream()
				.filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(i -> clientlistId != 0 && i.getClientList().getCid() == clientlistId)
				.filter(i -> productId != 0 && i.getProduct().getProductId().equals(productId)).map(i -> {
					DisplayAllClientDetailsDto displayAllClientDetailsDto = new DisplayAllClientDetailsDto();
					displayAllClientDetailsDto.setClientDetailsId(i.getClientDetailsId());
					displayAllClientDetailsDto.setClientDetailsName(i.getClientDetailsName());
					String[] fileNameParts = i.getFileName().split("_");
					if (fileNameParts.length > 1) {

						String remainingFileName = String.join("_",
								Arrays.copyOfRange(fileNameParts, 1, fileNameParts.length));
						displayAllClientDetailsDto.setFileName(remainingFileName);
					} else {
						// If there is no underscore, set the full fileName
						displayAllClientDetailsDto.setFileName(i.getFileName());
					}
					return displayAllClientDetailsDto;
				}).collect(Collectors.toList());
		if (result.isEmpty()) {
			String errorMessage = "No endorsement data found for clientlistId=" + clientlistId + " and productId="
					+ productId;
			throw new InvalidClientList(errorMessage);
		}

		return result;
	}

	@Override
	public DisplayAllClientDetailsDto getById(Long clientDetailsId) {

		ClientDetailsEntity e = clientDetailsRepository.findById(clientDetailsId)
				.orElseThrow(() -> new InvaildEndorsementException("Invalid Endorsement"));

		DisplayAllClientDetailsDto displayAllEndorsementDto = new DisplayAllClientDetailsDto();
		displayAllEndorsementDto.setClientDetailsId(e.getClientDetailsId());
		displayAllEndorsementDto.setClientDetailsName(e.getClientDetailsName());
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
	public String updateClientDetails(UpdateClientDetailsDto dto, Long id) {
		ClientDetailsEntity entity = clientDetailsRepository.findById(id)
				.orElseThrow(() -> new InvalidClientDetailsException("not found"));
		String filePath = retunFilePath(dto.getFileName());
		entity.setFileName(filePath);
		entity.setClientDetailsName(dto.getClientDetailsName());
		entity.setUpdateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		ClientDetailsEntity clientDetailsEntity = clientDetailsRepository.save(entity);
		String message = "";
		if (clientDetailsEntity != null) {
			message = "updated successful";
		}
		return message;
	}

	@Override
	public String deleteClientDetailsById(Long id) {

		ClientDetailsEntity entity = clientDetailsRepository.findById(id)
				.orElseThrow(() -> new InvalidClientDetailsException("Not Found"));
		entity.setRecordStatus("IN ACTIVE");

		ClientDetailsEntity endorsementEntity = clientDetailsRepository.save(entity);
		String message = "";
		if (endorsementEntity != null) {
			message = "delete successful";
		}

		return message;
	}

	@Override
	public String getFileExtension(String filePath) {
		int lastDotIndex = filePath.lastIndexOf('.');
		if (lastDotIndex > 0) {
			return filePath.substring(lastDotIndex + 1);
		}
		return null;
	}

	@Override
	public byte[] downloadClientDetialsDocumentByClientDetialsId(Long ClientDetailsId) throws IOException {
		try {
			if (ClientDetailsId != null) {
				ClientDetailsEntity endorsement = clientDetailsRepository.findById(ClientDetailsId)
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

	
	public List<ClientDetailsEntity> getAllClientDetailsDownload(Long clientlistId,Long productId) {
		List<ClientDetailsEntity> result= clientDetailsRepository.findAll().stream().filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(i -> clientlistId != null && i.getClientList().getCid() == clientlistId).
				filter(i -> productId!=null && i.getProduct().getProductId().equals( productId))

				.collect(Collectors.toList());
		if (result.isEmpty()) {
			String errorMessage = "No endorsement data found for clientlistId=" + clientlistId + " and productId=" + productId;
			throw new InvalidClientList(errorMessage);
		}
		return result;
	}
	@Override
	public byte[] getFileDataById(Long clientDetailsId) {
		ClientDetailsEntity endorsement = clientDetailsRepository.findById(clientDetailsId)
				.orElseThrow(() -> new EntityNotFoundException("Endorsement not found with id: " + clientDetailsId));
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
	public String cleareAllClientDetails() {

		clientDetailsRepository.deleteAll();
		return "All The clientDetails are deleted successfully";
	}

}
