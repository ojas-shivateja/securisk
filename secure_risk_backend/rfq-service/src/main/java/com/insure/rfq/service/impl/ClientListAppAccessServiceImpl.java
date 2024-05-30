package com.insure.rfq.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.insure.rfq.dto.ClientListAppAccessStatusDto;
import com.insure.rfq.dto.ClientLoginDto;
import com.insure.rfq.dto.GetAllAppAccessDto;
import com.insure.rfq.dto.UpdateAppAccessDto;
import com.insure.rfq.entity.ClientList;
import com.insure.rfq.entity.ClientListAppAccess;
import com.insure.rfq.entity.Product;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidDepartmentException;
import com.insure.rfq.exception.InvalidDesignationException;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.login.entity.Department;
import com.insure.rfq.login.entity.Designation;
import com.insure.rfq.login.repository.DepartmentRepository;
import com.insure.rfq.login.repository.DesignationRepository;
import com.insure.rfq.login.service.JwtService;
import com.insure.rfq.repository.ClientListAppAccessRepository;
import com.insure.rfq.repository.ClientListRepository;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ClientListAppAccessService;

@Service
//@Slf4j
public class ClientListAppAccessServiceImpl implements ClientListAppAccessService {
	@Autowired
	private ClientListRepository clientListRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private DesignationRepository designationRepository;

	@Autowired
	private ClientListAppAccessRepository clientListAppAccessRepository;

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	@Value("classpath:excelTemplate/App Access .xlsx")
	Resource resourceFile;

	private Workbook getWorkbook(MultipartFile file) throws IOException {
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

	private String getStringCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue();
	}

	@Override
	public String uploadAppAccessExcel(MultipartFile multipartFile, Long clientListId, Long productId)
			throws IOException {
		Workbook workbook = getWorkbook(multipartFile);
		List<ClientListAppAccess> clientListAppAccessDtos = new ArrayList<>();

		Sheet sheet = workbook.getSheetAt(0);
		for (Row row : sheet) {
			if (row.getRowNum() == 0) {
				// Skip header row
				continue;
			}

			ClientListAppAccess clientListAppAccess = new ClientListAppAccess(); // Create a new instance for each row

			if (clientListId != null) {
				try {
					ClientList clientList = clientListRepository.findById(clientListId)
							.orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
					clientListAppAccess.setClientList(clientList);
					clientListAppAccess.setRfqId(clientList.getRfqId());
				} catch (InvalidClientList e) {
					// Handle exception
				}
			}
			if (productId != null) {
				try {
					Product product = productRepository.findById(productId)
							.orElseThrow(() -> new InvalidProduct("Product is not Found"));
					clientListAppAccess.setProduct(product);
				} catch (InvalidProduct e) {
					// Handle exception
				}
			}
			clientListAppAccess.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
			clientListAppAccess.setRecordStatus("ACTIVE");
			clientListAppAccess.setAppAccessStatus("ACTIVATED");
			// Assuming the column order is: Emp ID, Emp Name, Relationship, Email, Phone
			clientListAppAccess.setEmployeeId(getStringCellValue(row.getCell(1))); // Emp ID
			clientListAppAccess.setEmployeeName(getStringCellValue(row.getCell(2)));
			clientListAppAccess.setDateOfBirth(getStringCellValue(row.getCell(3)));
			clientListAppAccess.setRelationship(getStringCellValue(row.getCell(6))); // Relationship
			clientListAppAccess.setAge(getStringCellValue(row.getCell(4)));
			clientListAppAccess.setGender(getStringCellValue(row.getCell(5)));
			clientListAppAccess.setSumInsured(getStringCellValue(row.getCell(7)));
			clientListAppAccess.setEmail(getStringCellValue(row.getCell(8))); // Email
			clientListAppAccess.setPhoneNumber(getStringCellValue(row.getCell(9))); // Phone

			clientListAppAccessDtos.add(clientListAppAccess);
		}

		List<ClientListAppAccess> clientListAppAccesses = clientListAppAccessRepository
				.saveAll(clientListAppAccessDtos);
		if (!clientListAppAccesses.isEmpty()) {
			return "Created Successfully";
		} else {
			return "Failed to create ";
		}
	}

	@Override
	public boolean sendLoginCredentials(List<String> employeeEmails) {
		List<String> rawPasswords = new ArrayList<>();
		List<String> encodedPasswords = generateRandomPasswords(employeeEmails.size(), rawPasswords);

		for (int i = 0; i < employeeEmails.size(); i++) {
			String email = employeeEmails.get(i);
			if (email.isEmpty()) {
				continue;
			}
			String rawPassword = rawPasswords.get(i);
			String encodedPassword = encodedPasswords.get(i);

			if (!sendEmail(email, rawPassword)) {
				return false;
			}

			updateUserPassword(email, encodedPassword);
		}
		return true;
	}

	@Override
	public List<GetAllAppAccessDto> getAllAppAccessDto(Long clientListId, Long productId) {
		return clientListAppAccessRepository.findAll().stream()
				.filter(i -> i.getRecordStatus().equalsIgnoreCase("ACTIVE"))
				.filter(i -> clientListId != 0 && i.getClientList().getCid() == clientListId)
				.filter(i -> productId != 0 && i.getProduct().getProductId().equals(productId)).map(i -> {
					GetAllAppAccessDto appAccessDto = new GetAllAppAccessDto();
					appAccessDto.setAppAccessId(i.getAppAccessId());
					appAccessDto.setEmployeeNo(i.getEmployeeId());
					appAccessDto.setName(i.getEmployeeName());
					appAccessDto.setRelationship(i.getRelationship());
					appAccessDto.setAge(i.getAge());
					appAccessDto.setRole(i.getRole());
					appAccessDto.setDateOfBirth(i.getDateOfBirth());
					appAccessDto.setEmail(i.getEmail());
					appAccessDto.setPhoneNumber(i.getPhoneNumber());
					appAccessDto.setGender(i.getGender());
					appAccessDto.setSumInsured(i.getSumInsured());

					appAccessDto.setAppAccessStatus(i.getAppAccessStatus());
					if (i.getDepartment() != null) {
						appAccessDto.setDepartment(i.getDepartment().getDepartmentName());
					} else {
						appAccessDto.setDepartment(null);
					}
					if (i.getDesignation() != null) {
						appAccessDto.setDesignation(i.getDesignation().getDesignationName());
					} else {
						appAccessDto.setDesignation(null);
					}
					return appAccessDto;
				}).toList();
	}

	@Override
	public GetAllAppAccessDto getAllAppAccessDtoById(Long appAccessId) {
		ClientListAppAccess appAccessDto = clientListAppAccessRepository.findById(appAccessId).orElse(null);
		GetAllAppAccessDto getAllAppAccessDto = new GetAllAppAccessDto();
		getAllAppAccessDto.setAppAccessId(appAccessDto.getAppAccessId());
		getAllAppAccessDto.setEmployeeNo(appAccessDto.getEmployeeId());
		getAllAppAccessDto.setName(appAccessDto.getEmployeeName());
		getAllAppAccessDto.setRelationship(appAccessDto.getRelationship());
		getAllAppAccessDto.setAge(appAccessDto.getAge());
		getAllAppAccessDto.setRole(appAccessDto.getRole());
		getAllAppAccessDto.setDateOfBirth(appAccessDto.getDateOfBirth());
		getAllAppAccessDto.setEmail(appAccessDto.getEmail());
		getAllAppAccessDto.setPhoneNumber(appAccessDto.getPhoneNumber());
		getAllAppAccessDto.setGender(appAccessDto.getGender());
		getAllAppAccessDto.setSumInsured(appAccessDto.getSumInsured());
		getAllAppAccessDto.setAppAccessStatus(appAccessDto.getAppAccessStatus());
		if (appAccessDto.getDepartment() != null) {
			getAllAppAccessDto.setDepartment(appAccessDto.getDepartment().getDepartmentName());
		} else {
			getAllAppAccessDto.setDepartment(null);
		}
		if (appAccessDto.getDesignation() != null) {
			getAllAppAccessDto.setDesignation(appAccessDto.getDesignation().getDesignationName());
		} else {
			getAllAppAccessDto.setDesignation(null);
		}
		return getAllAppAccessDto;
	}

	@Override
	public UpdateAppAccessDto updateAppAccessDtoById(Long appAccessId, UpdateAppAccessDto updateAppAccessDto) {
		ClientListAppAccess appAccessDto = clientListAppAccessRepository.findById(appAccessId).orElse(null);
		appAccessDto.setEmployeeId(updateAppAccessDto.getEmployeeNo());
		appAccessDto.setEmployeeName(updateAppAccessDto.getName());
		appAccessDto.setRelationship(updateAppAccessDto.getRelationship());
		appAccessDto.setGender(updateAppAccessDto.getGender());
		appAccessDto.setDateOfBirth(updateAppAccessDto.getDateOfBirth());
		appAccessDto.setAge(updateAppAccessDto.getAge());
		appAccessDto.setSumInsured(updateAppAccessDto.getSumInsured());
		appAccessDto.setEmail(updateAppAccessDto.getEmail());
		appAccessDto.setPhoneNumber(updateAppAccessDto.getPhoneNumber());
		if (appAccessDto.getDepartment() != null) {
			try {
				Department department = departmentRepository
						.findById(Long.parseLong(updateAppAccessDto.getDepartment()))
						.orElseThrow(() -> new InvalidDepartmentException("Department is not Found"));
				appAccessDto.setDepartment(department);
			} catch (InvalidDepartmentException e) {
				throw new InvalidDepartmentException("Invalid department");
			}
		}
		if (appAccessDto.getDesignation() != null) {
			try {
				Designation designation = designationRepository
						.findById(Long.parseLong(updateAppAccessDto.getDesignation()))
						.orElseThrow(() -> new InvalidDesignationException("Designation is not Found"));
				appAccessDto.setDesignation(designation);
			} catch (InvalidDepartmentException e) {
				throw new InvalidDesignationException("Invalid designation");
			}

		}
		appAccessDto.setRole(updateAppAccessDto.getRole());
		ClientListAppAccess savedClientListAppAccess = clientListAppAccessRepository.save(appAccessDto);
		updateAppAccessDto.setEmployeeNo(savedClientListAppAccess.getEmployeeId());
		updateAppAccessDto.setName(savedClientListAppAccess.getEmployeeName());
		updateAppAccessDto.setRelationship(savedClientListAppAccess.getRelationship());
		updateAppAccessDto.setGender(savedClientListAppAccess.getGender());
		updateAppAccessDto.setDateOfBirth(savedClientListAppAccess.getDateOfBirth());
		updateAppAccessDto.setAge(savedClientListAppAccess.getAge());
		updateAppAccessDto.setSumInsured(savedClientListAppAccess.getSumInsured());
		updateAppAccessDto.setEmail(savedClientListAppAccess.getEmail());
		updateAppAccessDto.setPhoneNumber(savedClientListAppAccess.getPhoneNumber());
		if (savedClientListAppAccess.getDepartment() != null) {
			updateAppAccessDto.setDepartment(appAccessDto.getDepartment().getDepartmentName());
		} else {
			updateAppAccessDto.setDepartment(null);
		}
		if (appAccessDto.getDesignation() != null) {
			updateAppAccessDto.setDesignation(appAccessDto.getDesignation().getDesignationName());
		} else {
			updateAppAccessDto.setDesignation(null);
		}
		updateAppAccessDto.setRole(savedClientListAppAccess.getRole());
		return updateAppAccessDto;
	}

	@Override
	public ClientLoginDto authenticate(String username, String password) {
//		ClientListAppAccess clientListAppAccess = clientListAppAccessRepository.findByEmail(username)
//				.orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
//
//		if (!password.equals(clientListAppAccess.getPassword())) {
//			throw new UsernameNotFoundException("Invalid credentials");
//		}
//
//		ClientLoginDto clientLoginDto = new ClientLoginDto();
//		clientLoginDto.setAccessToken(jwtService.generateToken(username));
//		clientLoginDto.setClientListId(clientListAppAccess.getClientList().getCid());
//		clientLoginDto.setProductId(clientListAppAccess.getProduct().getProductId());
//		clientLoginDto.setEmployeeId(clientListAppAccess.getEmployeeId());
//
//		return clientLoginDto;
		return null;
	}

	@Override
	public String changeAppAccessStatus(List<ClientListAppAccessStatusDto> clientListAppAccessStatusDto) {
		// Iterate over the DTOs
		for (ClientListAppAccessStatusDto dto : clientListAppAccessStatusDto) {
			Long appAccessId = dto.getAppAccessId();
			String status = dto.getStatus();

			// Retrieve the ClientListAppAccess entity by id
			ClientListAppAccess clientListAppAccess = clientListAppAccessRepository.findById(appAccessId).orElse(null);

			if (clientListAppAccess != null) {
				if ("ACTIVATED".equalsIgnoreCase(status)) {
					clientListAppAccess.setAppAccessStatus("ACTIVATED");
				} else if ("DEACTIVATED".equalsIgnoreCase(status)) {
					clientListAppAccess.setAppAccessStatus("DEACTIVATED");
				}
				clientListAppAccessRepository.save(clientListAppAccess);
			}
		}
		// Return some indication of success or failure
		return "App access status updated successfully";

	}

	@Override
	public String deleteAppAccessById(Long appAccessId) {
		ClientListAppAccess clientListAppAccess = clientListAppAccessRepository.findById(appAccessId).orElse(null);
		clientListAppAccess.setRecordStatus("INACTIVE");
		clientListAppAccessRepository.save(clientListAppAccess);
		return "App access deleted successfully";
	}

	@Override
	public byte[] downloadAppAccessTemplate() throws IOException {
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

	public List<String> generateRandomPasswords(int count, List<String> rawPasswords) {
		List<String> encodedPasswords = new ArrayList<>();
		Random random = new Random();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		for (int i = 0; i < count; i++) {
			StringBuilder passwordBuilder = new StringBuilder();
			for (int j = 0; j < 8; j++) {
				char randomChar = (char) (random.nextInt(26) + 'a'); // Generate lowercase letters only
				passwordBuilder.append(randomChar);
			}
			String rawPassword = passwordBuilder.toString();
			String encodedPassword = passwordEncoder.encode(rawPassword);

			rawPasswords.add(rawPassword);
			encodedPasswords.add(encodedPassword);
		}
		return encodedPasswords;
	}

	private void updateUserPassword(String email, String password) {
		ClientListAppAccess clientListAppAccess = clientListAppAccessRepository.findByEmail(email).orElse(null);
		clientListAppAccess.setPassword(password);
		clientListAppAccessRepository.save(clientListAppAccess);

	}

	private boolean sendEmail(String email, String password) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(email);
			message.setSubject("Your Login Credentials");
			message.setText("Hello User ,\n\n" + "Your login credentials are as follows:\n" + "Username: " + email
					+ "\n" + "Password: " + password + "\n\n"
					+ "Please use the following link to log in: http://14.99.138.131:9980/Securisk\n\n" + "Thank you,\n"
					+ "Securisk Insure Broker");

			emailSender.send(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String clearAllAppAccess() {
		clientListAppAccessRepository.deleteAll();
		return "All App access are deleted successfully";
	}

}
