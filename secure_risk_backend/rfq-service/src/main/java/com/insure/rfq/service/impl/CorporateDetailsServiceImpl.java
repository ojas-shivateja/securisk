package com.insure.rfq.service.impl;

import com.insure.rfq.dto.*;
import com.insure.rfq.entity.*;
import com.insure.rfq.generator.IrdaPdfGenerator;
import com.insure.rfq.login.entity.Location;
import com.insure.rfq.login.repository.LocationRepository;
import com.insure.rfq.repository.*;
import com.insure.rfq.service.CorporateDetailsService;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CorporateDetailsServiceImpl implements CorporateDetailsService {
	@Value("${spring.mail.host}")
	private String host;
	@Value("${spring.mail.username}")
	private String username;
	@Value("${spring.mail.password}")
	private String password;
	@Value("${spring.mail.port}")
	private int port;
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String smtpAuth;
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String starttls;
	@Autowired
	private CorporateDetailsRepository corporateDetailsRepo;
	@Value("${file.path.corporatepath}")
	private String emailUrl;
	@Autowired
	private ProductCategoryRepository prodCategoryRepo;
	@Autowired
	private ProductRepository prodRepo;
	@Autowired
	private CoverageDetailsRepository coverageDetailsRepo;
	@Autowired
	private PolicyTermsRepository policyTermsRepo;
	@Autowired
	private ExpiryPolicyDetailsRepository expiryPolicyDetailsRepository;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private ClientListRepository clientListRepo;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private IrdaPdfGenerator irda;
	
	@Autowired
	private ClaimsDetailsRepository claimsDetailsRepository;


	private static final JavaMailSender mailSender = null;

	@Override
	public String createRFQ(CorporateDetailsDto details) {
		CorporateDetailsEntity cd = new CorporateDetailsEntity();
		// policy details
		cd.setPolicyType(details.getPolicyType());
		cd.setProductId(details.getProductId());
		cd.setProdCategoryId(details.getProdCategoryId());
		// Corporate Details
		cd.setInsuredName(details.getInsuredName());
		cd.setAddress(details.getAddress());
		cd.setNob(details.getNob());
		cd.setNobCustom(details.getNobCustom());
		cd.setContactName(details.getContactName());
		cd.setEmail(details.getEmail());
		cd.setPhNo(details.getPhNo());

		// Intermediary Details
		cd.setIntermediaryName(details.getIntermediaryName());
		cd.setIntermediaryContactName(details.getIntermediaryContactName());
		cd.setIntermediaryEmail(details.getIntermediaryEmail());
		cd.setIntermediaryPhNo(details.getIntermediaryPhNo());

		// TPA Details
		cd.setTpaName(details.getTpaName());
		cd.setTpaContactName(details.getTpaContactName());
		cd.setTpaEmail(details.getTpaEmail());
		cd.setTpaPhNo(details.getTpaPhNo());

		cd.setCreateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		cd.setAppStatus("Pending");
		cd.setRecordStatus("ACTIVE");
		if (details.getLocation() != null) {
			Optional<Location> location = locationRepository.findById(Long.parseLong(details.getLocation()));
			cd.setLocation(location.get());
		}

		return corporateDetailsRepo.save(cd).getRfqId();
	}

	@Override
	public RFQCompleteDetailsDto getRfqById(String rfqId) {

		RFQCompleteDetailsDto rfqCompleteDetailsDto = new RFQCompleteDetailsDto();

		Optional<CorporateDetailsEntity> corporateDetails = corporateDetailsRepo.findByRfqId(rfqId);
		if (corporateDetails.isPresent()) {
			CorporateDetailsEntity corporateDetailsEntity = corporateDetails.get();
			CorporateDetailsDto corporateDetailsDto = new CorporateDetailsDto();

			corporateDetailsDto.setPolicyType(corporateDetailsEntity.getPolicyType());
			corporateDetailsDto.setProdCategoryId(corporateDetailsEntity.getProdCategoryId());
			corporateDetailsDto.setProductId(corporateDetailsEntity.getProductId());
			corporateDetailsDto.setRfqId(corporateDetailsEntity.getRfqId());

			corporateDetailsDto.setAddress(corporateDetailsEntity.getAddress());
			corporateDetailsDto.setContactName(corporateDetailsEntity.getContactName());
			corporateDetailsDto.setEmail(corporateDetailsEntity.getEmail());
			corporateDetailsDto.setNob(corporateDetailsEntity.getNob());

			if (corporateDetailsEntity.getNobCustom() != null) {
				corporateDetailsDto.setNobCustom(corporateDetailsEntity.getNobCustom());
			} else {
				corporateDetailsDto.setNobCustom(null);
			}

			corporateDetailsDto.setPhNo(corporateDetailsEntity.getPhNo());

			corporateDetailsDto.setInsuredName(corporateDetailsEntity.getInsuredName());
			corporateDetailsDto.setIntermediaryContactName(corporateDetailsEntity.getIntermediaryContactName());
			corporateDetailsDto.setIntermediaryEmail(corporateDetailsEntity.getIntermediaryEmail());
			corporateDetailsDto.setIntermediaryName(corporateDetailsEntity.getIntermediaryName());
			corporateDetailsDto.setIntermediaryPhNo(corporateDetailsEntity.getIntermediaryPhNo());

			corporateDetailsDto.setTpaContactName(corporateDetailsEntity.getTpaContactName());
			corporateDetailsDto.setTpaEmail(corporateDetailsEntity.getTpaEmail());
			corporateDetailsDto.setTpaName(corporateDetailsEntity.getTpaName());
			corporateDetailsDto.setTpaPhNo(corporateDetailsEntity.getTpaPhNo());

			corporateDetailsDto.setAppStatus(corporateDetailsEntity.getAppStatus());
			corporateDetailsDto.setRecordStatus(corporateDetailsEntity.getRecordStatus());
			corporateDetailsDto.setCreateDate(corporateDetailsEntity.getCreateDate());
			corporateDetailsDto.setUpdateDate(corporateDetailsEntity.getUpdateDate());
			if (corporateDetailsEntity.getLocation() != null) {
				corporateDetailsDto.setLocation(corporateDetailsEntity.getLocation().getLocationName());
			} else {
				corporateDetailsDto.setLocation(null);
			}
			rfqCompleteDetailsDto.setCorporateDetails(corporateDetailsDto);
		}

		Optional<CoverageDetailsEntity> coverages = coverageDetailsRepo.findByRfqId(rfqId);
		if (coverages.isPresent()) {
			CoverageDetailsDto coverageDetails = new CoverageDetailsDto();
			CoverageDetailsEntity coverageDetailsEntity = coverages.get();
			coverageDetails.setRfqId(coverageDetailsEntity.getRfqId());
			coverageDetails.setEmpData(coverageDetailsEntity.isEmpData());
			coverageDetails.setFamilyDefication13(coverageDetailsEntity.isFamilyDefication13());
			coverageDetails.setFamilyDefication13Amount(coverageDetailsEntity.getFamilyDefication13Amount());
			coverageDetails.setFamilyDefication15(coverageDetailsEntity.isFamilyDefication13());
			coverageDetails.setFamilyDefication15Amount(coverageDetailsEntity.getFamilyDefication15Amount());
			coverageDetails.setFamilyDeficationParents(coverageDetailsEntity.isFamilyDeficationParents());
			coverageDetails.setFamilyDeficationParentsAmount(coverageDetailsEntity.getFamilyDeficationParentsAmount());
			coverageDetails.setPolicyType(coverageDetailsEntity.getPolicyType());
			coverageDetails.setSumInsured(coverageDetailsEntity.getSumInsured());

			coverageDetails.setCreateDate(coverageDetailsEntity.getCreateDate());
			coverageDetails.setUpdateDate(coverageDetailsEntity.getUpdateDate());
			coverageDetails.setRecordStatus(coverageDetailsEntity.getRecordStatus());
			rfqCompleteDetailsDto.setCoverageDetails(coverageDetails);
		}

		List<PolicyTermsChildDto> policyTermChild = new ArrayList<>();
		List<PolicyTermsDto> policyTermsDetailsDto = new ArrayList<>();
		Optional<List<PolicyTermsEntity>> policyTermsDetails = policyTermsRepo.findByRfqId(rfqId);
		if (policyTermsDetails.isPresent()) {
			List<PolicyTermsEntity> policyTerms = policyTermsDetails.get();
			policyTerms.stream().forEach(j -> {
				PolicyTermsDto policyTermsDto = new PolicyTermsDto();
				policyTermsDto.setCreateDate(j.getCreateDate());
				PolicyTermsChildDto policyTermsChildDto = new PolicyTermsChildDto();
				policyTermsChildDto.setPolicyTermId(j.getId());
				policyTermsChildDto.setCoverageName(j.getCoverageName());
				policyTermsChildDto.setRemark(j.getRemark());
				policyTermChild.add(policyTermsChildDto);
				policyTermsDto.setPolicyDetails(policyTermChild);
				policyTermsDto.setRecordStatus(j.getRecordStatus());
				policyTermsDto.setRfqId(j.getRfqId());
				policyTermsDto.setUpdateDate(j.getUpdateDate());
				policyTermsDetailsDto.add(policyTermsDto);
				rfqCompleteDetailsDto.setPolicyTerms(policyTermsDetailsDto);
			});
		}

		return rfqCompleteDetailsDto;
	}

	@Override
	public String submitRfq(String rfqId, CorporateDetailsDto corporateDetailsDto) {
		if (rfqId != null || corporateDetailsDto != null) {
			Optional<CorporateDetailsEntity> findByrfqId = corporateDetailsRepo.findByRfqId(rfqId);
			log.info("findByrfqId {findByrfqId}");
			if (findByrfqId.isPresent()) {
				CorporateDetailsEntity corporateDetails = findByrfqId.get();
				if (corporateDetails.getRfqId().equals(rfqId)) {

					if (corporateDetailsDto.getPolicyType() != null) {
						corporateDetails.setPolicyType(corporateDetailsDto.getPolicyType());
					}
					if (corporateDetailsDto.getProdCategoryId() != null) {
						corporateDetails.setProdCategoryId(corporateDetailsDto.getProdCategoryId());
					}
					if (corporateDetailsDto.getCreateDate() != null) {
						corporateDetails.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
					}
					if (corporateDetailsDto.getAppStatus() != null) {
						corporateDetails.setAppStatus(corporateDetailsDto.getAppStatus());
					}
					if (corporateDetailsDto.getProductId() != null) {
						corporateDetails.setProductId(corporateDetailsDto.getProductId());
					}
					if (corporateDetailsDto.getRecordStatus() != null) {
						corporateDetails.setRecordStatus(corporateDetailsDto.getRecordStatus());
					}
					// to change the status
					if (corporateDetails.getAppStatus().equals("Closed")) {
						ClientList clientList = new ClientList();
						clientList.setClientName(corporateDetails.getInsuredName());
						clientList.setPolicyType(corporateDetails.getPolicyType());

						clientList.setRfqId(corporateDetails.getRfqId());
						clientList.setLocationId(corporateDetails.getLocation());

						clientList.setCreatedDate(LocalDateTime.now());
						clientList.setStatus("ACTIVE");
						clientListRepo.save(clientList);
					} else {
						Optional<ClientList> findByRfqId2 = clientListRepo.findByRfqId(corporateDetails.getRfqId());
						if (!findByRfqId2.isEmpty()) {
							ClientList clientList = findByRfqId2.get();
							clientList.setStatus("INACTIVE");
							clientListRepo.save(clientList);
						}
					}
					// changing Status Inactive

					corporateDetailsRepo.save(corporateDetails);
					return "RFQ Submitted Successfully";
				}
			}

		}
		return null;
	}

	@Override
	public List<AllRFQDetailsDto> getAllRFQs() {
		List<CorporateDetailsEntity> corporateDetailsList = corporateDetailsRepo.findAll();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		return corporateDetailsList.parallelStream().map(entity -> {
			AllRFQDetailsDto dto = new AllRFQDetailsDto();
			Product product = prodRepo.findByProductId(entity.getProductId()).get();
			ProductCategory productCategory = prodCategoryRepo.findById(entity.getProdCategoryId()).get();

			dto.setRfqId(entity.getRfqId());
			dto.setAppStatus(entity.getAppStatus());
			dto.setCreateDate(entity.getCreateDate());
			dto.setPolicyType(entity.getPolicyType());
			dto.setEmail(entity.getEmail());
			dto.setInsurerName(entity.getInsuredName());
			dto.setNob(entity.getNob());
			dto.setPhNo(entity.getPhNo());
			dto.setProductCategory(productCategory.getCategoryName());
			dto.setProduct(product.getProductName());
			return dto;
		}).filter(dto -> dto.getCreateDate() != null).sorted(Comparator
				.comparing(dto -> LocalDateTime.parse(dto.getCreateDate(), formatter), Comparator.reverseOrder()))
				.map(dto -> {
					LocalDateTime dateTime = LocalDateTime.parse(dto.getCreateDate(), formatter);
					String formattedDate = dateTime.format(outputFormatter);
					dto.setCreateDate(formattedDate);
					return dto;
				}).collect(Collectors.toList());
	}

	@Override
	public UpdateCorporateDetailsDto updateRFQ(String rfqIdValue, UpdateCorporateDetailsDto corporateDetailsDto) {
		if (rfqIdValue != null && corporateDetailsDto != null) {
			CorporateDetailsEntity corporateDetailsEntity = corporateDetailsRepo.findByRfqId(rfqIdValue).get();
			UpdateCorporateDetailsDto updatedDto = new UpdateCorporateDetailsDto();

			if (corporateDetailsDto.getProductId() != null) {
				Optional<Product> productOptional = prodRepo
						.findById(Long.parseLong(corporateDetailsDto.getProductId()));
				if (productOptional.isPresent()) {
					Product product = productOptional.get();
					corporateDetailsEntity.setProductId(product.getProductId());
					updatedDto.setProductId(product.getProductName());
				} else {
					// Handle the case when location is not found by throwing an exception or
					// logging an error
				}
			} else {
				corporateDetailsEntity.setProductId(null); // Set product to null if not provided
			}

			if (corporateDetailsDto.getProductCategoryId() != null) {
				Optional<ProductCategory> productCategoryOptional = prodCategoryRepo
						.findById(Long.parseLong(corporateDetailsDto.getProductCategoryId()));
				if (productCategoryOptional.isPresent()) {
					ProductCategory productCategory = productCategoryOptional.get();
					corporateDetailsEntity.setProdCategoryId(productCategory.getCategoryId());
					updatedDto.setProductCategoryId(productCategory.getCategoryName());
				} else {
					// Handle the case when location is not found by throwing an exception or
					// logging an error
				}
			} else {
				corporateDetailsEntity.setProdCategoryId(null); // Set location to null if not provided
			}

			Optional.ofNullable(corporateDetailsDto.getPolicyType()).ifPresentOrElse(
					corporateDetailsEntity::setPolicyType, () -> corporateDetailsEntity.setPolicyType(""));

			// Corporate Details
			Optional.ofNullable(corporateDetailsDto.getInsuredName()).ifPresentOrElse(
					corporateDetailsEntity::setInsuredName, () -> corporateDetailsEntity.setInsuredName(""));

			Optional.ofNullable(corporateDetailsDto.getAddress()).ifPresentOrElse(corporateDetailsEntity::setAddress,
					() -> corporateDetailsEntity.setAddress(""));

			Optional.ofNullable(corporateDetailsDto.getContactName()).ifPresentOrElse(
					corporateDetailsEntity::setContactName, () -> corporateDetailsEntity.setContactName(""));

			Optional.ofNullable(corporateDetailsDto.getNob()).ifPresentOrElse(corporateDetailsEntity::setNob,
					() -> corporateDetailsEntity.setNob(""));

			Optional.ofNullable(corporateDetailsDto.getNobCustom()).ifPresentOrElse(
					corporateDetailsEntity::setNobCustom, () -> corporateDetailsEntity.setNobCustom(""));

			Optional.ofNullable(corporateDetailsDto.getEmail()).ifPresentOrElse(corporateDetailsEntity::setEmail,
					() -> corporateDetailsEntity.setEmail(""));

			Optional.ofNullable(corporateDetailsDto.getPhNo()).ifPresentOrElse(corporateDetailsEntity::setPhNo,
					() -> corporateDetailsEntity.setPhNo(""));

			// Intermediary Details
			Optional.ofNullable(corporateDetailsDto.getIntermediaryName()).ifPresentOrElse(
					corporateDetailsEntity::setIntermediaryName, () -> corporateDetailsEntity.setIntermediaryName(""));

			Optional.ofNullable(corporateDetailsDto.getIntermediaryContactName()).ifPresentOrElse(
					corporateDetailsEntity::setIntermediaryContactName,
					() -> corporateDetailsEntity.setIntermediaryContactName(""));

			Optional.ofNullable(corporateDetailsDto.getIntermediaryEmail()).ifPresentOrElse(
					corporateDetailsEntity::setIntermediaryEmail,
					() -> corporateDetailsEntity.setIntermediaryEmail(""));

			Optional.ofNullable(corporateDetailsDto.getIntermediaryPhNo()).ifPresentOrElse(
					corporateDetailsEntity::setIntermediaryPhNo, () -> corporateDetailsEntity.setIntermediaryPhNo(""));

			// TPA Details
			Optional.ofNullable(corporateDetailsDto.getTpaName()).ifPresentOrElse(corporateDetailsEntity::setTpaName,
					() -> corporateDetailsEntity.setTpaName(""));

			Optional.ofNullable(corporateDetailsDto.getTpaContactName()).ifPresentOrElse(
					corporateDetailsEntity::setTpaContactName, () -> corporateDetailsEntity.setTpaContactName(""));

			Optional.ofNullable(corporateDetailsDto.getTpaEmail()).ifPresentOrElse(corporateDetailsEntity::setTpaEmail,
					() -> corporateDetailsEntity.setTpaEmail(""));

			Optional.ofNullable(corporateDetailsDto.getTpaPhNo()).ifPresentOrElse(corporateDetailsEntity::setTpaPhNo,
					() -> corporateDetailsEntity.setTpaPhNo(""));

			if (corporateDetailsDto.getLocation() != null) {
				Optional<Location> locationOptional = locationRepository
						.findById(Long.parseLong(corporateDetailsDto.getLocation()));
				if (locationOptional.isPresent()) {
					Location location = locationOptional.get();
					corporateDetailsEntity.setLocation(location);
				} else {
					// Handle the case when location is not found by throwing an exception or
					// logging an error
				}
			} else {
				corporateDetailsEntity.setLocation(null); // Set location to null if not provided
			}

			corporateDetailsEntity.setUpdateDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

			// Save the entity
			corporateDetailsRepo.save(corporateDetailsEntity);

			updatedDto.setPolicyType(corporateDetailsEntity.getPolicyType());
			updatedDto.setInsuredName(corporateDetailsEntity.getInsuredName());
			updatedDto.setAddress(corporateDetailsEntity.getAddress());
			updatedDto.setNob(corporateDetailsEntity.getNob());
			updatedDto.setEmail(corporateDetailsEntity.getEmail());
			updatedDto.setPhNo(corporateDetailsEntity.getPhNo());
			updatedDto.setContactName(corporateDetailsDto.getContactName());
			updatedDto.setIntermediaryName(corporateDetailsEntity.getIntermediaryName());
			updatedDto.setIntermediaryContactName(corporateDetailsEntity.getIntermediaryContactName());
			updatedDto.setIntermediaryEmail(corporateDetailsEntity.getIntermediaryEmail());
			updatedDto.setIntermediaryPhNo(corporateDetailsEntity.getIntermediaryPhNo());
			updatedDto.setTpaName(corporateDetailsEntity.getTpaName());
			updatedDto.setTpaContactName(corporateDetailsEntity.getTpaContactName());
			updatedDto.setTpaEmail(corporateDetailsEntity.getTpaEmail());
			updatedDto.setTpaPhNo(corporateDetailsEntity.getTpaPhNo());
			updatedDto.setLocation(corporateDetailsEntity.getLocation().getLocationName());
			// Return the updated DTO
			return updatedDto;
		}

		return null; // or throw an exception if needed
	}

	@Override
	public void sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentFileName) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(body);

			// Load the PDF file from the resources directory
			ClassPathResource pdfFile = new ClassPathResource(attachmentFileName);
			helper.addAttachment(attachmentFileName, pdfFile);

			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String sendEmailWithAttachments(InsureListDto insureListDto) {
		// Email configuration

		// Sender and recipient email addresses
		String senderEmail = username;

		// Set the mail properties
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);

		// Create a Session object with the authentication credentials
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// Create a MimeMessage object
			MimeMessage message = new MimeMessage(session);

			// Set the sender and recipient addresses
			message.setFrom(new InternetAddress(senderEmail));
			// Set the email subject and body
			message.setSubject("Insure List");
			message.setText("This test mail");

			// Send the email
			MimeBodyPart messageBodyPart2 = new MimeBodyPart();
			String filename = emailUrl;
			DataSource source = new FileDataSource(filename);
			messageBodyPart2.setDataHandler(new DataHandler(source));
			messageBodyPart2.setFileName(filename);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart2);
			message.setContent(multipart);
			Transport.send(message);
		} catch (MessagingException e) {
			e.getMessage();
		}
		return senderEmail;
	}

	@Override
	public byte[] getEmployeeData(String id) {

		Optional<CoverageDetailsEntity> findByRfqId = coverageDetailsRepo.findByRfqId(id);

		String empDepFilePath = findByRfqId.get().getEmpDepDataFilePath();
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		FileInputStream fileInputStream;

		try {
			 fileInputStream = new FileInputStream(new File(empDepFilePath));

			byte[] buffer = new byte[1024];
			int read;
			while ((read = fileInputStream.read(buffer)) != -1) {
				arrayOutputStream.write(buffer, 0, read);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return arrayOutputStream.toByteArray();

	}

	@Override
	public byte[] getIrdaData(String rfqId) throws IOException {
		CorporateDetailsEntity corporateDetails = corporateDetailsRepo.findByRfqId(rfqId).orElseThrow(); // Assuming
																											// this
																											// method
																											// always
																											// returns a
																											// value
		Optional<ExpiryPolicyDetails> expiryPolicyDetailsOptional = expiryPolicyDetailsRepository.findByrfqId(rfqId);
		Optional<ClaimsDetails> claimsDetailsOptional = claimsDetailsRepository.findByrfqId(rfqId);
		CoverageDetailsEntity coverageDetailsEntity = coverageDetailsRepo.findByRfqId(rfqId).orElseThrow(); // Assuming
																											// this
																											// method
																											// always
																											// returns a
																											// value
		List<String> employeeRelations = employeeRepository.findAllRelationShipByRfqId(rfqId)
				.orElse(Collections.emptyList());
		log.error("relation :{} ", employeeRelations);
		log.error("Count :{}", employeeRelations.size());

		ExpiryPolicyDetails expiryPolicyDetails = expiryPolicyDetailsOptional.orElse(null);
		ClaimsDetails claimsDetails = claimsDetailsOptional.orElse(null);

		return irda.generateEmployeeDataReport(corporateDetails, expiryPolicyDetails, claimsDetails,
				coverageDetailsEntity, employeeRelations);
	}

	@Override
	public DashBoardDto getCooperateDetailsInfoBasedOn_Month_Year(String month, int year) {

		int totalData = 0;
		Month monthData = Month.valueOf(month.toUpperCase());
		int monthValue = monthData.getValue();

		List<CooperateDetailsGraphDto> getAllDashBoardDetails = new LinkedList<>();
		Set<String> allAppStatus = corporateDetailsRepo.findAll().stream().filter(data -> data.getCreateDate() != null)
				.filter(data -> data.getAppStatus() != null).map(CorporateDetailsEntity::getAppStatus)
				.collect(Collectors.toSet());

		log.info("{} ", allAppStatus);

		for (String appStatusData : allAppStatus) {
			long count = corporateDetailsRepo.findAll().stream().filter(data -> data.getCreateDate() != null)
					.filter(data -> data.getAppStatus() != null).filter(data -> {
						String[] createDateParts = data.getCreateDate().split(" ")[0].split("-");
						int dbYear = Integer.parseInt(createDateParts[2]);
						int dbMonth = Integer.parseInt(createDateParts[1]);
						return dbYear == year && dbMonth == monthValue && data.getAppStatus().equals(appStatusData);
					}).count();

			totalData += count;
			getAllDashBoardDetails.add(CooperateDetailsGraphDto.builder().count(count).status(appStatusData).build());
		}

		return DashBoardDto.builder().getCooperateDetailsGraphDtos(getAllDashBoardDetails).totalCount(totalData)
				.build();
	}

	@Override
	public List<GetRfqCountWithLocationDto> getApplicationStatusCountByLocation(Long locationId) {
		// Check if the locationId is valid before proceeding
		Optional<Location> locationOptional = locationRepository.findById(locationId);
		if (locationOptional.isEmpty()) {
			// Handle the case where the location is not found
			return Collections.emptyList();
		}

		Location location = locationOptional.get();

		if (location == null) {
			return Collections.emptyList();
		}

		// Filter out null values before mapping to appStatus
		Set<String> collectStatus = corporateDetailsRepo.findAll().stream()
				.filter(data -> Objects.nonNull(data.getLocation()) && data.getLocation().equals(location))
				.map(data -> data.getAppStatus()).collect(Collectors.toSet());

		List<GetRfqCountWithLocationDto> getAllLocationData = new LinkedList<>();
		for (String value : collectStatus) {
			GetRfqCountWithLocationDto getRfqCount = new GetRfqCountWithLocationDto();
			long count = corporateDetailsRepo.findAll().stream()
					.filter(data1 -> Objects.nonNull(data1.getAppStatus())
							&& data1.getAppStatus().equalsIgnoreCase(value) && Objects.nonNull(data1.getLocation())
							&& data1.getLocation().getLocationId() == locationId)
					.count();
			getRfqCount.setAppStatus(value);
			getRfqCount.setCount(count);
			getAllLocationData.add(getRfqCount);
		}
		return getAllLocationData;
	}

}
