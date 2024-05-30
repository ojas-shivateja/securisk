package com.insure.rfq.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insure.rfq.dto.GetChildClientListPolicyDto;
import com.insure.rfq.dto.GetClientListPolicyDto;
import com.insure.rfq.dto.PolicyDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.CorporateDetailsEntity;
import com.insure.rfq.entity.PolicyEntity;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.CorporateDetailsRepository;
import com.insure.rfq.repository.PolicyRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;
@Service
@Slf4j
public class PolicyServiceImpl implements PolicyService {
	@Value("${file.path.coverageMain}")
	private String mainpath;
	@Autowired
	private PolicyRepository policyRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private CorporateDetailsRepository corporateDetailsRepository;

	@Override
	public String createPolicyData(PolicyDto policyDto, Long clientID, Long produtId) {
		log.error("policy Dto : {}", policyDto + " ==== " + clientID + " -==== > " + produtId);
		String message = "";
		Optional<PolicyEntity> byClientAndProductId = policyRepository.findByClientAndProductId(clientID, produtId);

		// if the data is present we need to update
		// update operation
		if (policyDto != null && byClientAndProductId.isPresent()) {
			ClientList clientList1 = clientListRepository.findById(clientID).get();
			String rfqId = clientList1.getRfqId();
			CorporateDetailsEntity corporateDetails = corporateDetailsRepository.findByRfqId(rfqId).get();
			PolicyEntity policyEntity = byClientAndProductId.get();

			if (corporateDetails != null) {
				// usig the corporate Details............
				policyEntity.setInsuranceBroker(policyDto.getInsuranceBroker());
				policyEntity.setInsuranceCompany(policyDto.getInsuranceCompany());
				policyEntity.setNameOfTheTPA(policyDto.getNameOfTheTPA());
				policyEntity.setRfqId(corporateDetails.getRfqId());
				policyEntity.setPolicyType(corporateDetails.getPolicyType());
				log.info("Tpa is  :{} ", corporateDetails.getTpaName() + " " + corporateDetails.getInsuredName() + "  "
						+ corporateDetails.getIntermediaryName());

			}

			policyEntity.setPolicyStartDate(policyDto.getPolicyStartDate());
			log.info("Policy Staring date   :: {}", policyDto.getPolicyStartDate());
			policyEntity.setPolicyEndDate(policyDto.getPolicyEndDate());
			log.info("Policy Staring date   :: {}", policyDto.getPolicyEndDate());
			policyEntity.setPPTPath(retunFilePath(policyDto.getPPTPath()));
			policyEntity.setPolicyCopyPath(retunFilePath(policyDto.getPolicyCopyPath()));

			policyEntity.setPolicyNumber(policyDto.getPolicyNumber());

			policyEntity.setInception_Premium(policyDto.getInception_Premium());
			policyEntity.setTillDatePremium(policyDto.getTillDatePremium());

			// Handle familyDefination from PolicyDto
			List<GetChildClientListPolicyDto> convertJsonToList = convertJsonToList(policyDto.getFamilyDefination());
			List<String> familyDefination = new ArrayList<>();
			List<Double> sumInsured = new ArrayList<>();
			if (convertJsonToList != null) {
				for (GetChildClientListPolicyDto family : convertJsonToList) {
					familyDefination.add(family.getFamilyDefination());
					sumInsured.add(family.getSumInsured());
				}
			}
			policyEntity.setFamilyDefination(familyDefination);
			policyEntity.setSumInsured(sumInsured);

			policyEntity.setRecordStatus("ACTIVE");
			policyEntity.setCreatedDate(byClientAndProductId.get().getCreatedDate());
			policyEntity.setUpdatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
			// Retrieve Product and set to PolicyEntity
			if (produtId != null) {
				Product product = productRepository.findById(produtId)
						.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
				policyEntity.setProductId(product);
				policyEntity.setPolicyType(product.getPolicyType());
				policyEntity.setPolicyName(policyDto.getPolicyName());
			}
			if (clientID != null) {
				ClientList clientList = clientListRepository.findById(clientID)
						.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
				policyEntity.setClientListId(clientList);
				policyEntity.setRfqId(clientList.getRfqId());
			}

			// Save PolicyEntity
			PolicyEntity savedPolicyEntity = policyRepository.save(policyEntity);

			if (savedPolicyEntity != null) {
				message = "The Policy is saved successfully";
			} else {
				message = "Failed to Save Policy!";
			}
		}
		// Creating new Entity .........
		else {
			PolicyEntity policyEntity = new PolicyEntity();
			ClientList clientList1 = clientListRepository.findById(clientID).get();
			String rfqId = clientList1.getRfqId();
			CorporateDetailsEntity corporateDetails = corporateDetailsRepository.findByRfqId(rfqId).get();
			// Retrieve CorporateDetailsEntity
			log.info("corporateDetails {}:", corporateDetails);
			if (corporateDetails != null) {
				policyEntity.setRfqId(corporateDetails.getRfqId());
				log.info("Creating in " + corporateDetails.getRfqId());
				// usig the corporate Details............
				policyEntity.setInsuranceBroker(corporateDetails.getInsuredName());
				policyEntity.setInsuranceCompany(corporateDetails.getIntermediaryName());
				policyEntity.setNameOfTheTPA(corporateDetails.getTpaName());
				}
			// Mapping fields directly from DTO to Entity

			policyEntity.setPolicyNumber(policyDto.getPolicyNumber());
			policyEntity.setPolicyStartDate(policyDto.getPolicyStartDate());
			policyEntity.setPolicyEndDate(policyDto.getPolicyEndDate());
			// Set PPTPath and PolicyCopyPath
			policyEntity.setPPTPath(retunFilePath(policyDto.getPPTPath()));
			policyEntity.setPolicyCopyPath(retunFilePath(policyDto.getPolicyCopyPath()));

			policyEntity.setInception_Premium(policyDto.getInception_Premium());
			policyEntity.setTillDatePremium(policyDto.getTillDatePremium());

			// Handle familyDefination from PolicyDto
			List<GetChildClientListPolicyDto> convertJsonToList = convertJsonToList(policyDto.getFamilyDefination());
			List<String> familyDefination = new ArrayList<>();
			List<Double> sumInsured = new ArrayList<>();
			if (convertJsonToList != null) {
				for (GetChildClientListPolicyDto family : convertJsonToList) {
					familyDefination.add(family.getFamilyDefination());
					sumInsured.add(family.getSumInsured());
				}
			}
			policyEntity.setFamilyDefination(familyDefination);
			policyEntity.setSumInsured(sumInsured);

			policyEntity.setRecordStatus("ACTIVE");
			policyEntity.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

			// Retrieve Product and set to PolicyEntity
			if (produtId != null) {
				Product product = productRepository.findById(produtId)
						.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
				policyEntity.setPolicyType(product.getPolicyType());
				policyEntity.setProductId(product);
				policyEntity.setPolicyName(product.getProductName());
			}
			if (clientID != null) {
				ClientList clientList = clientListRepository.findById(clientID)
						.orElseThrow(() -> new InvalidClientList("ClientList is Not Found"));
				policyEntity.setClientListId(clientList);
				policyEntity.setRfqId(clientList.getRfqId());
			}

			// Save PolicyEntity
			PolicyEntity savedPolicyEntity = policyRepository.save(policyEntity);

			if (savedPolicyEntity != null) {
				message = "The Policy is saved successfully";
			} else {
				message = "Failed to Save Policy!";
			}

		}
		return message;
	}

	@Override
	public List<GetClientListPolicyDto> getAllPolicyEntities() {
		return policyRepository.findAll().stream().map(i -> {
			GetClientListPolicyDto dto = new GetClientListPolicyDto();
			dto.setId(i.getId());
			dto.setPolicyName(i.getPolicyName());
			dto.setPolicyNumber(i.getPolicyNumber());
			dto.setPolicyStartDate(i.getPolicyStartDate());
			dto.setPolicyEndDate(i.getPolicyEndDate());
			dto.setPPTPath(i.getPPTPath());
			dto.setPolicyCopyPath(i.getPolicyCopyPath());
			dto.setInsuranceBroker(i.getInsuranceBroker());
			dto.setInsuranceCompany(i.getInsuranceCompany());
			dto.setNameOfTheTPA(i.getNameOfTheTPA());
			dto.setInception_Premium(i.getInception_Premium());
			dto.setProductid(i.getProductId().getProductId().toString());
			dto.setClientId(String.valueOf(i.getClientListId().getCid()));
			List<String> familyDefination = i.getFamilyDefination();
			List<Double> sumInsured = i.getSumInsured();
			List<GetChildClientListPolicyDto> childClientList1 = new ArrayList<>();

			// Check if familyDefination is not null and has the same size as sumInsured
			if (familyDefination != null && sumInsured != null && familyDefination.size() == sumInsured.size()) {
				childClientList1 = IntStream.range(0, familyDefination.size()).mapToObj(j -> {
					GetChildClientListPolicyDto childClientDto = new GetChildClientListPolicyDto();
					childClientDto.setFamilyDefination(familyDefination.get(j));
					childClientDto.setSumInsured(sumInsured.get(j));
					return childClientDto;
				}).toList();
			}

			dto.setFamilyDefination(childClientList1);

			dto.setTillDatePremium(i.getTillDatePremium());
			dto.setRfqId(i.getRfqId());

			return dto;
		}).toList();
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

			log.info(filePath + "      -----------path ......");
			// Return the path of the saved file
			return filePath.toString();
		} catch (IOException e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}

	public List<GetChildClientListPolicyDto> convertJsonToList(String jsonString) {
		try {
			return objectMapper.readValue(jsonString,
					new TypeReference<List<GetChildClientListPolicyDto>>() {
					});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public GetClientListPolicyDto getByProductAndClientId(Long clientId, Long productId) {
		Optional<PolicyEntity> optionalEntity = policyRepository.findByClientAndProductId(clientId, productId);
		GetClientListPolicyDto dto = new GetClientListPolicyDto();

		if (optionalEntity.isPresent()) {
			PolicyEntity entity = optionalEntity.get();
			dto.setId(entity.getId());
			dto.setPolicyName(entity.getPolicyName());
			dto.setPolicyNumber(entity.getPolicyNumber());
			dto.setPolicyStartDate(entity.getPolicyStartDate());
			dto.setPolicyEndDate(entity.getPolicyEndDate());
			dto.setInsuranceBroker(entity.getInsuranceBroker());
			dto.setInsuranceCompany(entity.getInsuranceCompany());
			dto.setNameOfTheTPA(entity.getNameOfTheTPA());
			dto.setInception_Premium(entity.getInception_Premium());
			dto.setTillDatePremium(entity.getTillDatePremium());
            dto.setPolicyType(entity.getPolicyType());          
			// ---
			dto.setPPTPath(entity.getPPTPath());
			dto.setPolicyCopyPath(entity.getPolicyCopyPath());
			dto.setRfqId(entity.getRfqId());
			dto.setProductid(entity.getProductId().getProductId().toString());
			dto.setClientId(String.valueOf(entity.getClientListId().getCid()));
			// -----
			List<String> familyDefination = entity.getFamilyDefination();
			List<Double> sumInsured = entity.getSumInsured();
			List<GetChildClientListPolicyDto> childClientList = new ArrayList<>();

			// Check if familyDefination is not null and has the same size as sumInsured
			if (familyDefination != null && sumInsured != null && familyDefination.size() == sumInsured.size()) {
				childClientList = IntStream.range(0, familyDefination.size())
						.mapToObj(i -> new GetChildClientListPolicyDto(familyDefination.get(i), sumInsured.get(i)))
						.toList();
			}

			dto.setFamilyDefination(childClientList);
		} else {
			// Policy not found, populate DTO with default values if product exists
			if (productId != null) {
				Product product = productRepository.findById(productId)
						.orElseThrow(() -> new InvalidProduct("Product is Not Found"));
				Optional<CorporateDetailsEntity> optionalCorporateDetails = corporateDetailsRepository
						.findById(productId);
				CorporateDetailsEntity corporateDetails = optionalCorporateDetails.orElse(null);

				dto.setPolicyName(product.getProductName());
				dto.setInsuranceBroker(corporateDetails.getInsuredName());
				dto.setInsuranceCompany(corporateDetails.getIntermediaryName());
				dto.setNameOfTheTPA(corporateDetails.getTpaName());
			}
		}
		return dto;
	}

	public byte[] downloadpolicyCopyPath(Long clientId, Long productId) throws IOException {
		Optional<PolicyEntity> policyentity = policyRepository.findByClientAndProductId(clientId, productId);
		if (policyentity.isPresent()) {
			PolicyEntity entity = policyentity.get();

			String pptPath = entity.getPolicyCopyPath();

			if (pptPath != null && !pptPath.isEmpty()) {
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

				try (FileInputStream fileInputStream = new FileInputStream(new File(pptPath))) {
					byte[] buffer = new byte[1024];
					int read;
					while ((read = fileInputStream.read(buffer)) != -1) {
						arrayOutputStream.write(buffer, 0, read);
					}
				}

				return arrayOutputStream.toByteArray();
			}
		}

		// Handle the case where data is not available
		return new byte[0]; // Return an empty byte array or null as per your requirements.
	}

	public byte[] downloadpptpath(Long clientId, Long productId) throws IOException {
		Optional<PolicyEntity> policyentity = policyRepository.findByClientAndProductId(clientId, productId);
		if (policyentity.isPresent() && policyentity.get().getProductId().getProductId().equals(productId)
				&& policyentity.get().getClientListId().getCid() == clientId) {
			PolicyEntity entity = policyentity.get();

			String pptPath = entity.getPPTPath();

			if (pptPath != null && !pptPath.isEmpty()) {
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

				try (FileInputStream fileInputStream = new FileInputStream(new File(pptPath))) {
					byte[] buffer = new byte[1024];
					int read;
					while ((read = fileInputStream.read(buffer)) != -1) {
						arrayOutputStream.write(buffer, 0, read);
					}
				}

				return arrayOutputStream.toByteArray();
			}
		}

		// Handle the case where data is not available
		return new byte[0]; // Return an empty byte array or null as per your requirements.
	}

}
