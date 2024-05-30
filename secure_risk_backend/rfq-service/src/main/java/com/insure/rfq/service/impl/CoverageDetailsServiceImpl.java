package com.insure.rfq.service.impl;

import com.insure.rfq.dto.*;
import com.insure.rfq.entity.*;
import com.insure.rfq.generator.AgeBindingReportPdfGenerator;
import com.insure.rfq.generator.CoverageDetailsPdfGenerator;
import com.insure.rfq.generator.EmployeeDataReportPdfGenerator;
import com.insure.rfq.generator.IrdaPdfGenerator;
import com.insure.rfq.payload.DataToEmail;
import com.insure.rfq.repository.*;
import com.insure.rfq.service.ClaimsMisService;
import com.insure.rfq.service.CoverageValidateFilenamesService;
import com.insure.rfq.utils.EmpDependentHeaderConstants;
import com.insure.rfq.utils.ExcelUtils;
import com.insure.rfq.utils.ExcelValidation;
import com.itextpdf.text.Font;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
public class CoverageDetailsServiceImpl
        implements com.insure.rfq.service.CoverageDetailsService, CoverageValidateFilenamesService, ClaimsMisService {

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

    @Value("classpath:excelTemplate/templateEmployee.xlsx")
    Resource resourceFile;
    @Autowired
    private PolicyTermsRepository policyTermsRepository;

    @Autowired
    private CoverageDetailsRepository cdEBRepo;
    @Autowired
    private CorporateDetailsRepository corporateDetailsRepo;
    @Autowired
    private ExpiryPolicyDetailsRepository expiryPolicyDetailsRepository;
    @Value("${file.path.coverageMain}")
    private String mainpath;
    @Autowired
    private CoverageDetailsPdfGenerator coverageDetailsPdfGenerator;
    @Autowired
    private EmpDependentRepository empDepRepo;
    @Autowired
    private CoverageValidateFilenamesRepository filesRepo;
    @Autowired
    private IrdaPdfGenerator generator;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CoverageFileUploadRepository fileUploadRepo;
    @Autowired
    private EmpDependentHeaderRepository empDepHeaderRepo;
    @Autowired
    private ExcelReportHeadersRepository reportHeaderRepo;
    @Autowired
    private ClaimsMisRepository claimsMisRepo;
    @Autowired
    private TpaRepository tpaRepo;

    private Map<String, Integer> columnMapping;
    @Autowired
    private AgeBindingReportPdfGenerator ageBindingReportPdfGenerator;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ExcelUtils excelUtils;

    @Autowired
    private ClaimsDetailsRepository claimsDetailsRepository;

    private static final int COLUMN_0_INDEX = 0;
    private static final int COLUMN_1_INDEX = 1;
    private static final int COLUMN_2_INDEX = 2;
    private static final int COLUMN_3_INDEX = 3;
    private static final int COLUMN_4_INDEX = 4;
    private static final int COLUMN_5_INDEX = 5;

    private static final Logger LOG = LoggerFactory.getLogger(CoverageDetailsServiceImpl.class);
    @Autowired
    private EmailAlongAttachement emailAlongAttachement;
    HttpServletResponse httpServletResponse;

    @Override
    public String createCoverageDetails(CoverageDetailsDto details) {
        // Check if there's an existing record with the same RFQ ID
        Optional<CoverageDetailsEntity> existingCoverageDetailsOptional = cdEBRepo.findByRfqId(details.getRfqId());

        // If an existing record is found, delete it
        existingCoverageDetailsOptional.ifPresent(cdEBRepo::delete);
        CoverageDetailsEntity entity = new CoverageDetailsEntity();
        entity.setRfqId(details.getRfqId());
        entity.setPolicyType(details.getPolicyType());
        entity.setFamilyDefination(details.getFamilyDefination());
        entity.setSumInsured(details.getSumInsured());
        entity.setFamilyDefication13(details.isFamilyDefication13());
        entity.setFamilyDefication15(details.isFamilyDefication15());
        entity.setFamilyDeficationParents(details.isFamilyDeficationParents());
        entity.setFamilyDefication13Amount(details.getFamilyDefication13Amount());
        entity.setFamilyDefication15Amount(details.getFamilyDefication15Amount());
        entity.setFamilyDeficationParentsAmount(details.getFamilyDeficationParentsAmount());
        entity.setClaimsMiscFilePath(details.getClaimsMiscFilePath());
        entity.setClaimsSummaryFilePath(details.getClaimsSummaryFilePath());
        entity.setCoveragesFilePath(details.getCoveragesFilePath());
        entity.setEmpDepDataFilePath(details.getEmpDepDataFilePath());
        entity.setMandateLetterFilePath(details.getMandateLetterFilePath());
        entity.setPolicyCopyFilePath(details.getPolicyCopyFilePath());
        entity.setTemplateFilePath(details.getTemplateFilePath());
        entity.setUploadedDocumentsPath(details.getUploadedDocumentsPath());
        entity.setEmpData(details.isEmpData());
        entity.setCreateDate(new Date());
        entity.setRecordStatus("ACTIVE");
        cdEBRepo.save(entity);
        return details.getRfqId();
    }

    @Override
    public CoverageDetailsEntity updateCoverageDetails(String rfqId, CoverageDetailsDto details) {
        CoverageDetailsEntity entity = null;
        Optional<CoverageDetailsEntity> findByRfqId = cdEBRepo.findByRfqId(rfqId);
        if (findByRfqId.isPresent()) {

            entity = findByRfqId.get();

            entity.setPolicyType(details.getPolicyType());
            entity.setFamilyDefination(details.getFamilyDefination());
            entity.setSumInsured(details.getSumInsured());
            entity.setFamilyDefication13(details.isFamilyDefication13());
            entity.setFamilyDefication15(details.isFamilyDefication15());
            entity.setFamilyDeficationParents(details.isFamilyDeficationParents());
            entity.setFamilyDefication13Amount(details.getFamilyDefication13Amount());
            entity.setFamilyDefication15Amount(details.getFamilyDefication15Amount());
            entity.setFamilyDeficationParentsAmount(details.getFamilyDeficationParentsAmount());

            entity.setClaimsMiscFilePath(details.getClaimsMiscFilePath());
            entity.setClaimsSummaryFilePath(details.getClaimsSummaryFilePath());
            entity.setCoveragesFilePath(details.getCoveragesFilePath());
            entity.setEmpDepDataFilePath(details.getEmpDepDataFilePath());
            entity.setMandateLetterFilePath(details.getMandateLetterFilePath());
            entity.setPolicyCopyFilePath(details.getPolicyCopyFilePath());
            entity.setTemplateFilePath(details.getTemplateFilePath());
            entity.setUploadedDocumentsPath(details.getUploadedDocumentsPath());

            deleteFile(entity.getClaimsMiscFilePath());
            deleteFile(entity.getClaimsSummaryFilePath());
            deleteFile(entity.getCoveragesFilePath());
            deleteFile(entity.getEmpDepDataFilePath());
            deleteFile(entity.getMandateLetterFilePath());
            deleteFile(entity.getPolicyCopyFilePath());
            deleteFile(entity.getTemplateFilePath());
            deleteFile(entity.getUploadedDocumentsPath());

            entity.setEmpData(details.isEmpData());
            entity.setFamilyDefication13(details.isFamilyDefication13());
            entity.setFamilyDefication15(details.isFamilyDefication15());
            entity.setFamilyDeficationParents(details.isFamilyDeficationParents());
            entity.setCreateDate(details.getCreateDate());
            entity.setRecordStatus("ACTIVE");
            entity.setUpdateDate(new Date());
            cdEBRepo.save(entity);
        }
        return entity;
    }

    private void deleteFileById(String entityId) {
        Optional<CoverageDetailsEntity> entityOptional = cdEBRepo.findById(Long.parseLong(entityId));

        if (entityOptional.isPresent()) {
            CoverageDetailsEntity entity = entityOptional.get();
            String ClaimsMisc = entity.getClaimsMiscFilePath();
            String ClaimsSummary = entity.getClaimsSummaryFilePath();
            String Coverages = entity.getCoveragesFilePath();
            String EmpDepData = entity.getEmpDepDataFilePath();
            String MandateLetter = entity.getMandateLetterFilePath();
            String PolicyCopy = entity.getPolicyCopyFilePath();
            String Template = entity.getTemplateFilePath();
            // Replace 'getFilePath()' with the actual method to retrieve the file path from
            // your entity
            deleteFile(ClaimsMisc);
            deleteFile(ClaimsSummary);
            deleteFile(Coverages);
            deleteFile(EmpDepData);
            deleteFile(MandateLetter);
            deleteFile(PolicyCopy);
            deleteFile(Template);

        } else {
            // Handle the case where the entity with the given ID is not found
            // e.g., throw an exception or log an error
            System.err.println("Entity with ID " + entityId + " not found.");
        }
    }

    private void deleteFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                Path path = Paths.get(filePath);
                Files.deleteIfExists(path);
                // Optionally, you can log the deletion if needed
                // logger.info("File deleted successfully: {}", filePath);
            } catch (Exception e) {
                // Handle exceptions appropriately (e.g., log or throw)
                // logger.error("Error deleting file: {}", filePath, e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<CoverageValidateFilenames> getCoverageValidateFilenames() {
        return filesRepo.findAll();
    }

    @Override
    public String addCoverageValidateFilenames(CoverageValidateFilenamesDto dto) {
        CoverageValidateFilenames fileNames = new CoverageValidateFilenames();
        log.info("dto From addCoverageValidateFileNames " + dto.getSno());
        if (dto.getSno() == null) {
            fileNames.setSno("SNO");
        } else {
            fileNames.setSno(dto.getSno());
        }
        if (dto.getEmployeeId() == null) {
            fileNames.setEMPLOYEEID("EMPLOYEEID");
        } else {
            fileNames.setEMPLOYEEID(dto.getEmployeeId());
        }
        if (dto.getEmployeeName() == null) {
            fileNames.setEMPLOYEENAME("EMPLOYEENAME");
        } else {
            fileNames.setEMPLOYEENAME(dto.getEmployeeName());
        }
        if (dto.getRelationship() == null) {
            fileNames.setRELATIONSHIP("RELATIONSHIP");
        } else {
            fileNames.setRELATIONSHIP(dto.getRelationship());
        }
        if (dto.getGender() == null) {
            fileNames.setGENDER("GENDER");
        } else {
            fileNames.setGENDER(dto.getGender());
        }
        if (dto.getAge() == null) {
            fileNames.setAGE("AGE");
        } else {
            fileNames.setAGE(dto.getAge());
        }
        if (dto.getDateOfBirth() == null) {
            fileNames.setDATEOFBIRTH("DATEOFBIRTH");
        } else {
            fileNames.setDATEOFBIRTH(dto.getDateOfBirth());
        }

        if (dto.getSumInsured() == null) {
            fileNames.setSUMINSURED("SUMINSURED");
        } else {
            fileNames.setSUMINSURED(dto.getSumInsured());
        }

        fileNames.setCreateDate(new Date());
        fileNames.setRecordStatus("Active");
        filesRepo.save(fileNames);
        return "FileNames Created Successfully !!";
    }

    public byte[] getEmployeeData() {
        List<EmployeeDepedentDetailsEntity> findAll = empDepRepo.findAll();
        EmployeeDataReportPdfGenerator pdf = new EmployeeDataReportPdfGenerator();
        byte[] generateEmployeeDataReport = pdf.generateEmployeeDataReport(findAll);
        return generateEmployeeDataReport;
    }

    @Override
    public byte[] getIrdaData() throws IOException {
        IrdaPdfGenerator irda = new IrdaPdfGenerator();
        byte[] generateEmployeeDataReport = irda.generateEmployeeDataReport();
        return generateEmployeeDataReport;
    }

    @Override
    public String saveEmployeesFromExcel(CoverageUploadDto coverageUploadDto) {
        CoverageUploadEntity uploadEntity = new CoverageUploadEntity();
        List<EmployeeDepedentDetailsEntity> employees = null;
        log.info("coverageUploadDto  From saveEmployeeExcel " + coverageUploadDto.getFileType());
        if (coverageUploadDto.getFileType().equals("EmpData")) {
            employees = new ExcelUtils().readEmployeesFromExcel(coverageUploadDto.getFile(),
                    coverageUploadDto.getFileType(), coverageUploadDto.getRfqId());
            log.info("employees from saveEmployeesFromExcel " + employees);
            employeeRepository.saveAll(employees);
        } else if (coverageUploadDto.getFileType().equals("MandateLetter")) {
            log.info("------  MandateLetter ----------");
            File folder = new File(mainpath);
            File mandateLetterFileDest = new File(folder.getAbsolutePath(),
                    "MandateLetterFile" + RandomStringUtils.random(10, true, false));
            if (!coverageUploadDto.getFile().isEmpty() && !mandateLetterFileDest.exists()) {
                log.info("------  file Upload ----------");
                try {
                    coverageUploadDto.getFile().transferTo(mandateLetterFileDest);
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("------  file not Upload ----------");
                LOG.info(coverageUploadDto.getFile().getOriginalFilename() + " already exists !!");
            }
        } else if (coverageUploadDto.getFileType().equals("CoveragesSought")) {
            log.info("------  CoveragesSought ----------");
            File folder = new File(mainpath);
            File CoveragesSoughtDest = new File(folder.getAbsolutePath(),
                    "CoveragesSought" + RandomStringUtils.random(10, true, false));
            if (!coverageUploadDto.getFile().isEmpty() && !CoveragesSoughtDest.exists()) {
                log.info("------  file Upload ----------");
                try {
                    coverageUploadDto.getFile().transferTo(CoveragesSoughtDest);
                    // Create a FileInputStream to read the file
                    FileInputStream fileInputStream = new FileInputStream(new File(folder.getAbsolutePath()));

                    // Create a ByteArrayOutputStream to store the bytes
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    // Read data from the file and write it to the ByteArrayOutputStream
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    // Close the streams
                    fileInputStream.close();
                    byteArrayOutputStream.close();

                    // Get the byte array
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    // Now the byteArray contains the contents of the file as bytes
                    log.info("File converted to byte array with length: " + byteArray.length);
                    uploadEntity.setCoveragesSought(byteArray);
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("------  file not Upload  ----------");
                LOG.info(coverageUploadDto.getFile().getOriginalFilename() + " already exists !!");
            }
        }
        return null;
    }

    @Override
    public List<EmployeeDepedentDetailsEntity> getAllEmployeeDepedentDataByRfqId(String rfqId) {
        if (rfqId != null) {
            List<EmployeeDepedentDetailsEntity> findAll = employeeRepository.findAll();
            return findAll.stream().filter(i -> i.getRfqId().equals(rfqId)).toList();
        }
        return new ArrayList<>();
    }

    int snoColumnIndex = -1;
    int employeeIdColumnIndex = -1;
    int employeeNameColumnIndex = -1;
    int relationshipColumnIndex = -1;
    int genderColumnIndex = -1;
    int ageColumnIndex = -1;
    int dateOfBirthColumnIndex = -1;
    int sumInsuredColumnIndex = -1;
    int memberCodeColumnIndex = -1;
    int validFromColumnIndex = -1;
    int validTillColumnIndex = -1;
    int hospitalStateColumnIndex = -1;
    int claimsStatusColumnIndex = -1;

    @Override
    public EmpDepdentValidationDto validateUploadedFileNames(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        EmpDepdentValidationDto empDepdentValidationDto = new EmpDepdentValidationDto();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = null;
            sheet = workbook.getSheetAt(0);

            List<EmpDependentHeaders> empDepHeaders = empDepHeaderRepo.findAll();

            Row headerRow = null;
            for (Row row : sheet) {
                for (Cell cell : row) {
                    for (EmpDependentHeaders headers : empDepHeaders) {
                        if (cell.getCellType() == CellType.STRING
                                && headers.getHeaderName().equalsIgnoreCase(cell.getStringCellValue())) {
                            headerRow = row;
                            break;
                        }
                    }
                    if (headerRow != null) {
                        break;
                    }
                }
                if (headerRow != null) {
                    break;
                }
            }

            if (headerRow != null) {
                Iterator<Cell> cellIterator = headerRow.cellIterator();
                int columnIndex = 0;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String columnName = cell.getStringCellValue().trim(); // Convert to lowercase for
                    // case-insensitive matching

                    for (EmpDependentHeaders headers : empDepHeaders) {
                        List<EmpDependentHeaderMapping> headerMappings = headers.getHeaders();
                        for (EmpDependentHeaderMapping headerMapping : headerMappings) {
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.EMPLOYEEID)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                employeeIdColumnIndex = columnIndex;
                                empDepdentValidationDto.setEmployeeId(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.EMPLOYEENAME)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                employeeNameColumnIndex = columnIndex;
                                empDepdentValidationDto.setEmployeeName(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.RELATIONSHIP)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                relationshipColumnIndex = columnIndex;
                                empDepdentValidationDto.setRelationship(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.GENDER)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                genderColumnIndex = columnIndex;
                                empDepdentValidationDto.setGender(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.AGE)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                ageColumnIndex = columnIndex;
                                empDepdentValidationDto.setAge(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.DATEOFBIRTH)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                dateOfBirthColumnIndex = columnIndex;
                                empDepdentValidationDto.setDateOfBirth(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.SUMINSURED)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                sumInsuredColumnIndex = columnIndex;
                                empDepdentValidationDto.setSumInsured(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.MEMBERCODE)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                memberCodeColumnIndex = columnIndex;
                                empDepdentValidationDto.setSumInsured(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.VALIDFROM)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                validFromColumnIndex = columnIndex;
                                empDepdentValidationDto.setSumInsured(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.VALIDTILL)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                validTillColumnIndex = columnIndex;
                                empDepdentValidationDto.setSumInsured(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.HOSPITALSTATE)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                hospitalStateColumnIndex = columnIndex;
                                empDepdentValidationDto.setSumInsured(true);
                            }
                            if (headers.getHeaderName().equals(EmpDependentHeaderConstants.CLAIMSTATUS)
                                    && headerMapping.getAliasName().equals(columnName)) {
                                claimsStatusColumnIndex = columnIndex;
                                empDepdentValidationDto.setSumInsured(true);
                            }

                        }
                    }

                    columnIndex++;
                }
            }

            return empDepdentValidationDto;

        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public Cell getMergedCell(Sheet sheet, int rowIdx, int colIdx) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            if (mergedRegion.isInRange(rowIdx, colIdx)) {
                int firstRow = mergedRegion.getFirstRow();
                int firstCol = mergedRegion.getFirstColumn();
                Row mergedRow = sheet.getRow(firstRow);
                if (mergedRow != null) {
                    return mergedRow.getCell(firstCol);
                }
            }
        }
        return null;
    }

    @Override
    public List<CoverageDetailsChildValidateValuesDto> validateFileValues(MultipartFile file) {

        String employeeId;
        String TempEmpID = null;
        String sumInsured;
        String TempSumIssured = null;
        String remarks = null;

        List<CoverageDetailsChildValidateValuesDto> validateValues = new ArrayList<>();
        List<EmpDependentHeaders> empDepHeaders = empDepHeaderRepo.findAll();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = null;
            for (Row row : sheet) {
                for (Cell cell : row) {
                    for (EmpDependentHeaders headers : empDepHeaders) {
                        if (cell.getCellType() == CellType.STRING
                                && headers.getHeaderName().equalsIgnoreCase(cell.getStringCellValue())) {
                            headerRow = row;
                            break;
                        }
                    }
                    if (headerRow != null) {
                        break;
                    }
                }
                if (headerRow != null) {
                    break;
                }
            }

            LOG.info("employeeIdColumnIndex :: {} " , employeeIdColumnIndex);
            LOG.info("employeeNameColumnIndex :: {}" , employeeNameColumnIndex);
            LOG.info("relationshipColumnIndex :: {}" ,relationshipColumnIndex);
            LOG.info("genderColumnIndex ::{} " , genderColumnIndex);
            LOG.info("ageColumnIndex :: {}" ,ageColumnIndex);
            LOG.info("dateOfBirthColumnIndex ::{} " , dateOfBirthColumnIndex);

            if (headerRow != null) {
                for (Row dataRow : sheet) {
                    int headerNum = headerRow.getRowNum();
                    int rowNum = dataRow.getRowNum();
                    if (rowNum > headerNum) {
                        CoverageDetailsChildValidateValuesDto validateDto = new CoverageDetailsChildValidateValuesDto();
                        if (dataRow.getCell(employeeNameColumnIndex).getCellType() == CellType.BLANK
                                && dataRow.getCell(relationshipColumnIndex).getCellType() == CellType.BLANK
                                && dataRow.getCell(genderColumnIndex).getCellType() == CellType.BLANK
                                && dataRow.getCell(ageColumnIndex).getCellType() == CellType.BLANK
                                && dataRow.getCell(dateOfBirthColumnIndex).getCellType() == CellType.BLANK) {
                            // Handle the case when any of the required columns are missing
                            // You can choose to skip the row or take appropriate action based on your
                            // requirements
                            continue;
                        }

                        for (int colIdx = dataRow.getFirstCellNum(); colIdx < dataRow.getLastCellNum(); colIdx++) {

                            int rowIdx = rowNum;
                            boolean isMergedCell = false;
                            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                                CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
                                if (mergedRegion.isInRange(rowIdx, colIdx)) {
                                    isMergedCell = true;
                                    break;
                                }
                            }
                            // Merged cell Logic
                            Row currentRow = sheet.getRow(rowNum);
                            if (currentRow != null) {

                                if (colIdx == employeeIdColumnIndex) {

                                    if (isMergedCell) {
                                        Cell employeeIdCell = getMergedCell(sheet, rowIdx, colIdx);

                                        if (dataRow.getCell(employeeIdColumnIndex) != null) {

                                            if (employeeIdCell.getCellType() == CellType.STRING) {
                                                validateDto.setEmployeeIdValidationStatus(true);
                                                employeeId = dataRow.getCell(employeeIdColumnIndex)
                                                        .getStringCellValue();
                                                if (employeeId.equals("") || employeeId.equals(" ")) {
                                                    employeeId = TempEmpID;
                                                }
                                                TempEmpID = employeeId;
                                                validateDto.setEmployeeIdValue(employeeId);
                                            } else if (employeeIdCell.getCellType() == CellType.NUMERIC) {
                                                validateDto.setEmployeeIdValidationStatus(true);
                                                employeeId = String.valueOf(
                                                        dataRow.getCell(employeeIdColumnIndex).getNumericCellValue());
                                                if (employeeId.equals("0.0")) {
                                                    employeeId = TempEmpID;
                                                }
                                                TempEmpID = employeeId;
                                                validateDto.setEmployeeIdValue(employeeId);
                                            } else if (employeeIdCell.getCellType() == CellType.BLANK) {
                                                employeeId = TempEmpID;
                                                validateDto.setEmployeeIdValue(employeeId);
                                                validateDto.setEmployeeIdValidationStatus(true);
                                                validateDto.setEmployeeIdErrorMessage(ExcelValidation.BLANK.getValue());
                                            } else if (employeeIdCell.getCellType() == CellType._NONE) {
                                                employeeId = TempEmpID;
                                                validateDto.setEmployeeIdValue(employeeId);
                                                validateDto.setEmployeeIdValidationStatus(true);
                                                validateDto.setEmployeeIdErrorMessage(ExcelValidation.NONE.getValue());
                                            } else if (employeeIdCell.getCellType() == CellType.BOOLEAN) {
                                                employeeId = String.valueOf(
                                                        dataRow.getCell(employeeIdColumnIndex).getBooleanCellValue());
                                                validateDto.setEmployeeIdValue(employeeId);
                                                validateDto.setEmployeeIdValidationStatus(false);
                                                TempEmpID = employeeId;
                                                validateDto
                                                        .setEmployeeIdErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                            } else if (employeeIdCell.getCellType() == CellType.ERROR) {
                                                employeeId = String.valueOf(
                                                        dataRow.getCell(employeeIdColumnIndex).getErrorCellValue());
                                                validateDto.setEmployeeIdValue(employeeId);
                                                validateDto.setEmployeeIdValidationStatus(false);
                                                validateDto.setEmployeeIdErrorMessage(ExcelValidation.ERROR.getValue());
                                                TempEmpID = employeeId;
                                            } else if (employeeIdCell.getCellType() == CellType.FORMULA) {
                                                switch (employeeIdCell.getCachedFormulaResultType()) {
                                                    case NUMERIC: {
                                                        validateDto.setEmployeeIdValidationStatus(true);
                                                        employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex)
                                                                .getNumericCellValue());
                                                        if (employeeId.equals("0.0")) {
                                                            employeeId = TempEmpID;
                                                        }
                                                        TempEmpID = employeeId;
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        break;
                                                    }
                                                    case STRING: {
                                                        validateDto.setEmployeeIdValidationStatus(true);
                                                        employeeId = dataRow.getCell(employeeIdColumnIndex)
                                                                .getStringCellValue();
                                                        if (employeeId.equals("") || employeeId.equals(" ")) {
                                                            employeeId = TempEmpID;
                                                        }
                                                        TempEmpID = employeeId;
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        break;
                                                    }
                                                    case BLANK: {
                                                        employeeId = TempEmpID;
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        validateDto.setEmployeeIdValidationStatus(true);
                                                        validateDto.setEmployeeIdErrorMessage(
                                                                ExcelValidation.BLANK.getValue());
                                                        break;
                                                    }
                                                    case _NONE: {
                                                        employeeId = TempEmpID;
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        validateDto.setEmployeeIdValidationStatus(true);
                                                        validateDto
                                                                .setEmployeeIdErrorMessage(ExcelValidation.NONE.getValue());
                                                        break;
                                                    }
                                                    case BOOLEAN: {
                                                        employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex)
                                                                .getBooleanCellValue());
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        validateDto.setEmployeeIdValidationStatus(false);
                                                        TempEmpID = employeeId;
                                                        validateDto.setEmployeeIdErrorMessage(
                                                                ExcelValidation.BOOLEAN.getValue());
                                                        break;
                                                    }
                                                    case ERROR: {
                                                        employeeId = String.valueOf(
                                                                dataRow.getCell(employeeIdColumnIndex).getErrorCellValue());
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        validateDto.setEmployeeIdValidationStatus(false);
                                                        validateDto.setEmployeeIdErrorMessage(
                                                                ExcelValidation.ERROR.getValue());
                                                        TempEmpID = employeeId;
                                                        break;
                                                    }
                                                    default:
                                                        throw new IllegalArgumentException("Unexpected value: "
                                                                + employeeIdCell.getCachedFormulaResultType());
                                                }
                                            }
                                        }

                                    } else {

                                        if (dataRow.getCell(employeeIdColumnIndex) != null) {
                                            Cell employeeIdCell = dataRow.getCell(employeeIdColumnIndex);

                                            if (employeeIdCell.getCellType() == CellType.STRING) {
                                                validateDto.setEmployeeIdValidationStatus(true);
                                                employeeId = dataRow.getCell(employeeIdColumnIndex)
                                                        .getStringCellValue();
                                                validateDto.setEmployeeIdValue(employeeId);
                                                TempEmpID = employeeId;
                                            } else if (employeeIdCell.getCellType() == CellType.NUMERIC) {
                                                validateDto.setEmployeeIdValidationStatus(true);
                                                double numericCellValue = dataRow.getCell(employeeIdColumnIndex)
                                                        .getNumericCellValue();
                                                // Formatting without scientific notation
                                                DecimalFormat df = new DecimalFormat("#");
                                                employeeId = df.format(numericCellValue);
//                                                employeeId = String.valueOf(
//                                                        dataRow.getCell(employeeIdColumnIndex).getNumericCellValue());
                                                validateDto.setEmployeeIdValue(employeeId);
                                                TempEmpID = employeeId;
                                            } else if (employeeIdCell.getCellType() == CellType.BLANK) {
                                                employeeId = TempEmpID;
                                                validateDto.setEmployeeIdValue(employeeId);
                                                validateDto.setEmployeeIdValidationStatus(true);
                                                validateDto.setEmployeeIdErrorMessage(ExcelValidation.BLANK.getValue());
                                            } else if (employeeIdCell.getCellType() == CellType._NONE) {
                                                employeeId = TempEmpID;
                                                validateDto.setEmployeeIdValue(employeeId);
                                                validateDto.setEmployeeIdValidationStatus(true);
                                                validateDto.setEmployeeIdErrorMessage(ExcelValidation.NONE.getValue());
                                            } else if (employeeIdCell.getCellType() == CellType.BOOLEAN) {
                                                employeeId = String.valueOf(
                                                        dataRow.getCell(employeeIdColumnIndex).getBooleanCellValue());
                                                validateDto.setEmployeeIdValue(employeeId);
                                                validateDto.setEmployeeIdValidationStatus(false);
                                                TempEmpID = employeeId;
                                                validateDto
                                                        .setEmployeeIdErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                            } else if (employeeIdCell.getCellType() == CellType.ERROR) {
                                                employeeId = String.valueOf(
                                                        dataRow.getCell(employeeIdColumnIndex).getErrorCellValue());
                                                validateDto.setEmployeeIdValue(employeeId);
                                                validateDto.setEmployeeIdValidationStatus(false);
                                                validateDto.setEmployeeIdErrorMessage(ExcelValidation.ERROR.getValue());
                                                TempEmpID = employeeId;
                                            } else if (employeeIdCell.getCellType() == CellType.FORMULA) {
                                                switch (employeeIdCell.getCachedFormulaResultType()) {
                                                    case NUMERIC: {
                                                        validateDto.setEmployeeIdValidationStatus(true);
                                                        employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex)
                                                                .getNumericCellValue());
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        TempEmpID = employeeId;
                                                        break;
                                                    }
                                                    case STRING: {
                                                        validateDto.setEmployeeIdValidationStatus(true);
                                                        employeeId = dataRow.getCell(employeeIdColumnIndex)
                                                                .getStringCellValue();
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        TempEmpID = employeeId;
                                                        break;
                                                    }
                                                    case BLANK: {
                                                        employeeId = TempEmpID;
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        validateDto.setEmployeeIdValidationStatus(true);
                                                        validateDto.setEmployeeIdErrorMessage(
                                                                ExcelValidation.BLANK.getValue());
                                                        break;
                                                    }
                                                    case _NONE: {
                                                        employeeId = TempEmpID;
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        validateDto.setEmployeeIdValidationStatus(true);
                                                        validateDto
                                                                .setEmployeeIdErrorMessage(ExcelValidation.NONE.getValue());
                                                        break;
                                                    }
                                                    case BOOLEAN: {
                                                        employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex)
                                                                .getBooleanCellValue());
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        validateDto.setEmployeeIdValidationStatus(false);
                                                        TempEmpID = employeeId;
                                                        validateDto.setEmployeeIdErrorMessage(
                                                                ExcelValidation.BOOLEAN.getValue());
                                                        break;
                                                    }
                                                    case ERROR: {
                                                        employeeId = String.valueOf(
                                                                dataRow.getCell(employeeIdColumnIndex).getErrorCellValue());
                                                        validateDto.setEmployeeIdValue(employeeId);
                                                        validateDto.setEmployeeIdValidationStatus(false);
                                                        validateDto.setEmployeeIdErrorMessage(
                                                                ExcelValidation.ERROR.getValue());
                                                        TempEmpID = employeeId;
                                                        break;
                                                    }
                                                    default:
                                                        throw new IllegalArgumentException("Unexpected value: "
                                                                + employeeIdCell.getCachedFormulaResultType());
                                                }
                                            }
                                        }

                                    }

                                }

                                if (colIdx == employeeNameColumnIndex) {
                                    if (dataRow.getCell(employeeNameColumnIndex) != null) {
                                        String employeeName;
                                        Cell employeeNameCell = dataRow.getCell(employeeNameColumnIndex);
                                        if (employeeNameCell.getCellType() == CellType.STRING) {
                                            validateDto.setEmployeeNameValidationStatus(true);
                                            employeeName = dataRow.getCell(employeeNameColumnIndex)
                                                    .getStringCellValue();
                                            validateDto.setEmployeeNameValue(employeeName);
                                        } else if (employeeNameCell.getCellType() == CellType.NUMERIC) {
                                            validateDto.setEmployeeNameValidationStatus(false);
                                            employeeName = String.valueOf(
                                                    dataRow.getCell(employeeNameColumnIndex).getNumericCellValue());
                                            validateDto.setEmployeeNameValue(employeeName);
                                            validateDto.setEmployeeNameErrorMessage(employeeName);
                                            remarks = "Invalid Employee Name";
                                        } else if (employeeNameCell.getCellType() == CellType.BLANK) {
                                            validateDto.setEmployeeNameValue("");
                                            validateDto.setEmployeeNameValidationStatus(false);
                                            validateDto.setEmployeeNameErrorMessage(ExcelValidation.BLANK.getValue());
                                            remarks = "Blank Employee Name";
                                        } else if (employeeNameCell.getCellType() == CellType._NONE) {
                                            validateDto.setEmployeeNameValue("");
                                            validateDto.setEmployeeNameValidationStatus(false);
                                            validateDto.setEmployeeNameErrorMessage(ExcelValidation.NONE.getValue());
                                            remarks = "Blank Employee Name";
                                        } else if (employeeNameCell.getCellType() == CellType.BOOLEAN) {
                                            employeeName = String.valueOf(
                                                    dataRow.getCell(employeeNameColumnIndex).getBooleanCellValue());
                                            validateDto.setEmployeeNameValue(employeeName);
                                            validateDto.setEmployeeNameValidationStatus(false);
                                            validateDto.setEmployeeNameErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                            remarks = "Boolean Employee Name";
                                        } else if (employeeNameCell.getCellType() == CellType.ERROR) {
                                            employeeName = String.valueOf(
                                                    dataRow.getCell(employeeNameColumnIndex).getErrorCellValue());
                                            validateDto.setEmployeeIdValue(employeeName);
                                            validateDto.setEmployeeNameValidationStatus(false);
                                            validateDto.setEmployeeNameErrorMessage(ExcelValidation.ERROR.getValue());
                                            remarks = "Error Employee Name";
                                        } else if (employeeNameCell.getCellType() == CellType.FORMULA) {
                                            switch (employeeNameCell.getCachedFormulaResultType()) {
                                                case NUMERIC: {
                                                    validateDto.setEmployeeNameValidationStatus(false);
                                                    employeeName = String.valueOf(
                                                            dataRow.getCell(employeeNameColumnIndex).getNumericCellValue());
                                                    validateDto.setEmployeeNameValue(employeeName);
                                                    validateDto.setEmployeeNameErrorMessage(employeeName);
                                                    remarks = "Invalid Employee Name";
                                                    break;
                                                }
                                                case STRING: {
                                                    validateDto.setEmployeeNameValidationStatus(true);
                                                    employeeName = dataRow.getCell(employeeNameColumnIndex)
                                                            .getStringCellValue();
                                                    validateDto.setEmployeeNameValue(employeeName);
                                                    break;
                                                }
                                                case BLANK: {
                                                    validateDto.setEmployeeNameValue("");
                                                    validateDto.setEmployeeNameValidationStatus(false);
                                                    validateDto
                                                            .setEmployeeNameErrorMessage(ExcelValidation.BLANK.getValue());
                                                    validateDto
                                                            .setEmployeeNameErrorMessage(ExcelValidation.BLANK.getValue());
                                                    remarks = "Blank Employee Name";
                                                    break;
                                                }
                                                case _NONE: {
                                                    validateDto.setEmployeeNameValue("");
                                                    validateDto.setEmployeeNameValidationStatus(false);
                                                    validateDto
                                                            .setEmployeeNameErrorMessage(ExcelValidation.NONE.getValue());
                                                    validateDto
                                                            .setEmployeeNameErrorMessage(ExcelValidation.NONE.getValue());
                                                    remarks = "Blank Employee Name";
                                                    break;
                                                }
                                                case BOOLEAN: {
                                                    employeeName = String.valueOf(
                                                            dataRow.getCell(employeeNameColumnIndex).getBooleanCellValue());
                                                    validateDto.setEmployeeNameValue(employeeName);
                                                    validateDto.setEmployeeNameValidationStatus(false);
                                                    validateDto.setEmployeeNameErrorMessage(
                                                            ExcelValidation.BOOLEAN.getValue());
                                                    validateDto.setEmployeeNameErrorMessage(
                                                            ExcelValidation.BOOLEAN.getValue());
                                                    remarks = "Boolean Employee Name";
                                                    break;
                                                }
                                                case ERROR: {
                                                    employeeName = String.valueOf(
                                                            dataRow.getCell(employeeNameColumnIndex).getErrorCellValue());
                                                    validateDto.setEmployeeIdValue(employeeName);
                                                    validateDto.setEmployeeNameValidationStatus(false);
                                                    validateDto
                                                            .setEmployeeNameErrorMessage(ExcelValidation.ERROR.getValue());
                                                    validateDto
                                                            .setEmployeeNameErrorMessage(ExcelValidation.ERROR.getValue());
                                                    remarks = "Error Employee Name";
                                                    break;
                                                }
                                                default:
                                                    throw new IllegalArgumentException("Unexpected value: "
                                                            + employeeNameCell.getCachedFormulaResultType());
                                            }
                                        }
                                    }
                                }

                                if (colIdx == relationshipColumnIndex) {
                                    if (dataRow.getCell(relationshipColumnIndex) != null) {
                                        String relationship;

                                        Cell relationshipCell = dataRow.getCell(relationshipColumnIndex);
                                        if (relationshipCell.getCellType() == CellType.STRING) {
                                            validateDto.setRelationshipValidationStatus(true);
                                            relationship = dataRow.getCell(relationshipColumnIndex)
                                                    .getStringCellValue();
                                            validateDto.setRelationshipValue(relationship);
                                        } else if (relationshipCell.getCellType() == CellType.NUMERIC) {
                                            validateDto.setRelationshipValidationStatus(false);
                                            relationship = String.valueOf(
                                                    dataRow.getCell(relationshipColumnIndex).getNumericCellValue());
                                            validateDto.setRelationshipValue(relationship);
                                            validateDto.setRelationshipErrorMessage(ExcelValidation.NONE.getValue());
                                            remarks = "Invalid Relation ";
                                        } else if (relationshipCell.getCellType() == CellType.BLANK) {
                                            validateDto.setRelationshipValue("");
                                            validateDto.setRelationshipValidationStatus(false);
                                            validateDto.setRelationshipErrorMessage(ExcelValidation.BLANK.getValue());
                                            remarks = ExcelValidation.BLANK.getValue() + " relation,";
                                        } else if (relationshipCell.getCellType() == CellType._NONE) {
                                            validateDto.setRelationshipValue("");
                                            validateDto.setRelationshipValidationStatus(false);
                                            validateDto.setRelationshipErrorMessage(ExcelValidation.NONE.getValue());
                                            remarks = ExcelValidation.NONE.getValue() + " relation,";
                                        } else if (relationshipCell.getCellType() == CellType.BOOLEAN) {
                                            relationship = String.valueOf(
                                                    dataRow.getCell(relationshipColumnIndex).getBooleanCellValue());
                                            validateDto.setRelationshipValue(relationship);
                                            validateDto.setRelationshipValidationStatus(false);
                                            validateDto.setRelationshipErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                            remarks = ExcelValidation.BOOLEAN.getValue() + " relation,";
                                        } else if (relationshipCell.getCellType() == CellType.ERROR) {
                                            relationship = String.valueOf(
                                                    dataRow.getCell(relationshipColumnIndex).getErrorCellValue());
                                            validateDto.setEmployeeIdValue(relationship);
                                            validateDto.setRelationshipValidationStatus(false);
                                            validateDto.setRelationshipErrorMessage(ExcelValidation.ERROR.getValue());
                                            remarks = ExcelValidation.ERROR.getValue() + " relation,";
                                        } else if (relationshipCell.getCellType() == CellType.FORMULA) {
                                            switch (relationshipCell.getCachedFormulaResultType()) {
                                                case NUMERIC: {
                                                    validateDto.setRelationshipValidationStatus(false);
                                                    relationship = String.valueOf(
                                                            dataRow.getCell(relationshipColumnIndex).getNumericCellValue());
                                                    validateDto.setRelationshipValue(relationship);
                                                    validateDto
                                                            .setRelationshipErrorMessage(ExcelValidation.NONE.getValue());
                                                    remarks = "Invalid Relation ";
                                                    break;
                                                }
                                                case STRING: {
                                                    validateDto.setRelationshipValidationStatus(true);
                                                    relationship = dataRow.getCell(relationshipColumnIndex)
                                                            .getStringCellValue();
                                                    validateDto.setRelationshipValue(relationship);
                                                    break;
                                                }
                                                case BLANK: {
                                                    validateDto.setRelationshipValue("");
                                                    validateDto.setRelationshipValidationStatus(false);
                                                    validateDto
                                                            .setRelationshipErrorMessage(ExcelValidation.BLANK.getValue());
                                                    remarks = ExcelValidation.BLANK.getValue() + " relation,";
                                                    break;
                                                }
                                                case _NONE: {
                                                    validateDto.setRelationshipValue("");
                                                    validateDto.setRelationshipValidationStatus(false);
                                                    validateDto
                                                            .setRelationshipErrorMessage(ExcelValidation.NONE.getValue());
                                                    remarks = ExcelValidation.NONE.getValue() + " relation,";
                                                    break;
                                                }
                                                case BOOLEAN: {
                                                    relationship = String.valueOf(
                                                            dataRow.getCell(relationshipColumnIndex).getBooleanCellValue());
                                                    validateDto.setRelationshipValue(relationship);
                                                    validateDto.setRelationshipValidationStatus(false);
                                                    validateDto.setRelationshipErrorMessage(
                                                            ExcelValidation.BOOLEAN.getValue());
                                                    remarks = ExcelValidation.BOOLEAN.getValue() + " relation,";
                                                    break;
                                                }
                                                case ERROR: {
                                                    relationship = String.valueOf(
                                                            dataRow.getCell(relationshipColumnIndex).getErrorCellValue());
                                                    validateDto.setEmployeeIdValue(relationship);
                                                    validateDto.setRelationshipValidationStatus(false);
                                                    validateDto
                                                            .setRelationshipErrorMessage(ExcelValidation.ERROR.getValue());
                                                    remarks = ExcelValidation.ERROR.getValue() + " relation,";
                                                    break;
                                                }
                                                default:
                                                    throw new IllegalArgumentException("Unexpected value: "
                                                            + relationshipCell.getCachedFormulaResultType());
                                            }
                                        }
                                    }
                                }
                                if (colIdx == genderColumnIndex) {
                                    if (dataRow.getCell(genderColumnIndex) != null) {
                                        String gender;
                                        String[] genderArr = {"M", "F", "Male", "Female", "Trans", "TransGender"};
                                        Cell genderCell = dataRow.getCell(genderColumnIndex);
                                        if (genderCell.getCellType() == CellType.STRING) {
                                            gender = dataRow.getCell(genderColumnIndex).getStringCellValue().trim()
                                                    .replaceAll("\\p{C}", "").replaceAll("\\s", "")
                                                    .replaceAll("[^\\p{Print}]", "");
                                            if (Arrays.asList(genderArr).stream().map(s -> s.toLowerCase()).toList()
                                                    .contains(gender.toLowerCase())) {
                                                validateDto.setGenderValidationStatus(true);
                                                validateDto.setGenderValue(gender);
                                            } else {
                                                validateDto.setGenderValue(String.valueOf(gender));
                                                validateDto.setGenderValidationStatus(false);
                                                validateDto.setGenderErrorMessage(ExcelValidation.OTHER.getValue());
                                                remarks = ExcelValidation.OTHER.getValue() + " gender";
                                            }
                                        } else if (genderCell.getCellType() == CellType.NUMERIC) {
                                            gender = String
                                                    .valueOf(dataRow.getCell(genderColumnIndex).getNumericCellValue());
                                            validateDto.setGenderValue(String.valueOf(gender));
                                            validateDto.setGenderValidationStatus(false);
                                            validateDto.setGenderErrorMessage("Gender should not be in number format");
                                            remarks = ExcelValidation.OTHER.getValue() + " gender";
                                        } else if (genderCell.getCellType() == CellType.BLANK) {
                                            validateDto.setGenderValue("");
                                            validateDto.setGenderValidationStatus(false);
                                            validateDto.setGenderErrorMessage(ExcelValidation.BLANK.getValue());
                                            remarks = ExcelValidation.BLANK.getValue() + " gender";
                                        } else if (genderCell.getCellType() == CellType._NONE) {
                                            validateDto.setGenderValue("");
                                            validateDto.setGenderValidationStatus(false);
                                            validateDto.setGenderErrorMessage(ExcelValidation.NONE.getValue());
                                            remarks = ExcelValidation.NONE.getValue() + " gender";
                                        } else if (genderCell.getCellType() == CellType.BOOLEAN) {
                                            gender = String
                                                    .valueOf(dataRow.getCell(genderColumnIndex).getBooleanCellValue());
                                            validateDto.setGenderValue(gender);
                                            validateDto.setGenderValidationStatus(false);
                                            validateDto.setGenderErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                            remarks = ExcelValidation.BOOLEAN.getValue() + " gender";
                                        } else if (genderCell.getCellType() == CellType.ERROR) {
                                            gender = String
                                                    .valueOf(dataRow.getCell(genderColumnIndex).getErrorCellValue());
                                            validateDto.setGenderValue(gender);
                                            validateDto.setGenderValidationStatus(false);
                                            validateDto.setGenderErrorMessage(ExcelValidation.ERROR.getValue());
                                            remarks = ExcelValidation.ERROR.getValue() + " gender";
                                        } else if (genderCell.getCellType() == CellType.FORMULA) {
                                            switch (genderCell.getCachedFormulaResultType()) {
                                                case NUMERIC: {
                                                    gender = String.valueOf(
                                                            dataRow.getCell(genderColumnIndex).getNumericCellValue());
                                                    validateDto.setGenderValue(String.valueOf(gender));
                                                    validateDto.setGenderValidationStatus(false);
                                                    validateDto
                                                            .setGenderErrorMessage("Gender should not be in number format");
                                                    remarks = ExcelValidation.OTHER.getValue() + " gender";
                                                    break;
                                                }
                                                case STRING: {
                                                    gender = dataRow.getCell(genderColumnIndex).getStringCellValue().trim()
                                                            .replaceAll("\\p{C}", "").replaceAll("\\s", "")
                                                            .replaceAll("[^\\p{Print}]", "");
                                                    if (Arrays.asList(genderArr).stream().map(s -> s.toLowerCase()).toList()
                                                            .contains(gender.toLowerCase())) {
                                                        validateDto.setGenderValidationStatus(true);
                                                        validateDto.setGenderValue(gender);
                                                    } else {
                                                        gender = dataRow.getCell(genderColumnIndex).getStringCellValue();
                                                        validateDto.setGenderValue(String.valueOf(gender));
                                                        validateDto.setGenderValidationStatus(false);
                                                        validateDto.setGenderErrorMessage(ExcelValidation.OTHER.getValue());
                                                        remarks = ExcelValidation.OTHER.getValue() + " gender";
                                                    }
                                                    break;
                                                }
                                                case BLANK: {
                                                    validateDto.setGenderValue("");
                                                    validateDto.setGenderValidationStatus(false);
                                                    validateDto.setGenderErrorMessage(ExcelValidation.BLANK.getValue());
                                                    remarks = ExcelValidation.BLANK.getValue() + " gender";
                                                    break;
                                                }
                                                case _NONE: {
                                                    validateDto.setGenderValue("");
                                                    validateDto.setGenderValidationStatus(false);
                                                    validateDto.setGenderErrorMessage(ExcelValidation.NONE.getValue());
                                                    remarks = ExcelValidation.NONE.getValue() + " gender";
                                                    break;
                                                }
                                                case BOOLEAN: {
                                                    gender = String.valueOf(
                                                            dataRow.getCell(genderColumnIndex).getBooleanCellValue());
                                                    validateDto.setGenderValue(gender);
                                                    validateDto.setGenderValidationStatus(false);
                                                    validateDto.setGenderErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                                    remarks = ExcelValidation.BOOLEAN.getValue() + " gender";
                                                    break;
                                                }
                                                case ERROR: {
                                                    gender = String.valueOf(
                                                            dataRow.getCell(genderColumnIndex).getErrorCellValue());
                                                    validateDto.setGenderValue(gender);
                                                    validateDto.setGenderValidationStatus(false);
                                                    validateDto.setGenderErrorMessage(ExcelValidation.ERROR.getValue());
                                                    remarks = ExcelValidation.ERROR.getValue() + " gender";
                                                    break;
                                                }
                                                default:
                                                    throw new IllegalArgumentException(
                                                            "Unexpected value: " + genderCell.getCachedFormulaResultType());
                                            }
                                        }
                                    }
                                }

                                if (colIdx == ageColumnIndex) {
                                    if (dataRow.getCell(ageColumnIndex) != null) {
                                        String age;
                                        Cell ageCell = dataRow.getCell(ageColumnIndex);
                                        if (ageCell.getCellType() == CellType.NUMERIC) {
                                            double numericCellValue = ageCell.getNumericCellValue();
                                            DecimalFormat df = new DecimalFormat("#");
                                            age = df.format(numericCellValue);
                                            if (numericCellValue > -1) {
                                                validateDto.setAgeValidationStatus(true);
                                                validateDto.setAgeValue(age);
                                            } else {
                                                validateDto.setAgeValue(age);
                                                validateDto.setAgeValidationStatus(false);
                                                validateDto.setAgeErrorMessage(ExcelValidation.OTHER.getValue());
                                                remarks = "Age less than Zero";
                                            }

                                        } else if (ageCell.getCellType() == CellType.STRING) {

                                            age = ageCell.getStringCellValue();
                                            double parseDouble = Double.parseDouble(age);
                                            if (parseDouble > -1) {
                                                validateDto.setAgeValidationStatus(true);
                                                validateDto.setAgeValue(age);
                                            } else {
                                                validateDto.setAgeValue(age);
                                                validateDto.setAgeValidationStatus(false);
                                                validateDto.setAgeErrorMessage(ExcelValidation.OTHER.getValue());
                                                remarks = "Age less than Zero";
                                            }
                                            validateDto.setAgeValue(age);
                                            validateDto.setAgeValidationStatus(false);
                                            validateDto.setAgeErrorMessage(ExcelValidation.OTHER.getValue());
                                            remarks = ExcelValidation.OTHER.getValue() + " Age";
                                        } else if (ageCell.getCellType() == CellType.BLANK) {
                                            validateDto.setAgeValue("");
                                            validateDto.setAgeValidationStatus(false);
                                            validateDto.setAgeErrorMessage(ExcelValidation.BLANK.getValue());
                                            remarks = ExcelValidation.BLANK.getValue() + " Age";
                                        } else if (ageCell.getCellType() == CellType.BOOLEAN) {
                                            age = String.valueOf(ageCell.getBooleanCellValue());
                                            validateDto.setAgeValue(age);
                                            validateDto.setAgeValidationStatus(false);
                                            validateDto.setAgeErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                            remarks = ExcelValidation.BOOLEAN.getValue() + " Age";
                                        } else if (ageCell.getCellType() == CellType.ERROR) {
                                            age = String.valueOf(ageCell.getErrorCellValue());
                                            validateDto.setAgeValue(age);
                                            validateDto.setAgeValidationStatus(false);
                                            validateDto.setAgeErrorMessage(ExcelValidation.ERROR.getValue());
                                            remarks = ExcelValidation.ERROR.getValue() + " Age";
                                        } else if (ageCell.getCellType() == CellType._NONE) {
                                            validateDto.setAgeValue("");
                                            validateDto.setAgeValidationStatus(false);
                                            validateDto.setAgeErrorMessage(ExcelValidation.NONE.getValue());
                                            remarks = ExcelValidation.NONE.getValue() + " Age";
                                        } else if (ageCell.getCellType() == CellType.FORMULA) {
                                            switch (ageCell.getCachedFormulaResultType()) {
                                                case NUMERIC: {
                                                    validateDto.setAgeValidationStatus(true);
                                                    age = String.valueOf((int) ageCell.getNumericCellValue());
                                                    validateDto.setAgeValue(age);
                                                    break;
                                                }
                                                case STRING: {
                                                    age = ageCell.getStringCellValue();
                                                    validateDto.setAgeValue(age);
                                                    validateDto.setAgeValidationStatus(false);
                                                    validateDto.setAgeErrorMessage(ExcelValidation.OTHER.getValue());
                                                    remarks = ExcelValidation.OTHER.getValue() + " Age";
                                                    break;
                                                }
                                                case BLANK: {
                                                    validateDto.setAgeValue("");
                                                    validateDto.setAgeValidationStatus(false);
                                                    validateDto.setAgeErrorMessage(ExcelValidation.BLANK.getValue());
                                                    remarks = ExcelValidation.BLANK.getValue() + " Age";
                                                    break;
                                                }
                                                case BOOLEAN: {
                                                    age = String.valueOf(ageCell.getBooleanCellValue());
                                                    validateDto.setAgeValue(age);
                                                    validateDto.setAgeValidationStatus(false);
                                                    validateDto.setAgeErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                                    remarks = ExcelValidation.BOOLEAN.getValue() + " Age";
                                                    break;
                                                }
                                                case ERROR: {
                                                    age = String.valueOf(ageCell.getErrorCellValue());
                                                    validateDto.setAgeValue(age);
                                                    validateDto.setAgeValidationStatus(false);
                                                    validateDto.setAgeErrorMessage(ExcelValidation.ERROR.getValue());
                                                    remarks = ExcelValidation.ERROR.getValue() + " Age";
                                                    break;
                                                }
                                                case _NONE: {
                                                    validateDto.setAgeValue("");
                                                    validateDto.setAgeValidationStatus(false);
                                                    validateDto.setAgeErrorMessage(ExcelValidation.NONE.getValue());
                                                    remarks = ExcelValidation.NONE.getValue() + " Age";
                                                    break;
                                                }
                                                default:
                                                    throw new IllegalArgumentException(
                                                            "Unexpected value: " + ageCell.getCachedFormulaResultType());
                                            }

                                        }
                                    }
                                }

                                if (colIdx == dateOfBirthColumnIndex) {
                                    if (dataRow.getCell(dateOfBirthColumnIndex) != null) {
                                        String dateOfBirth;
                                        Cell dateOfBirthCell = dataRow.getCell(dateOfBirthColumnIndex);
                                        if (dateOfBirthCell.getCellType() == CellType.NUMERIC) {
                                            validateDto.setDateOfBirthValidationStatus(true);
                                            dateOfBirth = String.valueOf(dateOfBirthCell.getDateCellValue());
                                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                            String format = outputFormat.format(dateOfBirthCell.getDateCellValue());
                                            validateDto.setDateOfBirthValue(format);
                                        } else if (dateOfBirthCell.getCellType() == CellType.STRING) {
                                            dateOfBirth = dateOfBirthCell.getStringCellValue();
                                            if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                                                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                try {
                                                    Date parse = inputFormat.parse(dateOfBirth);
                                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

                                                    // Format the 'parse' Date object into a string
                                                    String formattedDate = outputFormat.format(parse);

                                                    validateDto.setDateOfBirthValue(formattedDate);
                                                    validateDto.setDateOfBirthValidationStatus(true);
                                                } catch (ParseException e) {
                                                    System.err.println("Failed to parse date: " + e.getMessage());
                                                }
                                            } else {
                                                System.err.println("Date of birth is empty or null.");
                                            }

                                        } else if (dateOfBirthCell.getCellType() == CellType.BLANK) {
                                            validateDto.setDateOfBirthValue("");
                                            validateDto.setDateOfBirthValidationStatus(true);

                                        } else if (dateOfBirthCell.getCellType() == CellType.BOOLEAN) {
                                            dateOfBirth = String.valueOf(dateOfBirthCell.getBooleanCellValue());
                                            validateDto.setDateOfBirthValue(dateOfBirth);
                                            validateDto.setDateOfBirthValidationStatus(true);

                                        } else if (dateOfBirthCell.getCellType() == CellType.ERROR) {
                                            dateOfBirth = String.valueOf(dateOfBirthCell.getErrorCellValue());
                                            validateDto.setDateOfBirthValue(dateOfBirth);
                                            validateDto.setDateOfBirthValidationStatus(true);

                                        } else if (dateOfBirthCell.getCellType() == CellType._NONE) {
                                            validateDto.setDateOfBirthValue("");
                                            validateDto.setDateOfBirthValidationStatus(true);

                                        } else if (dateOfBirthCell.getCellType() == CellType.FORMULA) {
                                            switch (dateOfBirthCell.getCachedFormulaResultType()) {
                                                case NUMERIC: {
                                                    validateDto.setDateOfBirthValidationStatus(true);
                                                    dateOfBirth = String.valueOf(dateOfBirthCell.getDateCellValue());
                                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                                    String format = outputFormat.format(dateOfBirthCell.getDateCellValue());
                                                    validateDto.setDateOfBirthValue(format);
                                                    break;
                                                }
                                                case STRING: {
                                                    dateOfBirth = dateOfBirthCell.getStringCellValue();
                                                    validateDto.setDateOfBirthValue(dateOfBirth);
                                                    validateDto.setDateOfBirthValidationStatus(true);

                                                    break;
                                                }
                                                case BLANK: {
                                                    validateDto.setDateOfBirthValue("");
                                                    validateDto.setDateOfBirthValidationStatus(true);

                                                    break;
                                                }
                                                case BOOLEAN: {
                                                    dateOfBirth = String.valueOf(dateOfBirthCell.getBooleanCellValue());
                                                    validateDto.setDateOfBirthValue(dateOfBirth);
                                                    validateDto.setDateOfBirthValidationStatus(true);

                                                    break;
                                                }
                                                case ERROR: {
                                                    dateOfBirth = String.valueOf(dateOfBirthCell.getErrorCellValue());
                                                    validateDto.setDateOfBirthValue(dateOfBirth);
                                                    validateDto.setDateOfBirthValidationStatus(true);

                                                    break;
                                                }
                                                case _NONE: {
                                                    validateDto.setDateOfBirthValue("");
                                                    validateDto.setDateOfBirthValidationStatus(true);

                                                    break;
                                                }
                                                default:
                                                    throw new IllegalArgumentException("Unexpected value: "
                                                            + dateOfBirthCell.getCachedFormulaResultType());
                                            }

                                        }
                                    }
                                }

                                if (colIdx == sumInsuredColumnIndex) {
                                    if (dataRow.getCell(sumInsuredColumnIndex) != null) {
                                        Cell sumInsuredCell = dataRow.getCell(sumInsuredColumnIndex);
                                        if (sumInsuredCell.getCellType() == CellType.NUMERIC) {
                                            validateDto.setSumInsuredValidationStatus(true);
                                            double numericCellValue = sumInsuredCell.getNumericCellValue();
                                            // Using DecimalFormat to remove decimal part
                                            DecimalFormat df = new DecimalFormat("#0");
                                            sumInsured = df.format(numericCellValue);
                                            validateDto.setSumInsuredValue(sumInsured);
                                            TempSumIssured = sumInsured;
                                        } else if (sumInsuredCell.getCellType() == CellType.STRING) {
                                            sumInsured = sumInsuredCell.getStringCellValue();
                                            validateDto.setSumInsuredValidationStatus(false);
                                            validateDto.setSumInsuredValue(sumInsured);
                                            validateDto.setSumInsuredErrorMessage(ExcelValidation.OTHER.getValue());
                                            remarks = ExcelValidation.OTHER.getValue() + " Sum Insured";
                                            TempSumIssured = sumInsured;
                                        } else if (sumInsuredCell.getCellType() == CellType.BLANK) {
                                            sumInsured = TempSumIssured;
                                            validateDto.setSumInsuredValidationStatus(true);
                                            validateDto.setSumInsuredValue(sumInsured);
                                        } else if (sumInsuredCell.getCellType() == CellType.BOOLEAN) {
                                            sumInsured = String.valueOf(sumInsuredCell.getBooleanCellValue());
                                            validateDto.setSumInsuredValue(sumInsured);
                                            validateDto.setSumInsuredValidationStatus(false);
                                            TempSumIssured = sumInsured;
                                            validateDto.setSumInsuredErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                        } else if (sumInsuredCell.getCellType() == CellType.ERROR) {
                                            sumInsured = String.valueOf(sumInsuredCell.getErrorCellValue());
                                            validateDto.setSumInsuredValue(sumInsured);
                                            validateDto.setSumInsuredValidationStatus(false);
                                            validateDto.setSumInsuredErrorMessage(ExcelValidation.ERROR.getValue());
                                            remarks = ExcelValidation.ERROR.getValue() + " Sum Insured";
                                            TempSumIssured = sumInsured;
                                        } else if (sumInsuredCell.getCellType() == CellType._NONE) {
                                            validateDto.setSumInsuredValue("");
                                            validateDto.setSumInsuredValidationStatus(false);
                                            validateDto.setSumInsuredErrorMessage(ExcelValidation.NONE.getValue());
                                            remarks = ExcelValidation.NONE.getValue() + " Sum Insured";
                                        } else if (sumInsuredCell.getCellType() == CellType.FORMULA) {
                                            switch (sumInsuredCell.getCachedFormulaResultType()) {
                                                case NUMERIC: {
                                                    validateDto.setSumInsuredValidationStatus(true);
                                                    sumInsured = String.valueOf((int) sumInsuredCell.getNumericCellValue());
                                                    validateDto.setSumInsuredValue(sumInsured);
                                                    TempSumIssured = sumInsured;
                                                    break;
                                                }
                                                case STRING: {
                                                    sumInsured = sumInsuredCell.getStringCellValue();
                                                    validateDto.setSumInsuredValidationStatus(false);
                                                    validateDto.setSumInsuredValue(sumInsured);
                                                    validateDto.setSumInsuredErrorMessage(ExcelValidation.OTHER.getValue());
                                                    remarks = ExcelValidation.OTHER.getValue() + " Sum Insured";
                                                    TempSumIssured = sumInsured;
                                                    break;
                                                }
                                                case BLANK: {
                                                    sumInsured = TempSumIssured;
                                                    validateDto.setSumInsuredValidationStatus(true);
                                                    validateDto.setSumInsuredValue(sumInsured);
                                                    break;
                                                }
                                                case BOOLEAN: {
                                                    sumInsured = String.valueOf(sumInsuredCell.getBooleanCellValue());
                                                    validateDto.setSumInsuredValue(sumInsured);
                                                    validateDto.setSumInsuredValidationStatus(false);
                                                    TempSumIssured = sumInsured;
                                                    validateDto
                                                            .setSumInsuredErrorMessage(ExcelValidation.BOOLEAN.getValue());
                                                    remarks = ExcelValidation.BOOLEAN.getValue() + " Sum Insured";
                                                    break;
                                                }
                                                case ERROR: {
                                                    sumInsured = String.valueOf(sumInsuredCell.getErrorCellValue());
                                                    validateDto.setSumInsuredValue(sumInsured);
                                                    validateDto.setSumInsuredValidationStatus(false);
                                                    validateDto.setSumInsuredErrorMessage(ExcelValidation.ERROR.getValue());
                                                    remarks = ExcelValidation.ERROR.getValue() + " Sum Insured";
                                                    TempSumIssured = sumInsured;
                                                    break;
                                                }
                                                case _NONE: {
                                                    validateDto.setSumInsuredValue("");
                                                    validateDto.setSumInsuredValidationStatus(false);
                                                    validateDto.setSumInsuredErrorMessage(ExcelValidation.NONE.getValue());
                                                    remarks = ExcelValidation.NONE.getValue() + " Sum Insured";
                                                    break;
                                                }
                                                default:
                                                    throw new IllegalArgumentException("Unexpected value: "
                                                            + sumInsuredCell.getCachedFormulaResultType());
                                            }

                                        }
                                    }
                                }

                            }

                        }
                        validateDto.setRemarks(remarks);
                        remarks = null;
                        validateValues.add(validateDto);
                    }
                }
            }

            return validateValues;

        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void validateBasedOnColumnIndex(String employeeId, String TempEmpID, String sumInsured,
                                           String TempSumIssured, Row dataRow, int colIndex, CoverageDetailsChildValidateValuesDto validateDto) {

        if (colIndex == employeeIdColumnIndex) {
            if (dataRow.getCell(employeeIdColumnIndex) != null) {
                Cell employeeIdCell = dataRow.getCell(employeeIdColumnIndex);

                if (employeeIdCell.getCellType() == CellType.STRING) {
                    validateDto.setEmployeeIdValidationStatus(true);
                    employeeId = dataRow.getCell(employeeIdColumnIndex).getStringCellValue();
                    validateDto.setEmployeeIdValue(employeeId);
                    TempEmpID = employeeId;
                } else if (employeeIdCell.getCellType() == CellType.NUMERIC) {
                    validateDto.setEmployeeIdValidationStatus(true);
                    employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex).getNumericCellValue());
                    validateDto.setEmployeeIdValue(employeeId);
                    TempEmpID = employeeId;
                } else if (employeeIdCell.getCellType() == CellType.BLANK) {
                    employeeId = TempEmpID;
                    validateDto.setEmployeeIdValue(employeeId);
                    validateDto.setEmployeeIdValidationStatus(true);
                } else if (employeeIdCell.getCellType() == CellType._NONE) {
                    employeeId = TempEmpID;
                    validateDto.setEmployeeIdValue(employeeId);
                    validateDto.setEmployeeIdValidationStatus(true);
                } else if (employeeIdCell.getCellType() == CellType.BOOLEAN) {
                    employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex).getBooleanCellValue());
                    validateDto.setEmployeeIdValue(employeeId);
                    validateDto.setEmployeeIdValidationStatus(false);
                    TempEmpID = employeeId;
                    validateDto.setEmployeeIdErrorMessage(ExcelValidation.BOOLEAN.getValue());
                } else if (employeeIdCell.getCellType() == CellType.ERROR) {
                    employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex).getErrorCellValue());
                    validateDto.setEmployeeIdValue(employeeId);
                    validateDto.setEmployeeIdValidationStatus(false);
                    validateDto.setEmployeeIdErrorMessage(ExcelValidation.ERROR.getValue());
                    TempEmpID = employeeId;
                } else if (employeeIdCell.getCellType() == CellType.FORMULA) {
                    switch (employeeIdCell.getCachedFormulaResultType()) {
                        case NUMERIC: {
                            validateDto.setEmployeeIdValidationStatus(true);
                            employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex).getNumericCellValue());
                            validateDto.setEmployeeIdValue(employeeId);
                            TempEmpID = employeeId;
                            break;
                        }
                        case STRING: {
                            validateDto.setEmployeeIdValidationStatus(true);
                            employeeId = dataRow.getCell(employeeIdColumnIndex).getStringCellValue();
                            validateDto.setEmployeeIdValue(employeeId);
                            TempEmpID = employeeId;
                            break;
                        }
                        case BLANK: {
                            employeeId = TempEmpID;
                            validateDto.setEmployeeIdValue(employeeId);
                            validateDto.setEmployeeIdValidationStatus(true);
                            break;
                        }
                        case _NONE: {
                            employeeId = TempEmpID;
                            validateDto.setEmployeeIdValue(employeeId);
                            validateDto.setEmployeeIdValidationStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex).getBooleanCellValue());
                            validateDto.setEmployeeIdValue(employeeId);
                            validateDto.setEmployeeIdValidationStatus(false);
                            TempEmpID = employeeId;
                            break;
                        }
                        case ERROR: {
                            employeeId = String.valueOf(dataRow.getCell(employeeIdColumnIndex).getErrorCellValue());
                            validateDto.setEmployeeIdValue(employeeId);
                            validateDto.setEmployeeIdValidationStatus(false);
                            validateDto.setEmployeeIdErrorMessage(ExcelValidation.ERROR.getValue());
                            TempEmpID = employeeId;
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + employeeIdCell.getCachedFormulaResultType());
                    }
                }
            }
        }

        if (colIndex == employeeNameColumnIndex) {
            if (dataRow.getCell(employeeNameColumnIndex) != null) {
                String employeeName;
                Cell employeeNameCell = dataRow.getCell(employeeNameColumnIndex);
                if (employeeNameCell.getCellType() == CellType.STRING) {
                    validateDto.setEmployeeNameValidationStatus(true);
                    employeeName = dataRow.getCell(employeeNameColumnIndex).getStringCellValue();
                    validateDto.setEmployeeNameValue(employeeName);
                } else if (employeeNameCell.getCellType() == CellType.NUMERIC) {
                    validateDto.setEmployeeNameValidationStatus(false);
                    employeeName = String.valueOf(dataRow.getCell(employeeNameColumnIndex).getNumericCellValue());
                    validateDto.setEmployeeNameValue(employeeName);
                } else if (employeeNameCell.getCellType() == CellType.BLANK) {
                    validateDto.setEmployeeNameValue("");
                    validateDto.setEmployeeNameValidationStatus(false);
                    validateDto.setEmployeeNameErrorMessage(ExcelValidation.BLANK.getValue());
                } else if (employeeNameCell.getCellType() == CellType._NONE) {
                    validateDto.setEmployeeNameValue("");
                    validateDto.setEmployeeNameValidationStatus(false);
                } else if (employeeNameCell.getCellType() == CellType.BOOLEAN) {
                    employeeName = String.valueOf(dataRow.getCell(employeeNameColumnIndex).getBooleanCellValue());
                    validateDto.setEmployeeNameValue(employeeName);
                    validateDto.setEmployeeNameValidationStatus(false);
                    validateDto.setEmployeeNameErrorMessage(ExcelValidation.BOOLEAN.getValue());
                } else if (employeeNameCell.getCellType() == CellType.ERROR) {
                    employeeName = String.valueOf(dataRow.getCell(employeeNameColumnIndex).getErrorCellValue());
                    validateDto.setEmployeeIdValue(employeeName);
                    validateDto.setEmployeeNameValidationStatus(false);
                    validateDto.setEmployeeNameErrorMessage(ExcelValidation.ERROR.getValue());
                } else if (employeeNameCell.getCellType() == CellType.FORMULA) {
                    switch (employeeNameCell.getCachedFormulaResultType()) {
                        case NUMERIC: {
                            validateDto.setEmployeeNameValidationStatus(false);
                            employeeName = String.valueOf(dataRow.getCell(employeeNameColumnIndex).getNumericCellValue());
                            validateDto.setEmployeeNameValue(employeeName);
                            break;
                        }
                        case STRING: {
                            validateDto.setEmployeeNameValidationStatus(true);
                            employeeName = dataRow.getCell(employeeNameColumnIndex).getStringCellValue();
                            validateDto.setEmployeeNameValue(employeeName);
                            break;
                        }
                        case BLANK: {
                            validateDto.setEmployeeNameValue("");
                            validateDto.setEmployeeNameValidationStatus(false);
                            validateDto.setEmployeeNameErrorMessage(ExcelValidation.BLANK.getValue());
                            break;
                        }
                        case _NONE: {
                            validateDto.setEmployeeNameValue("");
                            validateDto.setEmployeeNameValidationStatus(false);
                            validateDto.setEmployeeNameErrorMessage(ExcelValidation.NONE.getValue());
                            break;
                        }
                        case BOOLEAN: {
                            employeeName = String.valueOf(dataRow.getCell(employeeNameColumnIndex).getBooleanCellValue());
                            validateDto.setEmployeeNameValue(employeeName);
                            validateDto.setEmployeeNameValidationStatus(false);
                            validateDto.setEmployeeNameErrorMessage(ExcelValidation.BOOLEAN.getValue());
                            break;
                        }
                        case ERROR: {
                            employeeName = String.valueOf(dataRow.getCell(employeeNameColumnIndex).getErrorCellValue());
                            validateDto.setEmployeeIdValue(employeeName);
                            validateDto.setEmployeeNameValidationStatus(false);
                            validateDto.setEmployeeNameErrorMessage(ExcelValidation.ERROR.getValue());
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + employeeNameCell.getCachedFormulaResultType());
                    }
                }
            }
        }

        if (colIndex == relationshipColumnIndex) {
            if (dataRow.getCell(relationshipColumnIndex) != null) {
                String relationship;

                Cell relationshipCell = dataRow.getCell(relationshipColumnIndex);
                if (relationshipCell.getCellType() == CellType.STRING) {
                    validateDto.setRelationshipValidationStatus(true);
                    relationship = dataRow.getCell(relationshipColumnIndex).getStringCellValue();
                    validateDto.setRelationshipValue(relationship);
                } else if (relationshipCell.getCellType() == CellType.NUMERIC) {
                    validateDto.setRelationshipValidationStatus(false);
                    relationship = String.valueOf(dataRow.getCell(relationshipColumnIndex).getNumericCellValue());
                    validateDto.setRelationshipValue(relationship);
                } else if (relationshipCell.getCellType() == CellType.BLANK) {
                    validateDto.setRelationshipValue("");
                    validateDto.setRelationshipValidationStatus(false);
                    validateDto.setRelationshipErrorMessage(ExcelValidation.BLANK.getValue());
                } else if (relationshipCell.getCellType() == CellType._NONE) {
                    validateDto.setRelationshipValue("");
                    validateDto.setRelationshipValidationStatus(false);
                    validateDto.setRelationshipErrorMessage(ExcelValidation.NONE.getValue());
                } else if (relationshipCell.getCellType() == CellType.BOOLEAN) {
                    relationship = String.valueOf(dataRow.getCell(relationshipColumnIndex).getBooleanCellValue());
                    validateDto.setRelationshipValue(relationship);
                    validateDto.setRelationshipValidationStatus(false);
                    validateDto.setRelationshipErrorMessage(ExcelValidation.BOOLEAN.getValue());
                } else if (relationshipCell.getCellType() == CellType.ERROR) {
                    relationship = String.valueOf(dataRow.getCell(relationshipColumnIndex).getErrorCellValue());
                    validateDto.setEmployeeIdValue(relationship);
                    validateDto.setRelationshipValidationStatus(false);
                    validateDto.setRelationshipErrorMessage(ExcelValidation.ERROR.getValue());
                } else if (relationshipCell.getCellType() == CellType.FORMULA) {
                    switch (relationshipCell.getCachedFormulaResultType()) {
                        case NUMERIC: {
                            validateDto.setRelationshipValidationStatus(false);
                            relationship = String.valueOf(dataRow.getCell(relationshipColumnIndex).getNumericCellValue());
                            validateDto.setRelationshipValue(relationship);
                            break;
                        }
                        case STRING: {
                            validateDto.setRelationshipValidationStatus(true);
                            relationship = dataRow.getCell(relationshipColumnIndex).getStringCellValue();
                            validateDto.setRelationshipValue(relationship);
                            break;
                        }
                        case BLANK: {
                            validateDto.setRelationshipValue("");
                            validateDto.setRelationshipValidationStatus(false);
                            validateDto.setRelationshipErrorMessage(ExcelValidation.BLANK.getValue());
                            break;
                        }
                        case _NONE: {
                            validateDto.setRelationshipValue("");
                            validateDto.setRelationshipValidationStatus(false);
                            validateDto.setRelationshipErrorMessage(ExcelValidation.NONE.getValue());
                            break;
                        }
                        case BOOLEAN: {
                            relationship = String.valueOf(dataRow.getCell(relationshipColumnIndex).getBooleanCellValue());
                            validateDto.setRelationshipValue(relationship);
                            validateDto.setRelationshipValidationStatus(false);
                            validateDto.setRelationshipErrorMessage(ExcelValidation.BOOLEAN.getValue());
                            break;
                        }
                        case ERROR: {
                            relationship = String.valueOf(dataRow.getCell(relationshipColumnIndex).getErrorCellValue());
                            validateDto.setEmployeeIdValue(relationship);
                            validateDto.setRelationshipValidationStatus(false);
                            validateDto.setRelationshipErrorMessage(ExcelValidation.ERROR.getValue());
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + relationshipCell.getCachedFormulaResultType());
                    }
                }
            }
        }

        if (colIndex == genderColumnIndex) {
            if (dataRow.getCell(genderColumnIndex) != null) {
                String gender;
                String[] genderArr = {"M", "F", "Male", "Female", "Trans", "TransGender"};
                Cell genderCell = dataRow.getCell(genderColumnIndex);
                if (genderCell.getCellType() == CellType.STRING) {
                    gender = dataRow.getCell(genderColumnIndex).getStringCellValue().replaceAll(" ", "");
                    if (Arrays.asList(genderArr).contains(gender)) {
                        validateDto.setGenderValidationStatus(true);
                        validateDto.setGenderValue(gender);
                    } else {
                        gender = dataRow.getCell(genderColumnIndex).getStringCellValue();
                        validateDto.setGenderValue(String.valueOf(gender));
                        validateDto.setGenderValidationStatus(false);
                        validateDto.setGenderErrorMessage(ExcelValidation.OTHER.getValue());
                    }
                } else if (genderCell.getCellType() == CellType.NUMERIC) {
                    gender = String.valueOf(dataRow.getCell(genderColumnIndex).getNumericCellValue());
                    validateDto.setGenderValue(String.valueOf(gender));
                    validateDto.setGenderValidationStatus(false);
                    validateDto.setGenderErrorMessage("Gender should not be in number format");
                } else if (genderCell.getCellType() == CellType.BLANK) {
                    validateDto.setGenderValue("");
                    validateDto.setGenderValidationStatus(false);
                    validateDto.setGenderErrorMessage(ExcelValidation.BLANK.getValue());
                } else if (genderCell.getCellType() == CellType._NONE) {
                    validateDto.setGenderValue("");
                    validateDto.setGenderValidationStatus(false);
                    validateDto.setGenderErrorMessage(ExcelValidation.NONE.getValue());
                } else if (genderCell.getCellType() == CellType.BOOLEAN) {
                    gender = String.valueOf(dataRow.getCell(genderColumnIndex).getBooleanCellValue());
                    validateDto.setGenderValue(gender);
                    validateDto.setGenderValidationStatus(false);
                    validateDto.setGenderErrorMessage(ExcelValidation.BOOLEAN.getValue());
                } else if (genderCell.getCellType() == CellType.ERROR) {
                    gender = String.valueOf(dataRow.getCell(genderColumnIndex).getErrorCellValue());
                    validateDto.setGenderValue(gender);
                    validateDto.setGenderValidationStatus(false);
                    validateDto.setGenderErrorMessage(ExcelValidation.ERROR.getValue());
                } else if (genderCell.getCellType() == CellType.FORMULA) {
                    switch (genderCell.getCachedFormulaResultType()) {
                        case NUMERIC: {
                            gender = String.valueOf(dataRow.getCell(genderColumnIndex).getNumericCellValue());
                            validateDto.setGenderValue(String.valueOf(gender));
                            validateDto.setGenderValidationStatus(false);
                            validateDto.setGenderErrorMessage("Gender should not be in number format");
                            break;
                        }
                        case STRING: {
                            gender = dataRow.getCell(genderColumnIndex).getStringCellValue().replaceAll(" ", "");
                            if (Arrays.asList(genderArr).contains(gender)) {
                                validateDto.setGenderValidationStatus(true);
                                validateDto.setGenderValue(gender);
                            } else {
                                gender = dataRow.getCell(genderColumnIndex).getStringCellValue();
                                validateDto.setGenderValue(String.valueOf(gender));
                                validateDto.setGenderValidationStatus(false);
                                validateDto.setGenderErrorMessage(ExcelValidation.OTHER.getValue());
                            }
                            break;
                        }
                        case BLANK: {
                            validateDto.setGenderValue("");
                            validateDto.setGenderValidationStatus(false);
                            validateDto.setGenderErrorMessage(ExcelValidation.BLANK.getValue());
                            break;
                        }
                        case _NONE: {
                            validateDto.setGenderValue("");
                            validateDto.setGenderValidationStatus(false);
                            validateDto.setGenderErrorMessage(ExcelValidation.NONE.getValue());
                            break;
                        }
                        case BOOLEAN: {
                            gender = String.valueOf(dataRow.getCell(genderColumnIndex).getBooleanCellValue());
                            validateDto.setGenderValue(gender);
                            validateDto.setGenderValidationStatus(false);
                            validateDto.setGenderErrorMessage(ExcelValidation.BOOLEAN.getValue());
                            break;
                        }
                        case ERROR: {
                            gender = String.valueOf(dataRow.getCell(genderColumnIndex).getErrorCellValue());
                            validateDto.setGenderValue(gender);
                            validateDto.setGenderValidationStatus(false);
                            validateDto.setGenderErrorMessage(ExcelValidation.ERROR.getValue());
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + genderCell.getCachedFormulaResultType());
                    }
                }
            }
        }

        if (colIndex == ageColumnIndex) {
            if (dataRow.getCell(ageColumnIndex) != null) {
                String age;
                Cell ageCell = dataRow.getCell(ageColumnIndex);
                if (ageCell.getCellType() == CellType.NUMERIC) {
                    validateDto.setAgeValidationStatus(true);
                    age = String.valueOf(ageCell.getNumericCellValue());
                    validateDto.setAgeValue(age);
                } else if (ageCell.getCellType() == CellType.STRING) {
                    age = ageCell.getStringCellValue();
                    validateDto.setAgeValue(age);
                    validateDto.setAgeValidationStatus(false);
                    validateDto.setAgeErrorMessage(ExcelValidation.OTHER.getValue());
                } else if (ageCell.getCellType() == CellType.BLANK) {
                    validateDto.setAgeValue("");
                    validateDto.setAgeValidationStatus(false);
                    validateDto.setAgeErrorMessage(ExcelValidation.BLANK.getValue());
                } else if (ageCell.getCellType() == CellType.BOOLEAN) {
                    age = String.valueOf(ageCell.getBooleanCellValue());
                    validateDto.setAgeValue(age);
                    validateDto.setAgeValidationStatus(false);
                    validateDto.setAgeErrorMessage(ExcelValidation.BOOLEAN.getValue());
                } else if (ageCell.getCellType() == CellType.ERROR) {
                    age = String.valueOf(ageCell.getErrorCellValue());
                    validateDto.setAgeValue(age);
                    validateDto.setAgeValidationStatus(false);
                    validateDto.setAgeErrorMessage(ExcelValidation.ERROR.getValue());
                } else if (ageCell.getCellType() == CellType._NONE) {
                    validateDto.setAgeValue("");
                    validateDto.setAgeValidationStatus(false);
                    validateDto.setAgeErrorMessage(ExcelValidation.NONE.getValue());
                } else if (ageCell.getCellType() == CellType.FORMULA) {
                    switch (ageCell.getCachedFormulaResultType()) {
                        case NUMERIC: {
                            validateDto.setAgeValidationStatus(true);
                            age = String.valueOf(ageCell.getNumericCellValue());
                            validateDto.setAgeValue(age);
                            break;
                        }
                        case STRING: {
                            age = ageCell.getStringCellValue();
                            validateDto.setAgeValue(age);
                            validateDto.setAgeValidationStatus(false);
                            validateDto.setAgeErrorMessage(ExcelValidation.OTHER.getValue());
                            break;
                        }
                        case BLANK: {
                            validateDto.setAgeValue("");
                            validateDto.setAgeValidationStatus(false);
                            validateDto.setAgeErrorMessage(ExcelValidation.BLANK.getValue());
                            break;
                        }
                        case BOOLEAN: {
                            age = String.valueOf(ageCell.getBooleanCellValue());
                            validateDto.setAgeValue(age);
                            validateDto.setAgeValidationStatus(false);
                            validateDto.setAgeErrorMessage(ExcelValidation.BOOLEAN.getValue());
                            break;
                        }
                        case ERROR: {
                            age = String.valueOf(ageCell.getErrorCellValue());
                            validateDto.setAgeValue(age);
                            validateDto.setAgeValidationStatus(false);
                            validateDto.setAgeErrorMessage(ExcelValidation.ERROR.getValue());
                            break;
                        }
                        case _NONE: {
                            validateDto.setAgeValue("");
                            validateDto.setAgeValidationStatus(false);
                            validateDto.setAgeErrorMessage(ExcelValidation.NONE.getValue());
                            break;
                        }
                        default:
                            throw new IllegalArgumentException("Unexpected value: " + ageCell.getCachedFormulaResultType());
                    }

                }
            }
        }

        if (colIndex == dateOfBirthColumnIndex) {
            if (dataRow.getCell(dateOfBirthColumnIndex) != null) {
                String dateOfBirth;
                Cell dateOfBirthCell = dataRow.getCell(dateOfBirthColumnIndex);
                if (dateOfBirthCell.getCellType() == CellType.NUMERIC) {
                    validateDto.setDateOfBirthValidationStatus(true);
                    dateOfBirth = String.valueOf(dateOfBirthCell.getDateCellValue());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String format = outputFormat.format(dateOfBirthCell.getDateCellValue());
                    validateDto.setDateOfBirthValue(format);
                } else if (dateOfBirthCell.getCellType() == CellType.STRING) {
                    dateOfBirth = dateOfBirthCell.getStringCellValue();
                    if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date parse = inputFormat.parse(dateOfBirth);
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

                            // Format the 'parse' Date object into a string
                            String formattedDate = outputFormat.format(parse);

                            validateDto.setDateOfBirthValue(formattedDate);
                            validateDto.setDateOfBirthValidationStatus(true);
                        } catch (ParseException e) {
                            System.err.println("Failed to parse date: " + e.getMessage());
                        }
                    } else {
                        System.err.println("Date of birth is empty or null.");
                    }
                } else if (dateOfBirthCell.getCellType() == CellType.BLANK) {
                    validateDto.setDateOfBirthValue("");
                    validateDto.setDateOfBirthValidationStatus(false);
                    validateDto.setDateOfBirthErrorMessage(ExcelValidation.BLANK.getValue());
                } else if (dateOfBirthCell.getCellType() == CellType.BOOLEAN) {
                    dateOfBirth = String.valueOf(dateOfBirthCell.getBooleanCellValue());
                    validateDto.setDateOfBirthValue(dateOfBirth);
                    validateDto.setDateOfBirthValidationStatus(false);
                    validateDto.setDateOfBirthErrorMessage(ExcelValidation.BOOLEAN.getValue());
                } else if (dateOfBirthCell.getCellType() == CellType.ERROR) {
                    dateOfBirth = String.valueOf(dateOfBirthCell.getErrorCellValue());
                    validateDto.setDateOfBirthValue(dateOfBirth);
                    validateDto.setDateOfBirthValidationStatus(false);
                    validateDto.setDateOfBirthErrorMessage(ExcelValidation.ERROR.getValue());
                } else if (dateOfBirthCell.getCellType() == CellType._NONE) {
                    validateDto.setDateOfBirthValue("");
                    validateDto.setDateOfBirthValidationStatus(false);
                    validateDto.setDateOfBirthErrorMessage(ExcelValidation.NONE.getValue());
                } else if (dateOfBirthCell.getCellType() == CellType.FORMULA) {
                    switch (dateOfBirthCell.getCachedFormulaResultType()) {
                        case NUMERIC: {
                            validateDto.setDateOfBirthValidationStatus(true);
                            dateOfBirth = String.valueOf(dateOfBirthCell.getDateCellValue());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String format = outputFormat.format(dateOfBirthCell.getDateCellValue());
                            validateDto.setDateOfBirthValue(format);
                            break;
                        }
                        case STRING: {
                            dateOfBirth = dateOfBirthCell.getStringCellValue();
                            validateDto.setDateOfBirthValue(dateOfBirth);
                            validateDto.setDateOfBirthValidationStatus(false);
                            validateDto.setDateOfBirthErrorMessage(ExcelValidation.OTHER.getValue());
                            break;
                        }
                        case BLANK: {
                            validateDto.setDateOfBirthValue("");
                            validateDto.setDateOfBirthValidationStatus(false);
                            validateDto.setDateOfBirthErrorMessage(ExcelValidation.BLANK.getValue());
                            break;
                        }
                        case BOOLEAN: {
                            dateOfBirth = String.valueOf(dateOfBirthCell.getBooleanCellValue());
                            validateDto.setDateOfBirthValue(dateOfBirth);
                            validateDto.setDateOfBirthValidationStatus(false);
                            validateDto.setDateOfBirthErrorMessage(ExcelValidation.BOOLEAN.getValue());
                            break;
                        }
                        case ERROR: {
                            dateOfBirth = String.valueOf(dateOfBirthCell.getErrorCellValue());
                            validateDto.setDateOfBirthValue(dateOfBirth);
                            validateDto.setDateOfBirthValidationStatus(false);
                            validateDto.setDateOfBirthErrorMessage(ExcelValidation.ERROR.getValue());
                            break;
                        }
                        case _NONE: {
                            validateDto.setDateOfBirthValue("");
                            validateDto.setDateOfBirthValidationStatus(false);
                            validateDto.setDateOfBirthErrorMessage(ExcelValidation.NONE.getValue());
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + dateOfBirthCell.getCachedFormulaResultType());
                    }

                }
            }
        }

        if (colIndex == sumInsuredColumnIndex) {
            if (dataRow.getCell(sumInsuredColumnIndex) != null) {
                Cell sumInsuredCell = dataRow.getCell(sumInsuredColumnIndex);
                if (sumInsuredCell.getCellType() == CellType.NUMERIC) {
                    validateDto.setSumInsuredValidationStatus(true);
                    sumInsured = String.valueOf(sumInsuredCell.getNumericCellValue());
                    validateDto.setSumInsuredValue(sumInsured);
                    TempSumIssured = sumInsured;
                } else if (sumInsuredCell.getCellType() == CellType.STRING) {
                    sumInsured = sumInsuredCell.getStringCellValue();
                    validateDto.setSumInsuredValidationStatus(false);
                    validateDto.setSumInsuredValue(sumInsured);
                    validateDto.setSumInsuredErrorMessage(ExcelValidation.OTHER.getValue());
                    TempSumIssured = sumInsured;
                } else if (sumInsuredCell.getCellType() == CellType.BLANK) {
                    sumInsured = TempSumIssured;
                    validateDto.setSumInsuredValidationStatus(true);
                    validateDto.setSumInsuredValue(sumInsured);
                    validateDto.setSumInsuredErrorMessage(ExcelValidation.BLANK.getValue());
                } else if (sumInsuredCell.getCellType() == CellType.BOOLEAN) {
                    sumInsured = String.valueOf(sumInsuredCell.getBooleanCellValue());
                    validateDto.setSumInsuredValue(sumInsured);
                    validateDto.setSumInsuredValidationStatus(false);
                    TempSumIssured = sumInsured;
                    validateDto.setSumInsuredErrorMessage(ExcelValidation.BOOLEAN.getValue());
                } else if (sumInsuredCell.getCellType() == CellType.ERROR) {
                    sumInsured = String.valueOf(sumInsuredCell.getErrorCellValue());
                    validateDto.setSumInsuredValue(sumInsured);
                    validateDto.setSumInsuredValidationStatus(false);
                    validateDto.setSumInsuredErrorMessage(ExcelValidation.ERROR.getValue());
                    TempSumIssured = sumInsured;
                } else if (sumInsuredCell.getCellType() == CellType._NONE) {
                    validateDto.setSumInsuredValue("");
                    validateDto.setSumInsuredValidationStatus(false);
                    validateDto.setSumInsuredErrorMessage(ExcelValidation.NONE.getValue());
                } else if (sumInsuredCell.getCellType() == CellType.FORMULA) {
                    switch (sumInsuredCell.getCachedFormulaResultType()) {
                        case NUMERIC: {
                            validateDto.setSumInsuredValidationStatus(true);
                            sumInsured = String.valueOf(sumInsuredCell.getNumericCellValue());
                            validateDto.setSumInsuredValue(sumInsured);
                            TempSumIssured = sumInsured;
                            break;
                        }
                        case STRING: {
                            sumInsured = sumInsuredCell.getStringCellValue();
                            validateDto.setSumInsuredValidationStatus(false);
                            validateDto.setSumInsuredValue(sumInsured);
                            validateDto.setSumInsuredErrorMessage(ExcelValidation.OTHER.getValue());
                            TempSumIssured = sumInsured;
                            break;
                        }
                        case BLANK: {
                            sumInsured = TempSumIssured;
                            validateDto.setSumInsuredValidationStatus(true);
                            validateDto.setSumInsuredValue(sumInsured);
                            break;
                        }
                        case BOOLEAN: {
                            sumInsured = String.valueOf(sumInsuredCell.getBooleanCellValue());
                            validateDto.setSumInsuredValue(sumInsured);
                            validateDto.setSumInsuredValidationStatus(false);
                            TempSumIssured = sumInsured;
                            validateDto.setSumInsuredErrorMessage(ExcelValidation.BOOLEAN.getValue());
                            break;
                        }
                        case ERROR: {
                            sumInsured = String.valueOf(sumInsuredCell.getErrorCellValue());
                            validateDto.setSumInsuredValue(sumInsured);
                            validateDto.setSumInsuredValidationStatus(false);
                            validateDto.setSumInsuredErrorMessage(ExcelValidation.ERROR.getValue());
                            TempSumIssured = sumInsured;
                            break;
                        }
                        case _NONE: {
                            validateDto.setSumInsuredValue("");
                            validateDto.setSumInsuredValidationStatus(false);
                            validateDto.setSumInsuredErrorMessage(ExcelValidation.NONE.getValue());
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + sumInsuredCell.getCachedFormulaResultType());
                    }

                }
            }
        }
    }

    @Override
    public CovergaeHeaderValidateDto validateClaimsMisHeader(MultipartFile file, String tpaName) {

        try (Workbook workbook = getWorkbook(file, tpaName)) {
            Sheet sheet = null;
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
                sheet = workbook.getSheetAt(0);
            }
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }
            Iterator<Row> rowIterator = sheet.iterator();

            // Assuming the first row contains the column names
            Row headerRow = rowIterator.next();

            CovergaeHeaderValidateDto validateDto = new CovergaeHeaderValidateDto();
            Iterator<Cell> cellIterator = headerRow.cellIterator();
            List<String> headerList = new ArrayList<>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String columnName = cell.getStringCellValue().trim(); // Convert to lowercase for
                // case-insensitive matching
                headerList.add(columnName);
            }

            Tpa findByTpaName = tpaRepo.findByTpaName(tpaName);
            List<ClaimsTPAHeaders> tpaHeaders = findByTpaName.getTpaHeaders();
            tpaHeaders.stream().forEach(i -> {

                switch (i.getHeaderAliasName()) {
                    case "Policy Number": {
                        validateDto.setPolicyNumberStatus(true);
                        break;
                    }
                    case "Claim Number": {
                        validateDto.setClaimsNumberStatus(true);
                        break;
                    }
                    case "Employee Id": {
                        validateDto.setEmployeeIdStatus(true);
                        break;
                    }
                    case "Employee Name": {
                        validateDto.setEmployeeNameStatus(true);
                        break;
                    }
                    case "RelationShip": {
                        validateDto.setRelationshipStatus(true);
                        break;
                    }
                    case "Gender": {
                        validateDto.setGenderStatus(true);
                        break;
                    }
                    case "Age": {
                        validateDto.setAgeStatus(true);
                        break;
                    }
                    case "Patient Name": {
                        validateDto.setPatientNameStatus(true);
                        break;
                    }
                    case "Sum Insured": {
                        validateDto.setSumInsuredStatus(true);
                        break;
                    }
                    case "Claimed Amount": {
                        validateDto.setClaimedAmountStatus(true);
                        break;
                    }
                    case "Paid Amount": {
                        validateDto.setPaidAmountStatus(true);
                        break;
                    }
                    case "Outstanding Amount": {
                        validateDto.setOutstandingAmountStatus(true);
                        break;
                    }
                    case "Claim Status": {
                        validateDto.setClaimStatus(true);
                        break;
                    }
                    case "Date Of Claim": {
                        validateDto.setDateOfClaimStatus(true);
                        break;
                    }
                    case "Claim Type": {
                        validateDto.setClaimTypeStatus(true);
                        break;
                    }
                    case "Network Type": {
                        validateDto.setNetworkTypeStatus(true);
                        break;
                    }
                    case "Hospital Name": {
                        validateDto.setHospitalNameStatus(true);
                        break;
                    }
                    case "Admission Date": {
                        validateDto.setAdmissionDateStatus(true);
                        break;
                    }
                    case "Disease": {
                        validateDto.setDiseaseStatus(true);
                        break;
                    }
                    case "Date of Discharge": {
                        validateDto.setDischargeDateStatus(true);
                        break;
                    }
                    case "Member Code": {
                        validateDto.setMemberCodeStatus(true);
                        break;
                    }
                    case "Policy Start Date": {
                        validateDto.setPolicyStartDateStatus(true);
                        break;
                    }
                    case "Policy End Date": {
                        validateDto.setPolicyEndDateStatus(true);
                        break;
                    }
                    case "Hospital State": {
                        validateDto.setHospitalStateStatus(true);
                        break;
                    }
                    case "Hospital City": {
                        validateDto.setHospitalCityStatus(true);
                        break;
                    }
                }
            });

            if (tpaName.equals("MediAssit")) {
                validateDto.setNetworkTypeStatus(true);
                validateDto.setHospitalStateStatus(true);
            } else if (tpaName.equals("Medseva")) {
                validateDto.setSumInsuredStatus(true);
                validateDto.setNetworkTypeStatus(true);
            } else if (tpaName.equals("R-Care")) {
                validateDto.setNetworkTypeStatus(true);
            } else if (tpaName.equals("GHPL")) {
                validateDto.setNetworkTypeStatus(true);
                validateDto.setHospitalStateStatus(true);
            } else if (tpaName.equals("ICICI")) {
                validateDto.setAgeStatus(true);
                validateDto.setGenderStatus(true);
                validateDto.setNetworkTypeStatus(true);
                validateDto.setPolicyStartDateStatus(true);
                validateDto.setPolicyEndDateStatus(true);
                validateDto.setHospitalStateStatus(true);
                validateDto.setHospitalCityStatus(true);
            } else if (tpaName.equals("MD India")) {
                validateDto.setNetworkTypeStatus(true);
            }

            validateDto.setStatus(true);

            return validateDto;

        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ClaimsMisEntity> getAllClaimsMisByRfqId(String rfqId) {
        List<ClaimsMisEntity> claimsBasedOnRFQ = claimsMisRepo.findByRfqId(rfqId);
        return claimsBasedOnRFQ;
    }

    private Workbook getWorkbook(MultipartFile file, String tpaName) throws IOException {

        String extension = FileNameUtils.getExtension(file.getOriginalFilename());
        String fileName = file.getOriginalFilename();

        log.info("extension :: " + extension);
        log.info("fileName :: " + fileName);

        if (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".XLSX") || fileName.endsWith(".xlsb")
                || fileName.endsWith(".XLSB"))) {
            // Handle XLSX
            return new XSSFWorkbook(file.getInputStream());
        } else {
            // Handle XLS
            return new HSSFWorkbook(file.getInputStream());
        }

    }

    public void validateBasedOnSheet(Sheet sheet, String tpaName,
                                     List<ClaimsMisDataStatusValidateDto> claimsMisValidateData) {

        Iterator<Row> rowIterator = sheet.iterator();

        // Assuming the first row contains the column names
        Row headerRow = rowIterator.next();
        int snoColumnIndex = -1;
        int policyNumberColumnIndex = -1;
        int claimsNumberColumnIndex = -1;
        int employeeIdColumnIndex = -1;
        int employeeNameColumnIndex = -1;
        int relationshipColumnIndex = -1;
        int genderColumnIndex = -1;
        int ageColumnIndex = -1;
        int patientNameColumnIndex = -1;
        int sumInsuredColumnIndex = -1;
        int claimedAmountColumnIndex = -1;
        int paidAmountColumnIndex = -1;
        int outstandingAmountColumnIndex = -1;
        int claimStatusColumnIndex = -1;
        int dateOfClaimColumnIndex = -1;
        int claimTypeColumnIndex = -1;
        int networkTypeColumnIndex = -1;
        int hospitalNameColumnIndex = -1;
        int admissionDateColumnIndex = -1;
        int dischargeDateColumnIndex = -1;
        int diseaseColumnIndex = -1;
        int memberCodeColumnIndex = -1;
        int policyStartDateColumnIndex = -1;
        int policyEndDateColumnIndex = -1;
        int hospitalStateColumnIndex = -1;
        int hospitalCityColumnIndex = -1;

        boolean snoColumnIndexFlag = false;
        boolean policyNumberColumnIndexFlag = false;
        boolean claimsNumberColumnIndexFlag = false;
        boolean employeeIdColumnIndexFlag = false;
        boolean employeeNameColumnIndexFlag = false;
        boolean relationshipColumnIndexFlag = false;
        boolean genderColumnIndexFlag = false;
        boolean ageColumnIndexFlag = false;
        boolean patientNameColumnIndexFlag = false;
        boolean sumInsuredColumnIndexFlag = false;
        boolean claimedAmountColumnIndexFlag = false;
        boolean paidAmountColumnIndexFlag = false;
        boolean outstandingColumnIndexFlag = false;
        boolean claimStatusColumnIndexFlag = false;
        boolean dateOfClaimColumnIndexFlag = false;
        boolean claimTypeColumnIndexFlag = false;
        boolean networkTypeColumnIndexFlag = false;
        boolean hospitalNameColumnIndexFlag = false;
        boolean admissionDateColumnIndexFlag = false;
        boolean dischargeDateColumnIndexFlag = false;
        boolean diseaseColumnIndexFlag = false;
        boolean memberCodeColumnIndexFlag = false;
        boolean policyStartDateColumnIndexFlag = false;
        boolean policyEndDateColumnIndexFlag = false;
        boolean hospitalStateColumnIndexFlag = false;
        boolean hospitalCityColumnIndexFlag = false;

        Tpa tpa_data = tpaRepo.findByTpaName(tpaName);
        List<ClaimsTPAHeaders> tpaHeadersBasedOnTPA = tpa_data.getTpaHeaders();

        Iterator<Cell> cellIterator = headerRow.cellIterator();
        int columnIndex = 0;
        while (cellIterator.hasNext()) {

            Cell cell = cellIterator.next();
            String columnName = cell.getStringCellValue().trim();

            for (ClaimsTPAHeaders i : tpaHeadersBasedOnTPA) {
                if (columnName.trim().equals(i.getHeaderName().trim()) && i.getHeaderAliasName().trim().equals("SNO")) {
                    snoColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Policy Number")) {
                    policyNumberColumnIndex = columnIndex;
                    log.info("policyNumberColumnIndex : " + policyNumberColumnIndex);
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Claim Number")) {
                    claimsNumberColumnIndex = columnIndex;
                    log.info("claimsNumberColumnIndex : " + claimsNumberColumnIndex);
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Employee Id")) {
                    log.info("employeeIdColumnIndex : " + employeeIdColumnIndex);
                    employeeIdColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Employee Name")) {
                    employeeNameColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Gender")) {
                    genderColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("RelationShip")) {
                    relationshipColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Age")) {
                    ageColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Patient Name")) {
                    log.info("patientNameColumnIndex : " + patientNameColumnIndex);
                    patientNameColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Sum Insured")) {
                    sumInsuredColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Claimed Amount")) {
                    claimedAmountColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Paid Amount")) {
                    log.info("paidAmountColumnIndex : " + columnIndex);
                    paidAmountColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Outstanding Amount")) {
                    outstandingAmountColumnIndex = columnIndex;
                    log.info("outStandingAmount :" + outstandingAmountColumnIndex);
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Claim Status")) {
                    claimStatusColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Date Of Claim")) {
                    dateOfClaimColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Hospital Name")) {
                    hospitalNameColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Network Type")) {
                    networkTypeColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Claim Type")) {
                    claimTypeColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Admission Date")) {
                    admissionDateColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Disease")) {
                    diseaseColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Date of Discharge")) {
                    dischargeDateColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Member Code")) {
                    memberCodeColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Policy Start Date")) {
                    policyStartDateColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Policy End Date")) {
                    policyEndDateColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Hospital State")) {
                    hospitalStateColumnIndex = columnIndex;
                } else if (columnName.trim().equals(i.getHeaderName().trim())
                        && i.getHeaderAliasName().trim().equals("Hospital City")) {
                    hospitalCityColumnIndex = columnIndex;
                }
            }
            columnIndex++;
        }

        // while loop used to handle any cell having null value,not to throw
        // NullPointerException
        while (rowIterator.hasNext()) {
            Row dataRow = rowIterator.next();
            if (policyNumberColumnIndex >= 0 && claimsNumberColumnIndex >= 0 && employeeIdColumnIndex >= 0
                    && employeeNameColumnIndex >= 0 && relationshipColumnIndex >= 0 && genderColumnIndex >= 0
                    && ageColumnIndex >= 0 && patientNameColumnIndex >= 0 && sumInsuredColumnIndex >= 0
                    && claimedAmountColumnIndex >= 0 && paidAmountColumnIndex >= 0 && claimTypeColumnIndex >= 0
                    && outstandingAmountColumnIndex >= 0 && claimStatusColumnIndex >= 0 && dateOfClaimColumnIndex >= 0
                    && networkTypeColumnIndex >= 0 && hospitalNameColumnIndex >= 0 && admissionDateColumnIndex >= 0
                    && diseaseColumnIndex >= 0 && dischargeDateColumnIndex >= 0 && memberCodeColumnIndex >= 0
                    && policyStartDateColumnIndex >= 0 && policyEndDateColumnIndex >= 0 && hospitalStateColumnIndex >= 0
                    && hospitalCityColumnIndex >= 0) {
                if (dataRow.getCell(policyNumberColumnIndex) != null && dataRow.getCell(claimsNumberColumnIndex) != null
                        && dataRow.getCell(employeeIdColumnIndex) != null
                        && dataRow.getCell(employeeNameColumnIndex) != null
                        && dataRow.getCell(relationshipColumnIndex) != null
                        && dataRow.getCell(genderColumnIndex) != null && dataRow.getCell(ageColumnIndex) != null
                        && dataRow.getCell(patientNameColumnIndex) != null
                        && dataRow.getCell(sumInsuredColumnIndex) != null
                        && dataRow.getCell(claimedAmountColumnIndex) != null
                        && dataRow.getCell(paidAmountColumnIndex) != null
                        && dataRow.getCell(outstandingAmountColumnIndex) != null
                        && dataRow.getCell(claimStatusColumnIndex) != null
                        && dataRow.getCell(dateOfClaimColumnIndex) != null
                        && dataRow.getCell(claimTypeColumnIndex) != null
                        && dataRow.getCell(networkTypeColumnIndex) != null
                        && dataRow.getCell(hospitalNameColumnIndex) != null
                        && dataRow.getCell(admissionDateColumnIndex) != null
                        && dataRow.getCell(dischargeDateColumnIndex) != null
                        && dataRow.getCell(diseaseColumnIndex) != null && dataRow.getCell(memberCodeColumnIndex) != null
                        && dataRow.getCell(policyStartDateColumnIndex) != null
                        && dataRow.getCell(policyEndDateColumnIndex) != null
                        && dataRow.getCell(hospitalStateColumnIndex) != null
                        && dataRow.getCell(hospitalCityColumnIndex) != null) {
                    if (dataRow.getCell(policyNumberColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(claimsNumberColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(employeeIdColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(employeeNameColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(relationshipColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(genderColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(ageColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(patientNameColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(sumInsuredColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(claimedAmountColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(paidAmountColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(outstandingAmountColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(claimStatusColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(dateOfClaimColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(claimTypeColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(networkTypeColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(hospitalNameColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(admissionDateColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(dischargeDateColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(diseaseColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(memberCodeColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(policyStartDateColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(policyEndDateColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(hospitalStateColumnIndex).getCellType() == CellType.BLANK
                            && dataRow.getCell(hospitalCityColumnIndex).getCellType() == CellType.BLANK) {
                        continue;
                    }
                }

            }

            String remarks;

            int policyNumberInteger, claimsNumberInteger, employeeIdInteger, memberCodeInteger;
            ClaimsMisDataStatusValidateDto validateDto = new ClaimsMisDataStatusValidateDto();
            if (dataRow.getCell(policyNumberColumnIndex) != null) {
                Cell policyNumberCell = dataRow.getCell(policyNumberColumnIndex);
                if (policyNumberCell.getCellType() == CellType.NUMERIC) {
                    policyNumberInteger = (int) policyNumberCell.getNumericCellValue();
                    validateDto.setPolicyNumber(String.valueOf(policyNumberInteger).trim());
                    validateDto.setPolicyNumberStatus(true);
                } else if (policyNumberCell.getCellType() == CellType.STRING) {
                    String policyNumber = policyNumberCell.getStringCellValue().trim();
                    validateDto.setPolicyNumber(policyNumber);
                    validateDto.setPolicyNumberStatus(true);
                } else if (policyNumberCell.getCellType() == CellType._NONE) {
                    policyNumberColumnIndexFlag = true;
                    validateDto.setPolicyNumberStatus(false);
                    validateDto.setPolicyNumberErrorMessage("Policy Number None :: ");
                    remarks = "No Policy Number";
                } else if (policyNumberCell.getCellType() == CellType.BLANK) {
                    policyNumberColumnIndexFlag = true;
                    validateDto.setPolicyNumberStatus(false);
                    validateDto.setPolicyNumberErrorMessage("Policy Number Blank :: ");
                    remarks = "Blank Policy Number";
                } else if (policyNumberCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = policyNumberCell.getErrorCellValue();
                    validateDto.setPolicyNumber(String.valueOf(errorCellValue).trim());
                    validateDto.setPolicyNumberStatus(false);
                    validateDto.setPolicyNumberErrorMessage("Policy Number Error :: ");
                    remarks = "Error Policy Number";
                } else if (policyNumberCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = policyNumberCell.getBooleanCellValue();
                    validateDto.setPolicyNumber(String.valueOf(booleanCellValue).trim());
                    validateDto.setPolicyNumberStatus(false);
                    validateDto.setPolicyNumberErrorMessage("Policy Number Boolean :: ");
                } else if (policyNumberCell.getCellType() == CellType.FORMULA) {
                    switch (policyNumberCell.getCellType()) {
                        case NUMERIC: {
                            policyNumberInteger = (int) policyNumberCell.getNumericCellValue();
                            validateDto.setPolicyNumber(String.valueOf(policyNumberInteger).trim());
                            validateDto.setPolicyNumberStatus(true);
                            break;
                        }
                        case STRING: {
                            String policyNumber = policyNumberCell.getStringCellValue().trim();
                            validateDto.setPolicyNumber(policyNumber);
                            validateDto.setPolicyNumberStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = policyNumberCell.getBooleanCellValue();
                            validateDto.setPolicyNumber(String.valueOf(booleanCellValue).trim());
                            validateDto.setPolicyNumberStatus(false);
                            validateDto.setPolicyNumberErrorMessage("Policy Number Boolean :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = policyNumberCell.getErrorCellValue();
                            validateDto.setPolicyNumber(String.valueOf(errorCellValue).trim());
                            validateDto.setPolicyNumberStatus(false);
                            validateDto.setPolicyNumberErrorMessage("Policy Number Error :: ");
                            remarks = "Error Policy Number";
                            break;
                        }
                        case _NONE: {
                            policyNumberColumnIndexFlag = true;
                            validateDto.setPolicyNumberStatus(false);
                            validateDto.setPolicyNumberErrorMessage("Policy Number None :: ");
                            remarks = "No Policy Number";
                            break;
                        }
                        case BLANK: {
                            policyNumberColumnIndexFlag = true;
                            validateDto.setPolicyNumberStatus(false);
                            validateDto.setPolicyNumberErrorMessage("Policy Number Blank :: ");
                            remarks = "Blank Policy Number";
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + policyNumberCell.getCachedFormulaResultType());
                    }
                }
            }

            if (dataRow.getCell(claimsNumberColumnIndex) != null) {
                Cell claimsNumberCell = dataRow.getCell(claimsNumberColumnIndex);
                if (claimsNumberCell.getCellType() == CellType.NUMERIC) {
                    claimsNumberInteger = (int) claimsNumberCell.getNumericCellValue();
                    validateDto.setClaimsNumber(String.valueOf(claimsNumberInteger).trim());
                    validateDto.setClaimsNumberStatus(true);
                } else if (claimsNumberCell.getCellType() == CellType.STRING) {
                    String claimsNumber = claimsNumberCell.getStringCellValue().trim();
                    validateDto.setClaimsNumber(claimsNumber);
                    validateDto.setClaimsNumberStatus(true);
                } else if (claimsNumberCell.getCellType() == CellType._NONE) {
                    claimsNumberColumnIndexFlag = true;
                    validateDto.setClaimsNumberStatus(false);
                    validateDto.setClaimsNumberErrorMessage("Claims Number None :: ");
                } else if (claimsNumberCell.getCellType() == CellType.BLANK) {
                    claimsNumberColumnIndexFlag = true;
                    validateDto.setClaimsNumberStatus(false);
                    validateDto.setClaimsNumberErrorMessage("Claims Number Blank :: ");
                } else if (claimsNumberCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = claimsNumberCell.getErrorCellValue();
                    validateDto.setClaimsNumber(String.valueOf(errorCellValue).trim());
                    validateDto.setClaimsNumberStatus(false);
                    validateDto.setClaimsNumberErrorMessage("Claims Number Error :: ");
                } else if (claimsNumberCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = claimsNumberCell.getBooleanCellValue();
                    validateDto.setClaimsNumber(String.valueOf(booleanCellValue).trim());
                    validateDto.setClaimsNumberStatus(false);
                    validateDto.setClaimsNumberErrorMessage("Claims Number Boolean :: ");
                } else if (claimsNumberCell.getCellType() == CellType.FORMULA) {
                    switch (claimsNumberCell.getCellType()) {
                        case NUMERIC: {
                            claimsNumberInteger = (int) claimsNumberCell.getNumericCellValue();
                            validateDto.setClaimsNumber(String.valueOf(claimsNumberInteger).trim());
                            validateDto.setClaimsNumberStatus(true);
                            break;
                        }
                        case STRING: {
                            String claimsNumber = claimsNumberCell.getStringCellValue().trim();
                            validateDto.setClaimsNumber(claimsNumber);
                            validateDto.setClaimsNumberStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = claimsNumberCell.getBooleanCellValue();
                            validateDto.setClaimsNumber(String.valueOf(booleanCellValue).trim());
                            validateDto.setClaimsNumberStatus(false);
                            validateDto.setClaimsNumberErrorMessage("Claims Number Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = claimsNumberCell.getErrorCellValue();
                            validateDto.setClaimsNumber(String.valueOf(errorCellValue).trim());
                            validateDto.setClaimsNumberStatus(false);
                            validateDto.setClaimsNumberErrorMessage("Claims Number Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            claimsNumberColumnIndexFlag = true;
                            validateDto.setClaimsNumberStatus(false);
                            validateDto.setClaimsNumberErrorMessage("Claims Number None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            claimsNumberColumnIndexFlag = true;
                            validateDto.setClaimsNumberStatus(false);
                            validateDto.setClaimsNumberErrorMessage("Claims Number Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + claimsNumberCell.getCachedFormulaResultType());
                    }
                }
            }

            if (dataRow.getCell(employeeIdColumnIndex) != null) {
                Cell employeeIdCell = dataRow.getCell(employeeIdColumnIndex);
                if (employeeIdCell.getCellType() == CellType.NUMERIC) {
                    employeeIdInteger = (int) employeeIdCell.getNumericCellValue();
                    validateDto.setEmployeeId(String.valueOf(employeeIdInteger).trim());
                    validateDto.setEmployeeIdStatus(true);
                } else if (employeeIdCell.getCellType() == CellType.STRING) {
                    String employeeId = employeeIdCell.getStringCellValue().trim();
                    validateDto.setEmployeeId(employeeId);
                    validateDto.setEmployeeIdStatus(true);
                } else if (employeeIdCell.getCellType() == CellType._NONE) {
                    employeeIdColumnIndexFlag = true;
                    validateDto.setEmployeeIdStatus(false);
                    validateDto.setEmployeeIdErrorMessage("Employee Id None :: ");
                } else if (employeeIdCell.getCellType() == CellType.BLANK) {
                    employeeIdColumnIndexFlag = true;
                    validateDto.setEmployeeIdStatus(false);
                    validateDto.setEmployeeIdErrorMessage("Employee Id Blank :: ");
                } else if (employeeIdCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = employeeIdCell.getErrorCellValue();
                    validateDto.setEmployeeId(String.valueOf(errorCellValue).trim());
                    validateDto.setEmployeeIdStatus(false);
                    validateDto.setEmployeeIdErrorMessage("Employee Id Error :: ");
                } else if (employeeIdCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = employeeIdCell.getBooleanCellValue();
                    validateDto.setEmployeeId(String.valueOf(booleanCellValue).trim());
                    validateDto.setEmployeeIdStatus(false);
                    validateDto.setEmployeeIdErrorMessage("Employee Id Boolean :: ");
                } else if (employeeIdCell.getCellType() == CellType.FORMULA) {
                    switch (employeeIdCell.getCellType()) {
                        case NUMERIC: {
                            employeeIdInteger = (int) employeeIdCell.getNumericCellValue();
                            validateDto.setEmployeeId(String.valueOf(employeeIdInteger).trim());
                            validateDto.setEmployeeIdStatus(true);
                            break;
                        }
                        case STRING: {
                            String employeeId = employeeIdCell.getStringCellValue().trim();
                            validateDto.setEmployeeId(employeeId);
                            validateDto.setEmployeeIdStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = employeeIdCell.getBooleanCellValue();
                            validateDto.setEmployeeId(String.valueOf(booleanCellValue).trim());
                            validateDto.setEmployeeIdStatus(false);
                            validateDto.setEmployeeIdErrorMessage("Employee Id Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = employeeIdCell.getErrorCellValue();
                            validateDto.setEmployeeId(String.valueOf(errorCellValue).trim());
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

            if (dataRow.getCell(memberCodeColumnIndex) != null) {
                Cell memberCodeCell = dataRow.getCell(memberCodeColumnIndex);
                if (memberCodeCell.getCellType() == CellType.NUMERIC) {
                    memberCodeInteger = (int) memberCodeCell.getNumericCellValue();
                    validateDto.setMemberCode(String.valueOf(memberCodeInteger).trim());
                    validateDto.setMemberCodeStatus(true);
                } else if (memberCodeCell.getCellType() == CellType.STRING) {
                    String memberCode = memberCodeCell.getStringCellValue().trim();
                    validateDto.setMemberCode(memberCode);
                    validateDto.setMemberCodeStatus(true);
                } else if (memberCodeCell.getCellType() == CellType._NONE) {
                    memberCodeColumnIndexFlag = true;
                    validateDto.setMemberCodeStatus(false);
                    validateDto.setMemberCodeErrorMessage("Member Code None :: ");
                } else if (memberCodeCell.getCellType() == CellType.BLANK) {
                    memberCodeColumnIndexFlag = true;
                    validateDto.setMemberCodeStatus(false);
                    validateDto.setMemberCodeErrorMessage("Member Code Blank :: ");
                } else if (memberCodeCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = memberCodeCell.getErrorCellValue();
                    validateDto.setMemberCode(String.valueOf(errorCellValue).trim());
                    validateDto.setMemberCodeStatus(false);
                    validateDto.setMemberCodeErrorMessage("Member Code Error :: ");
                } else if (memberCodeCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = memberCodeCell.getBooleanCellValue();
                    validateDto.setMemberCode(String.valueOf(booleanCellValue).trim());
                    validateDto.setMemberCodeStatus(false);
                    validateDto.setMemberCodeErrorMessage("Member Code Boolean :: ");
                } else if (memberCodeCell.getCellType() == CellType.FORMULA) {
                    switch (memberCodeCell.getCellType()) {
                        case NUMERIC: {
                            memberCodeInteger = (int) memberCodeCell.getNumericCellValue();
                            validateDto.setMemberCode(String.valueOf(memberCodeInteger).trim());
                            validateDto.setMemberCodeStatus(true);
                            break;
                        }
                        case STRING: {
                            String memberCode = memberCodeCell.getStringCellValue().trim();
                            validateDto.setMemberCode(memberCode);
                            validateDto.setMemberCodeStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = memberCodeCell.getBooleanCellValue();
                            validateDto.setMemberCode(String.valueOf(booleanCellValue).trim());
                            validateDto.setMemberCodeStatus(false);
                            validateDto.setMemberCodeErrorMessage("Member Code Boolean :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = memberCodeCell.getErrorCellValue();
                            validateDto.setMemberCode(String.valueOf(errorCellValue).trim());
                            validateDto.setMemberCodeStatus(false);
                            validateDto.setMemberCodeErrorMessage("Member Code Error :: ");
                            break;
                        }
                        case _NONE: {
                            memberCodeColumnIndexFlag = true;
                            validateDto.setMemberCodeStatus(false);
                            validateDto.setMemberCodeErrorMessage("Member Code None :: ");
                            break;
                        }
                        case BLANK: {
                            memberCodeColumnIndexFlag = true;
                            validateDto.setMemberCodeStatus(false);
                            validateDto.setMemberCodeErrorMessage("Member Code Blank :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + memberCodeCell.getCachedFormulaResultType());
                    }
                }
            }

            String employeeName;
            String gender;
            String relationship;
            String patientName;
            String claimType;
            String networkType;
            String hospitalName;
            String disease;
            String hospitalState;
            String hospitalCity;

            if (dataRow.getCell(employeeNameColumnIndex) != null) {
                Cell employeeNameCell = dataRow.getCell(employeeNameColumnIndex);
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

            if (!tpaName.equals("ICICI")) {
                String[] genderArr = {"M", "F", "Male", "Female", "Trans", "TransGender"};
                if (dataRow.getCell(genderColumnIndex) != null) {
                    Cell genderCell = dataRow.getCell(genderColumnIndex);
                    if (genderCell.getCellType() == CellType.NUMERIC) {
                        gender = String.valueOf(genderCell.getNumericCellValue()).trim();
                        validateDto.setGenderStatus(false);
                        validateDto.setGender(gender);
                    } else if (genderCell.getCellType() == CellType.STRING) {
                        gender = genderCell.getStringCellValue().trim().replaceAll("\\p{C}", "").replaceAll("\\s", "")
                                .replaceAll("[^\\p{Print}]", "").trim();
                        if (Arrays.asList(genderArr).stream().map(String::toLowerCase).toList()
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
                                gender = genderCell.getStringCellValue().trim().replaceAll("\\p{C}", "")
                                        .replaceAll("\\s", "").replaceAll("[^\\p{Print}]", "").trim();
                                if (Arrays.asList(genderArr).stream().map(String::toLowerCase).toList()
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

                String ageValue;
                if (dataRow.getCell(ageColumnIndex) != null) {
                    Cell ageCell = dataRow.getCell(ageColumnIndex);
                    if (ageCell.getCellType() == CellType.NUMERIC) {
                        double numericCellValue = ageCell.getNumericCellValue();
                        DecimalFormat df = new DecimalFormat("#");
                        String stringValue = df.format(numericCellValue).trim();
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
                                throw new IllegalArgumentException(
                                        "Unexpected value: " + ageCell.getCachedFormulaResultType());
                        }
                    }
                }
            }

            if (!tpaName.equals("ICICI") || !tpaName.equals("GHPL") || !tpaName.equals("RCare")
                    || !tpaName.equals("Medseva") || !tpaName.equals("MediAssist")) {
                if (dataRow.getCell(networkTypeColumnIndex) != null) {
                    Cell networkTypeCell = dataRow.getCell(networkTypeColumnIndex);

                    if (networkTypeCell.getCellType() == CellType.NUMERIC) {
                        networkType = String.valueOf(networkTypeCell.getNumericCellValue()).trim();
                        validateDto.setNetworkTypeStatus(false);
                        validateDto.setNetworkType(networkType);
                    } else if (networkTypeCell.getCellType() == CellType.STRING) {
                        networkType = networkTypeCell.getStringCellValue().trim();
                        validateDto.setNetworkTypeStatus(true);
                        validateDto.setNetworkType(networkType);
                    } else if (networkTypeCell.getCellType() == CellType._NONE) {
                        validateDto.setNetworkTypeStatus(true);
                    } else if (networkTypeCell.getCellType() == CellType.BLANK) {
                        validateDto.setNetworkTypeStatus(true);
                    } else if (networkTypeCell.getCellType() == CellType.ERROR) {
                        byte errorCellValue = networkTypeCell.getErrorCellValue();
                        validateDto.setNetworkType(String.valueOf(errorCellValue).trim());
                        validateDto.setNetworkTypeStatus(true);
                    } else if (networkTypeCell.getCellType() == CellType.BOOLEAN) {
                        boolean booleanCellValue = networkTypeCell.getBooleanCellValue();
                        validateDto.setNetworkType(String.valueOf(booleanCellValue).trim());
                        validateDto.setNetworkTypeStatus(true);
                    } else if (networkTypeCell.getCellType() == CellType.FORMULA) {
                        switch (networkTypeCell.getCellType()) {
                            case NUMERIC: {
                                networkType = String.valueOf(networkTypeCell.getNumericCellValue()).trim();
                                validateDto.setNetworkTypeStatus(true);
                                validateDto.setNetworkType(networkType);
                                break;
                            }
                            case STRING: {
                                networkType = networkTypeCell.getStringCellValue().trim();
                                validateDto.setNetworkTypeStatus(true);
                                validateDto.setNetworkType(networkType);
                                break;
                            }
                            case BOOLEAN: {
                                boolean booleanCellValue = networkTypeCell.getBooleanCellValue();
                                validateDto.setNetworkType(String.valueOf(booleanCellValue).trim());
                                validateDto.setNetworkTypeStatus(true);
                                break;
                            }
                            case ERROR: {
                                byte errorCellValue = networkTypeCell.getErrorCellValue();
                                validateDto.setNetworkType(String.valueOf(errorCellValue).trim());
                                validateDto.setNetworkTypeStatus(true);
                                break;
                            }
                            case _NONE: {
                                validateDto.setNetworkTypeStatus(true);
                                break;
                            }
                            case BLANK: {
                                validateDto.setNetworkTypeStatus(true);
                                break;
                            }
                            default:
                                throw new IllegalArgumentException(
                                        "Unexpected value: " + networkTypeCell.getCachedFormulaResultType());
                        }
                    }
                }
            }

            if (!tpaName.equals("Medseva")) {
                String sumInsuredValue;
                if (dataRow.getCell(sumInsuredColumnIndex) != null) {
                    Cell sumInsuredCell = dataRow.getCell(sumInsuredColumnIndex);
                    if (sumInsuredCell.getCellType() == CellType.NUMERIC) {
                        double numericCellValue = sumInsuredCell.getNumericCellValue();
                        DecimalFormat df = new DecimalFormat("#");
                        String stringValue = df.format(numericCellValue).trim();
                        validateDto.setSumInsuredStatus(true);
                        validateDto.setSumInsured(stringValue);
                    } else if (sumInsuredCell.getCellType() == CellType.STRING) {
                        String sumInsuredStr = sumInsuredCell.getStringCellValue().replaceAll(",", "");
                        try {
                            double sumInsuredDouble = Double.parseDouble(sumInsuredStr);
                            DecimalFormat df = new DecimalFormat("#");
                            String stringValue = df.format(sumInsuredDouble).trim();
                            validateDto.setSumInsuredStatus(true);
                            validateDto.setSumInsured(stringValue);
                        } catch (NumberFormatException e) {
                            // Handle parsing exception
                            validateDto.setSumInsuredStatus(false);
                            validateDto.setSumInsuredErrorMessage("Error parsing sum insured value: " + e.getMessage());
                        }

                    } else if (sumInsuredCell.getCellType() == CellType._NONE) {
                        validateDto.setSumInsuredStatus(true);
                        validateDto.setSumInsured("0");
                    } else if (sumInsuredCell.getCellType() == CellType.BLANK) {
                        validateDto.setSumInsuredStatus(true);
                        validateDto.setSumInsured("0");
                    } else if (sumInsuredCell.getCellType() == CellType.ERROR) {
                        byte errorCellValue = sumInsuredCell.getErrorCellValue();
                        validateDto.setSumInsured(String.valueOf(errorCellValue).trim());
                        validateDto.setSumInsuredStatus(true);
                    } else if (sumInsuredCell.getCellType() == CellType.BOOLEAN) {
                        boolean booleanCellValue = sumInsuredCell.getBooleanCellValue();
                        validateDto.setSumInsured(String.valueOf(booleanCellValue).trim());
                        validateDto.setSumInsuredStatus(true);
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
                                validateDto.setSumInsuredStatus(true);
                                validateDto.setSumInsured(stringValue);
                            } catch (NumberFormatException e) {
                                // Handle parsing exception
                                validateDto.setSumInsuredStatus(false);
                                validateDto.setSumInsuredErrorMessage(
                                        "Error parsing sum insured value: " + e.getMessage());
                            }
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
                            validateDto.setSumInsuredStatus(true);
                        } else if (sumInsuredCell.getCachedFormulaResultType() == CellType.BLANK) {
                            validateDto.setSumInsuredStatus(true);
                        } else {
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + sumInsuredCell.getCachedFormulaResultType());
                        }
                    }
                }
            }

            if (dataRow.getCell(relationshipColumnIndex) != null) {
                Cell relationshipCell = dataRow.getCell(relationshipColumnIndex);
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

            if (dataRow.getCell(patientNameColumnIndex) != null) {
                Cell patientNameCell = dataRow.getCell(patientNameColumnIndex);
                if (patientNameCell.getCellType() == CellType.NUMERIC) {
                    patientName = String.valueOf(patientNameCell.getNumericCellValue()).trim();
                    validateDto.setPatientNameStatus(false);
                    validateDto.setPatientName(patientName);
                } else if (patientNameCell.getCellType() == CellType.STRING) {
                    patientName = patientNameCell.getStringCellValue().trim();
                    validateDto.setPatientNameStatus(true);
                    validateDto.setPatientName(patientName);
                } else if (patientNameCell.getCellType() == CellType._NONE) {
                    patientNameColumnIndexFlag = true;
                    validateDto.setPatientNameStatus(false);
                    validateDto.setPatientNameErrorMessage("PatientName None :: ");
                } else if (patientNameCell.getCellType() == CellType.BLANK) {
                    patientNameColumnIndexFlag = true;
                    validateDto.setPatientNameStatus(false);
                    validateDto.setPatientNameErrorMessage("PatientName Blank :: ");
                } else if (patientNameCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = patientNameCell.getErrorCellValue();
                    validateDto.setPatientName(String.valueOf(errorCellValue).trim());
                    validateDto.setPatientNameStatus(false);
                    validateDto.setPatientNameErrorMessage("PatientName Error :: ");
                } else if (patientNameCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = patientNameCell.getBooleanCellValue();
                    validateDto.setPatientName(String.valueOf(booleanCellValue).trim());
                    validateDto.setPatientNameStatus(false);
                    validateDto.setPatientNameErrorMessage("PatientName Boolean :: ");
                } else if (patientNameCell.getCellType() == CellType.FORMULA) {
                    switch (patientNameCell.getCellType()) {
                        case NUMERIC: {
                            patientName = String.valueOf(patientNameCell.getNumericCellValue()).trim();
                            validateDto.setPatientNameStatus(false);
                            validateDto.setPatientName(patientName);
                            break;
                        }
                        case STRING: {
                            patientName = patientNameCell.getStringCellValue().trim();
                            validateDto.setPatientNameStatus(true);
                            validateDto.setPatientName(patientName);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = patientNameCell.getBooleanCellValue();
                            validateDto.setPatientName(String.valueOf(booleanCellValue).trim());
                            validateDto.setPatientNameStatus(false);
                            validateDto.setPatientNameErrorMessage("PatientName Boolean Formula:: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = patientNameCell.getErrorCellValue();
                            validateDto.setPatientName(String.valueOf(errorCellValue).trim());
                            validateDto.setPatientNameStatus(false);
                            validateDto.setPatientNameErrorMessage("PatientName Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            patientNameColumnIndexFlag = true;
                            validateDto.setPatientNameStatus(false);
                            validateDto.setPatientNameErrorMessage("PatientName None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            patientNameColumnIndexFlag = true;
                            validateDto.setPatientNameStatus(false);
                            validateDto.setPatientNameErrorMessage("PatientName Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + patientNameCell.getCachedFormulaResultType());
                    }
                }

            }

            String claimStatus = null;
            if (dataRow.getCell(claimStatusColumnIndex) != null) {
                Cell claimStatusCell = dataRow.getCell(claimStatusColumnIndex);
                if (claimStatusCell.getCellType() == CellType.NUMERIC) {
                    claimStatus = String.valueOf(claimStatusCell.getNumericCellValue()).trim();
                    validateDto.setClaimStatus(false);
                    validateDto.setClaimStatusValue(claimStatus);
                } else if (claimStatusCell.getCellType() == CellType.STRING) {
                    claimStatus = claimStatusCell.getStringCellValue().trim();
                    validateDto.setClaimStatus(true);
                    validateDto.setClaimStatusValue(claimStatus);
                } else if (claimStatusCell.getCellType() == CellType._NONE) {
                    claimStatusColumnIndexFlag = true;
                    validateDto.setClaimStatus(false);
                    validateDto.setClaimStatusErrorMessage("Claim Type None :: ");
                } else if (claimStatusCell.getCellType() == CellType.BLANK) {
                    claimStatusColumnIndexFlag = true;
                    validateDto.setClaimStatus(false);
                    validateDto.setClaimStatusErrorMessage("Claim Type Blank :: ");
                } else if (claimStatusCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = claimStatusCell.getErrorCellValue();
                    validateDto.setClaimStatusValue(String.valueOf(errorCellValue).trim());
                    validateDto.setClaimStatus(false);
                    validateDto.setClaimStatusErrorMessage("Claim Type Error :: ");
                } else if (claimStatusCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = claimStatusCell.getBooleanCellValue();
                    validateDto.setClaimStatusValue(String.valueOf(booleanCellValue).trim());
                    validateDto.setClaimStatus(false);
                    validateDto.setClaimStatusErrorMessage("Claim Type Boolean :: ");
                } else if (claimStatusCell.getCellType() == CellType.FORMULA) {
                    switch (claimStatusCell.getCellType()) {
                        case NUMERIC: {
                            claimStatus = String.valueOf(claimStatusCell.getNumericCellValue()).trim();
                            validateDto.setClaimStatus(false);
                            validateDto.setClaimStatusValue(claimStatus);
                            break;
                        }
                        case STRING: {
                            claimStatus = claimStatusCell.getStringCellValue().trim();
                            validateDto.setClaimStatus(true);
                            validateDto.setClaimStatusValue(claimStatus);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = claimStatusCell.getBooleanCellValue();
                            validateDto.setClaimStatusValue(String.valueOf(booleanCellValue).trim());
                            validateDto.setClaimStatus(false);
                            validateDto.setClaimStatusErrorMessage("Claim Type Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = claimStatusCell.getErrorCellValue();
                            validateDto.setClaimStatusValue(String.valueOf(errorCellValue).trim());
                            validateDto.setClaimStatus(false);
                            validateDto.setClaimStatusErrorMessage("Claim Type Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            claimStatusColumnIndexFlag = true;
                            validateDto.setClaimStatus(false);
                            validateDto.setClaimStatusErrorMessage("Claim Type None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            claimStatusColumnIndexFlag = true;
                            validateDto.setClaimStatus(false);
                            validateDto.setClaimStatusErrorMessage("Claim Type Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + claimStatusCell.getCachedFormulaResultType());
                    }
                }
            }

            if (dataRow.getCell(claimTypeColumnIndex) != null) {
                Cell claimTypeCell = dataRow.getCell(claimTypeColumnIndex);
                if (claimTypeCell.getCellType() == CellType.NUMERIC) {
                    claimType = String.valueOf(claimTypeCell.getNumericCellValue()).trim();
                    validateDto.setClaimTypeStatus(false);
                    validateDto.setClaimType(claimType);
                } else if (claimTypeCell.getCellType() == CellType.STRING) {
                    claimType = claimTypeCell.getStringCellValue().trim();
                    validateDto.setClaimTypeStatus(true);
                    validateDto.setClaimType(claimType);
                } else if (claimTypeCell.getCellType() == CellType._NONE) {
                    claimTypeColumnIndexFlag = true;
                    validateDto.setClaimTypeStatus(false);
                    validateDto.setClaimTypeErrorMessage("Claim Type None :: ");
                } else if (claimTypeCell.getCellType() == CellType.BLANK) {
                    claimTypeColumnIndexFlag = true;
                    validateDto.setClaimTypeStatus(false);
                    validateDto.setClaimTypeErrorMessage("Claim Type Blank :: ");
                } else if (claimTypeCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = claimTypeCell.getErrorCellValue();
                    validateDto.setClaimType(String.valueOf(errorCellValue).trim());
                    validateDto.setClaimTypeStatus(false);
                    validateDto.setClaimTypeErrorMessage("Claim Type Error :: ");
                } else if (claimTypeCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = claimTypeCell.getBooleanCellValue();
                    validateDto.setClaimType(String.valueOf(booleanCellValue).trim());
                    validateDto.setClaimTypeStatus(false);
                    validateDto.setClaimTypeErrorMessage("Claim Type Boolean :: ");
                } else if (claimTypeCell.getCellType() == CellType.FORMULA) {
                    switch (claimTypeCell.getCellType()) {
                        case NUMERIC: {
                            claimType = String.valueOf(claimTypeCell.getNumericCellValue()).trim();
                            validateDto.setClaimTypeStatus(false);
                            validateDto.setClaimType(claimType);
                            break;
                        }
                        case STRING: {
                            claimType = claimTypeCell.getStringCellValue().trim();
                            validateDto.setClaimTypeStatus(true);
                            validateDto.setClaimType(claimType);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = claimTypeCell.getBooleanCellValue();
                            validateDto.setClaimType(String.valueOf(booleanCellValue).trim());
                            validateDto.setClaimTypeStatus(false);
                            validateDto.setClaimTypeErrorMessage("Claim Type Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = claimTypeCell.getErrorCellValue();
                            validateDto.setClaimType(String.valueOf(errorCellValue).trim());
                            validateDto.setClaimTypeStatus(false);
                            validateDto.setClaimTypeErrorMessage("Claim Type Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            claimTypeColumnIndexFlag = true;
                            validateDto.setClaimTypeStatus(false);
                            validateDto.setClaimTypeErrorMessage("Claim Type None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            claimTypeColumnIndexFlag = true;
                            validateDto.setClaimTypeStatus(false);
                            validateDto.setClaimTypeErrorMessage("Claim Type Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + claimTypeCell.getCachedFormulaResultType());
                    }
                }
            }

            if (dataRow.getCell(hospitalNameColumnIndex) != null) {
                Cell hospitalNameCell = dataRow.getCell(hospitalNameColumnIndex);
                if (hospitalNameCell.getCellType() == CellType.NUMERIC) {
                    hospitalName = String.valueOf(hospitalNameCell.getNumericCellValue()).trim();
                    validateDto.setHospitalNameStatus(true);
                    validateDto.setHospitalName(hospitalName);
                } else if (hospitalNameCell.getCellType() == CellType.STRING) {
                    hospitalName = hospitalNameCell.getStringCellValue().trim();
                    validateDto.setHospitalNameStatus(true);
                    validateDto.setHospitalName(hospitalName);
                } else if (hospitalNameCell.getCellType() == CellType._NONE) {
                    validateDto.setHospitalNameStatus(true);
                } else if (hospitalNameCell.getCellType() == CellType.BLANK) {
                    validateDto.setHospitalNameStatus(true);
                } else if (hospitalNameCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = hospitalNameCell.getErrorCellValue();
                    validateDto.setHospitalName(String.valueOf(errorCellValue).trim());
                    validateDto.setHospitalNameStatus(true);
                } else if (hospitalNameCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = hospitalNameCell.getBooleanCellValue();
                    validateDto.setHospitalName(String.valueOf(booleanCellValue).trim());
                    validateDto.setHospitalNameStatus(true);
                } else if (hospitalNameCell.getCellType() == CellType.FORMULA) {
                    switch (hospitalNameCell.getCellType()) {
                        case NUMERIC: {
                            hospitalName = String.valueOf(hospitalNameCell.getNumericCellValue()).trim();
                            validateDto.setHospitalNameStatus(true);
                            validateDto.setHospitalName(hospitalName);
                            break;
                        }
                        case STRING: {
                            hospitalName = hospitalNameCell.getStringCellValue().trim();
                            validateDto.setHospitalNameStatus(true);
                            validateDto.setHospitalName(hospitalName);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = hospitalNameCell.getBooleanCellValue();
                            validateDto.setHospitalName(String.valueOf(booleanCellValue).trim());
                            validateDto.setHospitalNameStatus(true);
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = hospitalNameCell.getErrorCellValue();
                            validateDto.setHospitalName(String.valueOf(errorCellValue).trim());
                            validateDto.setHospitalNameStatus(true);
                            break;
                        }
                        case _NONE: {
                            validateDto.setHospitalNameStatus(true);
                            break;
                        }
                        case BLANK: {
                            validateDto.setHospitalNameStatus(true);
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + hospitalNameCell.getCachedFormulaResultType());
                    }
                }
            }

            if (dataRow.getCell(diseaseColumnIndex) != null) {

                Cell diseaseCell = dataRow.getCell(diseaseColumnIndex);

                if (diseaseCell.getCellType() == CellType.NUMERIC) {
                    disease = String.valueOf(diseaseCell.getNumericCellValue()).trim();
                    validateDto.setDiseaseStatus(true);
                    validateDto.setDisease(disease);
                } else if (diseaseCell.getCellType() == CellType.STRING) {
                    disease = diseaseCell.getStringCellValue().trim();
                    validateDto.setDiseaseStatus(true);
                    validateDto.setDisease(disease);
                } else if (diseaseCell.getCellType() == CellType._NONE) {
                    validateDto.setDiseaseStatus(true);
                } else if (diseaseCell.getCellType() == CellType.BLANK) {
                    validateDto.setDiseaseStatus(true);
                } else if (diseaseCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = diseaseCell.getErrorCellValue();
                    validateDto.setDisease(String.valueOf(errorCellValue).trim());
                    validateDto.setDiseaseStatus(true);
                } else if (diseaseCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = diseaseCell.getBooleanCellValue();
                    validateDto.setDisease(String.valueOf(booleanCellValue).trim());
                    validateDto.setDiseaseStatus(true);
                } else if (diseaseCell.getCellType() == CellType.FORMULA) {
                    switch (diseaseCell.getCellType()) {
                        case NUMERIC: {
                            disease = String.valueOf(diseaseCell.getNumericCellValue()).trim();
                            validateDto.setDiseaseStatus(true);
                            validateDto.setDisease(disease);
                            break;
                        }
                        case STRING: {
                            disease = diseaseCell.getStringCellValue().trim();
                            validateDto.setDiseaseStatus(true);
                            validateDto.setDisease(disease);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = diseaseCell.getBooleanCellValue();
                            validateDto.setDisease(String.valueOf(booleanCellValue).trim());
                            validateDto.setDiseaseStatus(true);
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = diseaseCell.getErrorCellValue();
                            validateDto.setDisease(String.valueOf(errorCellValue).trim());
                            validateDto.setDiseaseStatus(true);
                            break;
                        }
                        case _NONE: {
                            validateDto.setDiseaseStatus(true);
                            break;
                        }
                        case BLANK: {
                            validateDto.setDiseaseStatus(true);
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + diseaseCell.getCachedFormulaResultType());
                    }
                }
            }

            String dateOfClaimValue = null;
            if (dataRow.getCell(dateOfClaimColumnIndex) != null) {
                Cell dateOfClaimCell = dataRow.getCell(dateOfClaimColumnIndex);
                if (dateOfClaimCell.getCellType() == CellType.NUMERIC) {
                    dateOfClaimValue = String.valueOf(dateOfClaimCell.getDateCellValue());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String format = outputFormat.format(dateOfClaimCell.getDateCellValue()).trim();
                    validateDto.setDateOfClaimStatus(true);
                    validateDto.setDateOfClaim(format);
                } else if (dateOfClaimCell.getCellType() == CellType.STRING) {
                    dateOfClaimValue = dateOfClaimCell.getStringCellValue().trim();
                    validateDto.setDateOfClaimStatus(true);
                    validateDto.setDateOfClaim(dateOfClaimValue);
                } else if (dateOfClaimCell.getCellType() == CellType._NONE) {
                    dateOfClaimColumnIndexFlag = true;
                    validateDto.setDateOfClaimStatus(false);
                    validateDto.setDateOfClaimErrorMessage("DateOfClaim None :: ");
                } else if (dateOfClaimCell.getCellType() == CellType.BLANK) {
                    dateOfClaimColumnIndexFlag = true;
                    validateDto.setDateOfClaimStatus(false);
                    validateDto.setDateOfClaimErrorMessage("DateOfClaim Blank :: ");
                } else if (dateOfClaimCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = dateOfClaimCell.getErrorCellValue();
                    validateDto.setDateOfClaim(String.valueOf(errorCellValue).trim());
                    validateDto.setDateOfClaimStatus(false);
                    validateDto.setDateOfClaimErrorMessage("DateOfClaim Error :: ");
                } else if (dateOfClaimCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = dateOfClaimCell.getBooleanCellValue();
                    validateDto.setDateOfClaim(String.valueOf(booleanCellValue).trim());
                    validateDto.setDateOfClaimStatus(false);
                    validateDto.setDateOfClaimErrorMessage("DateOfClaim Boolean :: ");
                } else if (dateOfClaimCell.getCellType() == CellType.FORMULA) {
                    switch (dateOfClaimCell.getCellType()) {
                        case NUMERIC: {
                            dateOfClaimValue = String.valueOf(dateOfClaimCell.getDateCellValue());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String format = outputFormat.format(dateOfClaimCell.getDateCellValue()).trim();
                            validateDto.setDateOfClaimStatus(true);
                            validateDto.setDateOfClaim(format);
                            break;
                        }
                        case STRING: {
                            dateOfClaimValue = dateOfClaimCell.getStringCellValue().trim();
                            validateDto.setDateOfClaimStatus(true);
                            validateDto.setDateOfClaim(dateOfClaimValue);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = dateOfClaimCell.getBooleanCellValue();
                            validateDto.setDateOfClaim(String.valueOf(booleanCellValue));
                            validateDto.setDateOfClaimStatus(false);
                            validateDto.setDateOfClaimErrorMessage("DateOfClaim Boolean :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = dateOfClaimCell.getErrorCellValue();
                            validateDto.setDateOfClaim(String.valueOf(errorCellValue).trim());
                            validateDto.setDateOfClaimStatus(false);
                            validateDto.setDateOfClaimErrorMessage("DateOfClaim Error :: ");
                            break;
                        }
                        case _NONE: {
                            dateOfClaimColumnIndexFlag = true;
                            validateDto.setDateOfClaimStatus(false);
                            validateDto.setDateOfClaimErrorMessage("DateOfClaim None :: ");
                            break;
                        }
                        case BLANK: {
                            dateOfClaimColumnIndexFlag = true;
                            validateDto.setDateOfClaimStatus(false);
                            validateDto.setDateOfClaimErrorMessage("DateOfClaim Blank :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + dateOfClaimCell.getCachedFormulaResultType());
                    }
                }
            }

            String claimedAmountValue;
            if (dataRow.getCell(claimedAmountColumnIndex) != null) {
                Cell claimedAmountCell = dataRow.getCell(claimedAmountColumnIndex);
                if (claimedAmountCell.getCellType() == CellType.NUMERIC) {
                    double numericCellValue = claimedAmountCell.getNumericCellValue();
                    DecimalFormat df = new DecimalFormat("#");
                    String stringValue = df.format(numericCellValue).trim();
                    validateDto.setClaimedAmountStatus(true);
                    validateDto.setClaimedAmount(stringValue);
                } else if (claimedAmountCell.getCellType() == CellType.STRING) {
                    String sumInsuredStr = claimedAmountCell.getStringCellValue().replaceAll(",", "");
                    try {
                        double claimedAmountDouble = Double.parseDouble(sumInsuredStr);
                        DecimalFormat df = new DecimalFormat("#");
                        String stringValue = df.format(claimedAmountDouble).trim();
                        validateDto.setClaimedAmountStatus(true);
                        validateDto.setClaimedAmount(stringValue);
                    } catch (NumberFormatException e) {
                        // Handle parsing exception
                        validateDto.setClaimedAmountStatus(false);
                        validateDto.setAdmissionDateErrorMessage("Error parsing sum insured value: " + e.getMessage());
                    }
                } else if (claimedAmountCell.getCellType() == CellType._NONE) {
                    validateDto.setClaimedAmountStatus(true);
                    validateDto.setClaimedAmount("0");
                } else if (claimedAmountCell.getCellType() == CellType.BLANK) {
                    validateDto.setClaimedAmountStatus(true);
                    validateDto.setClaimedAmount("0");
                } else if (claimedAmountCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = claimedAmountCell.getErrorCellValue();
                    validateDto.setClaimedAmount(String.valueOf(errorCellValue).trim());
                    validateDto.setClaimedAmountStatus(true);
                } else if (claimedAmountCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = claimedAmountCell.getBooleanCellValue();
                    validateDto.setClaimedAmount(String.valueOf(booleanCellValue).trim());
                    validateDto.setClaimedAmountStatus(true);
                } else if (claimedAmountCell.getCellType() == CellType.FORMULA) {
                    if (claimedAmountCell.getCachedFormulaResultType() == CellType.NUMERIC) {
                        double numericCellValue = claimedAmountCell.getNumericCellValue();
                        DecimalFormat df = new DecimalFormat("#");
                        String stringValue = df.format(numericCellValue).trim();
                        validateDto.setClaimedAmountStatus(true);
                        validateDto.setClaimedAmount(stringValue);
                    } else if (claimedAmountCell.getCachedFormulaResultType() == CellType.STRING) {
                        String sumInsuredStr = claimedAmountCell.getStringCellValue().replaceAll(",", "");
                        try {
                            double claimedAmountDouble = Double.parseDouble(sumInsuredStr);
                            DecimalFormat df = new DecimalFormat("#");
                            String stringValue = df.format(claimedAmountDouble).trim();
                            validateDto.setClaimedAmountStatus(true);
                            validateDto.setClaimedAmount(stringValue);
                        } catch (NumberFormatException e) {
                            // Handle parsing exception
                            validateDto.setClaimedAmountStatus(false);
                            validateDto
                                    .setAdmissionDateErrorMessage("Error parsing sum insured value: " + e.getMessage());
                        }
                    } else if (claimedAmountCell.getCachedFormulaResultType() == CellType.BOOLEAN) {
                        boolean booleanCellValue = claimedAmountCell.getBooleanCellValue();
                        validateDto.setClaimedAmount(String.valueOf(booleanCellValue).trim());
                        validateDto.setClaimedAmountStatus(true);
                    } else if (claimedAmountCell.getCachedFormulaResultType() == CellType.ERROR) {
                        byte errorCellValue = claimedAmountCell.getErrorCellValue();
                        validateDto.setClaimedAmount(String.valueOf(errorCellValue).trim());
                        validateDto.setClaimedAmountStatus(true);
                    } else if (claimedAmountCell.getCachedFormulaResultType() == CellType._NONE) {
                        validateDto.setClaimedAmountStatus(true);
                        validateDto.setClaimedAmount("0");
                    } else if (claimedAmountCell.getCachedFormulaResultType() == CellType.BLANK) {
                        validateDto.setClaimedAmountStatus(true);
                        validateDto.setClaimedAmount("0");
                    } else {
                        throw new IllegalArgumentException(
                                "Unexpected value: " + claimedAmountCell.getCachedFormulaResultType());
                    }
                }
            }

            String paidAmountValue;
            if (dataRow.getCell(paidAmountColumnIndex) != null) {
                Cell paidAmountCell = dataRow.getCell(paidAmountColumnIndex);
                if (paidAmountCell.getCellType() == CellType.NUMERIC) {
                    double numericCellValue = paidAmountCell.getNumericCellValue();
                    DecimalFormat df = new DecimalFormat("#");
                    String stringValue = df.format(numericCellValue).trim();
                    validateDto.setPaidAmountStatus(true);
                    validateDto.setPaidAmount(stringValue);
                } else if (paidAmountCell.getCellType() == CellType.STRING) {
                    String sumInsuredStr = paidAmountCell.getStringCellValue().replaceAll(",", "");
                    try {
                        double claimedAmountDouble = Double.parseDouble(sumInsuredStr);
                        DecimalFormat df = new DecimalFormat("#");
                        String stringValue = df.format(claimedAmountDouble).trim();
                        validateDto.setPaidAmountStatus(true);
                        validateDto.setPaidAmount(stringValue);
                    } catch (NumberFormatException e) {
                        // Handle parsing exception
                        validateDto.setPaidAmountStatus(false);
                        validateDto.setPaidAmountErrorMessage("Error parsing sum insured value: " + e.getMessage());
                    }
                } else if (paidAmountCell.getCellType() == CellType._NONE) {
                    validateDto.setPaidAmountStatus(true);
                    validateDto.setPaidAmount("0");
                } else if (paidAmountCell.getCellType() == CellType.BLANK) {
                    validateDto.setPaidAmountStatus(true);
                    validateDto.setPaidAmount("0");
                } else if (paidAmountCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = paidAmountCell.getErrorCellValue();
                    validateDto.setPaidAmount(String.valueOf(errorCellValue).trim());
                    validateDto.setPaidAmountStatus(true);
                } else if (paidAmountCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = paidAmountCell.getBooleanCellValue();
                    validateDto.setPaidAmount(String.valueOf(booleanCellValue).trim());
                    validateDto.setPaidAmountStatus(true);
                } else if (paidAmountCell.getCellType() == CellType.FORMULA) {
                    if (paidAmountCell.getCachedFormulaResultType() == CellType.NUMERIC) {
                        double numericCellValue = paidAmountCell.getNumericCellValue();
                        DecimalFormat df = new DecimalFormat("#");
                        String stringValue = df.format(numericCellValue).trim();
                        validateDto.setPaidAmountStatus(true);
                        validateDto.setPaidAmount(stringValue);
                    } else if (paidAmountCell.getCachedFormulaResultType() == CellType.STRING) {
                        String sumInsuredStr = paidAmountCell.getStringCellValue().replaceAll(",", "");
                        try {
                            double claimedAmountDouble = Double.parseDouble(sumInsuredStr);
                            DecimalFormat df = new DecimalFormat("#");
                            String stringValue = df.format(claimedAmountDouble).trim();
                            validateDto.setPaidAmountStatus(true);
                            validateDto.setPaidAmount(stringValue);
                        } catch (NumberFormatException e) {
                            // Handle parsing exception
                            validateDto.setPaidAmountStatus(false);
                            validateDto.setPaidAmountErrorMessage("Error parsing sum insured value: " + e.getMessage());
                        }
                    } else if (paidAmountCell.getCachedFormulaResultType() == CellType.BOOLEAN) {
                        boolean booleanCellValue = paidAmountCell.getBooleanCellValue();
                        validateDto.setPaidAmount(String.valueOf(booleanCellValue).trim());
                        validateDto.setPaidAmountStatus(true);
                    } else if (paidAmountCell.getCachedFormulaResultType() == CellType.ERROR) {
                        byte errorCellValue = paidAmountCell.getErrorCellValue();
                        validateDto.setPaidAmount(String.valueOf(errorCellValue).trim());
                        validateDto.setPaidAmountStatus(true);
                    } else if (paidAmountCell.getCachedFormulaResultType() == CellType._NONE) {
                        validateDto.setClaimedAmountStatus(true);
                        validateDto.setClaimedAmount("0");
                    } else if (paidAmountCell.getCachedFormulaResultType() == CellType.BLANK) {
                        validateDto.setPaidAmountStatus(true);
                        validateDto.setPaidAmount("0");
                    } else {
                        throw new IllegalArgumentException(
                                "Unexpected value: " + paidAmountCell.getCachedFormulaResultType());
                    }
                }
            }


            String outstandingAmountValue;
            if (dataRow.getCell(outstandingAmountColumnIndex) != null) {
                Cell outstandingAmountCell = dataRow.getCell(outstandingAmountColumnIndex);
                if (outstandingAmountCell.getCellType() == CellType.NUMERIC) {
                    double numericValue = outstandingAmountCell.getNumericCellValue();
                    DecimalFormat df = new DecimalFormat("#");
                    String stringValue = df.format(numericValue).trim();
                    validateDto.setOutstandingAmountStatus(true);
                    validateDto.setOutstandingAmount(stringValue); // Set explicitly if the numeric value is 0.0
                } else if (outstandingAmountCell.getCellType() == CellType.STRING) {
                    String sumInsuredStr = outstandingAmountCell.getStringCellValue().replaceAll(",", "");
                    try {
                        double sumInsuredDouble = Double.parseDouble(sumInsuredStr);
                        DecimalFormat df = new DecimalFormat("#");
                        String stringValue = df.format(sumInsuredDouble).trim();
                        validateDto.setOutstandingAmountStatus(true);
                        validateDto.setOutstandingAmount(stringValue);
                    } catch (NumberFormatException e) {
                        // Handle parsing exception
                        validateDto.setOutstandingAmountStatus(false);
                        validateDto
                                .setOutstandingAmountErrorMessage("Error parsing sum insured value: " + e.getMessage());
                    }
                } else if (outstandingAmountCell.getCellType() == CellType._NONE) {
                    validateDto.setOutstandingAmountStatus(true);
                    validateDto.setOutstandingAmount("0");
                } else if (outstandingAmountCell.getCellType() == CellType.BLANK) {
                    validateDto.setOutstandingAmountStatus(true);
                    validateDto.setOutstandingAmount("0");
                } else if (outstandingAmountCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = outstandingAmountCell.getErrorCellValue();
                    validateDto.setOutstandingAmount(String.valueOf(errorCellValue).trim());
                    validateDto.setOutstandingAmountStatus(true);
                } else if (outstandingAmountCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = outstandingAmountCell.getBooleanCellValue();
                    validateDto.setOutstandingAmount(String.valueOf(booleanCellValue).trim());
                    validateDto.setOutstandingAmountStatus(true);
                } else if (outstandingAmountCell.getCellType() == CellType.FORMULA) {
                    switch (outstandingAmountCell.getCellType()) {
                        case NUMERIC: {
                            double numericValue = outstandingAmountCell.getNumericCellValue();
                            DecimalFormat df = new DecimalFormat("#");
                            String stringValue = df.format(numericValue).trim();
                            validateDto.setOutstandingAmountStatus(true);
                            validateDto.setOutstandingAmount(stringValue); // Set explicitly if the numeric value is 0.0
                            break;
                        }
                        case STRING: {
                            String sumInsuredStr = outstandingAmountCell.getStringCellValue().replaceAll(",", "");
                            try {
                                double sumInsuredDouble = Double.parseDouble(sumInsuredStr);
                                DecimalFormat df = new DecimalFormat("#");
                                String stringValue = df.format(sumInsuredDouble).trim();
                                validateDto.setOutstandingAmountStatus(true);
                                validateDto.setOutstandingAmount(stringValue);
                            } catch (NumberFormatException e) {
                                // Handle parsing exception
                                validateDto.setOutstandingAmountStatus(false);
                                validateDto.setOutstandingAmountErrorMessage(
                                        "Error parsing sum insured value: " + e.getMessage());
                            }
                            validateDto.setOutstandingAmountErrorMessage("OutstandingAmount String");
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = outstandingAmountCell.getBooleanCellValue();
                            validateDto.setOutstandingAmount(String.valueOf(booleanCellValue).trim());
                            validateDto.setOutstandingAmountStatus(true);
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = outstandingAmountCell.getErrorCellValue();
                            validateDto.setOutstandingAmount(String.valueOf(errorCellValue).trim());
                            validateDto.setOutstandingAmountStatus(true);
                            break;
                        }
                        case _NONE: {
                            validateDto.setOutstandingAmountStatus(true);
                            validateDto.setOutstandingAmount("0");
                            break;
                        }
                        case BLANK: {
                            validateDto.setOutstandingAmountStatus(true);
                            validateDto.setOutstandingAmount("0");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + outstandingAmountCell.getCachedFormulaResultType());
                    }
                }
            }

            String admissionDateValue = null;
            if (dataRow.getCell(admissionDateColumnIndex) != null) {
                Cell admissionDateCell = dataRow.getCell(admissionDateColumnIndex);
                if (admissionDateCell.getCellType() == CellType.NUMERIC) {
                    admissionDateValue = String.valueOf(admissionDateCell.getDateCellValue()).trim();
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String format = outputFormat.format(admissionDateCell.getDateCellValue());
                    validateDto.setAdmissionDateStatus(true);
                    validateDto.setAdmissionDate(format);
                } else if (admissionDateCell.getCellType() == CellType.STRING) {
                    admissionDateValue = admissionDateCell.getStringCellValue().trim();
                    validateDto.setAdmissionDateStatus(true);
                    validateDto.setAdmissionDate(admissionDateValue);
                } else if (admissionDateCell.getCellType() == CellType._NONE) {
                    admissionDateColumnIndexFlag = true;
                    validateDto.setAdmissionDateStatus(false);
                    validateDto.setAdmissionDateErrorMessage("AdmissionDate None :: ");
                } else if (admissionDateCell.getCellType() == CellType.BLANK) {
                    admissionDateColumnIndexFlag = true;
                    validateDto.setAdmissionDateStatus(false);
                    validateDto.setAdmissionDateErrorMessage("AdmissionDate Blank :: ");
                } else if (admissionDateCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = admissionDateCell.getErrorCellValue();
                    validateDto.setAdmissionDate(String.valueOf(errorCellValue).trim());
                    validateDto.setAdmissionDateStatus(false);
                    validateDto.setAdmissionDateErrorMessage("DateOfClaim Error :: ");
                } else if (admissionDateCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = admissionDateCell.getBooleanCellValue();
                    validateDto.setAdmissionDate(String.valueOf(booleanCellValue).trim());
                    validateDto.setAdmissionDateStatus(false);
                    validateDto.setAdmissionDateErrorMessage("DateOfClaim Boolean :: ");
                } else if (admissionDateCell.getCellType() == CellType.FORMULA) {
                    switch (admissionDateCell.getCellType()) {
                        case NUMERIC: {
                            admissionDateValue = String.valueOf(admissionDateCell.getDateCellValue()).trim();
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String format = outputFormat.format(admissionDateCell.getDateCellValue());
                            validateDto.setAdmissionDateStatus(true);
                            validateDto.setAdmissionDate(format);
                            break;
                        }
                        case STRING: {
                            admissionDateValue = admissionDateCell.getStringCellValue().trim();
                            validateDto.setAdmissionDateStatus(true);
                            validateDto.setAdmissionDate(admissionDateValue);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = admissionDateCell.getBooleanCellValue();
                            validateDto.setAdmissionDate(String.valueOf(booleanCellValue).trim());
                            validateDto.setAdmissionDateStatus(false);
                            validateDto.setAdmissionDateErrorMessage("AdmissionDate Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = admissionDateCell.getErrorCellValue();
                            validateDto.setAdmissionDate(String.valueOf(errorCellValue).trim());
                            validateDto.setAdmissionDateStatus(false);
                            validateDto.setAdmissionDateErrorMessage("AdmissionDate Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            admissionDateColumnIndexFlag = true;
                            validateDto.setAdmissionDateStatus(false);
                            validateDto.setAdmissionDateErrorMessage("AdmissionDate None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            admissionDateColumnIndexFlag = true;
                            validateDto.setAdmissionDateStatus(false);
                            validateDto.setAdmissionDateErrorMessage("AdmissionDate Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + admissionDateCell.getCachedFormulaResultType());
                    }
                }
            }

            String dischargeDateValue = null;
            if (dataRow.getCell(dischargeDateColumnIndex) != null) {
                Cell dischargeDateCell = dataRow.getCell(dischargeDateColumnIndex);
                if (dischargeDateCell.getCellType() == CellType.NUMERIC) {
                    dischargeDateValue = String.valueOf(dischargeDateCell.getDateCellValue()).trim();
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String format = outputFormat.format(dischargeDateCell.getDateCellValue());
                    validateDto.setDischargeDateStatus(true);
                    validateDto.setDischargeDate(format);
                } else if (dischargeDateCell.getCellType() == CellType.STRING) {
                    dischargeDateValue = dischargeDateCell.getStringCellValue().trim();
                    validateDto.setDischargeDateStatus(true);
                    validateDto.setDischargeDate(dischargeDateValue);
                } else if (dischargeDateCell.getCellType() == CellType._NONE) {
                    dischargeDateColumnIndexFlag = true;
                    validateDto.setDischargeDateStatus(false);
                    validateDto.setDischargeDateErrorMessage("DischargeDate None :: ");
                } else if (dischargeDateCell.getCellType() == CellType.BLANK) {
                    dischargeDateColumnIndexFlag = true;
                    validateDto.setDischargeDateStatus(false);
                    validateDto.setDischargeDateErrorMessage("DischargeDate Blank :: ");
                } else if (dischargeDateCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = dischargeDateCell.getErrorCellValue();
                    validateDto.setDischargeDate(String.valueOf(errorCellValue).trim());
                    validateDto.setDischargeDateStatus(false);
                    validateDto.setDischargeDateErrorMessage("DischargeDate Error :: ");
                } else if (dischargeDateCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = dischargeDateCell.getBooleanCellValue();
                    validateDto.setDischargeDate(String.valueOf(booleanCellValue).trim());
                    validateDto.setDischargeDateStatus(false);
                    validateDto.setDischargeDateErrorMessage("DischargeDate Boolean :: ");
                } else if (dischargeDateCell.getCellType() == CellType.FORMULA) {
                    switch (dischargeDateCell.getCellType()) {
                        case NUMERIC: {
                            dischargeDateValue = String.valueOf(dischargeDateCell.getDateCellValue()).trim();
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String format = outputFormat.format(dischargeDateCell.getDateCellValue());
                            validateDto.setDischargeDateStatus(true);
                            validateDto.setDischargeDate(format);
                            break;
                        }
                        case STRING: {
                            dischargeDateValue = dischargeDateCell.getStringCellValue().trim();
                            validateDto.setDischargeDateStatus(true);
                            validateDto.setDischargeDate(dischargeDateValue);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = dischargeDateCell.getBooleanCellValue();
                            validateDto.setDischargeDate(String.valueOf(booleanCellValue).trim());
                            validateDto.setDischargeDateStatus(false);
                            validateDto.setDischargeDateErrorMessage("DischargeDate Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = dischargeDateCell.getErrorCellValue();
                            validateDto.setDischargeDate(String.valueOf(errorCellValue).trim());
                            validateDto.setDischargeDateStatus(false);
                            validateDto.setDischargeDateErrorMessage("DischargeDate Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            dischargeDateColumnIndexFlag = true;
                            validateDto.setDischargeDateStatus(false);
                            validateDto.setDischargeDateErrorMessage("DischargeDate None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            dischargeDateColumnIndexFlag = true;
                            validateDto.setDischargeDateStatus(false);
                            validateDto.setDischargeDateErrorMessage("DischargeDate Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + dischargeDateCell.getCachedFormulaResultType());
                    }
                }
            }

            String policyStartDateValue = null;
            if (dataRow.getCell(policyStartDateColumnIndex) != null) {
                Cell policyStartDateCell = dataRow.getCell(policyStartDateColumnIndex);
                if (policyStartDateCell.getCellType() == CellType.NUMERIC) {
                    policyStartDateValue = String.valueOf(policyStartDateCell.getDateCellValue()).trim();
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String format = outputFormat.format(policyStartDateCell.getDateCellValue());
                    validateDto.setPolicyStartDateStatus(true);
                    validateDto.setPolicyStartDate(format);
                } else if (policyStartDateCell.getCellType() == CellType.STRING) {
                    policyStartDateValue = policyStartDateCell.getStringCellValue().trim();
                    validateDto.setPolicyStartDateStatus(true);
                    validateDto.setPolicyStartDate(admissionDateValue);
                } else if (policyStartDateCell.getCellType() == CellType._NONE) {
                    validateDto.setPolicyStartDateStatus(true);
                } else if (policyStartDateCell.getCellType() == CellType.BLANK) {
                    validateDto.setPolicyStartDateStatus(true);
                } else if (policyStartDateCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = policyStartDateCell.getErrorCellValue();
                    validateDto.setPolicyStartDate(String.valueOf(errorCellValue).trim());
                    validateDto.setPolicyStartDateStatus(true);
                } else if (policyStartDateCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = policyStartDateCell.getBooleanCellValue();
                    validateDto.setPolicyStartDate(String.valueOf(booleanCellValue).trim());
                    validateDto.setPolicyStartDateStatus(true);
                } else if (policyStartDateCell.getCellType() == CellType.FORMULA) {
                    switch (policyStartDateCell.getCellType()) {
                        case NUMERIC: {
                            policyStartDateValue = String.valueOf(policyStartDateCell.getDateCellValue()).trim();
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
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
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = policyStartDateCell.getErrorCellValue();
                            validateDto.setPolicyStartDate(String.valueOf(errorCellValue).trim());
                            validateDto.setPolicyStartDateStatus(true);
                            break;
                        }
                        case _NONE: {
                            validateDto.setPolicyStartDateStatus(true);
                            break;
                        }
                        case BLANK: {
                            validateDto.setPolicyStartDateStatus(true);
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + policyStartDateCell.getCachedFormulaResultType());
                    }
                }
            }

            String policyEndDateValue = null;
            if (dataRow.getCell(policyEndDateColumnIndex) != null) {
                Cell policyEndDateCell = dataRow.getCell(policyEndDateColumnIndex);
                if (policyEndDateCell.getCellType() == CellType.NUMERIC) {
                    policyEndDateValue = String.valueOf(policyEndDateCell.getDateCellValue()).trim();
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String format = outputFormat.format(policyEndDateCell.getDateCellValue());
                    validateDto.setPolicyEndDateStatus(true);
                    validateDto.setPolicyEndDate(format);
                } else if (policyEndDateCell.getCellType() == CellType.STRING) {
                    policyEndDateValue = policyEndDateCell.getStringCellValue().trim();
                    validateDto.setPolicyEndDateStatus(true);
                    validateDto.setPolicyEndDate(policyEndDateValue);
                } else if (policyEndDateCell.getCellType() == CellType._NONE) {
                    validateDto.setPolicyEndDateStatus(true);
                } else if (policyEndDateCell.getCellType() == CellType.BLANK) {
                    validateDto.setPolicyEndDateStatus(true);
                } else if (policyEndDateCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = policyEndDateCell.getErrorCellValue();
                    validateDto.setPolicyEndDate(String.valueOf(errorCellValue).trim());
                    validateDto.setPolicyEndDateStatus(true);
                } else if (policyEndDateCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = policyEndDateCell.getBooleanCellValue();
                    validateDto.setPolicyEndDate(String.valueOf(booleanCellValue).trim());
                    validateDto.setPolicyEndDateStatus(true);
                } else if (policyEndDateCell.getCellType() == CellType.FORMULA) {
                    switch (policyEndDateCell.getCellType()) {
                        case NUMERIC: {
                            policyEndDateValue = String.valueOf(policyEndDateCell.getDateCellValue()).trim();
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
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
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = policyEndDateCell.getErrorCellValue();
                            validateDto.setPolicyEndDate(String.valueOf(errorCellValue).trim());
                            validateDto.setPolicyEndDateStatus(true);
                            break;
                        }
                        case _NONE: {
                            validateDto.setPolicyEndDateStatus(true);
                            break;
                        }
                        case BLANK: {
                            validateDto.setPolicyEndDateStatus(true);
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + policyEndDateCell.getCachedFormulaResultType());
                    }
                }
            }

            if (!tpaName.equals("GHPL") || !tpaName.equals("ICICI") || !tpaName.equals("MediAssist")) {
                if (dataRow.getCell(hospitalStateColumnIndex) != null) {
                    Cell hospitalStateCell = dataRow.getCell(hospitalStateColumnIndex);
                    if (hospitalStateCell.getCellType() == CellType.NUMERIC) {
                        hospitalState = String.valueOf(hospitalStateCell.getNumericCellValue()).trim();
                        validateDto.setHospitalStateStatus(false);
                        validateDto.setHospitalState(hospitalState);
                    } else if (hospitalStateCell.getCellType() == CellType.STRING) {
                        hospitalState = hospitalStateCell.getStringCellValue().trim();
                        validateDto.setHospitalStateStatus(true);
                        validateDto.setHospitalState(hospitalState);
                    } else if (hospitalStateCell.getCellType() == CellType._NONE) {
                        validateDto.setHospitalStateStatus(true);
                    } else if (hospitalStateCell.getCellType() == CellType.BLANK) {
                        validateDto.setHospitalStateStatus(true);
                    } else if (hospitalStateCell.getCellType() == CellType.ERROR) {
                        byte errorCellValue = hospitalStateCell.getErrorCellValue();
                        validateDto.setHospitalState(String.valueOf(errorCellValue).trim());
                        validateDto.setHospitalStateStatus(true);
                    } else if (hospitalStateCell.getCellType() == CellType.BOOLEAN) {
                        boolean booleanCellValue = hospitalStateCell.getBooleanCellValue();
                        validateDto.setHospitalState(String.valueOf(booleanCellValue).trim());
                        validateDto.setHospitalStateStatus(true);
                    } else if (hospitalStateCell.getCellType() == CellType.FORMULA) {
                        switch (hospitalStateCell.getCellType()) {
                            case NUMERIC: {
                                hospitalState = String.valueOf(hospitalStateCell.getNumericCellValue()).trim();
                                validateDto.setHospitalStateStatus(true);
                                validateDto.setHospitalState(hospitalState);
                                break;
                            }
                            case STRING: {
                                hospitalState = hospitalStateCell.getStringCellValue().trim();
                                validateDto.setHospitalStateStatus(true);
                                validateDto.setHospitalState(hospitalState);
                                break;
                            }
                            case BOOLEAN: {
                                boolean booleanCellValue = hospitalStateCell.getBooleanCellValue();
                                validateDto.setHospitalState(String.valueOf(booleanCellValue).trim());
                                validateDto.setHospitalStateStatus(true);
                                break;
                            }
                            case ERROR: {
                                byte errorCellValue = hospitalStateCell.getErrorCellValue();
                                validateDto.setHospitalState(String.valueOf(errorCellValue).trim());
                                validateDto.setHospitalStateStatus(true);
                                break;
                            }
                            case _NONE: {
                                validateDto.setHospitalStateStatus(true);
                                break;
                            }
                            case BLANK: {
                                validateDto.setHospitalStateStatus(true);
                                break;
                            }
                            default:
                                throw new IllegalArgumentException(
                                        "Unexpected value: " + hospitalStateCell.getCachedFormulaResultType());
                        }
                    }
                }
            }

            if (dataRow.getCell(hospitalCityColumnIndex) != null) {
                Cell hospitalCityCell = dataRow.getCell(hospitalCityColumnIndex);
                if (hospitalCityCell.getCellType() == CellType.NUMERIC) {
                    hospitalCity = String.valueOf(hospitalCityCell.getNumericCellValue()).trim();
                    validateDto.setHospitalCityStatus(false);
                    validateDto.setHospitalCity(hospitalCity);
                } else if (hospitalCityCell.getCellType() == CellType.STRING) {
                    hospitalCity = hospitalCityCell.getStringCellValue().trim();
                    validateDto.setHospitalCityStatus(true);
                    validateDto.setHospitalCity(hospitalCity);
                } else if (hospitalCityCell.getCellType() == CellType._NONE) {
                    validateDto.setHospitalCityStatus(true);
                } else if (hospitalCityCell.getCellType() == CellType.BLANK) {
                    validateDto.setHospitalCityStatus(true);
                } else if (hospitalCityCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = hospitalCityCell.getErrorCellValue();
                    validateDto.setHospitalCity(String.valueOf(errorCellValue).trim());
                    validateDto.setHospitalCityStatus(true);
                } else if (hospitalCityCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = hospitalCityCell.getBooleanCellValue();
                    validateDto.setHospitalCity(String.valueOf(booleanCellValue).trim());
                    validateDto.setHospitalCityStatus(true);
                } else if (hospitalCityCell.getCellType() == CellType.FORMULA) {
                    switch (hospitalCityCell.getCellType()) {
                        case NUMERIC: {
                            hospitalCity = String.valueOf(hospitalCityCell.getNumericCellValue()).trim();
                            validateDto.setHospitalCityStatus(true);
                            validateDto.setHospitalCity(hospitalCity);
                            break;
                        }
                        case STRING: {
                            hospitalCity = hospitalCityCell.getStringCellValue().trim();
                            validateDto.setHospitalCityStatus(true);
                            validateDto.setHospitalCity(hospitalCity);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = hospitalCityCell.getBooleanCellValue();
                            validateDto.setHospitalCity(String.valueOf(booleanCellValue).trim());
                            validateDto.setHospitalCityStatus(true);
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = hospitalCityCell.getErrorCellValue();
                            validateDto.setHospitalCity(String.valueOf(errorCellValue).trim());
                            validateDto.setHospitalCityStatus(true);
                            break;
                        }
                        case _NONE: {
                            validateDto.setHospitalCityStatus(true);
                            break;
                        }
                        case BLANK: {
                            validateDto.setHospitalCityStatus(true);
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(
                                    "Unexpected value: " + hospitalCityCell.getCachedFormulaResultType());
                    }
                }
            }

            if (tpaName.equals("MD India")) {
                validateDto.setNetworkTypeStatus(true);
            } else if (tpaName.equals("ICICI")) {
                validateDto.setAgeStatus(true);
                validateDto.setGenderStatus(true);
                validateDto.setNetworkTypeStatus(true);
                validateDto.setPolicyStartDateStatus(true);
                validateDto.setPolicyEndDateStatus(true);
                validateDto.setHospitalStateStatus(true);
                validateDto.setHospitalCityStatus(true);
            } else if (tpaName.equals("RCare")) {
                validateDto.setNetworkTypeStatus(true);
            } else if (tpaName.equals("Medseva")) {
                validateDto.setNetworkTypeStatus(true);
                validateDto.setSumInsuredStatus(true);
            } else if (tpaName.equals("MediAssist")) {
                validateDto.setNetworkTypeStatus(true);
                validateDto.setHospitalStateStatus(true);
            } else if (tpaName.equals("GHPL")) {
                validateDto.setNetworkTypeStatus(true);
                validateDto.setHospitalStateStatus(true);
            }

            if (!validateDto.isPolicyNumberStatus()) {
                validateDto.setRemarks("No Proper Policy Number");
            }
            if (!validateDto.isClaimsNumberStatus()) {
                validateDto.setRemarks("No Proper ClaimsNumber");
            }
            if (!validateDto.isEmployeeIdStatus()) {
                validateDto.setRemarks("No Proper EmployeeId");
            }
            if (!validateDto.isEmployeeNameStatus()) {
                validateDto.setRemarks("No Proper EmployeeName");
            }
            if (!validateDto.isAgeStatus()) {
                validateDto.setRemarks("No Proper Age");
            }
            if (!validateDto.isGenderStatus()) {
                validateDto.setRemarks("No Proper Gender");
            }
            if (!validateDto.isPatientNameStatus()) {
                validateDto.setRemarks("No Proper PatientName");
            }
            if (!validateDto.isRelationshipStatus()) {
                validateDto.setRemarks("No Proper Relationship");
            }
            if (!validateDto.isClaimStatus()) {
                validateDto.setRemarks("No Proper claim status");
            }
            if (!validateDto.isDateOfClaimStatus()) {
                validateDto.setRemarks("No Proper DateOfClaim");
            }
            if (!validateDto.isClaimTypeStatus()) {
                validateDto.setRemarks("No Proper ClaimType");
            }
            if (!validateDto.isAdmissionDateStatus()) {
                validateDto.setRemarks("No Proper AdmitDate");
            }
            if (!validateDto.isDischargeDateStatus()) {
                validateDto.setRemarks("No Proper DischargeDate");
            }
            if (!validateDto.isMemberCodeStatus()) {
                validateDto.setRemarks("No Proper MemberCode");
            }


            if (!validateDto.isSumInsuredStatus()) {
                validateDto.setSumInsured("0");
                validateDto.setSumInsuredStatus(true);
            }

            if (!validateDto.isClaimedAmountStatus()) {
                validateDto.setClaimedAmount("0");
                validateDto.setClaimedAmountStatus(true);
            }

            if (!validateDto.isPaidAmountStatus()) {
                validateDto.setPaidAmount("0");
                validateDto.setPaidAmountStatus(true);
            }
            if (!validateDto.isOutstandingAmountStatus()) {
                validateDto.setOutstandingAmount("0");
                validateDto.setOutstandingAmountStatus(true);
            }

            if (validateDto.isPolicyNumberStatus() && validateDto.isClaimsNumberStatus()
                    && validateDto.isEmployeeIdStatus() && validateDto.isEmployeeNameStatus()
                    && validateDto.isGenderStatus() && validateDto.isRelationshipStatus()
                    && validateDto.isPatientNameStatus() && validateDto.isAgeStatus() && validateDto.isDiseaseStatus()
                    && validateDto.isDateOfClaimStatus() && validateDto.isSumInsuredStatus()
                    && validateDto.isClaimedAmountStatus() && validateDto.isPaidAmountStatus()
                    && validateDto.isOutstandingAmountStatus() && validateDto.isClaimTypeStatus()
                    && validateDto.isNetworkTypeStatus() && validateDto.isHospitalNameStatus()
                    && validateDto.isMemberCodeStatus() && validateDto.isPolicyStartDateStatus()
                    && validateDto.isPolicyEndDateStatus() && validateDto.isHospitalStateStatus()
                    && validateDto.isHospitalCityStatus() && validateDto.isAdmissionDateStatus()
                    && validateDto.isDischargeDateStatus() && validateDto.isClaimStatus()) {

                validateDto.setStatus(true);
            }

            if (policyNumberColumnIndexFlag && claimsNumberColumnIndexFlag && employeeIdColumnIndexFlag
                    && employeeNameColumnIndexFlag && relationshipColumnIndexFlag && genderColumnIndexFlag
                    && ageColumnIndexFlag && patientNameColumnIndexFlag && sumInsuredColumnIndexFlag
                    && claimedAmountColumnIndexFlag && paidAmountColumnIndexFlag && outstandingColumnIndexFlag
                    && claimStatusColumnIndexFlag && dateOfClaimColumnIndexFlag && claimTypeColumnIndexFlag
                    && networkTypeColumnIndexFlag && hospitalNameColumnIndexFlag && admissionDateColumnIndexFlag
                    && dischargeDateColumnIndexFlag && diseaseColumnIndexFlag && memberCodeColumnIndexFlag
                    && policyStartDateColumnIndexFlag && policyEndDateColumnIndexFlag && hospitalStateColumnIndexFlag
                    && hospitalCityColumnIndexFlag) {
                LOG.info("Blank Row");
            } else {
                claimsMisValidateData.add(validateDto);
            }

        }
    }

    @Override
    public List<ClaimsMisDataStatusValidateDto> validateClaimsMisDataWithStatus(MultipartFile file, String tpaName) {

        List<ClaimsMisDataStatusValidateDto> claimsMisValidateData = new ArrayList<>();

        try (Workbook workbook = getWorkbook(file, tpaName)) {
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

                validateBasedOnSheet(sheet, tpaName, claimsMisValidateData);
            }
        } catch (EncryptedDocumentException | IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return claimsMisValidateData;

    }

    @Override
    public String sendEmailAlongPreparedAttachment(DataToEmail dataToEmail) throws IOException, DocumentException {

        for (String data : dataToEmail.getTo()) {
            log.info("{}", data);

        }
        String rfqId = dataToEmail.getRfqId();
        List<EmailFileDTo> fileData = new LinkedList<>();
        List<String> filePath = new LinkedList<>();

        if (dataToEmail != null) {

            for (String fileName : dataToEmail.getDocumentList()) {
                if (fileName.equalsIgnoreCase("Employee details")) {
                    Optional<CoverageDetailsEntity> coverageDetailsEntityOpt = cdEBRepo.findByRfqId(rfqId);

                    if (coverageDetailsEntityOpt.get().getEmpDepDataFilePath().equalsIgnoreCase("") == false) {
                        if (coverageDetailsEntityOpt != null) {
                            CoverageDetailsEntity coverageDetailsEntity = coverageDetailsEntityOpt.get();
                            String empDepDataFilePath = coverageDetailsEntity.getEmpDepDataFilePath();
                            filePath.add(empDepDataFilePath);

                            // Read the file and convert it into a byte array

                        }
                        log.info("Employee details");
                    }

                }

                if (fileName.equalsIgnoreCase("Ageband Analysis")) {
                    Optional<CoverageDetailsEntity> coverageDetailsEntityOpt = cdEBRepo.findByRfqId(rfqId);
                    if (coverageDetailsEntityOpt.get().getEmpDepDataFilePath().equalsIgnoreCase("") == false) {

                        byte[] ageBindingReport = ageBindingReportPdfGenerator
                                .generatePdf(modelMapper.map(coverageDetailsEntityOpt, CoverageDetailsDto.class));
                        if (ageBindingReport != null) {

                            EmailFileDTo file = new EmailFileDTo();
                            file.setFileByteData(ageBindingReport);
                            file.setFilename("ageBindingReport");
                            fileData.add(file);
                        }

                        log.info("Ageband Analysis");
                    }

                }
                if (fileName.equalsIgnoreCase("rfq coverage")) {

                    if (policyTermsRepository.getPolicyTermsByRfqId(rfqId) != null) {
                        byte[] rfq_coverage = coverageDetailsPdfGenerator.generateCoverageDetails(rfqId);
                        if (rfq_coverage != null) {

                            EmailFileDTo file = new EmailFileDTo();
                            file.setFileByteData(rfq_coverage);
                            file.setFilename("rfq_coverage");
                            fileData.add(file);
                        }

                        log.info("rfq coverage");
                    }

                }
                if (fileName.equalsIgnoreCase("IRDA Details")) {

                    Optional<CorporateDetailsEntity> corporateDetailsEntity = corporateDetailsRepo.findByRfqId(rfqId);
                    CoverageDetailsEntity coverageDetails = cdEBRepo.findByRfqId(rfqId).get();
                    List<String> employeeRelations = employeeRepository.findAllRelationShipByRfqId(rfqId).orElse(null);
                    if (!corporateDetailsEntity.isEmpty()) {
                        Optional<ExpiryPolicyDetails> findByrfqId = expiryPolicyDetailsRepository.findByrfqId(rfqId);
                        Optional<ClaimsDetails> byrfqId = claimsDetailsRepository.findByrfqId(rfqId);
                        ExpiryPolicyDetails expiryDetails = null;
                        ClaimsDetails claimsDetails = null;
                        if (!findByrfqId.isEmpty()) {
                            expiryDetails = findByrfqId.get();
                        }
                        if (!byrfqId.isEmpty()) {
                            claimsDetails = byrfqId.get();
                        }

                        byte[] IRDA_Details = generator.generateEmployeeDataReport(corporateDetailsEntity.get(),
                                expiryDetails, claimsDetails, coverageDetails, employeeRelations);

                        if (IRDA_Details != null) {

                            EmailFileDTo file = new EmailFileDTo();
                            file.setFileByteData(IRDA_Details);
                            file.setFilename("IRDA_Details");
                            fileData.add(file);

                        }

                        log.info("IRDA Details");

                    }

                }

                if (fileName.equalsIgnoreCase("Mandate Letter")) {

                    CoverageDetailsEntity coverageDetailsEntityOpt1 = cdEBRepo.findByRfqId(rfqId).get();

                    if (!coverageDetailsEntityOpt1.getMandateLetterFilePath().equalsIgnoreCase("")
                            || coverageDetailsEntityOpt1.getMandateLetterFilePath() != null) {
                        String mandateLetterFilePath = coverageDetailsEntityOpt1.getMandateLetterFilePath();
                        if (!mandateLetterFilePath.isEmpty()) {
                            filePath.add(coverageDetailsEntityOpt1.getMandateLetterFilePath());
                        }

                        log.info("Mandate Letter");
                    }

                }
            }

        }

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setGetAllFiles(fileData);
        emailRequest.setTo(dataToEmail.getTo());
        emailRequest.setRfqId(rfqId);
        emailRequest.setFilePath(filePath);

        return emailAlongAttachement.sendEmailWithAttachment(emailRequest);
    }

    @Override
    public byte[] downloadMandateLetter(String rfqId) throws IOException {
        Optional<CoverageDetailsEntity> coverageDetailsEntityOpt = cdEBRepo.findByRfqId(rfqId);

        if (coverageDetailsEntityOpt.isPresent()) {
            CoverageDetailsEntity coverageDetailsEntity = coverageDetailsEntityOpt.get();

            String mandateLetterFilePath = coverageDetailsEntity.getMandateLetterFilePath();

            if (mandateLetterFilePath != null && !mandateLetterFilePath.isEmpty()) {
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

                try (FileInputStream fileInputStream = new FileInputStream(new File(mandateLetterFilePath))) {
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

    @Override
    public CoverageDetailsEntity getCoverageByRfqId(String rfqId) {
        return cdEBRepo.findByRfqId(rfqId).get();
    }

    @Override
    public DownloadTemplateAttachementDto sendEmailAlongWithDownloadTEmplate(
            DownloadTemplateAttachementDto downloadTemplateAttachementDto) {
        String[] emailSeperatedByComma = downloadTemplateAttachementDto.getEmail().split(",");
        try {
            sendEmailAlogWitDownloadedAttachement(emailSeperatedByComma);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return downloadTemplateAttachementDto;
    }

    private void sendEmailAlogWitDownloadedAttachement(String[] email) throws IOException {

        String senderEmail = username;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        jakarta.mail.Session session = Session.getInstance(properties, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setSubject("Download Template  ");

            if (email != null && !(email.length == 0)) {
                for (String recipient : email) {
                    Multipart multipart = new MimeMultipart();

                    StringBuilder emailContentBuilder = new StringBuilder();
                    emailContentBuilder.append("Dear Sir / Madam,\n\n")
                            .append("Please find attached Template. This is a Renewal Policy.\n\n")
                            .append("Kindly provide your competitive quote as per RFQ at the earliest.\n\n")
                            .append("\n\n").append("For any queries, please do not hesitate to contact us.\n\n")
                            .append("Regards,\n\n").append("Securisk").append("\nPhone number: ").append(789456123)
                            .append("\nEmail: ").append("securerisk.com");

                    String emailContent = emailContentBuilder.toString();
                    MimeBodyPart textPart = new MimeBodyPart();
                    textPart.setText(emailContent);
                    multipart.addBodyPart(textPart);

                    // Add attachments
                    MimeBodyPart excelAttachment = new MimeBodyPart();
                    excelAttachment.setDataHandler(
                            new DataHandler(new ByteArrayDataSource(resourceFile.getContentAsByteArray(),
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")));
                    excelAttachment.setFileName("template.xlsx"); // Change the filename as needed
                    multipart.addBodyPart(excelAttachment);

                    // Set the multipart content to the message
                    message.setContent(multipart);

                    // Set the recipient and send the email

                    message.setRecipients(Message.RecipientType.TO, recipient);
                    Transport.send(message);
                    log.info("email delivered");
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public byte[] getExcleSheet() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, baos);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        String pdf_file_path = null;

        document.open();

        try {
            // Add the title on top of the table
            Font titleFont = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
            Paragraph title = new Paragraph("Employee Data Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(100f);
            document.add(title);

            // Add even more space between title and table
            for (int i = 0; i < 5; i++) {
                document.add(Chunk.NEWLINE);
            }

            Font font1 = FontFactory.getFont(FontFactory.TIMES, 8, BaseColor.BLACK);
            PdfPTable table = new PdfPTable(7);
            float[] columnWidths = {0.5f, 1f, 2f, 1.3f, 1f, 0.8f, 1.5f};
            try {
                table.setWidths(columnWidths);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            Stream.of("S.No", "Employee No", "Employee Name", "Relationship", "Gender", "Age", "Sum insured")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setPhrase(new Phrase(headerTitle, font1));
                        table.addCell(header);
                    });
            String format = "EmployeeDataReport"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            pdf_file_path = "C:\\Securisk\\myFiles" + format + ".pdf";

            PdfWriter.getInstance(document, new FileOutputStream(pdf_file_path));
            document.open();

            Font font = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

            PdfPCell id = new PdfPCell(new Phrase(String.valueOf(1), font));
            id.setVerticalAlignment(Element.ALIGN_MIDDLE);
            id.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell empId = new PdfPCell(new Phrase(String.valueOf(14875), font));
            empId.setVerticalAlignment(Element.ALIGN_MIDDLE);
            empId.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell memberName = new PdfPCell(new Phrase("XYZ", font));
            memberName.setVerticalAlignment(Element.ALIGN_MIDDLE);
            memberName.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell relation = new PdfPCell(new Phrase("Employee", font));
            relation.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell gender = new PdfPCell(new Phrase("M", font));
            gender.setVerticalAlignment(Element.ALIGN_MIDDLE);
            gender.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell age = new PdfPCell(new Phrase(String.valueOf(35), font));
            age.setVerticalAlignment(Element.ALIGN_MIDDLE);
            age.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell sumInsured = new PdfPCell(new Phrase(String.valueOf(300000), font));
            sumInsured.setVerticalAlignment(Element.ALIGN_MIDDLE);
            sumInsured.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(id);
            table.addCell(empId);
            table.addCell(memberName);
            table.addCell(relation);
            table.addCell(gender);
            table.addCell(age);
            table.addCell(sumInsured);

            table.setWidthPercentage(100);
            document.add(table);

            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    @Override
    public String uploadFileCoverage(CoverageUploadDto coverageUploadDto) {

        CoverageUploadEntity uploadEntity = new CoverageUploadEntity();
        List<EmpDependentHeaders> empDepHeaders = empDepHeaderRepo.findAll();
        String fileName = coverageUploadDto.getFile().getOriginalFilename().replace(" ", "");
        if (coverageUploadDto.getFileType().equals("EmpDepData")) {
            // store excel data in table
            List<EmployeeDepedentDetailsEntity> findByRfqId = employeeRepository
                    .findByRfqId(coverageUploadDto.getRfqId());
            if (!findByRfqId.isEmpty()) {
                for (EmployeeDepedentDetailsEntity employeeDepedentDetailsEntity : findByRfqId) {
                    employeeRepository.hardDeleteByRfqId(employeeDepedentDetailsEntity.getRfqId());
                }
            }
            List<EmployeeDepedentDetailsEntity> employees = new ExcelUtils().storeEmployeeDataANDfile(
                    coverageUploadDto.getFile(), coverageUploadDto.getFileType(), coverageUploadDto.getRfqId(),
                    empDepHeaders);
            log.info("Employees :: " + employees);
            employeeRepository.saveAll(employees);

            // store in excel file in db
            File folder = new File(mainpath);
            File EmpDepDataFileDest = new File(folder.getAbsolutePath(),
                    "EmpDepData" + RandomStringUtils.random(10, true, false) + fileName);
            if (!coverageUploadDto.getFile().isEmpty() && !EmpDepDataFileDest.exists()) {
                log.info("------  file Upload ----------");
                try {
                    coverageUploadDto.getFile().transferTo(EmpDepDataFileDest);
                    // Create a FileInputStream to read the file
                    FileInputStream fileInputStream = new FileInputStream(EmpDepDataFileDest.getAbsolutePath());

                    // Create a ByteArrayOutputStream to store the bytes
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    // Read data from the file and write it to the ByteArrayOutputStream
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    // Close the streams
                    fileInputStream.close();
                    byteArrayOutputStream.close();

                    // Get the byte array
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    uploadEntity.setEmployeeDepdenentData(byteArray);
                    fileUploadRepo.save(uploadEntity);
                    return EmpDepDataFileDest.getAbsolutePath();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("------  file not Upload ----------");
                LOG.info(coverageUploadDto.getFile().getOriginalFilename(), " already exists !!");
            }
            return EmpDepDataFileDest.getAbsolutePath();
        } else if (coverageUploadDto.getFileType().equals("MandateLetter")) {
            log.info("------  MandateLetter ----------");
            File folder = new File(mainpath);
            File mandateLetterFileDest = new File(folder.getAbsolutePath(),
                    "MandateLetterFile" + RandomStringUtils.random(10, true, false) + fileName);
            if (!coverageUploadDto.getFile().isEmpty() && !mandateLetterFileDest.exists()) {
                log.info("------  file Upload ----------");
                try {
                    coverageUploadDto.getFile().transferTo(mandateLetterFileDest);
                    // Create a FileInputStream to read the file
                    FileInputStream fileInputStream = new FileInputStream(mandateLetterFileDest.getAbsolutePath());

                    // Create a ByteArrayOutputStream to store the bytes
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    // Read data from the file and write it to the ByteArrayOutputStream
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    // Close the streams
                    fileInputStream.close();
                    byteArrayOutputStream.close();

                    // Get the byte array
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    uploadEntity.setMandateLetter(byteArray);
                    fileUploadRepo.save(uploadEntity);
                    return mandateLetterFileDest.getAbsolutePath();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("------  file not Upload ----------");
                LOG.info(coverageUploadDto.getFile().getOriginalFilename(), " already exists !!");
            }
        } else if (coverageUploadDto.getFileType().equals("CoveragesSought")) {
            log.info("------  CoveragesSought ----------");
            File folder = new File(mainpath);
            File CoveragesSoughtDest = new File(folder.getAbsolutePath(),
                    "CoveragesSought" + RandomStringUtils.random(10, true, false) + fileName);
            if (!coverageUploadDto.getFile().isEmpty() && !CoveragesSoughtDest.exists()) {
                log.info("------  file Upload ----------");
                try {
                    coverageUploadDto.getFile().transferTo(CoveragesSoughtDest);
                    // Create a FileInputStream to read the file
                    FileInputStream fileInputStream = new FileInputStream(CoveragesSoughtDest.getAbsolutePath());

                    // Create a ByteArrayOutputStream to store the bytes
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    // Read data from the file and write it to the ByteArrayOutputStream
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    // Close the streams
                    fileInputStream.close();
                    byteArrayOutputStream.close();

                    // Get the byte array
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    // Now the byteArray contains the contents of the file as bytes
                    log.info("File converted to byte array with length: " + byteArray.length);
                    uploadEntity.setCoveragesSought(byteArray);
                    fileUploadRepo.save(uploadEntity);
                    return CoveragesSoughtDest.getAbsolutePath();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }

            } else {
                log.info("------  file not Upload  ----------");
                LOG.info(coverageUploadDto.getFile().getOriginalFilename(), " already exists !!");
            }
        } else if (coverageUploadDto.getFileType().equals("ClaimsMis")) {

            List<ClaimsMisEntity> findByRfqId = claimsMisRepo.findByRfqId(coverageUploadDto.getRfqId());
            if (!findByRfqId.isEmpty()) {
                for (ClaimsMisEntity claimsMisEntity : findByRfqId) {
                    claimsMisRepo.hardDeleteByRfqId(claimsMisEntity.getRfqId());
                }
            }

            List<ClaimsMisEntity> claimsMisData = excelUtils.storeClaimsMisANDfile(coverageUploadDto.getFile(),
                    coverageUploadDto.getFileType(), coverageUploadDto.getTpaName(), coverageUploadDto.getRfqId());
            ;
            claimsMisRepo.saveAll(claimsMisData);

            File folder = new File(mainpath);
            File ClaimsMisDest = new File(folder.getAbsolutePath(),
                    "ClaimsMis" + RandomStringUtils.random(10, true, false) + fileName);
            if (!coverageUploadDto.getFile().isEmpty() && !ClaimsMisDest.exists()) {
                log.info("------  file Upload ----------");
                try {
                    coverageUploadDto.getFile().transferTo(ClaimsMisDest);
                    // Create a FileInputStream to read the file
                    FileInputStream fileInputStream = new FileInputStream(ClaimsMisDest.getAbsolutePath());

                    // Create a ByteArrayOutputStream to store the bytes
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    // Read data from the file and write it to the ByteArrayOutputStream
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    // Close the streams
                    fileInputStream.close();
                    byteArrayOutputStream.close();

                    // Get the byte array
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    // Now the byteArray contains the contents of the file as bytes
                    log.info("File converted to byte array with length: " + byteArray.length);
                    uploadEntity.setClaimsMis(byteArray);
                    fileUploadRepo.save(uploadEntity);
                    return ClaimsMisDest.getAbsolutePath();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }

            } else {
                log.info("------  file not Upload  ----------");
                LOG.info(coverageUploadDto.getFile().getOriginalFilename(), " already exists !!");
            }
        } else if (coverageUploadDto.getFileType().equals("ClaimsSummary")) {
            log.info("------  ClaimsSummary ----------");
            File folder = new File(mainpath);
            File claimsSummaryFileDest = new File(folder.getAbsolutePath(),
                    "ClaimsSummaryFile" + RandomStringUtils.random(10, true, false) + fileName);
            if (!coverageUploadDto.getFile().isEmpty() && !claimsSummaryFileDest.exists()) {
                log.info("------  file Upload ----------");
                try {
                    coverageUploadDto.getFile().transferTo(claimsSummaryFileDest);
                    // Create a FileInputStream to read the file
                    FileInputStream fileInputStream = new FileInputStream(claimsSummaryFileDest.getAbsolutePath());

                    // Create a ByteArrayOutputStream to store the bytes
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    // Read data from the file and write it to the ByteArrayOutputStream
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    // Close the streams
                    fileInputStream.close();
                    byteArrayOutputStream.close();

                    // Get the byte array
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    uploadEntity.setClaimsSummary(byteArray);
                    fileUploadRepo.save(uploadEntity);
                    return claimsSummaryFileDest.getAbsolutePath();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("------  file not Upload ----------");
                LOG.info(coverageUploadDto.getFile().getOriginalFilename(), " already exists !!");
            }
        } else {
            // Non-GHI products
            log.info("------  Non-GHI ----------");
            File folder = new File(mainpath);
            File claimsSummaryFileDest = new File(folder.getAbsolutePath(),
                    "Non-GHI" + RandomStringUtils.random(10, true, false) + fileName);
            if (!coverageUploadDto.getFile().isEmpty() && !claimsSummaryFileDest.exists()) {
                log.info("------  file Upload ----------");
                try {
                    coverageUploadDto.getFile().transferTo(claimsSummaryFileDest);
                    // Create a FileInputStream to read the file
                    FileInputStream fileInputStream = new FileInputStream(claimsSummaryFileDest.getAbsolutePath());

                    // Create a ByteArrayOutputStream to store the bytes
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    // Read data from the file and write it to the ByteArrayOutputStream
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    // Close the streams
                    fileInputStream.close();
                    byteArrayOutputStream.close();

                    // Get the byte array
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    uploadEntity.setClaimsSummary(byteArray);
                    uploadEntity.setFileName(coverageUploadDto.getFileName());
                    fileUploadRepo.save(uploadEntity);
                    return claimsSummaryFileDest.getAbsolutePath();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("------  file not Upload ----------");
                LOG.info(coverageUploadDto.getFile().getOriginalFilename(), " already exists !!");
            }
        }
        return null;

    }

    @Override
    public byte[] downloadClaimMISC(String rfqId) throws IOException {
        CoverageDetailsEntity coverageDetailsEntityOpt = cdEBRepo.findByRfqId(rfqId).get();
        String claimMiscFilePath = coverageDetailsEntityOpt.getClaimsMiscFilePath();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

        FileInputStream fileInputStream = new FileInputStream(new File(claimMiscFilePath));
        byte[] buffer = new byte[1024];
        int read;
        while ((read = fileInputStream.read(buffer)) != -1) {
            arrayOutputStream.write(buffer, 0, read);
        }

        return arrayOutputStream.toByteArray();
    }

    @Override
    public List<Object[]> getRfqCounts() {
        return claimsMisRepo.statusCount();
    }

    @Override
    public ClaimsUploadDto getClaimsAferUpload(String rfqId) {
        return claimsMisRepo.getClaimsDetailsAfterUpload(rfqId).stream().distinct().map(c -> {
            ClaimsUploadDto claimsUploadDto = new ClaimsUploadDto();
            claimsUploadDto.setPolicyNumber(c.getPolicyNumber());

            // Convert Date to LocalDate
            LocalDate startDate = c.getPolicyStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = c.getPolicyEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Format LocalDate to String in YYYY-MM-DD format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            claimsUploadDto.setStartDate(startDate.format(formatter));
            claimsUploadDto.setEndDate(endDate.format(formatter));

            return claimsUploadDto;
        }).findFirst().orElse(null);
    }

    @Override
    public ClaimsDumpDto getClaimsDump(String rfqId) {

        List<ClaimsMisEntity> claimsDump = claimsMisRepo.findByRfqId(rfqId);

        List<String> settled = Arrays.asList("Settled", "APPROVED AND PAID", "APPROVED", "Claim Paid", "Paid");
        List<String> lowerSettled = settled.stream().map(String::toLowerCase).toList();

        List<String> underProcessing = Arrays.asList("Under Process", "Underprocess", "Under Query", "Cheque Pending",
                "Intimation received", "Claim Bills Pending", "Deficiency", "OUTSTANDING", "Claim document awaited",
                "Pending claim adjudication", "Processed ready for payment",
                "Information / query pending from hospital - FINAL", "Pending for data entry", "Query", "In Process",
                "PreAuth Issued", "Intimation Registered", "Pending", "Approved (Enhancement)", "Approved (Pre Auth)",
                "SHORTFALL", "BILLS PENDING", "CHEQUE PENDING");
        List<String> lowerUnderProcessing = underProcessing.stream().map(String::toLowerCase)
                .toList();

        List<String> cashLess = List.of("Cashless");
        List<String> lowerCashLess = cashLess.stream().map(String::toLowerCase).toList();

        List<String> reimbursement = Arrays.asList("Reimbursement", "Member");
        List<String> lowerReimbursement = reimbursement.stream().map(String::toLowerCase).toList();

//  claims paid settled and Reimbursement
        double reimbursementPaidTotal = claimsDump.parallelStream()
                .filter(claim -> lowerSettled.contains(claim.getClaimStatus().toLowerCase())
                        && lowerReimbursement.contains(claim.getClaimType().toLowerCase()))
                .mapToDouble(ClaimsMisEntity::getPaidAmount).sum();

//  claims paid settled and Cashless
        double cashLessPaidTotal = claimsDump.parallelStream()
                .filter(claim -> lowerSettled.contains(claim.getClaimStatus().toLowerCase())
                        && lowerCashLess.contains(claim.getClaimType().toLowerCase()))
                .mapToDouble(ClaimsMisEntity::getPaidAmount).sum();

//  OutStanding Under process and Cashless
        double cashLessOutStandingTotal = claimsDump.parallelStream()
                .filter(claim -> lowerUnderProcessing.contains(claim.getClaimStatus().toLowerCase())
                        && lowerCashLess.contains(claim.getClaimType().toLowerCase()))
                .mapToDouble(ClaimsMisEntity::getPaidAmount).sum();

//  OutStanding Under process and Reimbursement
        double reimbursementOutStandingTotal = claimsDump.parallelStream()
                .filter(claim -> lowerUnderProcessing.contains(claim.getClaimStatus().toLowerCase())
                        && lowerReimbursement.contains(claim.getClaimType().toLowerCase()))
                .mapToDouble(ClaimsMisEntity::getPaidAmount).sum();

        return ClaimsDumpDto.builder().claimPaidReimbursement(reimbursementPaidTotal)
                .claimsPaidCashless(cashLessPaidTotal).claimsOutStandingReimbursement(reimbursementOutStandingTotal)
                .claimsOutStandingCashless(cashLessOutStandingTotal).build();
    }

    @Override
    public List<CoverageRemarksUploadDto> getAllRemarks(
            List<CoverageDetailsChildValidateValuesDto> coverageDetailsChildValidateValuesDtos) {
        return coverageDetailsChildValidateValuesDtos.stream()
                .filter(i -> i.getRemarks() != null && !i.isEmployeeNameValidationStatus()
                        || !i.isGenderValidationStatus() || !i.isAgeValidationStatus()
                        || !i.isRelationshipValidationStatus() || !i.isSumInsuredValidationStatus())
                .map(i -> {
                    CoverageRemarksUploadDto coverageRemarksUploadDto = new CoverageRemarksUploadDto();
                    coverageRemarksUploadDto.setRemarks(i.getRemarks());
                    coverageRemarksUploadDto.setEmployeeIdValue(i.getEmployeeIdValue());
                    coverageRemarksUploadDto.setAgeValue(i.getAgeValue());
                    coverageRemarksUploadDto.setEmployeeIdValue(i.getEmployeeIdValue());
                    coverageRemarksUploadDto.setSumInsuredValue(i.getSumInsuredValue());
                    coverageRemarksUploadDto.setEmployeeNameValue(i.getEmployeeNameValue());
                    coverageRemarksUploadDto.setRelationshipValue(i.getRelationshipValue());
                    return coverageRemarksUploadDto;
                }).toList();
    }

}
