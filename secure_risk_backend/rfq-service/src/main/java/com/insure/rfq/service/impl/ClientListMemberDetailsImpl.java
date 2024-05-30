package com.insure.rfq.service.impl;

import com.insure.rfq.dto.*;
import com.insure.rfq.entity.*;
import com.insure.rfq.exception.*;
import com.insure.rfq.login.entity.Department;
import com.insure.rfq.login.entity.Designation;
import com.insure.rfq.login.repository.DepartmentRepository;
import com.insure.rfq.login.repository.DesignationRepository;
import com.insure.rfq.repository.*;
import com.insure.rfq.service.ClientListMemberDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientListMemberDetailsImpl implements ClientListMemberDetailsService {
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private DesignationRepository designationRepository;
	@Autowired
	private ClientListMemberDetailsRepository clientListMemberDetailsRepository;
	@Value("classpath:excelTemplate/Addition format.xls")
	Resource resourceFile;
	@Autowired
	private TpaRepository tpaRepository;
	@Autowired
	private ClientListEnrollementHeadersMappingRepository clientListEnrollementHeadersMappingRepository;
	@Autowired
	private ClientListEnrollmentEntityRepository clientListEnrollmentEntityRepository;

	public ClientListMemberDetailsImpl(ClientListRepository clientListRepository, ProductRepository productRepository,
			DepartmentRepository departmentRepository, DesignationRepository designationRepository,
			ClientListMemberDetailsRepository clientListMemberDetailsRepository) {
		this.clientListRepository = clientListRepository;
		this.productRepository = productRepository;
		this.departmentRepository = departmentRepository;
		this.designationRepository = designationRepository;
		this.clientListMemberDetailsRepository = clientListMemberDetailsRepository;
	}

	@Override
	public ResponseDto createClientListMembersDetails(Long clientListId, Long productId,
			AddClientListMemberDetailsDto clientListMemberDetailsDto) {
		ClientListMemberDetails clientListMemberDetails = new ClientListMemberDetails();
		StringBuilder errorMessages = new StringBuilder();
//        if (clientListId!=null) {
//            ClientList clientList = clientListRepository.findById(clientListId).orElseThrow(() -> new InvalidClientList("ClientList Not Found"));
//            clientListMemberDetails.setRfqId(clientList.getRfqId());
//        }
		if (clientListId != null) {
			try {
				ClientList clientList = clientListRepository.findById(clientListId)
						.orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
				clientListMemberDetails.setClientList(clientList);
				clientListMemberDetails.setRfqId(clientList.getRfqId());
			} catch (InvalidClientList e) {
				errorMessages.append("ClientList is not Found with Id : ").append(clientListId).append(" . ");
			}
		}
		if (productId != null) {
			try {
				Product product = productRepository.findById(productId)
						.orElseThrow(() -> new InvalidProduct("Product is not Found"));
				clientListMemberDetails.setProduct(product);
			} catch (InvalidProduct e) {
				errorMessages.append("Product is not Found with Id : ").append(productId).append(" . ");
			}
		}

		if (clientListMemberDetailsDto.getDepartmentId() != null) {
			try {
				Department department = departmentRepository
						.findById(Long.parseLong(clientListMemberDetailsDto.getDepartmentId()))
						.orElseThrow(() -> new InvalidDepartmentException("Department is not Found"));
				clientListMemberDetails.setDepartment(department);
			} catch (InvalidDepartmentException e) {
				errorMessages.append("Department  is not Found with Id : ").append(" . ");
			}
		}
		if (clientListMemberDetailsDto.getDesignationId() != null) {
			try {
				Designation designation = designationRepository
						.findById(Long.parseLong(clientListMemberDetailsDto.getDesignationId()))
						.orElseThrow(() -> new InvalidDesignationException("Designation is not Found"));
				clientListMemberDetails.setDesignation(designation);
			} catch (InvalidDepartmentException e) {
				errorMessages.append("Designation is not Found with Id : ").append(" . ");
			}

		}
		clientListMemberDetails.setEmployeeNo(clientListMemberDetailsDto.getEmployeeNo());
		clientListMemberDetails.setName(clientListMemberDetailsDto.getName());
		clientListMemberDetails.setRelationShip(clientListMemberDetailsDto.getRelationShip());
		clientListMemberDetails.setGender(clientListMemberDetailsDto.getGender());
		clientListMemberDetails.setDateOfBirth(clientListMemberDetailsDto.getDateOfBirth());
		clientListMemberDetails.setAge(clientListMemberDetailsDto.getAge());
		clientListMemberDetails.setSumInsured(clientListMemberDetailsDto.getSumInsured());
		clientListMemberDetails.setEmail(clientListMemberDetailsDto.getEmail());
		clientListMemberDetails.setPhoneNumber(clientListMemberDetailsDto.getPhoneNumber());
		clientListMemberDetails.setRole(clientListMemberDetailsDto.getRole());
		clientListMemberDetails.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		clientListMemberDetails.setRecordStatus("ACTIVE");

		if (errorMessages.length() == 0) {
			clientListMemberDetailsRepository.save(clientListMemberDetails);
		}

		// If there are any error messages, return them
		if (errorMessages.length() > 0) {
			return new ResponseDto(errorMessages.toString());
		}
		return new ResponseDto("Created Sucessfully");

	}

	@Override
	public List<GetAllClientListMembersDetailsDto> getAllClientListMembersDetails(Long clientListId, Long productId,
			String month) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
		List<GetAllClientListMembersDetailsDto> listOfMembersDetails = clientListMemberDetailsRepository.findAll()
				.stream().filter(c -> clientListId != null && c.getClientList().getCid() == clientListId)
				.filter(c -> productId != null && c.getProduct().getProductId().equals(productId)).filter(c -> {
					if (month.equalsIgnoreCase("ALL")) {
						return true; // Return true for all records if month is "ALL"
					}
					try {
						LocalDateTime dateTime = LocalDateTime.parse(c.getCreatedDate(), formatter);
						String monthName = dateTime.getMonth().name(); // Get the month name from the date
						return monthName.equalsIgnoreCase(month); // Check if it matches the desired month
					} catch (NullPointerException | DateTimeParseException e) {
						return false; // Handle null or invalid dates
					}
				}).map(c -> {
					GetAllClientListMembersDetailsDto getAllClientListMembersDetailsDto = new GetAllClientListMembersDetailsDto();
					getAllClientListMembersDetailsDto.setMemberId(c.getMemberId());
					getAllClientListMembersDetailsDto.setEmployeeNo(c.getEmployeeNo());
					getAllClientListMembersDetailsDto.setName(c.getName());
					getAllClientListMembersDetailsDto.setRole(c.getRole());
					getAllClientListMembersDetailsDto.setRelationShip(c.getRelationShip());
					getAllClientListMembersDetailsDto.setGender(c.getGender());

					try {
						LocalDateTime dateTime = LocalDateTime.parse(c.getCreatedDate(), formatter);
						String monthName = dateTime.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH));
						getAllClientListMembersDetailsDto.setMonth(monthName);
						
					} catch (NullPointerException | DateTimeParseException e) {
						getAllClientListMembersDetailsDto.setMonth(null);
					}
					getAllClientListMembersDetailsDto.setAge(c.getAge());
					getAllClientListMembersDetailsDto.setSumInsured(c.getSumInsured());
					getAllClientListMembersDetailsDto.setEmail(c.getEmail());
					getAllClientListMembersDetailsDto.setPhoneNumber(c.getPhoneNumber());
					if (c.getDepartment() != null) {
						getAllClientListMembersDetailsDto.setDepartment(c.getDepartment().getDepartmentName());
					} else {
						getAllClientListMembersDetailsDto.setDepartment(null);
					}
					if (c.getDesignation() != null) {
						getAllClientListMembersDetailsDto.setDesignation(c.getDesignation().getDesignationName());
					} else {
						getAllClientListMembersDetailsDto.setDesignation(null);
					}
					return getAllClientListMembersDetailsDto;
				}).toList();
		if (listOfMembersDetails.isEmpty()) {
			throw new InvalidClientListMemberDetailsException("No Details are found for clientList and Product ");
		}
		return listOfMembersDetails;
	}

	@Override
	public byte[] getActiveListInExcelFormat(Long clientListId, Long productId) {
		List<ClientListMemberDetails> clientListMemberDetails = clientListMemberDetailsRepository.findAll().stream()
				.filter(c -> c.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(c -> clientListId != null && c.getClientList().getCid() == clientListId)
				.filter(c -> productId != null && c.getProduct().getProductId().equals(productId)).toList();

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Client List Members");

			// Create headers
			Row headerRow = sheet.createRow(0);
			String[] headers = { "Member ID", "Employee No", "Name", "Relationship", "Gender", "Date of Birth", "Age",
					"Sum Insured", "Email", "Phone Number", "Designation", "Department", "Role" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
			}

			// Populate data
			int rowNum = 1;
			for (ClientListMemberDetails member : clientListMemberDetails) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(member.getMemberId());
				row.createCell(1).setCellValue(member.getEmployeeNo());
				row.createCell(2).setCellValue(member.getName());
				row.createCell(3).setCellValue(member.getRelationShip());
				row.createCell(4).setCellValue(member.getGender());
				row.createCell(5).setCellValue(member.getDateOfBirth());
				row.createCell(6).setCellValue(member.getAge());
				row.createCell(7).setCellValue(member.getSumInsured());
				row.createCell(8).setCellValue(member.getEmail());
				row.createCell(9).setCellValue(member.getPhoneNumber());
				row.createCell(10).setCellValue(member.getDesignation().getDesignationName());
				row.createCell(11).setCellValue(member.getDepartment().getDepartmentName());
				row.createCell(12).setCellValue(member.getRole());
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception properly in your application
			return new byte[0]; // Return empty byte array if an error occurs
		}
	}

	@Override
	public byte[] getAdditionListInExcelFormat(Long clientListId, Long productId) {
		List<ClientListMemberDetails> clientListMemberDetails = clientListMemberDetailsRepository.findAll().stream()
				.filter(c -> c.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(c -> clientListId != null && c.getClientList().getCid() == clientListId)
				.filter(c -> productId != null && c.getProduct().getProductId().equals(productId)).toList();

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Client List Members");

			// Create headers
			Row headerRow = sheet.createRow(0);
			String[] headers = { "Member ID", "Employee No", "Name", "Relationship", "Gender", "Date of Birth", "Age",
					"Sum Insured", "Email", "Phone Number", "Designation", "Department", "Role" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
			}

			// Populate data
			int rowNum = 1;
			for (ClientListMemberDetails member : clientListMemberDetails) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(member.getMemberId());
				row.createCell(1).setCellValue(member.getEmployeeNo());
				row.createCell(2).setCellValue(member.getName());
				row.createCell(3).setCellValue(member.getRelationShip());
				row.createCell(4).setCellValue(member.getGender());
				row.createCell(5).setCellValue(member.getDateOfBirth());
				row.createCell(6).setCellValue(member.getAge());
				row.createCell(7).setCellValue(member.getSumInsured());
				row.createCell(8).setCellValue(member.getEmail());
				row.createCell(9).setCellValue(member.getPhoneNumber());
				row.createCell(10).setCellValue(member.getDesignation().getDesignationName());
				row.createCell(11).setCellValue(member.getDepartment().getDepartmentName());
				row.createCell(12).setCellValue(member.getRole());
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception properly in your application
			return new byte[0]; // Return empty byte array if an error occurs
		}
	}

	@Override
	public byte[] getDeletedListInExcelFormat(Long clientListId, Long productId) {
		List<ClientListMemberDetails> clientListMemberDetails = clientListMemberDetailsRepository.findAll().stream()
				.filter(c -> c.getDeletedStatus() != null && c.getDeletedStatus().equalsIgnoreCase("ACTIVE"))
				.filter(c -> clientListId != null && c.getClientList().getCid() == clientListId)
				.filter(c -> productId != null && c.getProduct().getProductId().equals(productId)).toList();

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Client List Members");

			// Create headers
			Row headerRow = sheet.createRow(0);
			String[] headers = { "Member ID", "Employee No", "Name", "Relationship", "Gender", "Date of Birth", "Age",
					"Sum Insured", "Email", "Phone Number", "Designation", "Department", "Role" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
			}

			// Populate data
			int rowNum = 1;
			for (ClientListMemberDetails member : clientListMemberDetails) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(member.getMemberId());
				row.createCell(1).setCellValue(member.getEmployeeNo());
				row.createCell(2).setCellValue(member.getName());
				row.createCell(3).setCellValue(member.getRelationShip());
				row.createCell(4).setCellValue(member.getGender());
				row.createCell(5).setCellValue(member.getDateOfBirth());
				row.createCell(6).setCellValue(member.getAge());
				row.createCell(7).setCellValue(member.getSumInsured());
				row.createCell(8).setCellValue(member.getEmail());
				row.createCell(9).setCellValue(member.getPhoneNumber());
				row.createCell(10).setCellValue(member.getDesignation().getDesignationName());
				row.createCell(11).setCellValue(member.getDepartment().getDepartmentName());
				row.createCell(12).setCellValue(member.getRole());
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception properly in your application
			return new byte[0]; // Return empty byte array if an error occurs
		}
	}

	@Override
	public byte[] getCorrectionListInExcelFormat(Long clientListId, Long productId) {
		List<ClientListMemberDetails> clientListMemberDetails = clientListMemberDetailsRepository.findAll().stream()
				.filter(c -> c.getRecordStatus().equalsIgnoreCase("ACTIVE") && c.getUpdatedStatus() != null
						&& c.getUpdatedStatus().equalsIgnoreCase("ACTIVE"))
				.filter(c -> clientListId != null && c.getClientList().getCid() == clientListId)
				.filter(c -> productId != null && c.getProduct().getProductId().equals(productId)).toList();

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Client List Members");

			// Create headers
			Row headerRow = sheet.createRow(0);
			String[] headers = { "Member ID", "Employee No", "Name", "Relationship", "Gender", "Date of Birth", "Age",
					"Sum Insured", "Email", "Phone Number", "Designation", "Department", "Role" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
			}

			// Populate data
			int rowNum = 1;
			for (ClientListMemberDetails member : clientListMemberDetails) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(member.getMemberId());
				row.createCell(1).setCellValue(member.getEmployeeNo());
				row.createCell(2).setCellValue(member.getName());
				row.createCell(3).setCellValue(member.getRelationShip());
				row.createCell(4).setCellValue(member.getGender());
				row.createCell(5).setCellValue(member.getDateOfBirth());
				row.createCell(6).setCellValue(member.getAge());
				row.createCell(7).setCellValue(member.getSumInsured());
				row.createCell(8).setCellValue(member.getEmail());
				row.createCell(9).setCellValue(member.getPhoneNumber());
				row.createCell(10).setCellValue(member.getDesignation().getDesignationName());
				row.createCell(11).setCellValue(member.getDepartment().getDepartmentName());
				row.createCell(12).setCellValue(member.getRole());
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception properly in your application
			return new byte[0]; // Return empty byte array if an error occurs
		}
	}

	@Override
	public byte[] getEnrollmentListInExcelFormat(Long clientListId, Long productId) {
		List<ClientListEnrollementEntity> clientListMemberDetails = clientListEnrollmentEntityRepository.findAll()
				.stream().filter(c -> c.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(c -> c.getClientList().getCid() == clientListId)
				.filter(c -> c.getProduct().getProductId().equals(productId)).toList();

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Client List Members");

			// Create headers
			Row headerRow = sheet.createRow(0);
			String[] headers = { "Employee No", "Name", "Relationship", "Gender", "Date of Birth", "Age",
					"ECardNumber" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
			}

			// Populate data
			int rowNum = 1;
			for (ClientListEnrollementEntity member : clientListMemberDetails) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(member.getEmployeeId());
				row.createCell(1).setCellValue(member.getEmployeeName());
				row.createCell(2).setCellValue(member.getRelation());
				row.createCell(3).setCellValue(member.getGender());
				row.createCell(4).setCellValue(member.getDateOfBirth());
				row.createCell(5).setCellValue(member.getAge());
				row.createCell(6).setCellValue(member.getECardNumber());

			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception properly in your application
			return new byte[0]; // Return empty byte array if an error occurs
		}
	}

	@Override
	public byte[] getPendingListInExcelFormat(Long clientListId, Long productId) {
		List<ClientListEnrollementEntity> enrollmentList = clientListEnrollmentEntityRepository.findAll().stream()
				.filter(i -> productId != null && i.getProduct() != null
						&& Objects.equals(i.getProduct().getProductId(), productId))
				.filter(i -> clientListId != null && i.getClientList() != null
						&& Objects.equals(i.getClientList().getCid(), clientListId))
				.toList();

		LocalDate currentDate = LocalDate.now();
		LocalDateTime startOfCurrentDay = currentDate.atStartOfDay();

		List<ClientListMemberDetails> membersList = clientListMemberDetailsRepository.findAll().stream()
				.filter(i -> clientListId != null && i.getClientList().getCid() == clientListId)
				.filter(i -> i.getProduct().getProductId().equals(productId)).filter(i -> {
					// Parse the createdDate to extract day part
					LocalDate createdDay = LocalDate.parse(i.getCreatedDate(),
							DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault()));
					// Compare the day part with the current day
					return createdDay.equals(currentDate);
				}).toList();

		List<GetAllClientListPendingListDto> pendingListDto = new ArrayList<>();

		for (ClientListMemberDetails member : membersList) {
			boolean foundInEnrollment = false;
			for (ClientListEnrollementEntity enrollment : enrollmentList) {
				if (Objects.equals(enrollment.getEmployeeId(), member.getEmployeeNo())) {
					foundInEnrollment = true;
					break;
				}
			}
			if (!foundInEnrollment) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
				GetAllClientListPendingListDto dto = new GetAllClientListPendingListDto();
				dto.setMemberId(member.getMemberId());
				dto.setEmployeeNo(member.getEmployeeNo());
				dto.setName(member.getName());
				dto.setRelationShip(member.getRelationShip());
				dto.setEmail(member.getEmail());
				dto.setPhoneNumber(member.getPhoneNumber());
				dto.setSumInsured(member.getSumInsured());
				dto.setRole(member.getRole());
				if (member.getDesignation() != null) {
					Designation designation = designationRepository.findById(member.getDesignation().getId())
							.orElseThrow(() -> new InvalidDesignationException("Designation is not Valid"));
					dto.setDesignation(designation.getDesignationName());
				}
				if (member.getDepartment() != null) {
					Department department = departmentRepository.findById(member.getDepartment().getId())
							.orElseThrow(() -> new InvalidDepartmentException("Department is not Valid"));
					dto.setDepartment(department.getDepartmentName());
				}
				LocalDateTime dateTime = LocalDateTime.parse(member.getCreatedDate(), formatter);
				String monthName = dateTime.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH));
				dto.setMonth(monthName); // You may need to adjust this field based on your requirement
				if (member.getDeletedStatus() != null && !member.getDeletedStatus().isEmpty()
						&& member.getDeletedStatus().equalsIgnoreCase("ACTIVE")) {
					dto.setStatus("to be deleted");
				} else if (member.getUpdatedStatus() != null && member.getUpdatedStatus().equalsIgnoreCase("ACTIVE")) {
					dto.setStatus("to be updated");
				} else {
					dto.setStatus("pending"); // Or any other default status you prefer
				}
				pendingListDto.add(dto);
			}
		}

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Pending List Members");

			// Create headers
			Row headerRow = sheet.createRow(0);
			String[] headers = { "Member ID", "Employee No", "Name", "Relationship", "Email", "Phone Number",
					"Sum Insured", "Role", "Designation", "Department", "Month", "Status" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
			}

			// Populate data
			int rowNum = 1;
			for (GetAllClientListPendingListDto dto : pendingListDto) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(dto.getMemberId());
				row.createCell(1).setCellValue(dto.getEmployeeNo());
				row.createCell(2).setCellValue(dto.getName());
				row.createCell(3).setCellValue(dto.getRelationShip());
				row.createCell(4).setCellValue(dto.getEmail());
				row.createCell(5).setCellValue(dto.getPhoneNumber());
				row.createCell(6).setCellValue(dto.getSumInsured());
				row.createCell(7).setCellValue(dto.getRole());
				row.createCell(8).setCellValue(dto.getDesignation());
				row.createCell(9).setCellValue(dto.getDepartment());
				row.createCell(10).setCellValue(dto.getMonth());
				row.createCell(11).setCellValue(dto.getStatus());
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception properly in your application
			return new byte[0]; // Return empty byte array if an error occurs
		}
	}

	@Override
	public AddClientListMemberDetailsDto updateClientListMemberDetails(Long memberDetailsId,
			AddClientListMemberDetailsDto clientListMemberDetailsDto) {
		if (memberDetailsId != null) {
			ClientListMemberDetails clientListMemberDetails = clientListMemberDetailsRepository
					.findById(memberDetailsId)
					.orElseThrow(() -> new InvalidClientListMemberDetailsException("Invalid Member Details"));
			clientListMemberDetails.setEmployeeNo(clientListMemberDetailsDto.getEmployeeNo());
			clientListMemberDetails.setName(clientListMemberDetailsDto.getName());
			clientListMemberDetails.setRelationShip(clientListMemberDetailsDto.getRelationShip());
			clientListMemberDetails.setGender(clientListMemberDetailsDto.getGender());
			clientListMemberDetails.setDateOfBirth(clientListMemberDetailsDto.getDateOfBirth());
			clientListMemberDetails.setAge(clientListMemberDetailsDto.getAge());
			clientListMemberDetails.setSumInsured(clientListMemberDetailsDto.getSumInsured());
			clientListMemberDetails.setEmail(clientListMemberDetailsDto.getEmail());
			clientListMemberDetails.setPhoneNumber(clientListMemberDetailsDto.getPhoneNumber());
			clientListMemberDetails.setRole(clientListMemberDetailsDto.getRole());
			clientListMemberDetails.setUpdatedStatus("ACTIVE");
			if (clientListMemberDetailsDto.getDesignationId() != null) {
				Designation designation = designationRepository
						.findById(Long.parseLong(clientListMemberDetailsDto.getDesignationId()))
						.orElseThrow(() -> new InvalidDesignationException("Designation is not Valid"));
				clientListMemberDetails.setDesignation(designation);
				clientListMemberDetailsDto.setDesignationId(designation.getDesignationName());
			}
			if (clientListMemberDetailsDto.getDepartmentId() != null) {
				Department department = departmentRepository
						.findById(Long.parseLong(clientListMemberDetailsDto.getDepartmentId()))
						.orElseThrow(() -> new InvalidDepartmentException("Department is not Valid"));
				clientListMemberDetails.setDepartment(department);
				clientListMemberDetailsDto.setDepartmentId(department.getDepartmentName());
			}
			clientListMemberDetails.setUpdatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
			ClientListMemberDetails savedClientListMemberDetails = clientListMemberDetailsRepository
					.save(clientListMemberDetails);
			clientListMemberDetailsDto.setEmployeeNo(savedClientListMemberDetails.getEmployeeNo());
			clientListMemberDetailsDto.setName(savedClientListMemberDetails.getName());
			clientListMemberDetailsDto.setRelationShip(savedClientListMemberDetails.getRelationShip());
			clientListMemberDetailsDto.setGender(savedClientListMemberDetails.getGender());
			clientListMemberDetailsDto.setDateOfBirth(savedClientListMemberDetails.getDateOfBirth());
			clientListMemberDetailsDto.setAge(savedClientListMemberDetails.getAge());
			clientListMemberDetailsDto.setSumInsured(savedClientListMemberDetails.getSumInsured());
			clientListMemberDetailsDto.setEmail(savedClientListMemberDetails.getEmail());
			clientListMemberDetailsDto.setPhoneNumber(savedClientListMemberDetails.getPhoneNumber());
			clientListMemberDetailsDto.setRole(savedClientListMemberDetails.getRole());
			return clientListMemberDetailsDto;
		}
		return null;
	}

	@Override
	public String deleteClientListMemberDetails(Long memberDetailsId) {
		if (memberDetailsId != null) {
			ClientListMemberDetails clientListMemberDetails = clientListMemberDetailsRepository
					.findById(memberDetailsId)
					.orElseThrow(() -> new InvalidClientListMemberDetailsException("Invalid Member Details"));
			clientListMemberDetails.setRecordStatus("INACTIVE");
			clientListMemberDetails.setDeletedStatus("ACTIVE");
			clientListMemberDetailsRepository.save(clientListMemberDetails);
			return "Deleted Successfully";
		}
		return "Unable to Delete";
	}

	@Override
	public List<GetClientListMemberDetailsActiveListCountDto> getAllActiveListForMemberDetailsByClientListProduct(
			Long clientListId, Long productId, String month) {
		// Fetch and filter active members based on the given criteria
		List<ClientListMemberDetails> activeMembers = clientListMemberDetailsRepository.findAll().stream()
				.filter(i -> clientListId != null && i.getClientList().getCid()==(clientListId))
				.filter(i -> i.getProduct().getProductId().equals(productId))
				.filter(i -> i.getUpdatedStatus() == null || !i.getUpdatedStatus().equalsIgnoreCase("ACTIVE"))
				.filter(i -> i.getDeletedStatus() == null || !i.getDeletedStatus().equalsIgnoreCase("ACTIVE"))
				.toList();

		// Calculate counts once to avoid redundant computations
		long employeeCount = activeMembers.stream()
				.filter(member -> member.getRelationShip().equalsIgnoreCase("Employee"))
				.count();
		long dependentCount = activeMembers.size() - employeeCount;
		long totalCount = activeMembers.size();

		// Map retrieved entities to DTOs
		return activeMembers.stream().map(c -> {
			GetClientListMemberDetailsActiveListCountDto dto = new GetClientListMemberDetailsActiveListCountDto();
			dto.setMemberId(c.getMemberId());
			dto.setEmployeeNo(c.getEmployeeNo());
			dto.setName(c.getName());
			dto.setRelationShip(c.getRelationShip());
			dto.setRole(c.getRole());
			dto.setGender(c.getGender());
			dto.setAge(c.getAge());
			dto.setSumInsured(c.getSumInsured());
			dto.setEmail(c.getEmail());
			dto.setPhoneNumber(c.getPhoneNumber());
			dto.setEmployeeCount(employeeCount);
			dto.setDependentCount(dependentCount);
			dto.setTotalCount(totalCount);

			if (c.getDepartment() != null) {
				dto.setDepartment(c.getDepartment().getDepartmentName());
			}
			if (c.getDesignation() != null) {
				dto.setDesignation(c.getDesignation().getDesignationName());
			}

			try {
				SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
				SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
				Date date = originalFormat.parse(c.getCreatedDate());
				String monthName = targetFormat.format(date);
				dto.setMonth(monthName);
			} catch (ParseException | NullPointerException e) {
				dto.setMonth(null);
			}

			return dto;
		}).toList();
	}

	@Override
	public AddClientListMemberDetailsDto getClaimsMemberDetailsByClaimsMemberId(Long memberDetailsId) {
		if (memberDetailsId != null) {
			ClientListMemberDetails clientListMemberDetails = clientListMemberDetailsRepository
					.findById(memberDetailsId).orElseThrow(
							() -> new InvalidClientListMemberDetailsException("Invalid ClientList Member Details"));

			AddClientListMemberDetailsDto clientListMemberDetailsDto = new AddClientListMemberDetailsDto();

			clientListMemberDetailsDto.setEmployeeNo(clientListMemberDetails.getEmployeeNo());
			clientListMemberDetailsDto.setName(clientListMemberDetails.getName());
			clientListMemberDetailsDto.setRelationShip(clientListMemberDetails.getRelationShip());
			clientListMemberDetailsDto.setGender(clientListMemberDetails.getGender());
			clientListMemberDetailsDto.setDateOfBirth(clientListMemberDetails.getDateOfBirth());
			clientListMemberDetailsDto.setAge(clientListMemberDetails.getAge());
			clientListMemberDetailsDto.setSumInsured(clientListMemberDetails.getSumInsured());
			clientListMemberDetailsDto.setEmail(clientListMemberDetails.getEmail());
			clientListMemberDetailsDto.setPhoneNumber(clientListMemberDetails.getPhoneNumber());

			if (clientListMemberDetails.getDesignation() != null) {
				Designation designation = designationRepository
						.findById(clientListMemberDetails.getDesignation().getId())
						.orElseThrow(() -> new InvalidDesignationException("Designation is not Valid"));
				clientListMemberDetailsDto.setDesignationId(designation.getDesignationName());
			}
			if (clientListMemberDetails.getDepartment() != null) {
				Department department = departmentRepository.findById(clientListMemberDetails.getDepartment().getId())
						.orElseThrow(() -> new InvalidDepartmentException("Department is not Valid"));
				clientListMemberDetailsDto.setDepartmentId(department.getDepartmentName());
			}
			clientListMemberDetailsDto.setRole(clientListMemberDetails.getRole());
			return clientListMemberDetailsDto;
		}
		return null;
	}

	@Override
	public List<GetAllClientListMembersDetailsDto> getAllDeletedListForMemberDetailsByClientListProduct(
			Long clientListId, Long productId, String month) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		List<GetAllClientListMembersDetailsDto> listOfMembersDetails = clientListMemberDetailsRepository.findAll()
				.stream().filter(c -> c.getDeletedStatus() != null && c.getDeletedStatus().equalsIgnoreCase("ACTIVE"))
				.filter(c -> clientListId != null && c.getClientList().getCid() == clientListId)
				.filter(c -> productId != null && c.getProduct().getProductId().equals(productId)).filter(c -> {
					if (month.equalsIgnoreCase("ALL")) {
						return true; // Return true for all records if month is "ALL"
					}
					try {
						LocalDateTime dateTime = LocalDateTime.parse(c.getCreatedDate(), formatter);
						String monthName = dateTime.getMonth().name(); // Get the month name from the date
						return monthName.equalsIgnoreCase(month); // Check if it matches the desired month
					} catch (NullPointerException | DateTimeParseException e) {
						return false; // Handle null or invalid dates
					}
				}).map(c -> {
					GetAllClientListMembersDetailsDto getAllClientListMembersDetailsDto = new GetAllClientListMembersDetailsDto();
					getAllClientListMembersDetailsDto.setMemberId(c.getMemberId());
					getAllClientListMembersDetailsDto.setEmployeeNo(c.getEmployeeNo());
					getAllClientListMembersDetailsDto.setName(c.getName());
					getAllClientListMembersDetailsDto.setRelationShip(c.getRelationShip());
					getAllClientListMembersDetailsDto.setRole(c.getRole());
					getAllClientListMembersDetailsDto.setGender(c.getGender());
					try {
						Calendar cal = Calendar.getInstance();
						cal.setTime(sdf.parse(c.getDateOfBirth()));
						String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.getTime());
						getAllClientListMembersDetailsDto.setMonth(monthName);
					} catch (NullPointerException | ParseException e) {
						getAllClientListMembersDetailsDto.setMonth(null);
					}
					getAllClientListMembersDetailsDto.setAge(c.getAge());
					getAllClientListMembersDetailsDto.setSumInsured(c.getSumInsured());
					getAllClientListMembersDetailsDto.setEmail(c.getEmail());
					getAllClientListMembersDetailsDto.setPhoneNumber(c.getPhoneNumber());
					if (c.getDepartment() != null) {
						getAllClientListMembersDetailsDto.setDepartment(c.getDepartment().getDepartmentName());
					} else {
						getAllClientListMembersDetailsDto.setDepartment(null);
					}
					if (c.getDesignation() != null) {
						getAllClientListMembersDetailsDto.setDesignation(c.getDesignation().getDesignationName());
					} else {
						getAllClientListMembersDetailsDto.setDesignation(null);
					}
					return getAllClientListMembersDetailsDto;
				}).toList();

		return listOfMembersDetails;

	}

	@Override
	public List<GetAllClientListMembersDetailsDto> getAllCorrectionsListForMemberDetailsByClientListProduct(
			Long clientListId, Long productId, String month) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		List<GetAllClientListMembersDetailsDto> listOfMembersDetails = clientListMemberDetailsRepository.findAll()
				.stream().filter(c -> c.getUpdatedStatus() != null && c.getUpdatedStatus().equalsIgnoreCase("ACTIVE"))
				.filter(c -> clientListId != null && c.getClientList().getCid() == clientListId)
				.filter(c -> productId != null && c.getProduct().getProductId().equals(productId)).filter(c -> {
					if (month.equalsIgnoreCase("ALL")) {
						return true; // Return true for all records if month is "ALL"
					}
					try {
						LocalDateTime dateTime = LocalDateTime.parse(c.getCreatedDate(), formatter);
						String monthName = dateTime.getMonth().name(); // Get the month name from the date
						return monthName.equalsIgnoreCase(month); // Check if it matches the desired month
					} catch (NullPointerException | DateTimeParseException e) {
						return false; // Handle null or invalid dates
					}
				}).map(c -> {
					GetAllClientListMembersDetailsDto getAllClientListMembersDetailsDto = new GetAllClientListMembersDetailsDto();
					getAllClientListMembersDetailsDto.setMemberId(c.getMemberId());
					getAllClientListMembersDetailsDto.setEmployeeNo(c.getEmployeeNo());
					getAllClientListMembersDetailsDto.setName(c.getName());
					getAllClientListMembersDetailsDto.setRelationShip(c.getRelationShip());
					getAllClientListMembersDetailsDto.setRole(c.getRole());
					getAllClientListMembersDetailsDto.setGender(c.getGender());
					try {
						Calendar cal = Calendar.getInstance();
						cal.setTime(sdf.parse(c.getDateOfBirth()));
						String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.getTime());
						getAllClientListMembersDetailsDto.setMonth(monthName);
					} catch (NullPointerException | ParseException e) {
						getAllClientListMembersDetailsDto.setMonth(null);
					}
					getAllClientListMembersDetailsDto.setAge(c.getAge());
					getAllClientListMembersDetailsDto.setSumInsured(c.getSumInsured());
					getAllClientListMembersDetailsDto.setEmail(c.getEmail());
					getAllClientListMembersDetailsDto.setPhoneNumber(c.getPhoneNumber());
					if (c.getDepartment() != null) {
						getAllClientListMembersDetailsDto.setDepartment(c.getDepartment().getDepartmentName());
					} else {
						getAllClientListMembersDetailsDto.setDepartment(null);
					}
					if (c.getDesignation() != null) {
						getAllClientListMembersDetailsDto.setDesignation(c.getDesignation().getDesignationName());
					} else {
						getAllClientListMembersDetailsDto.setDesignation(null);
					}
					return getAllClientListMembersDetailsDto;
				}).toList();

		return listOfMembersDetails;
	}

	@Override
	public byte[] getActiveListTemplate() throws IOException {
		// Check if the resource exists
		if (!resourceFile.exists()) {
			throw new IOException("Excel file not found");
		}

		// Get the input stream for the Excel file
		try (InputStream inputStream = resourceFile.getInputStream()) {
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return data;
		}
	}

	@Override
	public byte[] getAdditionListTemplate() throws IOException {
		// Check if the resource exists
		if (!resourceFile.exists()) {
			throw new IOException("Excel file not found");
		}

		// Get the input stream for the Excel file
		try (InputStream inputStream = resourceFile.getInputStream()) {
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return data;
		}
	}

	@Override
	public byte[] getDeleteListTemplate() throws IOException {
		// Check if the resource exists
		if (!resourceFile.exists()) {
			throw new IOException("Excel file not found");
		}

		// Get the input stream for the Excel file
		try (InputStream inputStream = resourceFile.getInputStream()) {
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return data;
		}
	}

	@Override
	public byte[] getCorrectionListTemplate() throws IOException {
		// Check if the resource exists
		if (!resourceFile.exists()) {
			throw new IOException("Excel file not found");
		}

		// Get the input stream for the Excel file
		try (InputStream inputStream = resourceFile.getInputStream()) {
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return data;
		}
	}

	@Override
	public byte[] getEnrollementListTemplate() throws IOException {
		// Check if the resource exists
		if (!resourceFile.exists()) {
			throw new IOException("Excel file not found");
		}

		// Get the input stream for the Excel file
		try (InputStream inputStream = resourceFile.getInputStream()) {
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return data;
		}
	}

	@Override
	public byte[] getPendingListTemplate() throws IOException {
		// Check if the resource exists
		if (!resourceFile.exists()) {
			throw new IOException("Excel file not found");
		}

		// Get the input stream for the Excel file
		try (InputStream inputStream = resourceFile.getInputStream()) {
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return data;
		}
	}

	@Override
	public List<GetAllClientListMembersDetailsDto> getAllAdditionListForMemberDetailsByClientListProduct(
			Long clientListId, Long productId, String month) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		List<GetAllClientListMembersDetailsDto> listOfMembersDetails = clientListMemberDetailsRepository.findAll()
				.stream().filter(c -> c.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(c -> clientListId != null && c.getClientList().getCid() == clientListId)
				.filter(c -> productId != null && c.getProduct().getProductId().equals(productId)).filter(c -> {
					if (month.equalsIgnoreCase("ALL")) {
						return true; // Return true for all records if month is "ALL"
					}
					try {
						LocalDateTime dateTime = LocalDateTime.parse(c.getCreatedDate(), formatter);
						String monthName = dateTime.getMonth().name(); // Get the month name from the date
						return monthName.equalsIgnoreCase(month); // Check if it matches the desired month
					} catch (NullPointerException | DateTimeParseException e) {
						return false; // Handle null or invalid dates
					}
				}).map(c -> {
					GetAllClientListMembersDetailsDto getAllClientListMembersDetailsDto = new GetAllClientListMembersDetailsDto();
					getAllClientListMembersDetailsDto.setMemberId(c.getMemberId());
					getAllClientListMembersDetailsDto.setEmployeeNo(c.getEmployeeNo());
					getAllClientListMembersDetailsDto.setName(c.getName());
					getAllClientListMembersDetailsDto.setRole(c.getRole());
					getAllClientListMembersDetailsDto.setRelationShip(c.getRelationShip());
					getAllClientListMembersDetailsDto.setGender(c.getGender());
					try {
						Calendar cal = Calendar.getInstance();
						cal.setTime(sdf.parse(c.getDateOfBirth()));
						String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.getTime());
						log.info("Month {}", monthName);
						getAllClientListMembersDetailsDto.setMonth(monthName);
					} catch (NullPointerException | ParseException e) {
						getAllClientListMembersDetailsDto.setMonth(null);
					}
					getAllClientListMembersDetailsDto.setAge(c.getAge());
					getAllClientListMembersDetailsDto.setSumInsured(c.getSumInsured());
					getAllClientListMembersDetailsDto.setEmail(c.getEmail());
					getAllClientListMembersDetailsDto.setPhoneNumber(c.getPhoneNumber());
					if (c.getDepartment() != null) {
						getAllClientListMembersDetailsDto.setDepartment(c.getDepartment().getDepartmentName());
					} else {
						getAllClientListMembersDetailsDto.setDepartment(null);
					}
					if (c.getDesignation() != null) {
						getAllClientListMembersDetailsDto.setDesignation(c.getDesignation().getDesignationName());
					} else {
						getAllClientListMembersDetailsDto.setDesignation(null);
					}
					return getAllClientListMembersDetailsDto;
				}).toList();
		if (listOfMembersDetails.isEmpty()) {
			throw new InvalidClientListMemberDetailsException("No Details are found for clientList and Product ");
		}
		return listOfMembersDetails;
	}

//    @Override
//    public String uploadExcelFile(ClientListEnrollmentUploadDto clientListEnrollmentUploadDto ,Long clientListId,Long productId) {
//        ClientListEnrollementEntity clientListEnrollementEntity= new ClientListEnrollementEntity();
//        String fileName = clientListEnrollmentUploadDto.getFile().getOriginalFilename().replace(" ", "");
//        if (clientListEnrollmentUploadDto.getTpaName().equals("ClaimsMis")) {
//            List<ClientListMemberDetailsDataStatus> claimsMisData = validateValuesBasedOnTpa(clientListEnrollmentUploadDto.getFile(),clientListEnrollmentUploadDto.getTpaName());
//            for (ClientListEnrollementEntity clientDetailsClaimsMis1 : claimsMisData) {
//                if (clientlistId != null) {
//                    try {
//                        ClientList clientList = clientListRepository.findById(clientlistId)
//                                .orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
//                        clientDetailsClaimsMis1.setClientList(clientList);
//                        clientDetailsClaimsMis1.setRfqId(clientList.getRfqId());
//                    } catch (InvalidClientList e) {
////                errorMessages.append("ClientList is not Found with Id : ").append(clientListId).append(" . ");
//                    }
//                }
//                if (productId != null) {
//                    try {
//                        Product product = productRepository.findById(productId)
//                                .orElseThrow(() -> new InvalidProduct("Product is not Found"));
//                        clientDetailsClaimsMis1.setProduct(product);
//                    } catch (InvalidProduct e) {
////                errorMessages.append("Product is not Found with Id : ").append(productId).append(" . ");
//                    }
//                }
//
//            }
//
//            log.error(" @Slf4j :{}", claimsMisData);
//            List<ClientDetailsClaimsMis> saveAll = clientDetailsClaimsMisRepository.saveAll(claimsMisData);
//            List<ClientDetailsClaimsMis> findByRfqId = clientDetailsClaimsMisRepository.findByRfqId(saveAll.stream().map(ClientDetailsClaimsMis::getRfqId).distinct().toString());
////            if (!findByRfqId.isEmpty()) {
////                for (ClientDetailsClaimsMis claimsMisEntity : findByRfqId) {
////                    //claimsMisRepo.hardDeleteByRfqId(claimsMisEntity.getRfqId());
////                }
////            }
//
////        System.out.println("------  ClaimsMis ----------");
//            File folder = new File(mainpath);
//            File ClaimsMisDest = new File(folder.getAbsolutePath(),
//                    "ClaimsMis" + RandomStringUtils.random(10, true, false) + fileName);
//            if (!coverageUploadDto.getFile().isEmpty() && !ClaimsMisDest.exists()) {
//                System.out.println("------  file Upload ----------");
//                try {
//                    coverageUploadDto.getFile().transferTo(ClaimsMisDest);
//                    // Create a FileInputStream to read the file
//                    FileInputStream fileInputStream = new FileInputStream(ClaimsMisDest.getAbsolutePath());
//
//                    // Create a ByteArrayOutputStream to store the bytes
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//                    // Read data from the file and write it to the ByteArrayOutputStream
//                    byte[] buffer = new byte[1024];
//                    int bytesRead;
//                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
//                        byteArrayOutputStream.write(buffer, 0, bytesRead);
//                    }
//
//                    // Close the streams
//                    fileInputStream.close();
//                    byteArrayOutputStream.close();
//
//                    // Get the byte array
//                    byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//
//                    return ClaimsMisDest.getAbsolutePath();
//                } catch (IllegalStateException | IOException e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//                System.out.println("------  file not Upload  ----------");
//                log.info(coverageUploadDto.getFile().getOriginalFilename() + " already exists !!");
//            }
//        }
//
//
//        return null;
//
//
//    }

	private Workbook getWorkbook(MultipartFile file, String tpaName) throws IOException {
		String extension = FileNameUtils.getExtension(file.getOriginalFilename());
		String fileName = file.getOriginalFilename();

		System.out.println("extension :: " + extension);
		System.out.println("fileName :: " + fileName);

		if (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".XLSX") || fileName.endsWith(".xlsb")
				|| fileName.endsWith(".XLSB"))) {
			// Handle XLSX
			return new XSSFWorkbook(file.getInputStream());
		} else if (fileName != null && (fileName.endsWith(".xls") || fileName.endsWith(".XLS"))) {
			// Handle XLS
			return new HSSFWorkbook(file.getInputStream());
		} else {
			// Throw an exception or handle the unsupported file format
			throw new IllegalArgumentException("Unsupported file format: " + fileName);
		}
	}

	private String[] getHeaders(Sheet sheet) {
		int columns = sheet.getRow(1).getLastCellNum();
		String[] headers = new String[columns];
		for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
			headers[columnIndex] = sheet.getRow(0).getCell(columnIndex).getStringCellValue();
		}
		return headers;
	}

	private boolean isMergedCell(Sheet sheet, Cell cell) {
		for (CellRangeAddress region : sheet.getMergedRegions()) {
			if (region.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
				return true;
			}
		}
		return false;
	}

	private boolean isCellMergedOrEmpty(Cell cell, Sheet sheet) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return true; // Cell is empty
		}

//        if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) {
//            return true; // Cell contains only spaces
//        }

		// Check if the cell belongs to a merged region
		for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
			if (mergedRegion.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
				return true; // Cell is part of a merged region
			}
		}

		return false; // Cell is neither empty nor part of a merged region
	}

	private String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return ""; // Return empty string for null cells
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				// Format date value and return as string
				return cell.getDateCellValue().toString();
			} else {
				// Format numeric value based on your requirements and return as string
				return String.valueOf(cell.getNumericCellValue());
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			// Evaluate formula cell and return the calculated value as string
			return String.valueOf(cell.getNumericCellValue()); // For simplicity, assuming formula results in a numeric
																// value
		default:
			return ""; // Return empty string for unsupported cell types
		}
	}

	private boolean isEmptyRow(Row row) {
		if (row == null) {
			return true;
		}
		for (Cell cell : row) {
			if (cell != null && cell.getCellType() != CellType.BLANK) {
				return false; // If any cell in the row is not empty, return false
			}
		}
		return true; // If all cells in the row are empty, return true
	}

	private boolean isEmptyColumn(Row row, int columnIndex) {
		if (row == null || columnIndex < 0) {
			return true;
		}
		Cell cell = row.getCell(columnIndex);
		return cell == null || cell.getCellType() == CellType.BLANK;
	}

	@Override
	public ClientListEnrollmentHeaderDto validateHeadersBasedOnTpa(MultipartFile multipartFile, String tpaName)
			throws IOException {
		Sheet sheet = null;
		try {
			Workbook workbook = getWorkbook(multipartFile, tpaName);
			ClientListEnrollmentHeaderDto clientListEnrollmentHeaderDto = new ClientListEnrollmentHeaderDto();

			if (tpaName.equals("HealthIndia")) {
				sheet = workbook.getSheetAt(0);
			} else if (tpaName.equals("Vidal")) {
				sheet = workbook.getSheetAt(0);
			} else if (tpaName.equals("Starhealth")) {
				sheet = workbook.getSheetAt(0);
			} else if (tpaName.equals("Medseva")) {
				sheet = workbook.getSheetAt(0);
			} else if (tpaName.equals("FHPL")) {
				sheet = workbook.getSheetAt(0);
			} else if (tpaName.equals("MediAssist")) {
				sheet = workbook.getSheetAt(0);
			} else if (tpaName.equals("GHPL")) {
				sheet = workbook.getSheetAt(0);
			} else if (tpaName.equals("ICICI")) {
				sheet = workbook.getSheetAt(0);
			} else if (tpaName.equals("MD India")) {
				sheet = workbook.getSheetAt(0);
			} else if (tpaName.equals("R-care")) {
				sheet = workbook.getSheetAt(0);
			} else {
				throw new TpaNotFoundException("TPA not found with name: " + tpaName);
			}
		} catch (NotOLE2FileException e) {
			// Log warning or error
			log.warn("The file is not in the expected OLE2 format: {}", e.getMessage());
			// Provide user-friendly error message
			throw new IllegalArgumentException("The supplied file is not a valid Excel file.");
		} catch (IOException e) {
			// Log or handle other IO errors
			log.error("An IO error occurred while processing the file: {}", e.getMessage());
			// Provide user-friendly error message
			throw new IOException("An error occurred while processing the file.");
		}
		ClientListEnrollmentHeaderDto headerDto = new ClientListEnrollmentHeaderDto();
		Iterator<Row> rowIterator = sheet.iterator();

		// Assuming the first row contains the column names
		Row headerRow = rowIterator.next();

		// Logging headers and their indexes
		log.info("Headers from Excel:");
		List<String> headerList = new ArrayList<>();
		for (int index = 0; index < headerRow.getPhysicalNumberOfCells(); index++) {
			Cell cell = headerRow.getCell(index);
			// Check if the cell is null (empty)
			if (cell == null) {
				log.info("Skipped empty cell at index: {}", index);
				continue;
			}
			// Check if the cell is part of a merged region
			if (isMergedCell(sheet, cell)) {
				log.info("Skipped merged cell at index: {}", index);
				continue;
			}
			String columnName = cell.getStringCellValue().trim(); // Convert to lowercase for case-insensitive matching
			headerList.add(columnName);
			log.info("Index: {}, Header: {}", index, columnName);
		}

		Tpa byTpaName = tpaRepository.findByTpaName(tpaName);
		List<ClientListEnrollementHeadersMappingEntity> validHeaders = clientListEnrollementHeadersMappingRepository
				.findByTpaName(byTpaName.getTpaName());
//        log.info("list of headers :{}", validHeaders);
		for (Row currentRow : sheet) {
			if (!isEmptyRow(currentRow)) { // Check if the row is not empty
				headerRow = currentRow; // Start with the current row
				boolean headerRowFound = false;
				// Iterate through cells in the current row to find headers
				for (int index = 0; index < headerRow.getPhysicalNumberOfCells(); index++) {
					Cell cell = headerRow.getCell(index);
					String columnName = getCellValueAsString(cell);
					if (cell != null && !isCellMergedOrEmpty(cell, sheet)) {
						// Check if the cell value matches any header from the database
						if (validHeaders.stream().anyMatch(header -> columnName.equals(header.getHeaderName())
								|| columnName.equals(header.getHeaderAliasName()))) {
							headerRowFound = true;
							break; // Exit the loop once header row is found
						}
					}
				}
				if (headerRowFound) {
					// Process header row to set flags
					for (int index = 0; index < headerRow.getPhysicalNumberOfCells(); index++) {
						Cell cell = headerRow.getCell(index);
						String columnName = getCellValueAsString(cell);
						if (cell != null && !isCellMergedOrEmpty(cell, sheet)) {
							validHeaders.forEach(header -> {
								if (columnName.equals(header.getHeaderName())
										|| columnName.equals(header.getHeaderAliasName())) {
									// Set flags based on header names
									switch (header.getHeaderName()) {
									case "Employee Id":
										headerDto.setEmployeeIdStatus(true);
										break;
									case "Employee Name":
										headerDto.setEmployeeNameStatus(true);
										break;
									case "Date of Birth":
										headerDto.setDateOfBirthStatus(true);
										break;
									case "Gender":
										headerDto.setGenderStatus(true);
										break;
									case "Age":
										headerDto.setAgeStatus(true);
										break;
									case "Relation":
										headerDto.setRelationStatus(true);
										break;
									case "Date of Joining":
										headerDto.setDateOfJoiningStatus(true);
										break;
									case "E Card Number":
										headerDto.setECardNumber(true);
										break;
									case "Policy Start Date":
										headerDto.setPolicyStartDateStatus(true);
										break;
									case "Policy End Date":
										headerDto.setPolicyEndDateStatus(true);
										break;
									case "Base SumInsured":
										headerDto.setBaseSumInsuredStatus(true);
										break;
									case "TopUp SumInsured":
										headerDto.setTopUpSumInsured(true);
										break;
									case "Group Name":
										headerDto.setGroupNameStatus(true);
										break;
									case "Insured Company Name":
										headerDto.setInsuredCompanyNameStatus(true);
										break;
									}
								}
							});
						}
					}
					break; // Exit the outer loop once header row is found and processed
				}
			}
		}
		return headerDto;
	}

	@Override
	public List<ClientListMemberDetailsDataStatus> validateValuesBasedOnTpa(MultipartFile multipartFile, String tpaName)
			throws IOException {
		List<ClientListMemberDetailsDataStatus> list = new ArrayList<>();
		try (Workbook workbook = getWorkbook(multipartFile, tpaName)) {
			Sheet sheet = null;
			int numberOfSheets = workbook.getNumberOfSheets();
			log.info("Sheet name  and it's no of sheets :{} ", numberOfSheets);
			for (int i = 0; i < numberOfSheets; i++) {
				if (tpaName.equals("HealthIndia")) {
					sheet = workbook.getSheetAt(i);
				} else if (tpaName.equals("Vidal")) {
					sheet = workbook.getSheetAt(i);
				} else if (tpaName.equals("Starhealth")) {
					sheet = workbook.getSheetAt(i);
				} else if (tpaName.equals("Medseva")) {
					sheet = workbook.getSheetAt(i);
				} else if (tpaName.equals("FHPL")) {
					sheet = workbook.getSheetAt(i);
				} else if (tpaName.equals("MediAssist")) {
					sheet = workbook.getSheetAt(i);
				} else if (tpaName.equals("GHPL")) {
					sheet = workbook.getSheetAt(i);
				} else if (tpaName.equals("ICICI")) {
					sheet = workbook.getSheetAt(i);
				} else if (tpaName.equals("MD India")) {
					sheet = workbook.getSheetAt(i);
				} else if (tpaName.equals("R-Care")) {
					sheet = workbook.getSheetAt(i);
				} else {
					sheet = workbook.getSheetAt(i);
				}
				if (sheet == null) {
					sheet = workbook.getSheetAt(i);
				}

				validateBasedOnSheet(sheet, tpaName, list);
			}
		} catch (NotOLE2FileException e) {
			// Log warning or error
			log.warn("The file is not in the expected OLE2 format: {}", e.getMessage());
			// Provide user-friendly error message
			throw new IllegalArgumentException("The supplied file is not a valid Excel file.");
		} catch (IOException e) {
			// Log or handle other IO errors
			log.error("An IO error occurred while processing the file: {}", e.getMessage());
			// Provide user-friendly error message
			throw new IOException("An error occurred while processing the file.");
		}
		return list;

	}

	@Override
	public void validateBasedOnSheet(Sheet sheet, String tpaName,
			List<ClientListMemberDetailsDataStatus> claimsMisValidateData) {

		int employeeIdColumnIndex = -1;
		int employeeNameColumnIndex = -1;
		int relationshipColumnIndex = -1;
		int genderColumnIndex = -1;
		int ageColumnIndex = -1;
		int sumInsuredColumnIndex = -1;
		int topUpSumInsuredColumnIndex = -1;
		int eCardNumberColumnIndex = -1;
		int policyStartDateColumnIndex = -1;
		int policyEndDateColumnIndex = -1;
		int dateOfJoiningColumnIndex = -1;
		int dateOfBirthColumnIndex = -1;
		int insuredCompanyNameColumnIndex = -1;
		int groupNameColumnIndex = -1;

		boolean employeeIdColumnIndexFlag = false;
		boolean employeeNameColumnIndexFlag = false;
		boolean relationshipColumnIndexFlag = false;
		boolean genderColumnIndexFlag = false;
		boolean ageColumnIndexFlag = false;
		boolean sumInsuredColumnIndexFlag = false;
		boolean topUpSumInsuredColumnIndexFlag = false;
		boolean eCardNumberColumnIndexFlag = false;
		boolean dateOfJoiningColumnIndexFlag = false;
		boolean dateOfBirthColumnIndexFlag = false;
		boolean policyStartDateColumnIndexFlag = false;
		boolean policyEndDateColumnIndexFlag = false;
		boolean insuredCompanyNameColumnIndexFlag = false;
		boolean groupNameColumnIndexFlag = false;

		Iterator<Row> rowIterator = sheet.iterator();
		Tpa byTpaName = tpaRepository.findByTpaName(tpaName);
		List<ClientListEnrollementHeadersMappingEntity> validHeaders = clientListEnrollementHeadersMappingRepository
				.findByTpaName(byTpaName.getTpaName());
		log.info("No of Rows from Excel :{}", sheet.getPhysicalNumberOfRows());

		Row headerRow = null;
		/*
		 * This Loop Finds Exact Row where Headers are Present
		 */
		while (rowIterator.hasNext()) {
			int columnIndex1 = 0; // Initialize columnIndex1 inside the loop
			Row row = rowIterator.next();
			if (row != null && !isEmptyRow(row)) {
				boolean foundHeaders = false;
				for (Cell cell : row) {
					if (cell == null || cell.getStringCellValue().trim().isEmpty()) {
						continue;
					}
					String cellValue = cell.getStringCellValue().trim();
// Check if cell value matches any header
					for (ClientListEnrollementHeadersMappingEntity header : validHeaders) {
						String headerName = header.getHeaderName().trim();
						String headerAliasName = header.getHeaderAliasName().trim();
						if (cellValue.equalsIgnoreCase(headerName) || cellValue.equalsIgnoreCase(headerAliasName)) {
							foundHeaders = true;
							break;
						}
					}
					if (foundHeaders) {
						headerRow = row;
						break;
					}
				}
				if (foundHeaders && !isEmptyColumn(row, columnIndex1)) {
					break;
				}
			}
		}

		/*
		 * This Loop Finds Index for Column from Excel and also maps it with DB's name
		 */
		if (headerRow != null) {
			int columnIndex = 0;
			for (Cell cell : headerRow) {
				if (cell == null || cell.getStringCellValue().trim().isEmpty()) {
					log.info("Skipped empty header at index: {}", columnIndex);
					columnIndex++;
					continue;
				}
				if (isMergedCell(sheet, cell)) {
					log.info("Skipped merged cell at index: {}", columnIndex);
					columnIndex++;
					continue;
				}

				String columnName = cell.getStringCellValue().trim();
				if (columnName.isEmpty()) {
					log.info("Skipped empty header at index: {}", columnIndex);
					columnIndex++;
					continue;
				}

				log.info("Processing header: '{}'", columnName);
				for (ClientListEnrollementHeadersMappingEntity header : validHeaders) {
					String headerName = header.getHeaderName().trim();
					String headerAliasName = header.getHeaderAliasName().trim();
					if (columnName.equalsIgnoreCase(headerName) || columnName.equalsIgnoreCase(headerAliasName)) {
						log.info("Mapping Excel column '{}' to header alias '{}'", columnName, headerAliasName);
						switch (headerName) {
						case "Employee Id":
							employeeIdColumnIndex = columnIndex;
							log.info("Captured employeeIdColumnIndex from excel :{}", employeeIdColumnIndex);
							break;
						case "Employee Name":
							employeeNameColumnIndex = columnIndex;
							log.info("Captured employeeNameColumnIndex from excel :{}", employeeNameColumnIndex);
							break;
						case "Date of Birth":
							dateOfBirthColumnIndex = columnIndex;
							log.info("Captured dateOfBirthColumnIndex from excel :{}", dateOfBirthColumnIndex);
							break;
						case "Gender":
							genderColumnIndex = columnIndex;
							log.info("Captured genderColumnIndex from excel :{}", genderColumnIndex);
							break;
						case "Age":
							ageColumnIndex = columnIndex;
							log.info("Captured ageColumnIndex from excel :{}", ageColumnIndex);
							break;
						case "Relation":
							relationshipColumnIndex = columnIndex;
							log.info("Captured relationshipColumnIndex from excel :{}", relationshipColumnIndex);
							break;
						case "Date of Joining":
							dateOfJoiningColumnIndex = columnIndex;
							log.info("Captured dateOfJoiningColumnIndex from excel :{}", dateOfJoiningColumnIndex);
							break;
						case "E Card Number":
							eCardNumberColumnIndex = columnIndex;
							log.info("Captured eCardNumberColumnIndex from excel :{}", eCardNumberColumnIndex);
							break;
						case "Policy Start Date":
							policyStartDateColumnIndex = columnIndex;
							log.info("Captured policyStartDateColumnIndex from excel :{}", policyStartDateColumnIndex);
							break;
						case "Policy End Date":
							policyEndDateColumnIndex = columnIndex;
							log.info("Captured policyEndDateColumnIndex from excel :{}", policyEndDateColumnIndex);
							break;
						case "Base SumInsured":
							sumInsuredColumnIndex = columnIndex;
							log.info("Captured sumInsuredColumnIndex from excel :{}", sumInsuredColumnIndex);
							break;
						case "TopUp SumInsured":
							topUpSumInsuredColumnIndex = columnIndex;
							log.info("Captured topUpSumInsuredColumnIndex from excel :{}", topUpSumInsuredColumnIndex);
							break;
						case "Insured Company Name":
							insuredCompanyNameColumnIndex = columnIndex;
							log.info("Captured insuredCompanyNameColumnIndex from excel :{}",
									insuredCompanyNameColumnIndex);
							break;
						// Add mappings for other headers here...
						}
					}
				}
				columnIndex++;
			}
		} else {
			log.error("Column headers not found in the sheet.");
		}

		/*
		 * This Loop Validates the data
		 */
		while (rowIterator.hasNext()) {
			Row dataRow = rowIterator.next();
			if (employeeIdColumnIndex >= 0 && employeeNameColumnIndex >= 0 && relationshipColumnIndex >= 0
					&& genderColumnIndex >= 0 && dateOfJoiningColumnIndex >= 0 && ageColumnIndex >= 0
					&& sumInsuredColumnIndex >= 0 && policyStartDateColumnIndex >= 0 && policyEndDateColumnIndex >= 0
					&& dateOfBirthColumnIndex >= 0 && eCardNumberColumnIndex >= 0) {
				if (dataRow.getCell(employeeIdColumnIndex) != null && dataRow.getCell(employeeNameColumnIndex) != null
						&& dataRow.getCell(relationshipColumnIndex) != null
						&& dataRow.getCell(genderColumnIndex) != null && dataRow.getCell(ageColumnIndex) != null
						&& dataRow.getCell(sumInsuredColumnIndex) != null
						&& dataRow.getCell(eCardNumberColumnIndex) != null
						&& dataRow.getCell(policyStartDateColumnIndex) != null
						&& dataRow.getCell(policyEndDateColumnIndex) != null
						&& dataRow.getCell(dateOfJoiningColumnIndex) != null
						&& dataRow.getCell(dateOfBirthColumnIndex) != null) {
				}
				if (dataRow.getCell(employeeIdColumnIndex).getCellType() == CellType.BLANK
						&& dataRow.getCell(employeeNameColumnIndex).getCellType() == CellType.BLANK
						&& dataRow.getCell(relationshipColumnIndex).getCellType() == CellType.BLANK
						&& dataRow.getCell(genderColumnIndex).getCellType() == CellType.BLANK
						&& dataRow.getCell(ageColumnIndex).getCellType() == CellType.BLANK
						&& dataRow.getCell(eCardNumberColumnIndex).getCellType() == CellType.BLANK
						&& dataRow.getCell(policyStartDateColumnIndex).getCellType() == CellType.BLANK
						&& dataRow.getCell(policyEndDateColumnIndex).getCellType() == CellType.BLANK
						&& dataRow.getCell(dateOfBirthColumnIndex).getCellType() == CellType.BLANK
						&& dataRow.getCell(dateOfJoiningColumnIndex).getCellType() == CellType.BLANK) {
					continue;
				}

			}
			String remarks;
			ClientListMemberDetailsDataStatus validateDto = new ClientListMemberDetailsDataStatus();
			/**
			 * EmployeeId Validation
			 */
			String TempEmpID = null;
			int employeeIdInteger;
			String employeeId = null;
			if (dataRow.getCell(employeeIdColumnIndex) != null) {
				Cell employeeIdCell = dataRow.getCell(employeeIdColumnIndex);
				if (employeeIdCell.getCellType() == CellType.NUMERIC) {
//if (employeeId.equals("0.0")) {
//employeeId = TempEmpID;
//}
//TempEmpID = employeeId;
					employeeIdInteger = (int) employeeIdCell.getNumericCellValue();
					validateDto.setEmployeeId(String.valueOf(employeeIdInteger));
					validateDto.setEmployeeIdStatus(true);
				} else if (employeeIdCell.getCellType() == CellType.STRING) {
					employeeId = employeeIdCell.getStringCellValue().trim();
//if (employeeId.isEmpty() || employeeId.equals(" ")) {
//employeeId = TempEmpID;
//}
//TempEmpID = employeeId;
					if (employeeId.isEmpty()) {
// Look for employee ID in previous cells
						int prevIndex = employeeIdColumnIndex - 1;
						while (prevIndex >= 0) {
							Cell prevCell = dataRow.getCell(prevIndex);
							if (prevCell != null && prevCell.getCellType() == CellType.STRING) {
								employeeId = prevCell.getStringCellValue().trim();
								if (!employeeId.isEmpty()) {
									validateDto.setEmployeeId(employeeId);
									validateDto.setEmployeeIdStatus(true);
									break; // Exit the loop if a non-empty employee ID is found
								}
							}
							prevIndex--;
						}
// If no valid employee ID is found, set status to false and provide error
// message
						if (employeeId.isEmpty()) {
							validateDto.setEmployeeIdStatus(false);
							validateDto.setEmployeeIdErrorMessage("Employee Id Blank");
						}
					} else {
						validateDto.setEmployeeId(employeeId);
						validateDto.setEmployeeIdStatus(true);
					}
				} else if (employeeIdCell.getCellType() == CellType.BLANK) {
// Look for employee ID in previous cells
					int prevIndex = employeeIdColumnIndex - 1;
					while (prevIndex >= 0) {
						Cell prevCell = dataRow.getCell(prevIndex);
						if (prevCell != null && prevCell.getCellType() == CellType.STRING) {
							employeeId = prevCell.getStringCellValue().trim();
							if (!employeeId.isEmpty()) {
								validateDto.setEmployeeId(employeeId);
								validateDto.setEmployeeIdStatus(true);
								break; // Exit the loop if a non-empty employee ID is found
							}
						}
						prevIndex--;
					}
// If no valid employee ID is found, set status to false and provide error
// message
					if (validateDto.getEmployeeId() == null) {
						validateDto.setEmployeeIdStatus(false);
						validateDto.setEmployeeIdErrorMessage("Employee Id Blank");
					}
				} else if (employeeIdCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = employeeIdCell.getErrorCellValue();
					validateDto.setEmployeeId(String.valueOf(errorCellValue));
					validateDto.setEmployeeIdStatus(false);
					validateDto.setEmployeeIdErrorMessage("Employee Id Error");
				} else if (employeeIdCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = employeeIdCell.getBooleanCellValue();
					validateDto.setEmployeeId(String.valueOf(booleanCellValue));
					validateDto.setEmployeeIdStatus(false);
					validateDto.setEmployeeIdErrorMessage("Employee Id Boolean");
				} else if (employeeIdCell.getCellType() == CellType.FORMULA) {
					switch (employeeIdCell.getCachedFormulaResultType()) {
					case NUMERIC: {
						employeeIdInteger = (int) employeeIdCell.getNumericCellValue();
						validateDto.setEmployeeId(String.valueOf(employeeIdInteger));
						validateDto.setEmployeeIdStatus(true);
						break;
					}
					case STRING: {
						employeeId = employeeIdCell.getStringCellValue().trim();
						if (employeeId.isEmpty()) {
							// Check the previous cell
							int prevIndex = employeeIdColumnIndex - 1;
							Cell prevCell = dataRow.getCell(prevIndex);
							if (prevCell != null && prevCell.getCellType() == CellType.STRING) {
								employeeId = prevCell.getStringCellValue().trim();
							}
						}
						validateDto.setEmployeeId(employeeId);
						validateDto.setEmployeeIdStatus(true);
						break;
					}
					case BOOLEAN: {
						boolean booleanCellValue = employeeIdCell.getBooleanCellValue();
						validateDto.setEmployeeId(String.valueOf(booleanCellValue));
						validateDto.setEmployeeIdStatus(false);
						validateDto.setEmployeeIdErrorMessage("Employee Id Boolean Formula :: ");
						break;
					}
					case ERROR: {
						byte errorCellValue = employeeIdCell.getErrorCellValue();
						validateDto.setEmployeeId(String.valueOf(errorCellValue));
						validateDto.setEmployeeIdStatus(false);
						validateDto.setEmployeeIdErrorMessage("Employee Id Error Formula :: ");
						break;
					}
					case _NONE: {
						employeeIdColumnIndexFlag = true;
						validateDto.setEmployeeIdStatus(false);
						validateDto.setEmployeeIdErrorMessage("Employee Id None Formula :: ");
						break;
					}
					case BLANK: {
						employeeIdColumnIndexFlag = true;
						validateDto.setEmployeeIdStatus(false);
						validateDto.setEmployeeIdErrorMessage("Employee Id Blank Formula :: ");
						break;
					}
					default:
						throw new IllegalArgumentException(
								"Unexpected value: " + employeeIdCell.getCachedFormulaResultType());
					}
				}
			}

			String employeeName;
			/**
			 * EmployeeName Validation
			 */
			if (dataRow.getCell(employeeNameColumnIndex) != null) {
				Cell employeeNameCell = dataRow.getCell(employeeNameColumnIndex);
//log.info("Employee Name From Excel : {} and it's dataType is :{}",employeeNameCell,employeeNameCell.getCellType());
				if (employeeNameCell.getCellType() == CellType.NUMERIC) {
					employeeName = String.valueOf(employeeNameCell.getNumericCellValue()).trim();
					validateDto.setEmployeeName(String.valueOf(employeeName));
					validateDto.setEmployeeNameStatus(false);
				} else if (employeeNameCell.getCellType() == CellType.STRING) {
					employeeName = employeeNameCell.getStringCellValue().trim();
					validateDto.setEmployeeName(employeeName);
					validateDto.setEmployeeNameStatus(true);
				} else if (employeeNameCell.getCellType() == CellType._NONE) {
					employeeNameColumnIndexFlag = true;
					validateDto.setEmployeeNameStatus(false);
					validateDto.setEmployeeNameErrorMessage("Employee Name None :: ");
				} else if (employeeNameCell.getCellType() == CellType.BLANK) {
					employeeNameColumnIndexFlag = true;
					validateDto.setEmployeeNameStatus(false);
					validateDto.setEmployeeNameErrorMessage("Employee Name Blank :: ");
				} else if (employeeNameCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = employeeNameCell.getErrorCellValue();
					validateDto.setEmployeeName(String.valueOf(errorCellValue).trim());
					validateDto.setEmployeeNameStatus(false);
					validateDto.setEmployeeNameErrorMessage("Employee Name Error :: ");
				} else if (employeeNameCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = employeeNameCell.getBooleanCellValue();
					validateDto.setEmployeeName(String.valueOf(booleanCellValue).trim());
					validateDto.setEmployeeNameStatus(false);
					validateDto.setEmployeeNameErrorMessage("Employee Name Boolean :: ");
				} else if (employeeNameCell.getCellType() == CellType.FORMULA) {
					switch (employeeNameCell.getCellType()) {
					case NUMERIC: {
						employeeName = String.valueOf(employeeNameCell.getNumericCellValue()).trim();
						validateDto.setEmployeeName(String.valueOf(employeeName));
						validateDto.setEmployeeNameStatus(false);
						break;
					}
					case STRING: {
						employeeName = employeeNameCell.getStringCellValue().trim();
						validateDto.setEmployeeName(employeeName);
						validateDto.setEmployeeNameStatus(true);
						break;
					}
					case BOOLEAN: {
						boolean booleanCellValue = employeeNameCell.getBooleanCellValue();
						validateDto.setEmployeeName(String.valueOf(booleanCellValue).trim());
						validateDto.setEmployeeNameStatus(false);
						validateDto.setEmployeeIdErrorMessage("Employee Name Boolean Formula :: ");
						break;
					}
					case ERROR: {
						byte errorCellValue = employeeNameCell.getErrorCellValue();
						validateDto.setEmployeeName(String.valueOf(errorCellValue).trim());
						validateDto.setEmployeeNameStatus(false);
						validateDto.setEmployeeIdErrorMessage("Employee Name Error Formula :: ");
						break;
					}
					case _NONE: {
						employeeNameColumnIndexFlag = true;
						validateDto.setEmployeeNameStatus(false);
						validateDto.setEmployeeNameErrorMessage("Employee Name None Formula :: ");
						break;
					}
					case BLANK: {
						employeeNameColumnIndexFlag = true;
						validateDto.setEmployeeNameStatus(false);
						validateDto.setEmployeeNameErrorMessage("Employee Name Blank Formula :: ");
						break;
					}
					default:
						throw new IllegalArgumentException(
								"Unexpected value: " + employeeNameCell.getCachedFormulaResultType());
					}
				}

			}

			String[] genderArr = { "M", "F", "Male", "Female", "Trans", "TransGender" };
			String gender;
			/**
			 * Gender Validation
			 */
			if (dataRow.getCell(genderColumnIndex) != null) {
				Cell genderCell = dataRow.getCell(genderColumnIndex);
//log.info("Gender From Excel : {} and it's dataType is :{}",genderCell,genderCell.getCellType());
				if (genderCell.getCellType() == CellType.NUMERIC) {
					gender = String.valueOf(genderCell.getNumericCellValue()).trim();
					validateDto.setGenderStatus(false);
					validateDto.setGender(gender);
				} else if (genderCell.getCellType() == CellType.STRING) {
					gender = genderCell.getStringCellValue().trim().replaceAll("\\p{C}", "").replaceAll("\\s", "")
							.replaceAll("[^\\p{Print}]", "").trim();
					if (Arrays.asList(genderArr).stream().map(s -> s.toLowerCase()).toList()
							.contains(gender.toLowerCase())) {
						validateDto.setGenderStatus(true);
						validateDto.setGender(gender);
					} else {
						validateDto.setGenderStatus(false);
						validateDto.setGenderErrorMessage("Gender is Improper :: ");
					}
				} else if (genderCell.getCellType() == CellType._NONE) {
					genderColumnIndexFlag = true;
					validateDto.setGenderStatus(false);
					validateDto.setGenderErrorMessage("Gender None :: ");
				} else if (genderCell.getCellType() == CellType.BLANK) {
					genderColumnIndexFlag = true;
					validateDto.setGenderStatus(false);
					validateDto.setGenderErrorMessage("Gender Blank :: ");
				} else if (genderCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = genderCell.getErrorCellValue();
					validateDto.setGender(String.valueOf(errorCellValue).trim());
					validateDto.setGenderStatus(false);
					validateDto.setGenderErrorMessage("Gender Error :: ");
				} else if (genderCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = genderCell.getBooleanCellValue();
					validateDto.setGender(String.valueOf(booleanCellValue).trim());
					validateDto.setGenderStatus(false);
					validateDto.setGenderErrorMessage("Gender Boolean :: ");
				} else if (genderCell.getCellType() == CellType.FORMULA) {
					switch (genderCell.getCellType()) {
					case NUMERIC: {
						gender = String.valueOf(genderCell.getNumericCellValue()).trim();
						validateDto.setGenderStatus(false);
						validateDto.setGender(gender);
						break;
					}
					case STRING: {
						gender = genderCell.getStringCellValue().trim().replaceAll("\\p{C}", "").replaceAll("\\s", "")
								.replaceAll("[^\\p{Print}]", "").trim();
						if (Arrays.asList(genderArr).stream().map(s -> s.toLowerCase()).toList()
								.contains(gender.toLowerCase())) {
							validateDto.setGenderStatus(true);
							validateDto.setGender(gender);
						} else {
							validateDto.setGenderStatus(false);
							validateDto.setGenderErrorMessage("Gender is Improper :: ");
						}
						break;
					}
					case BOOLEAN: {
						boolean booleanCellValue = genderCell.getBooleanCellValue();
						validateDto.setGender(String.valueOf(booleanCellValue).trim());
						validateDto.setGenderStatus(false);
						validateDto.setGenderErrorMessage("Gender Boolean Formula :: ");
						break;
					}
					case ERROR: {
						byte errorCellValue = genderCell.getErrorCellValue();
						validateDto.setGender(String.valueOf(errorCellValue).trim());
						validateDto.setGenderStatus(false);
						validateDto.setGenderErrorMessage("Gender Error Formula :: ");
						break;
					}
					case _NONE: {
						genderColumnIndexFlag = true;
						validateDto.setGenderStatus(false);
						validateDto.setGenderErrorMessage("Gender None Formula :: ");
						break;
					}
					case BLANK: {
						genderColumnIndexFlag = true;
						validateDto.setGenderStatus(false);
						validateDto.setGenderErrorMessage("Gender Blank Formula :: ");
						break;
					}
					default:
						throw new IllegalArgumentException(
								"Unexpected value: " + genderCell.getCachedFormulaResultType());
					}
				}

			}
			/**
			 * Age Validation
			 */
			String ageValue;
			if (dataRow.getCell(ageColumnIndex) != null) {
				Cell ageCell = dataRow.getCell(ageColumnIndex);
//log.info("Age From Excel : {} and it's dataType is :{}",ageCell,ageCell.getCellType());
				if (ageCell.getCellType() == CellType.NUMERIC) {
					double numericCellValue = ageCell.getNumericCellValue();
					DecimalFormat df = new DecimalFormat("#");
					String stringValue = df.format(numericCellValue).trim();
//ageValue = String.valueOf(ageCell.getNumericCellValue());
					validateDto.setAgeStatus(true);
					validateDto.setAge(stringValue);
				} else if (ageCell.getCellType() == CellType.STRING) {
					ageValue = ageCell.getStringCellValue().trim();
					validateDto.setAgeStatus(true);
					validateDto.setAge(ageValue);
				} else if (ageCell.getCellType() == CellType._NONE) {
					ageColumnIndexFlag = true;
					validateDto.setAgeStatus(false);
					validateDto.setAgeErrorMessage("Age None :: ");
				} else if (ageCell.getCellType() == CellType.BLANK) {
					ageColumnIndexFlag = true;
					validateDto.setAgeStatus(false);
					validateDto.setAgeErrorMessage("Age Blank :: ");
				} else if (ageCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = ageCell.getErrorCellValue();
					validateDto.setAge(String.valueOf(errorCellValue).trim());
					validateDto.setAgeStatus(false);
					validateDto.setAgeErrorMessage("Age Error :: ");
				} else if (ageCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = ageCell.getBooleanCellValue();
					validateDto.setAge(String.valueOf(booleanCellValue).trim());
					validateDto.setAgeStatus(false);
					validateDto.setAgeErrorMessage("Age Boolean :: ");
				} else if (ageCell.getCellType() == CellType.FORMULA) {
					switch (ageCell.getCellType()) {
					case NUMERIC: {
						ageValue = String.valueOf(ageCell.getNumericCellValue()).trim();
						validateDto.setAgeStatus(true);
						validateDto.setAge(ageValue);
						break;
					}
					case STRING: {
						ageValue = ageCell.getStringCellValue().trim();
						validateDto.setAgeStatus(true);
						validateDto.setAge(ageValue);
						break;
					}
					case BOOLEAN: {
						boolean booleanCellValue = ageCell.getBooleanCellValue();
						validateDto.setAge(String.valueOf(booleanCellValue).trim());
						validateDto.setAgeStatus(false);
						validateDto.setAgeErrorMessage("Age Boolean FORMULA:: ");
						break;
					}
					case ERROR: {
						byte errorCellValue = ageCell.getErrorCellValue();
						validateDto.setAge(String.valueOf(errorCellValue).trim());
						validateDto.setAgeStatus(false);
						validateDto.setAgeErrorMessage("Age Error FORMULA:: ");
						break;
					}
					case _NONE: {
						ageColumnIndexFlag = true;
						validateDto.setAgeStatus(false);
						validateDto.setAgeErrorMessage("Age None FORMULA:: ");
						break;
					}
					case BLANK: {
						ageColumnIndexFlag = true;
						validateDto.setAgeStatus(false);
						validateDto.setAgeErrorMessage("Age Blank FORMULA :: ");
						break;
					}
					default:
						throw new IllegalArgumentException("Unexpected value: " + ageCell.getCachedFormulaResultType());
					}
				}
			}
			/**
			 * BaseSumInsured Validation
			 */
			String sumInsuredValue;
			if (dataRow.getCell(sumInsuredColumnIndex) != null) {
				Cell sumInsuredCell = dataRow.getCell(sumInsuredColumnIndex);
//log.info("SumInsured From Excel : {}",sumInsuredCell);
				if (sumInsuredCell.getCellType() == CellType.NUMERIC) {
					double numericCellValue = sumInsuredCell.getNumericCellValue();
					DecimalFormat df = new DecimalFormat("#");
					String stringValue = df.format(numericCellValue).trim();
//sumInsuredValue = String.valueOf(sumInsuredCell.getNumericCellValue());
					validateDto.setSumInsuredStatus(true);
					validateDto.setSumInsured(stringValue);
				} else if (sumInsuredCell.getCellType() == CellType.STRING) {
					String sumInsuredStr = sumInsuredCell.getStringCellValue().replaceAll(",", "");
					try {
						double sumInsuredDouble = Double.parseDouble(sumInsuredStr);
						DecimalFormat df = new DecimalFormat("#");
						String stringValue = df.format(sumInsuredDouble).trim();
//   sumInsuredValue = String.valueOf(sumInsuredDouble);
						validateDto.setSumInsuredStatus(true);
						validateDto.setSumInsured(stringValue);
					} catch (NumberFormatException e) {
// Handle parsing exception
						validateDto.setSumInsuredStatus(false);
						validateDto.setSumInsuredErrorMessage("Error parsing sum insured value: " + e.getMessage());
					}

				} else if (sumInsuredCell.getCellType() == CellType._NONE) {
//sumInsuredColumnIndexFlag = true;
					validateDto.setSumInsuredStatus(true);
					validateDto.setSumInsured("0");
//validateDto.setSumInsuredErrorMessage("SumInsured None :: ");
				} else if (sumInsuredCell.getCellType() == CellType.BLANK) {
//sumInsuredColumnIndexFlag = true;
					validateDto.setSumInsuredStatus(true);
					validateDto.setSumInsured("0");
//validateDto.setSumInsuredErrorMessage("SumInsured Blank :: ");
				} else if (sumInsuredCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = sumInsuredCell.getErrorCellValue();
					validateDto.setSumInsured(String.valueOf(errorCellValue).trim());
					validateDto.setSumInsuredStatus(true);
//validateDto.setSumInsuredErrorMessage("SumInsured Error :: ");
				} else if (sumInsuredCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = sumInsuredCell.getBooleanCellValue();
					validateDto.setSumInsured(String.valueOf(booleanCellValue).trim());
					validateDto.setSumInsuredStatus(true);
//validateDto.setSumInsuredErrorMessage("SumInsured Boolean :: ");
				} else if (sumInsuredCell.getCellType() == CellType.FORMULA) {
					if (sumInsuredCell.getCachedFormulaResultType() == CellType.NUMERIC) {
						sumInsuredValue = String.valueOf(sumInsuredCell.getNumericCellValue()).trim();
						validateDto.setSumInsuredStatus(true);
						validateDto.setSumInsured(sumInsuredValue);
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType.STRING) {
						String sumInsuredStr = sumInsuredCell.getStringCellValue().replaceAll(",", "");
						try {
							double sumInsuredDouble = Double.parseDouble(sumInsuredStr);
							DecimalFormat df = new DecimalFormat("#");
							String stringValue = df.format(sumInsuredDouble).trim();
//   sumInsuredValue = String.valueOf(sumInsuredDouble);
							validateDto.setSumInsuredStatus(true);
							validateDto.setSumInsured(stringValue);
						} catch (NumberFormatException e) {
							// Handle parsing exception
							validateDto.setSumInsuredStatus(false);
							validateDto.setSumInsuredErrorMessage("Error parsing sum insured value: " + e.getMessage());
						}
//   sumInsuredValue = sumInsuredCell.getStringCellValue();
//   validateDto.setSumInsuredStatus(true);
//   validateDto.setSumInsured(sumInsuredValue);
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType.BOOLEAN) {
						boolean booleanCellValue = sumInsuredCell.getBooleanCellValue();
						validateDto.setSumInsured(String.valueOf(booleanCellValue).trim());
						validateDto.setSumInsuredStatus(false);
						validateDto.setSumInsuredErrorMessage("SumInsured Boolean Formula :: ");
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType.ERROR) {
						byte errorCellValue = sumInsuredCell.getErrorCellValue();
						validateDto.setSumInsured(String.valueOf(errorCellValue).trim());
						validateDto.setSumInsuredStatus(false);
						validateDto.setSumInsuredErrorMessage("SumInsured Error Formula :: ");
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType._NONE) {
//   sumInsuredColumnIndexFlag = true;
						validateDto.setSumInsuredStatus(true);
//   validateDto.setSumInsuredErrorMessage("SumInsured None Formula :: ");
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType.BLANK) {
//   sumInsuredColumnIndexFlag = true;
						validateDto.setSumInsuredStatus(true);
//   validateDto.setSumInsuredErrorMessage("SumInsured Blank Formula :: ");
					} else {
						throw new IllegalArgumentException(
								"Unexpected value: " + sumInsuredCell.getCachedFormulaResultType());
					}
				}
			} /**
				 * TopUpSumInsured Validation
				 */
			String topUpSumInsuredValue;
			if (dataRow.getCell(topUpSumInsuredColumnIndex) != null) {
				Cell sumInsuredCell = dataRow.getCell(topUpSumInsuredColumnIndex);
//log.info("SumInsured From Excel : {}",sumInsuredCell);
				if (sumInsuredCell.getCellType() == CellType.NUMERIC) {
					double numericCellValue = sumInsuredCell.getNumericCellValue();
					DecimalFormat df = new DecimalFormat("#");
					String stringValue = df.format(numericCellValue).trim();
//sumInsuredValue = String.valueOf(sumInsuredCell.getNumericCellValue());
					validateDto.setSumInsuredStatus(true);
					validateDto.setSumInsured(stringValue);
				} else if (sumInsuredCell.getCellType() == CellType.STRING) {
					String sumInsuredStr = sumInsuredCell.getStringCellValue().replaceAll(",", "");
					try {
						double sumInsuredDouble = Double.parseDouble(sumInsuredStr);
						DecimalFormat df = new DecimalFormat("#");
						String stringValue = df.format(sumInsuredDouble).trim();
//   sumInsuredValue = String.valueOf(sumInsuredDouble);
						validateDto.setSumInsuredStatus(true);
						validateDto.setSumInsured(stringValue);
					} catch (NumberFormatException e) {
// Handle parsing exception
						validateDto.setSumInsuredStatus(false);
						validateDto.setSumInsuredErrorMessage("Error parsing sum insured value: " + e.getMessage());
					}

				} else if (sumInsuredCell.getCellType() == CellType._NONE) {
//sumInsuredColumnIndexFlag = true;
					validateDto.setSumInsuredStatus(true);
					validateDto.setSumInsured("0");
//validateDto.setSumInsuredErrorMessage("SumInsured None :: ");
				} else if (sumInsuredCell.getCellType() == CellType.BLANK) {
//sumInsuredColumnIndexFlag = true;
					validateDto.setSumInsuredStatus(true);
					validateDto.setSumInsured("0");
//validateDto.setSumInsuredErrorMessage("SumInsured Blank :: ");
				} else if (sumInsuredCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = sumInsuredCell.getErrorCellValue();
					validateDto.setSumInsured(String.valueOf(errorCellValue).trim());
					validateDto.setSumInsuredStatus(true);
//validateDto.setSumInsuredErrorMessage("SumInsured Error :: ");
				} else if (sumInsuredCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = sumInsuredCell.getBooleanCellValue();
					validateDto.setSumInsured(String.valueOf(booleanCellValue).trim());
					validateDto.setSumInsuredStatus(true);
//validateDto.setSumInsuredErrorMessage("SumInsured Boolean :: ");
				} else if (sumInsuredCell.getCellType() == CellType.FORMULA) {
					if (sumInsuredCell.getCachedFormulaResultType() == CellType.NUMERIC) {
						topUpSumInsuredValue = String.valueOf(sumInsuredCell.getNumericCellValue()).trim();
						validateDto.setSumInsuredStatus(true);
						validateDto.setSumInsured(topUpSumInsuredValue);
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType.STRING) {
						String sumInsuredStr = sumInsuredCell.getStringCellValue().replaceAll(",", "");
						try {
							double sumInsuredDouble = Double.parseDouble(sumInsuredStr);
							DecimalFormat df = new DecimalFormat("#");
							String stringValue = df.format(sumInsuredDouble).trim();
//   sumInsuredValue = String.valueOf(sumInsuredDouble);
							validateDto.setSumInsuredStatus(true);
							validateDto.setSumInsured(stringValue);
						} catch (NumberFormatException e) {
							// Handle parsing exception
							validateDto.setSumInsuredStatus(false);
							validateDto.setSumInsuredErrorMessage("Error parsing sum insured value: " + e.getMessage());
						}
//   sumInsuredValue = sumInsuredCell.getStringCellValue();
//   validateDto.setSumInsuredStatus(true);
//   validateDto.setSumInsured(sumInsuredValue);
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType.BOOLEAN) {
						boolean booleanCellValue = sumInsuredCell.getBooleanCellValue();
						validateDto.setSumInsured(String.valueOf(booleanCellValue).trim());
						validateDto.setSumInsuredStatus(false);
						validateDto.setSumInsuredErrorMessage("SumInsured Boolean Formula :: ");
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType.ERROR) {
						byte errorCellValue = sumInsuredCell.getErrorCellValue();
						validateDto.setSumInsured(String.valueOf(errorCellValue).trim());
						validateDto.setSumInsuredStatus(false);
						validateDto.setSumInsuredErrorMessage("SumInsured Error Formula :: ");
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType._NONE) {
//   sumInsuredColumnIndexFlag = true;
						validateDto.setSumInsuredStatus(true);
//   validateDto.setSumInsuredErrorMessage("SumInsured None Formula :: ");
					} else if (sumInsuredCell.getCachedFormulaResultType() == CellType.BLANK) {
//   sumInsuredColumnIndexFlag = true;
						validateDto.setSumInsuredStatus(true);
//   validateDto.setSumInsuredErrorMessage("SumInsured Blank Formula :: ");
					} else {
						throw new IllegalArgumentException(
								"Unexpected value: " + sumInsuredCell.getCachedFormulaResultType());
					}
				}
			}
			String relationship;
			/**
			 * RelationShip Validation
			 */
			if (dataRow.getCell(relationshipColumnIndex) != null) {
				Cell relationshipCell = dataRow.getCell(relationshipColumnIndex);
//log.info("RelationShip From Excel : {} and it's dataType is :{}",relationshipCell,relationshipCell.getCellType());
				if (relationshipCell.getCellType() == CellType.NUMERIC) {
					relationship = String.valueOf(relationshipCell.getNumericCellValue()).trim();
					validateDto.setRelationshipStatus(false);
					validateDto.setRelationship(relationship);
				} else if (relationshipCell.getCellType() == CellType.STRING) {
					relationship = relationshipCell.getStringCellValue().trim();
					validateDto.setRelationship(relationship);
					validateDto.setRelationshipStatus(true);
				} else if (relationshipCell.getCellType() == CellType._NONE) {
					relationshipColumnIndexFlag = true;
					validateDto.setRelationshipStatus(false);
					validateDto.setRelationshipErrorMessage("Relationship None :: ");
				} else if (relationshipCell.getCellType() == CellType.BLANK) {
					relationshipColumnIndexFlag = true;
					validateDto.setRelationshipStatus(false);
					validateDto.setRelationshipErrorMessage("Relationship Blank :: ");
				} else if (relationshipCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = relationshipCell.getErrorCellValue();
					validateDto.setRelationship(String.valueOf(errorCellValue).trim());
					validateDto.setRelationshipStatus(false);
					validateDto.setRelationshipErrorMessage("Relationship Error :: ");
				} else if (relationshipCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = relationshipCell.getBooleanCellValue();
					validateDto.setRelationship(String.valueOf(booleanCellValue).trim());
					validateDto.setRelationshipStatus(false);
					validateDto.setRelationshipErrorMessage("Relationship Boolean :: ");
				} else if (relationshipCell.getCellType() == CellType.FORMULA) {
					switch (relationshipCell.getCellType()) {
					case NUMERIC: {
						relationship = String.valueOf(relationshipCell.getNumericCellValue()).trim();
						validateDto.setRelationshipStatus(false);
						validateDto.setRelationship(relationship);
						break;
					}
					case STRING: {
						relationship = relationshipCell.getStringCellValue().trim();
						validateDto.setRelationship(relationship);
						validateDto.setRelationshipStatus(true);
						break;
					}
					case BOOLEAN: {
						boolean booleanCellValue = relationshipCell.getBooleanCellValue();
						validateDto.setRelationship(String.valueOf(booleanCellValue).trim());
						validateDto.setRelationshipStatus(false);
						validateDto.setRelationshipErrorMessage("Relationship Boolean Formula :: ");
						break;
					}
					case ERROR: {
						byte errorCellValue = relationshipCell.getErrorCellValue();
						validateDto.setRelationship(String.valueOf(errorCellValue).trim());
						validateDto.setRelationshipStatus(false);
						validateDto.setRelationshipErrorMessage("Relationship Error Formula :: ");
						break;
					}
					case _NONE: {
						relationshipColumnIndexFlag = true;
						validateDto.setRelationshipStatus(false);
						validateDto.setRelationshipErrorMessage("Relationship None Formula :: ");
						break;
					}
					case BLANK: {
						relationshipColumnIndexFlag = true;
						validateDto.setRelationshipStatus(false);
						validateDto.setRelationshipErrorMessage("Relationship Blank Formula :: ");
						break;
					}
					default:
						throw new IllegalArgumentException(
								"Unexpected value: " + relationshipCell.getCachedFormulaResultType());
					}
				}

			}
			/**
			 * DateOfBirth Validation
			 */
			String dateOfBirthValue = null;
			if (dataRow.getCell(dateOfBirthColumnIndex) != null) {
				Cell dateOfClaimCell = dataRow.getCell(dateOfBirthColumnIndex);
//log.info("DateofBirth From Excel : {} and it's dataType is :{}",dateOfClaimCell,dateOfClaimCell.getCellType());
				if (dateOfClaimCell.getCellType() == CellType.NUMERIC) {
					dateOfBirthValue = String.valueOf(dateOfClaimCell.getDateCellValue());
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
					String format = outputFormat.format(dateOfClaimCell.getDateCellValue()).trim();
					validateDto.setDateOfBirthStatus(true);
					validateDto.setDateOfBirth(format);
				} else if (dateOfClaimCell.getCellType() == CellType.STRING) {
					dateOfBirthValue = dateOfClaimCell.getStringCellValue().trim();
					validateDto.setDateOfBirthStatus(true);
					validateDto.setDateOfBirth(dateOfBirthValue);
				} else if (dateOfClaimCell.getCellType() == CellType._NONE) {
					dateOfBirthColumnIndexFlag = true;
					validateDto.setDateOfBirthStatus(false);
					validateDto.setDateOfBirthErrorMessage("DateOfBirth None :: ");
				} else if (dateOfClaimCell.getCellType() == CellType.BLANK) {
					dateOfBirthColumnIndexFlag = true;
					validateDto.setDateOfBirthStatus(false);
					validateDto.setDateOfBirthErrorMessage("DateOfBirth Blank :: ");
//validateDto.setDateOfBirth(dateOfBirthValue);
				} else if (dateOfClaimCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = dateOfClaimCell.getErrorCellValue();
					validateDto.setDateOfBirth(String.valueOf(errorCellValue).trim());
					validateDto.setDateOfBirthStatus(false);
					validateDto.setDateOfBirthErrorMessage("DateOfBirth Error :: ");
				} else if (dateOfClaimCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = dateOfClaimCell.getBooleanCellValue();
					validateDto.setDateOfBirth(String.valueOf(booleanCellValue).trim());
					validateDto.setDateOfBirthStatus(false);
					validateDto.setDateOfBirthErrorMessage("DateOfBirth Boolean :: ");
				} else if (dateOfClaimCell.getCellType() == CellType.FORMULA) {
					switch (dateOfClaimCell.getCellType()) {
					case NUMERIC: {
						dateOfBirthValue = String.valueOf(dateOfClaimCell.getDateCellValue());
						SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
						String format = outputFormat.format(dateOfClaimCell.getDateCellValue()).trim();
						validateDto.setDateOfBirthStatus(true);
						validateDto.setDateOfBirth(format);
						break;
					}
					case STRING: {
						dateOfBirthValue = dateOfClaimCell.getStringCellValue().trim();
						validateDto.setDateOfBirthStatus(true);
						validateDto.setDateOfBirth(dateOfBirthValue);
						break;
					}
					case BOOLEAN: {
						boolean booleanCellValue = dateOfClaimCell.getBooleanCellValue();
						validateDto.setDateOfBirth(String.valueOf(booleanCellValue));
						validateDto.setDateOfBirthStatus(false);
						validateDto.setDateOfBirthErrorMessage("DateOfBirth Boolean :: ");
						break;
					}
					case ERROR: {
						byte errorCellValue = dateOfClaimCell.getErrorCellValue();
						validateDto.setDateOfBirth(String.valueOf(errorCellValue).trim());
						validateDto.setDateOfBirthStatus(false);
						validateDto.setDateOfBirthErrorMessage("DateOfBirth Error :: ");
						break;
					}
					case _NONE: {
						dateOfBirthColumnIndexFlag = true;
						validateDto.setDateOfBirthStatus(false);
						validateDto.setDateOfBirthErrorMessage("DateOfBirth None :: ");
						break;
					}
					case BLANK: {
						dateOfBirthColumnIndexFlag = true;
						validateDto.setDateOfBirthStatus(false);
						validateDto.setDateOfBirthErrorMessage("DateOfBirth Blank :: ");
						break;
					}
					default:
						throw new IllegalArgumentException(
								"Unexpected value: " + dateOfClaimCell.getCachedFormulaResultType());
					}
				}

			}
			/**
			 * PolicyStartDate Validation
			 */
			String policyStartDateValue = null;
			if (dataRow.getCell(policyStartDateColumnIndex) != null) {
				Cell policyStartDateCell = dataRow.getCell(policyStartDateColumnIndex);
				if (policyStartDateCell.getCellType() == CellType.NUMERIC) {
					policyStartDateValue = String.valueOf(policyStartDateCell.getDateCellValue()).trim();
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
					String format = outputFormat.format(policyStartDateCell.getDateCellValue());
					validateDto.setPolicyStartDateStatus(true);
					validateDto.setPolicyStartDate(format);
				} else if (policyStartDateCell.getCellType() == CellType.STRING) {
					policyStartDateValue = policyStartDateCell.getStringCellValue().trim();
					validateDto.setPolicyStartDateStatus(true);
					validateDto.setPolicyStartDate(policyStartDateValue);
				} else if (policyStartDateCell.getCellType() == CellType._NONE) {
//policyStartDateColumnIndexFlag = true;
					validateDto.setPolicyStartDateStatus(true);
//validateDto.setPolicyStartDateErrorMessage("PolicyStartDate None :: ");
				} else if (policyStartDateCell.getCellType() == CellType.BLANK) {
//policyStartDateColumnIndexFlag = true;
					validateDto.setPolicyStartDateStatus(true);
//validateDto.setPolicyStartDateErrorMessage("PolicyStartDate Blank :: ");
				} else if (policyStartDateCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = policyStartDateCell.getErrorCellValue();
					validateDto.setPolicyStartDate(String.valueOf(errorCellValue).trim());
					validateDto.setPolicyStartDateStatus(true);
//validateDto.setPolicyStartDateErrorMessage("PolicyStartDate Error :: ");
				} else if (policyStartDateCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = policyStartDateCell.getBooleanCellValue();
					validateDto.setPolicyStartDate(String.valueOf(booleanCellValue).trim());
					validateDto.setPolicyStartDateStatus(true);
//validateDto.setPolicyStartDateErrorMessage("PolicyStartDate Boolean :: ");
				} else if (policyStartDateCell.getCellType() == CellType.FORMULA) {
					switch (policyStartDateCell.getCellType()) {
					case NUMERIC: {
						policyStartDateValue = String.valueOf(policyStartDateCell.getDateCellValue()).trim();
						SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
						String format = outputFormat.format(policyStartDateCell.getDateCellValue());
						validateDto.setPolicyStartDateStatus(true);
						validateDto.setPolicyStartDate(format);
						break;
					}
					case STRING: {
						policyStartDateValue = policyStartDateCell.getStringCellValue().trim();
						validateDto.setPolicyStartDateStatus(true);
						validateDto.setPolicyStartDate(policyStartDateValue);
						break;
					}
					case BOOLEAN: {
						boolean booleanCellValue = policyStartDateCell.getBooleanCellValue();
						validateDto.setPolicyStartDate(String.valueOf(booleanCellValue).trim());
						validateDto.setPolicyStartDateStatus(true);
//   validateDto.setPolicyStartDateErrorMessage("PolicyStartDate Boolean Formula :: ");
						break;
					}
					case ERROR: {
						byte errorCellValue = policyStartDateCell.getErrorCellValue();
						validateDto.setPolicyStartDate(String.valueOf(errorCellValue).trim());
						validateDto.setPolicyStartDateStatus(true);
//   validateDto.setPolicyStartDateErrorMessage("PolicyStartDate Error Formula :: ");
						break;
					}
					case _NONE: {
//   policyStartDateColumnIndexFlag = true;
						validateDto.setPolicyStartDateStatus(true);
//   validateDto.setPolicyStartDateErrorMessage("PolicyStartDate None Formula :: ");
						break;
					}
					case BLANK: {
//   policyStartDateColumnIndexFlag = true;
						validateDto.setPolicyStartDateStatus(true);
//   validateDto.setPolicyStartDateErrorMessage("PolicyStartDate Blank Formula :: ");
						break;
					}
					default:
						throw new IllegalArgumentException(
								"Unexpected value: " + policyStartDateCell.getCachedFormulaResultType());
					}
				}
			}
			/**
			 * PolicyEndDate Validation
			 */
			String policyEndDateValue = null;
			if (dataRow.getCell(policyEndDateColumnIndex) != null) {
				Cell policyEndDateCell = dataRow.getCell(policyEndDateColumnIndex);
				if (policyEndDateCell.getCellType() == CellType.NUMERIC) {
					policyEndDateValue = String.valueOf(policyEndDateCell.getDateCellValue()).trim();
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
					String format = outputFormat.format(policyEndDateCell.getDateCellValue());
					validateDto.setPolicyEndDateStatus(true);
					validateDto.setPolicyEndDate(format);
				} else if (policyEndDateCell.getCellType() == CellType.STRING) {
					policyEndDateValue = policyEndDateCell.getStringCellValue().trim();
					validateDto.setPolicyEndDateStatus(true);
					validateDto.setPolicyEndDate(policyEndDateValue);
				} else if (policyEndDateCell.getCellType() == CellType._NONE) {
//policyEndDateColumnIndexFlag = true;
					validateDto.setPolicyEndDateStatus(true);
//validateDto.setPolicyEndDateErrorMessage("PolicyEndDate None :: ");
				} else if (policyEndDateCell.getCellType() == CellType.BLANK) {
//policyEndDateColumnIndexFlag = true;
					validateDto.setPolicyEndDateStatus(true);
//validateDto.setPolicyEndDateErrorMessage("PolicyEndDate Blank :: ");
				} else if (policyEndDateCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = policyEndDateCell.getErrorCellValue();
					validateDto.setPolicyEndDate(String.valueOf(errorCellValue).trim());
					validateDto.setPolicyEndDateStatus(true);
//validateDto.setPolicyEndDateErrorMessage("PolicyEndDate Error :: ");
				} else if (policyEndDateCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = policyEndDateCell.getBooleanCellValue();
					validateDto.setPolicyEndDate(String.valueOf(booleanCellValue).trim());
					validateDto.setPolicyEndDateStatus(true);
//validateDto.setPolicyEndDateErrorMessage("PolicyEndDate Boolean :: ");
				} else if (policyEndDateCell.getCellType() == CellType.FORMULA) {
					switch (policyEndDateCell.getCellType()) {
					case NUMERIC: {
						policyEndDateValue = String.valueOf(policyEndDateCell.getDateCellValue()).trim();
						SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
						String format = outputFormat.format(policyEndDateCell.getDateCellValue());
						validateDto.setPolicyEndDateStatus(true);
						validateDto.setPolicyEndDate(format);
						break;
					}
					case STRING: {
						policyEndDateValue = policyEndDateCell.getStringCellValue().trim();
						validateDto.setPolicyEndDateStatus(true);
						validateDto.setPolicyEndDate(policyEndDateValue);
						break;
					}
					case BOOLEAN: {
						boolean booleanCellValue = policyEndDateCell.getBooleanCellValue();
						validateDto.setPolicyEndDate(String.valueOf(booleanCellValue).trim());
						validateDto.setPolicyEndDateStatus(true);
//   validateDto.setPolicyEndDateErrorMessage("PolicyEndDate Boolean Formula :: ");
						break;
					}
					case ERROR: {
						byte errorCellValue = policyEndDateCell.getErrorCellValue();
						validateDto.setPolicyEndDate(String.valueOf(errorCellValue).trim());
						validateDto.setPolicyEndDateStatus(true);
//   validateDto.setPolicyEndDateErrorMessage("PolicyEndDate Error Formula :: ");
						break;
					}
					case _NONE: {
//   policyEndDateColumnIndexFlag = true;
						validateDto.setPolicyEndDateStatus(true);
//   validateDto.setPolicyEndDateErrorMessage("PolicyEndDate None Formula :: ");
						break;
					}
					case BLANK: {
//   policyEndDateColumnIndexFlag = true;
						validateDto.setPolicyEndDateStatus(true);
//   validateDto.setPolicyEndDateErrorMessage("PolicyEndDate Blank Formula :: ");
						break;
					}
					default:
						throw new IllegalArgumentException(
								"Unexpected value: " + policyEndDateCell.getCachedFormulaResultType());
					}
				}
			}
			/**
			 * ECardNumber Validation
			 */
			int memberCodeInteger;
			if (dataRow.getCell(eCardNumberColumnIndex) != null) {
				Cell memberCodeCell = dataRow.getCell(eCardNumberColumnIndex);
//log.info("ECardNumber From Excel : {} and it's dataType is :{}",memberCodeCell,memberCodeCell.getCellType());
				if (memberCodeCell.getCellType() == CellType.NUMERIC) {
					memberCodeInteger = (int) memberCodeCell.getNumericCellValue();
					validateDto.setECardNumber(String.valueOf(memberCodeInteger).trim());
					validateDto.setECardNumberStatus(true);
				} else if (memberCodeCell.getCellType() == CellType.STRING) {
					String memberCode = memberCodeCell.getStringCellValue().trim();
					validateDto.setECardNumber(memberCode);
					validateDto.setECardNumberStatus(true);
				} else if (memberCodeCell.getCellType() == CellType._NONE) {
					eCardNumberColumnIndexFlag = true;
					validateDto.setECardNumberStatus(false);
					validateDto.setECardNumberErrorMessage("ECard Number None :: ");
				} else if (memberCodeCell.getCellType() == CellType.BLANK) {
					eCardNumberColumnIndexFlag = true;
					validateDto.setECardNumberStatus(false);
					validateDto.setECardNumberErrorMessage("ECard Number Blank :: ");
				} else if (memberCodeCell.getCellType() == CellType.ERROR) {
					byte errorCellValue = memberCodeCell.getErrorCellValue();
					validateDto.setECardNumber(String.valueOf(errorCellValue).trim());
					validateDto.setECardNumberStatus(false);
					validateDto.setECardNumberErrorMessage("ECard Number Error :: ");
				} else if (memberCodeCell.getCellType() == CellType.BOOLEAN) {
					boolean booleanCellValue = memberCodeCell.getBooleanCellValue();
					validateDto.setECardNumber(String.valueOf(booleanCellValue).trim());
					validateDto.setECardNumberStatus(false);
					validateDto.setECardNumberErrorMessage("ECard Number Boolean :: ");
				} else if (memberCodeCell.getCellType() == CellType.FORMULA) {
					switch (memberCodeCell.getCellType()) {
					case NUMERIC: {
						memberCodeInteger = (int) memberCodeCell.getNumericCellValue();
						validateDto.setECardNumber(String.valueOf(memberCodeInteger).trim());
						validateDto.setECardNumberStatus(true);
						break;
					}
					case STRING: {
						String memberCode = memberCodeCell.getStringCellValue().trim();
						validateDto.setECardNumber(memberCode);
						validateDto.setECardNumberStatus(true);
						break;
					}
					case BOOLEAN: {
						boolean booleanCellValue = memberCodeCell.getBooleanCellValue();
						validateDto.setECardNumber(String.valueOf(booleanCellValue).trim());
						validateDto.setECardNumberStatus(false);
						validateDto.setECardNumberErrorMessage("ECard Number Boolean :: ");
						break;
					}
					case ERROR: {
						byte errorCellValue = memberCodeCell.getErrorCellValue();
						validateDto.setECardNumber(String.valueOf(errorCellValue).trim());
						validateDto.setECardNumberStatus(false);
						validateDto.setECardNumberErrorMessage("ECard Number Error :: ");
						break;
					}
					case _NONE: {
						eCardNumberColumnIndexFlag = true;
						validateDto.setECardNumberStatus(false);
						validateDto.setECardNumberErrorMessage("ECard Number None :: ");
						break;
					}
					case BLANK: {
						eCardNumberColumnIndexFlag = true;
						validateDto.setECardNumberStatus(false);
						validateDto.setECardNumberErrorMessage("ECard Number Blank :: ");
						break;
					}
					default:
						throw new IllegalArgumentException(
								"Unexpected value: " + memberCodeCell.getCachedFormulaResultType());
					}
				}
			}
//

			if (employeeIdColumnIndexFlag && employeeNameColumnIndexFlag && relationshipColumnIndexFlag
					&& genderColumnIndexFlag && ageColumnIndexFlag && dateOfBirthColumnIndexFlag
					&& sumInsuredColumnIndexFlag && policyStartDateColumnIndexFlag && policyEndDateColumnIndexFlag
					&& eCardNumberColumnIndexFlag) {
				log.info("Blank Row");
			} else {
				claimsMisValidateData.add(validateDto);
			}
		}

	}

	@Override
	public String uploadEnrollmentData(List<ClientListMemberDetailsDataStatus> clientListMemberDetailsDataStatuses,
			Long clientListId, Long productId) {
		List<ClientListEnrollementEntity> clientListEnrollementEntities = clientListMemberDetailsDataStatuses.stream()
				.map(dto -> {
					ClientListEnrollementEntity entity = new ClientListEnrollementEntity();
					entity.setEmployeeId(dto.getEmployeeId());
					entity.setEmployeeName(dto.getEmployeeName());
					entity.setDateOfBirth(dto.getDateOfBirth());
					entity.setGender(dto.getGender());
					entity.setRelation(dto.getRelationship());
					entity.setAge(Double.parseDouble(dto.getAge()));
					entity.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
					entity.setRecordStatus("ACTIVE");
					entity.setECardNumber(dto.getECardNumber());
					if (clientListId != null) {
						ClientList clientList = clientListRepository.findById(clientListId)
								.orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
						entity.setClientList(clientList);
						entity.setRfqId(clientList.getRfqId());
					}
					if (productId != null) {
						Product product = productRepository.findById(productId)
								.orElseThrow(() -> new InvalidProduct("Product is not Found"));
						entity.setProduct(product);
					}
					return entity;
				}).collect(Collectors.toList());

		clientListEnrollmentEntityRepository.saveAll(clientListEnrollementEntities);
		return "Upload successful"; // Return a success message or handle errors appropriately
	}

	@Override
	public List<GetAllClientListEnrollmentDto> getAllclientListEnrollmentData(Long clientListId, Long productId,
			String month) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
		return clientListEnrollmentEntityRepository.findAll().stream()
				.filter(i -> i.getProduct() != null && i.getProduct().getProductId().equals(productId))
				.filter(i -> i.getClientList() != null && i.getClientList().getCid() == clientListId)
				.filter(i -> clientListId != null && i.getClientList().getCid() == clientListId).filter(c -> {
					if (month.equalsIgnoreCase("ALL")) {
						return true; // Return true for all records if month is "ALL"
					}
					try {
						LocalDateTime dateTime = LocalDateTime.parse(c.getCreatedDate(), formatter);
						String monthName = dateTime.getMonth().name(); // Get the month name from the date
						return monthName.equalsIgnoreCase(month); // Check if it matches the desired month
					} catch (NullPointerException | DateTimeParseException e) {
						return false; // Handle null or invalid dates
					}
				}).map(i -> {
					GetAllClientListEnrollmentDto getAllClientListEnrollmentDto = new GetAllClientListEnrollmentDto();
					getAllClientListEnrollmentDto.setEmployeeId(i.getEmployeeId());
					getAllClientListEnrollmentDto.setEmployeeName(i.getEmployeeName());
					getAllClientListEnrollmentDto.setDateOfBirth(i.getDateOfBirth());
					getAllClientListEnrollmentDto.setGender(i.getGender());
					getAllClientListEnrollmentDto.setRelation(i.getRelation());
					getAllClientListEnrollmentDto.setDateOfJoining(i.getDateOfJoining());
					getAllClientListEnrollmentDto.setECardNumber(i.getECardNumber());
					getAllClientListEnrollmentDto.setPolicyCommencementDate(i.getPolicyCommencementDate());
					getAllClientListEnrollmentDto.setPolicyValidUpto(i.getPolicyValidUpTo());
					getAllClientListEnrollmentDto.setBaseSumInsured(i.getBaseSumInsured());
					getAllClientListEnrollmentDto.setTopUpSumInsured(i.getTopUpSumInsured());
					getAllClientListEnrollmentDto.setGroupName(i.getGroupName());
					getAllClientListEnrollmentDto.setInsuredCompanyName(i.getInsuredCompanyName());
					LocalDateTime dateTime = LocalDateTime.parse(i.getCreatedDate(), formatter);
					String monthName = dateTime.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH));
					getAllClientListEnrollmentDto.setMonth(monthName); // You may need to adjust this field based on
																		// your requirement
//	                   getAllClientListEnrollmentDto.setAge(String.valueOf(i.getAge()));
					return getAllClientListEnrollmentDto;
				}).toList();

	}

	@Override
	public List<GetAllClientListPendingListDto> getAllClientPendingListData(Long productId, Long clientListId) {
		List<ClientListEnrollementEntity> enrollmentList = clientListEnrollmentEntityRepository.findAll().stream()
				.filter(i -> productId != null && i.getProduct() != null
						&& Objects.equals(i.getProduct().getProductId(), productId))
				.filter(i -> clientListId != null && i.getClientList() != null
						&& Objects.equals(i.getClientList().getCid(), clientListId))
				.toList();
		LocalDate currentDate = LocalDate.now();
		LocalDateTime startOfCurrentDay = currentDate.atStartOfDay();

		List<ClientListMemberDetails> membersList = clientListMemberDetailsRepository.findAll().stream()
				.filter(i -> clientListId != null && i.getClientList().getCid() == clientListId)
				.filter(i -> i.getProduct().getProductId().equals(productId)).filter(i -> {
					// Parse the createdDate to extract day part
					LocalDate createdDay = LocalDate.parse(i.getCreatedDate(),
							DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault()));
					// Compare the day part with the current day
					return createdDay.equals(currentDate);
				}).toList();

		List<GetAllClientListPendingListDto> pendingListDto = new ArrayList<>();

		for (ClientListMemberDetails member : membersList) {
			boolean foundInEnrollment = false;
			for (ClientListEnrollementEntity enrollment : enrollmentList) {
				if (Objects.equals(enrollment.getEmployeeId(), member.getEmployeeNo())) {
					foundInEnrollment = true;
					break;
				}
			}
			if (!foundInEnrollment) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
				GetAllClientListPendingListDto dto = new GetAllClientListPendingListDto();
				dto.setMemberId(member.getMemberId());
				dto.setEmployeeNo(member.getEmployeeNo());
				dto.setName(member.getName());
				dto.setRelationShip(member.getRelationShip());
				dto.setEmail(member.getEmail());
				dto.setPhoneNumber(member.getPhoneNumber());
				dto.setSumInsured(member.getSumInsured());
				dto.setRole(member.getRole());
				if (member.getDesignation() != null) {
					Designation designation = designationRepository.findById(member.getDesignation().getId())
							.orElseThrow(() -> new InvalidDesignationException("Designation is not Valid"));
					dto.setDesignation(designation.getDesignationName());
				}
				if (member.getDepartment() != null) {
					Department department = departmentRepository.findById(member.getDepartment().getId())
							.orElseThrow(() -> new InvalidDepartmentException("Department is not Valid"));
					dto.setDepartment(department.getDepartmentName());
				}
				LocalDateTime dateTime = LocalDateTime.parse(member.getCreatedDate(), formatter);
				String monthName = dateTime.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH));
				dto.setMonth(monthName); // You may need to adjust this field based on your requirement
				if (member.getDeletedStatus() != null && !member.getDeletedStatus().isEmpty()
						&& member.getDeletedStatus().equalsIgnoreCase("ACTIVE")) {
					dto.setStatus("to be deleted");
				} else if (member.getUpdatedStatus() != null && member.getUpdatedStatus().equalsIgnoreCase("ACTIVE")) {
					dto.setStatus("to be updated");
				} else {
					dto.setStatus("pending"); // Or any other default status you prefer
				}
				pendingListDto.add(dto);
			}
		}
		return pendingListDto;
	}

	@Override
	public List<GetAllClientListMembersDetailsDto> getAllClientListMembersDetailsForEmployee(Long clientListId,
			Long productId, String employeeId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
		return clientListMemberDetailsRepository.findAll().stream()
				.filter(i -> clientListId != null && i.getClientList().getCid() == clientListId)
				.filter(i -> productId != null && i.getProduct().getProductId().equals(productId))
				.filter(i -> employeeId != null && i.getEmployeeNo().equalsIgnoreCase(employeeId)).map(i -> {
					GetAllClientListMembersDetailsDto dto = new GetAllClientListMembersDetailsDto();
					dto.setMemberId(i.getMemberId());
					dto.setEmployeeNo(i.getEmployeeNo());
					dto.setName(i.getName());
					dto.setRelationShip(i.getRelationShip());
					dto.setGender(i.getGender());
					LocalDateTime dateTime = LocalDateTime.parse(i.getCreatedDate(), formatter);
					String monthName = dateTime.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH));
					dto.setMonth(monthName);
					dto.setAge(i.getAge());
					dto.setSumInsured(i.getSumInsured());
					dto.setEmail(i.getEmail());
					dto.setPhoneNumber(i.getPhoneNumber());
					if (i.getDesignation() != null) {
						Designation designation = designationRepository.findById(i.getDesignation().getId())
								.orElseThrow(() -> new InvalidDesignationException("Designation is not Valid"));
						dto.setDesignation(designation.getDesignationName());
					}
					if (i.getDepartment() != null) {
						Department department = departmentRepository.findById(i.getDepartment().getId())
								.orElseThrow(() -> new InvalidDepartmentException("Department is not Valid"));
						dto.setDepartment(department.getDepartmentName());
					}
					dto.setRole(i.getRole());
					return dto;
				}).toList();
	}

	@Override
	public byte[] downloadMembersDetailsForEmployeeInExcelFormat(Long clientListId, Long productId, String employeeId) {
		List<ClientListMemberDetails> clientListMemberDetails = clientListMemberDetailsRepository.findAll().stream()
				.filter(i -> clientListId != null && i.getClientList().getCid() == clientListId)
				.filter(i -> productId != null && i.getProduct().getProductId().equals(productId))
				.filter(i -> employeeId != null && i.getEmployeeNo().equalsIgnoreCase(employeeId)).toList();

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Client List Members");

			// Create headers
			Row headerRow = sheet.createRow(0);
			String[] headers = { "Employee No", "Name", "Relationship", "Gender", "Date of Birth", "Age", "Sum Insured",
					"Email", "Phone Number" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
			}

			// Populate data
			int rowNum = 1;
			for (ClientListMemberDetails member : clientListMemberDetails) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(member.getEmployeeNo());
				row.createCell(1).setCellValue(member.getName());
				row.createCell(2).setCellValue(member.getRelationShip());
				row.createCell(3).setCellValue(member.getGender());
				row.createCell(4).setCellValue(member.getDateOfBirth());
				row.createCell(5).setCellValue(member.getAge());
				row.createCell(6).setCellValue(member.getSumInsured());
				row.createCell(7).setCellValue(member.getEmail());
				row.createCell(8).setCellValue(member.getPhoneNumber());
			}

			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception properly in your application
			return new byte[0]; // Return empty byte array if an error occurs
		}
	}

	@Override
	public String validateEmployeeDetails(List<CoverageDetailsChildValidateValuesDto> coverageDetailsChildValidateValuesDtos, Long clientListId, Long productId) {
		List<ClientListMemberDetails> list = coverageDetailsChildValidateValuesDtos.stream().map(i -> {
			ClientListMemberDetails childValidateValuesDto = new ClientListMemberDetails();
			childValidateValuesDto.setEmployeeNo(i.getEmployeeIdValue());
			childValidateValuesDto.setName(i.getEmployeeNameValue());
			childValidateValuesDto.setRelationShip(i.getRelationshipValue());
			childValidateValuesDto.setGender(i.getGenderValue());
			childValidateValuesDto.setAge(Double.valueOf(i.getAgeValue()));
			childValidateValuesDto.setDateOfBirth(i.getDateOfBirthValue());
			childValidateValuesDto.setSumInsured(Double.valueOf(i.getSumInsuredValue()));

			if (clientListId != null) {
				ClientList clientList = clientListRepository.findById(clientListId)
						.orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
				childValidateValuesDto.setClientList(clientList);
				childValidateValuesDto.setRfqId(clientList.getRfqId());
			}

			if (productId != null) {
				Product product = productRepository.findById(productId)
						.orElseThrow(() -> new InvalidProduct("Product is not Found"));
				childValidateValuesDto.setProduct(product);
			}

			childValidateValuesDto.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
			childValidateValuesDto.setRecordStatus("ACTIVE");

			return childValidateValuesDto;
		}).toList();

		// Delete existing records for the given clientListId and productId
		clientListMemberDetailsRepository.deleteClientListMemberDetails(clientListId, productId);

		// Save the new list of ClientListMemberDetails
		clientListMemberDetailsRepository.saveAll(list);

		if (!list.isEmpty()) {
			return "Uploaded Successfully";
		} else {
			return "Failed to Upload";
		}
	}
}
