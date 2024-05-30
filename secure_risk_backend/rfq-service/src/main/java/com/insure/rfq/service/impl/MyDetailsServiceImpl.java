package com.insure.rfq.service.impl;

import com.insure.rfq.dto.DisplayAllMyDetailsDto;
import com.insure.rfq.dto.MyDetailsDto;
import com.insure.rfq.dto.UpdateMyDetailsDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.MyDetailsEntity;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.EntityNotFoundException;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidMyDetailsIdException;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.MyDetailsRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.MyDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

@Service
@Slf4j
public class MyDetailsServiceImpl implements MyDetailsService {

	@Autowired
	private MyDetailsRepository myDetailsRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ClientListRepository clientListRepository;

	@Value("${file.path.coverageMain}")
	private String mainpath;

	@Override
	public MyDetailsDto createMyDetails(MyDetailsDto myDetailsDto, Long clientListId, Long productId) {

		MyDetailsEntity myDetailsEntity = new MyDetailsEntity();
		myDetailsEntity.setDetailName(myDetailsDto.getDetailName());
		String filepath = returnFilePath(myDetailsDto.getFileName());
		myDetailsEntity.setFileName(filepath);

		if (clientListId != null) {
			ClientList clientList = clientListRepository.findById(clientListId)
					.orElseThrow(() -> new InvalidClientList("ClientList Not Found"));
			myDetailsEntity.setRfqId(clientList.getRfqId());
		}
		if (clientListId != null) {
			try {
				ClientList clientList = clientListRepository.findById(clientListId)
						.orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
				myDetailsEntity.setClientList(clientList);
			} catch (InvalidClientList e) {
				throw new InvalidClientList("ClientList is not Found");
			}
		}
		if (productId != null) {
			try {
				Product product = productRepository.findById(productId)
						.orElseThrow(() -> new InvalidProduct("Product is not Found"));
				myDetailsEntity.setProduct(product);
			} catch (InvalidProduct e) {
				throw new InvalidProduct("Product is not Found");
			}
		}

		myDetailsEntity.setCreateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		myDetailsEntity.setRecordStatus("ACTIVE");

		MyDetailsEntity myDetailsEntity1 = myDetailsRepository.save(myDetailsEntity);
		myDetailsDto.setMydetailId(myDetailsEntity1.getMydetailId());

		return myDetailsDto;
	}

	public String returnFilePath(MultipartFile file) {
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
	public List<DisplayAllMyDetailsDto> getAllMyDetail(Long clientListId, Long productId) {
		return myDetailsRepository.findAll().stream().filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(i -> clientListId != 0 && i.getClientList().getCid() == clientListId)
				.filter(i -> productId != 0 && i.getProduct().getProductId().equals(productId)).map(i -> {
					DisplayAllMyDetailsDto displayAllMyDetailsDto = new DisplayAllMyDetailsDto();
					displayAllMyDetailsDto.setMydetailId(i.getMydetailId());
					displayAllMyDetailsDto.setDetailName(i.getDetailName());
					String[] fileNameParts = i.getFileName().split("_");

					if (fileNameParts.length > 1) {
						displayAllMyDetailsDto.setFileName(fileNameParts[1]);
					} else {
						// If there is no underscore, set the full fileName
						displayAllMyDetailsDto.setFileName(i.getFileName());
					}

					return displayAllMyDetailsDto;
				}).toList();
	}

	@Override
	public DisplayAllMyDetailsDto getById(Long mydetailId) {
		MyDetailsEntity e = myDetailsRepository.findById(mydetailId)
				.orElseThrow(() -> new InvalidMyDetailsIdException("Invalid Id :" + " " + mydetailId));

		DisplayAllMyDetailsDto displayAllMyDetailsDto = new DisplayAllMyDetailsDto();
		displayAllMyDetailsDto.setMydetailId(e.getMydetailId());
		displayAllMyDetailsDto.setDetailName(e.getDetailName());
		String[] fileNameParts = e.getFileName().split("_");
		if (fileNameParts.length > 1) {
			// Joining the remaining parts after the first underscore
			String remainingFileName = String.join("_", Arrays.copyOfRange(fileNameParts, 1, fileNameParts.length));
			displayAllMyDetailsDto.setFileName(remainingFileName);
		} else {
			// If there is no underscore, set the full fileName
			displayAllMyDetailsDto.setFileName(e.getFileName());
		}

		return displayAllMyDetailsDto;
	}

	@Override
	public String updateMyDetailsById(UpdateMyDetailsDto updateMyDetailsDto, Long Id) {
		MyDetailsEntity entity = myDetailsRepository.findById(Id)
				.orElseThrow(() -> new InvalidMyDetailsIdException("Mydetails File not found with id: " + Id));
		String filePath = returnFilePath(updateMyDetailsDto.getFileName());
		entity.setFileName(filePath);
		entity.setDetailName(updateMyDetailsDto.getDetailName());
		entity.setUpdateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		MyDetailsEntity myDetailsEntity = myDetailsRepository.save(entity);
		String message = "";
		if (myDetailsEntity != null) {
			message = "updated successful";
		}
		return message;
	}

	@Override
	public String deleteMyDetailsById(Long id) {
		MyDetailsEntity entity = myDetailsRepository.findById(id)
				.orElseThrow(() -> new InvalidMyDetailsIdException("Mydetails File not found with id: " + id));
		entity.setRecordStatus("IN ACTIVE");

		MyDetailsEntity myDetailsEntity = myDetailsRepository.save(entity);
		String message = "";
		if (myDetailsEntity != null) {
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
	public List<MyDetailsEntity> getAllMyDetails(Long clientListId, Long productId) {
		List<MyDetailsEntity> result = myDetailsRepository.findAll().stream()
				.filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(i -> clientListId != null && i.getClientList().getCid() == (clientListId))
				.filter(i -> productId != null && i.getProduct().getProductId().equals(productId))
				.toList();

		if (result.isEmpty()) {
			String errorMessage = "No myDetails data found for clientListId=" + clientListId + " and productId="
					+ productId;
			throw new InvalidClientList(errorMessage);
		}

		return result;
	}

	@Override
	public byte[] getFileDataById(Long mydetailId) {
		MyDetailsEntity detailsEntity = myDetailsRepository.findById(mydetailId)
				.orElseThrow(() -> new EntityNotFoundException("Mydetails File not found with id: " + mydetailId));
		MyDetailsDto myDetailsDto = new MyDetailsDto();
		String fileDataAsString = detailsEntity.getFileName();

		byte[] bytes = convertStringToByteArray(fileDataAsString);

		myDetailsDto.setFileData(bytes);

		return myDetailsDto.getFileData();
	}

	private byte[] convertStringToByteArray(String data) {
		// Implement the logic to convert the string to a byte array
		// This could be based on encoding or other requirements of your application
		return data.getBytes(StandardCharsets.UTF_8); // Change the charset based on your needs
	}
}
