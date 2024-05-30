package com.insure.rfq.utils;

import com.insure.rfq.dto.ClaimsMisDataStatusValidateDto;
import com.insure.rfq.dto.EmpDepdentValidationDto;
import com.insure.rfq.entity.*;
import com.insure.rfq.repository.ClaimsMisRepository;
import com.insure.rfq.repository.EmpDependentRepository;
import com.insure.rfq.repository.TpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class ExcelUtils {

    @Autowired
    private EmpDependentRepository empDepRepo;

    @Autowired
    private ClaimsMisRepository claimsRepo;

    @Autowired
    private TpaRepository tpaRepo;

    private static final Logger LOG = LoggerFactory.getLogger(ExcelUtils.class);

    int snoColumnIndex = -1;
    int employeeIdColumnIndex = -1;
    int employeeNameColumnIndex = -1;
    int relationshipColumnIndex = -1;
    int genderColumnIndex = -1;
    int ageColumnIndex = -1;
    int dateOfBirthColumnIndex = -1;
    int sumInsuredColumnIndex = -1;

    public List<EmployeeDepedentDetailsEntity> storeEmployeeDataANDfile(MultipartFile file, String fileName,
                                                                        String rfqId, List<EmpDependentHeaders> empDepHeaders) {

        List<EmployeeDepedentDetailsEntity> employees = new ArrayList<>();

        // Employee Dependent File columns index arranging properly starts
        String originalFileName = file.getOriginalFilename();
        EmpDepdentValidationDto empDepdentValidationDto = new EmpDepdentValidationDto();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = null;
            sheet = workbook.getSheetAt(0);

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
                        }
                    }

                    columnIndex++;
                }
            }

        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }

        // Employee Dependent File Data capturing starts

        String employeeId;
        String TempEmpID = null;
        double sumInsured;
        double TempSumIssured;

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

            LOG.info("employeeIdColumnIndex :: {}"
                    , employeeIdColumnIndex);
            LOG.info("employeeNameColumnIndex :: {}"
                    , employeeNameColumnIndex);
            LOG.info("relationshipColumnIndex :: {}"
                    , relationshipColumnIndex);
            LOG.info("genderColumnIndex :: {}"
                    , genderColumnIndex);
            LOG.info("ageColumnIndex :: {}"
                    , ageColumnIndex);
            LOG.info("dateOfBirthColumnIndex :: {}"
                    , dateOfBirthColumnIndex);

            if (headerRow != null) {
                for (Row dataRow : sheet) {
                    int headerNum = headerRow.getRowNum();
                    int rowNum = dataRow.getRowNum();
                    if (rowNum > headerNum) {

                        EmployeeDepedentDetailsEntity employee = new EmployeeDepedentDetailsEntity();

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

                        if (employeeIdColumnIndex >= 0) {
                            Cell employeeIdCell = dataRow.getCell(employeeIdColumnIndex);

                            if (employeeIdCell.getCellType() == CellType.STRING) {
                                employeeId = dataRow.getCell(employeeIdColumnIndex).getStringCellValue();
                                employee.setEmployeeId(employeeId);
                                TempEmpID = employeeId;
                            } else if (employeeIdCell.getCellType() == CellType.NUMERIC) {
                                double numericCellValue = dataRow.getCell(employeeIdColumnIndex).getNumericCellValue();
                                // Using DecimalFormat to remove decimal part
                                DecimalFormat df = new DecimalFormat("#");
                                employeeId = df.format(numericCellValue);
                                TempEmpID = employeeId;
                                employee.setEmployeeId(employeeId);
                            } else if (employeeIdCell.getCellType() == CellType.BLANK) {
                                employeeId = TempEmpID;
                                employee.setEmployeeId(employeeId);
                            } else if (employeeIdCell.getCellType() == CellType._NONE) {
                                employeeId = TempEmpID;
                                employee.setEmployeeId(employeeId);
                            } else if (employeeIdCell.getCellType() == CellType.BOOLEAN) {
                                LOG.info("Employee Id Boolean ::{}"
                                        , dataRow.getCell(employeeIdColumnIndex).getBooleanCellValue());
                            } else if (employeeIdCell.getCellType() == CellType.ERROR) {
                                LOG.info("Employee Id Error ::{}"
                                        , dataRow.getCell(employeeIdColumnIndex).getErrorCellValue());
                            } else if (employeeIdCell.getCellType() == CellType.FORMULA) {
                                switch (employeeIdCell.getCachedFormulaResultType()) {
                                    case NUMERIC: {
                                        employeeId = String
                                                .valueOf(dataRow.getCell(employeeIdColumnIndex).getNumericCellValue());
                                        TempEmpID = employeeId;
                                        employee.setEmployeeId(employeeId);
                                        break;
                                    }
                                    case STRING: {
                                        employeeId = dataRow.getCell(employeeIdColumnIndex).getStringCellValue();
                                        employee.setEmployeeId(employeeId);
                                        TempEmpID = employeeId;
                                        break;
                                    }
                                    case BLANK: {
                                        employeeId = TempEmpID;
                                        employee.setEmployeeId(employeeId);
                                        break;
                                    }
                                    case _NONE: {
                                        employeeId = TempEmpID;
                                        employee.setEmployeeId(employeeId);
                                        break;
                                    }
                                    case BOOLEAN: {
                                        LOG.info("Employee Id Boolean Formula::{}"
                                                , dataRow.getCell(employeeIdColumnIndex).getBooleanCellValue());
                                        break;
                                    }
                                    case ERROR: {
                                        LOG.info("Employee Id Error ::{}"
                                                , dataRow.getCell(employeeIdColumnIndex).getErrorCellValue());
                                        break;
                                    }
                                    default:
                                        throw new IllegalArgumentException(
                                                "Unexpected value: " + employeeIdCell.getCachedFormulaResultType());
                                }
                            }
                        }

                        if (employeeNameColumnIndex >= 0) {
                            String employeeName;
                            Cell employeeNameCell = dataRow.getCell(employeeNameColumnIndex);
                            if (employeeNameCell.getCellType() == CellType.STRING) {
                                employeeName = dataRow.getCell(employeeNameColumnIndex).getStringCellValue();
                                employee.setEmployeeName(employeeName);
                            } else if (employeeNameCell.getCellType() == CellType.NUMERIC) {
                                LOG.info("Employee Name Numeric ::{}"
                                        ,dataRow.getCell(employeeNameColumnIndex).getNumericCellValue());
                            } else if (employeeNameCell.getCellType() == CellType.BLANK) {
                                LOG.info("Employee Name Blank");
                            } else if (employeeNameCell.getCellType() == CellType._NONE) {
                                LOG.info("Employee Name None");
                            } else if (employeeNameCell.getCellType() == CellType.BOOLEAN) {
                                LOG.info("Employee Name Boolean :: {}"
                                        , dataRow.getCell(employeeNameColumnIndex).getBooleanCellValue());
                            } else if (employeeNameCell.getCellType() == CellType.ERROR) {
                                LOG.info("Employee Name Error :: {}"
                                        , dataRow.getCell(employeeNameColumnIndex).getErrorCellValue());
                            } else if (employeeNameCell.getCellType() == CellType.FORMULA) {
                                switch (employeeNameCell.getCachedFormulaResultType()) {
                                    case NUMERIC: {
                                        LOG.info("Employee Name Numeric Formula :: {}"
                                                , dataRow.getCell(employeeNameColumnIndex).getNumericCellValue());
                                        break;
                                    }
                                    case STRING: {
                                        employeeName = dataRow.getCell(employeeNameColumnIndex).getStringCellValue();
                                        employee.setEmployeeName(employeeName);
                                        break;
                                    }
                                    case BLANK: {
                                        LOG.info("Employee Name Blank Formula");
                                        break;
                                    }
                                    case _NONE: {
                                        LOG.info("Employee Name None Formula");
                                        break;
                                    }
                                    case BOOLEAN: {
                                        LOG.info("Employee Name Boolean :: {}"
                                                , dataRow.getCell(employeeNameColumnIndex).getBooleanCellValue());
                                        break;
                                    }
                                    case ERROR: {
                                        LOG.info("Employee Name Error :: {}"
                                                , dataRow.getCell(employeeNameColumnIndex).getErrorCellValue());
                                        break;
                                    }
                                    default:
                                        throw new IllegalArgumentException(
                                                "Unexpected value: " + employeeNameCell.getCachedFormulaResultType());
                                }
                            }
                        }

                        if (relationshipColumnIndex >= 0) {
                            String relationship;

                            Cell relationshipCell = dataRow.getCell(relationshipColumnIndex);
                            if (relationshipCell.getCellType() == CellType.STRING) {
                                relationship = dataRow.getCell(relationshipColumnIndex).getStringCellValue();
                                employee.setRelationship(relationship);
                            } else if (relationshipCell.getCellType() == CellType.NUMERIC) {
                                LOG.info("Relationship Numeric :: {}"
                                        ,dataRow.getCell(relationshipColumnIndex).getNumericCellValue());
                            } else if (relationshipCell.getCellType() == CellType.BLANK) {
                                LOG.info("Relationship BLANK :: ");
                            } else if (relationshipCell.getCellType() == CellType._NONE) {
                                LOG.info("Relationship NONE :: ");
                            } else if (relationshipCell.getCellType() == CellType.BOOLEAN) {
                                LOG.info("Relationship Boolean :: {}"
                                        , dataRow.getCell(relationshipColumnIndex).getBooleanCellValue());
                            } else if (relationshipCell.getCellType() == CellType.ERROR) {
                                LOG.info("Relationship Error :: ");
                            } else if (relationshipCell.getCellType() == CellType.FORMULA) {
                                switch (relationshipCell.getCachedFormulaResultType()) {
                                    case NUMERIC: {
                                        LOG.info("Relationship Numeric Formula ::{}"
                                                , dataRow.getCell(relationshipColumnIndex).getNumericCellValue());
                                        break;
                                    }
                                    case STRING: {
                                        relationship = dataRow.getCell(relationshipColumnIndex).getStringCellValue();
                                        employee.setRelationship(relationship);
                                        break;
                                    }
                                    case BLANK: {
                                        LOG.info("Relationship BLANK Formula :: ");
                                        break;
                                    }
                                    case _NONE: {
                                        LOG.info("Relationship NONE Formula :: ");
                                        break;
                                    }
                                    case BOOLEAN: {
                                        LOG.info("Relationship Boolean Formula :: "
                                                + dataRow.getCell(relationshipColumnIndex).getBooleanCellValue());
                                        break;
                                    }
                                    case ERROR: {
                                        LOG.info("Relationship Error Formula :: ");
                                        break;
                                    }
                                    default:
                                        throw new IllegalArgumentException(
                                                "Unexpected value: " + relationshipCell.getCachedFormulaResultType());
                                }
                            }
                        }

                        if (genderColumnIndex >= 0) {
                            String gender;
                            Cell genderCell = dataRow.getCell(genderColumnIndex);
                            if (genderCell.getCellType() == CellType.STRING) {
                                gender = dataRow.getCell(genderColumnIndex).getStringCellValue().trim()
                                        .replaceAll("\\p{C}", "").replaceAll("\\s", "").replaceAll("[^\\p{Print}]", "");
                                employee.setGender(gender);
                            } else if (genderCell.getCellType() == CellType.NUMERIC) {
                                LOG.info("Gender numermic :: {} "
                                        , dataRow.getCell(genderColumnIndex).getNumericCellValue());
                            } else if (genderCell.getCellType() == CellType.BLANK) {
                                LOG.info("Gender blank ::");
                            } else if (genderCell.getCellType() == CellType._NONE) {
                                LOG.info("Gender none ::");
                            } else if (genderCell.getCellType() == CellType.BOOLEAN) {
                                LOG.info("Gender boolean :: {} "
                                        , dataRow.getCell(genderColumnIndex).getBooleanCellValue());
                            } else if (genderCell.getCellType() == CellType.ERROR) {
                                LOG.info("Gender boolean :: {}"
                                        , dataRow.getCell(genderColumnIndex).getErrorCellValue());
                            } else if (genderCell.getCellType() == CellType.FORMULA) {
                                switch (genderCell.getCachedFormulaResultType()) {
                                    case NUMERIC: {
                                        LOG.info("Gender numermic :: {}"
                                                , dataRow.getCell(genderColumnIndex).getNumericCellValue());
                                        break;
                                    }
                                    case STRING: {
                                        gender = dataRow.getCell(genderColumnIndex).getStringCellValue().trim()
                                                .replaceAll("\\p{C}", "").replaceAll("\\s", "")
                                                .replaceAll("[^\\p{Print}]", "");
                                        employee.setGender(gender);
                                        break;
                                    }
                                    case BLANK: {
                                        LOG.info("Gender Formula blank ::");
                                        break;
                                    }
                                    case _NONE: {
                                        LOG.info("Gender Formula none ::");
                                        break;
                                    }
                                    case BOOLEAN: {
                                        LOG.info("Gender Formula boolean :: {}"
                                                , dataRow.getCell(genderColumnIndex).getBooleanCellValue());
                                        break;
                                    }
                                    case ERROR: {
                                        LOG.info("Gender Formula error :: {}"
                                                , dataRow.getCell(genderColumnIndex).getErrorCellValue());
                                        break;
                                    }
                                    default:
                                        throw new IllegalArgumentException(
                                                "Unexpected value: " + genderCell.getCachedFormulaResultType());
                                }
                            }
                        }

                        if (ageColumnIndex >= 0) {
                            String age;
                            Cell ageCell = dataRow.getCell(ageColumnIndex);
                            if (ageCell.getCellType() == CellType.NUMERIC) {
                                double numericCellValue = ageCell.getNumericCellValue();
                                // Using DecimalFormat to remove decimal part
                                DecimalFormat df = new DecimalFormat("#");
                                age = df.format(numericCellValue);
                                employee.setAge(age);
                            } else if (ageCell.getCellType() == CellType.STRING) {
                                LOG.info("Age String :: {}"
                                        , ageCell.getStringCellValue());
                            } else if (ageCell.getCellType() == CellType.BLANK) {
                                LOG.info("Age Blank :: ");
                            } else if (ageCell.getCellType() == CellType.BOOLEAN) {
                                LOG.info("Age String :: {}"
                                        , ageCell.getBooleanCellValue());
                            } else if (ageCell.getCellType() == CellType.ERROR) {
                                LOG.info("Age String :: {}"
                                        , ageCell.getErrorCellValue());
                            } else if (ageCell.getCellType() == CellType._NONE) {
                                LOG.info("Age None :: ");
                            } else if (ageCell.getCellType() == CellType.FORMULA) {
                                switch (ageCell.getCachedFormulaResultType()) {
                                    case NUMERIC: {
                                        double numericCellValue = ageCell.getNumericCellValue();
                                        // Using DecimalFormat to remove decimal part
                                        DecimalFormat df = new DecimalFormat("#");
                                        age = df.format(numericCellValue);
                                        employee.setAge(age);
                                        break;
                                    }
                                    case STRING: {
                                        LOG.info("Age String :: {}"
                                                , ageCell.getStringCellValue());
                                        break;
                                    }
                                    case BLANK: {
                                        LOG.info("Age Blank :: ");
                                        break;
                                    }
                                    case BOOLEAN: {
                                        LOG.info("Age String ::{}"
                                                , ageCell.getBooleanCellValue());
                                        break;
                                    }
                                    case ERROR: {
                                        LOG.info("Age String :: {}"
                                                , ageCell.getErrorCellValue());
                                        break;
                                    }
                                    case _NONE: {
                                        LOG.info("Age None :: ");
                                        break;
                                    }
                                    default:
                                        throw new IllegalArgumentException(
                                                "Unexpected value: " + ageCell.getCachedFormulaResultType());
                                }

                            }
                        }

                        if (dateOfBirthColumnIndex >= 0) {
                            Date dateOfBirth;
                            Cell dateOfBirthCell = dataRow.getCell(dateOfBirthColumnIndex);
                            if (dateOfBirthCell.getCellType() == CellType.NUMERIC) {
                                dateOfBirth = dateOfBirthCell.getDateCellValue();
                                SimpleDateFormat outputFormat = new SimpleDateFormat(
                                        ExcelValidation.UNIVERSAL_DATE_FORMAT.getValue());
                                String dateOfBirthStr = outputFormat.format(dateOfBirthCell.getDateCellValue());
                                dateOfBirth = outputFormat.parse(dateOfBirthStr);
                                employee.setDateOfBirth(dateOfBirth);
                            } else if (dateOfBirthCell.getCellType() == CellType.STRING) {
                                LOG.info("Date Of Birth String ::{}"
                                        , dateOfBirthCell.getStringCellValue());
                            } else if (dateOfBirthCell.getCellType() == CellType.BLANK) {
                                LOG.info("Date Of Birth Blank ");
                            } else if (dateOfBirthCell.getCellType() == CellType.BOOLEAN) {
                                LOG.info("Date Of Birth Boolean :: {}"
                                        , dateOfBirthCell.getBooleanCellValue());
                            } else if (dateOfBirthCell.getCellType() == CellType.ERROR) {
                                LOG.info("Date Of Birth Error :: {}"
                                        , dateOfBirthCell.getErrorCellValue());
                            } else if (dateOfBirthCell.getCellType() == CellType._NONE) {
                                LOG.info("Date Of Birth None ");
                            } else if (dateOfBirthCell.getCellType() == CellType.FORMULA) {
                                switch (dateOfBirthCell.getCachedFormulaResultType()) {
                                    case NUMERIC: {
                                        dateOfBirth = dateOfBirthCell.getDateCellValue();
                                        SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                ExcelValidation.UNIVERSAL_DATE_FORMAT.getValue());
                                        String dateOfBirthStr = outputFormat.format(dateOfBirthCell.getDateCellValue());
                                        dateOfBirth = outputFormat.parse(dateOfBirthStr);
                                        employee.setDateOfBirth(dateOfBirth);
                                        break;
                                    }
                                    case STRING: {
                                        LOG.info("Date Of Birth String Formula ::{}"
                                                , dateOfBirthCell.getStringCellValue());
                                        break;
                                    }
                                    case BLANK: {
                                        LOG.info("Date Of Birth Blank Formula ");
                                        break;
                                    }
                                    case BOOLEAN: {
                                        LOG.info("Date Of Birth Boolean :: {}"
                                                , dateOfBirthCell.getBooleanCellValue());
                                        break;
                                    }
                                    case ERROR: {
                                        LOG.info("Date Of Birth Error Formula :: {}"
                                                , dateOfBirthCell.getErrorCellValue());
                                        break;
                                    }
                                    case _NONE: {
                                        LOG.info("Date Of Birth None Formula");
                                        break;
                                    }
                                    default:
                                        throw new IllegalArgumentException(
                                                "Unexpected value: " + dateOfBirthCell.getCachedFormulaResultType());
                                }

                            }
                        }

                        if (sumInsuredColumnIndex >= 0) {
                            Cell sumInsuredCell = dataRow.getCell(sumInsuredColumnIndex);
                            if (sumInsuredCell.getCellType() == CellType.NUMERIC) {
                                sumInsured = sumInsuredCell.getNumericCellValue();
                                TempSumIssured = sumInsured;
                                employee.setSumInsured(sumInsured);
                            } else if (sumInsuredCell.getCellType() == CellType.STRING) {
                                LOG.info("Sum Insured String :: {}"
                                        , sumInsuredCell.getStringCellValue());
                            } else if (sumInsuredCell.getCellType() == CellType.BLANK) {
                                LOG.info("Sum Insured Blank");
                            } else if (sumInsuredCell.getCellType() == CellType.BOOLEAN) {
                                LOG.info("Sum Insured Boolean :: {}"
                                        , sumInsuredCell.getBooleanCellValue());
                            } else if (sumInsuredCell.getCellType() == CellType.ERROR) {
                                LOG.info("Sum Insured Error :: {}"
                                        , sumInsuredCell.getErrorCellValue());
                            } else if (sumInsuredCell.getCellType() == CellType._NONE) {
                                LOG.info("Sum Insured None");
                            } else if (sumInsuredCell.getCellType() == CellType.FORMULA) {
                                switch (sumInsuredCell.getCachedFormulaResultType()) {
                                    case NUMERIC: {
                                        sumInsured = sumInsuredCell.getNumericCellValue();
                                        TempSumIssured = sumInsured;
                                        employee.setSumInsured(sumInsured);
                                        break;
                                    }
                                    case STRING: {
                                        LOG.info("Sum Insured String Formula ::{}"
                                                , sumInsuredCell.getStringCellValue());
                                        break;
                                    }
                                    case BLANK: {
                                        LOG.info("Sum Insured Blank Formula");
                                        break;
                                    }
                                    case BOOLEAN: {
                                        LOG.info("Sum Insured Boolean Formula ::{}"
                                                , sumInsuredCell.getBooleanCellValue());
                                        break;
                                    }
                                    case ERROR: {
                                        LOG.info("Sum Insured Error Formula::{}"
                                                , sumInsuredCell.getErrorCellValue());
                                        break;
                                    }
                                    case _NONE: {
                                        LOG.info("Sum Insured None");
                                        break;
                                    }
                                    default:
                                        throw new IllegalArgumentException(
                                                "Unexpected value: " + sumInsuredCell.getCachedFormulaResultType());
                                }

                            }
                        }

                        employee.setRfqId(rfqId);
                        employee.setRecordStatus("ACTIVE");
                        employee.setCreatedDate(new Date());
                        employees.add(employee);
                    }
                }
            }

        } catch (EncryptedDocumentException | IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }

        return employees;
    }

    public List<ClaimsMisEntity> storeClaimsMisANDfile(MultipartFile file, String fileName, String tpaName,
                                                       String rfqId) {


        List<ClaimsMisEntity> claimsMisData = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = null;
            if (tpaName.equals("HealthIndia")) {
                sheet = workbook.getSheetAt(0);
            } else if (tpaName.equals("Vipul")) {
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
            } else if (tpaName.equals("RCare")) {
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
                        System.out.println("policyNumberColumnIndex : " + policyNumberColumnIndex);
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Claim Number")) {
                        claimsNumberColumnIndex = columnIndex;
                        System.out.println("claimsNumberColumnIndex : " + claimsNumberColumnIndex);
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Employee Id")) {
                        System.out.println("employeeIdColumnIndex : " + employeeIdColumnIndex);
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
                        System.out.println("patientNameColumnIndex : " + patientNameColumnIndex);
                        patientNameColumnIndex = columnIndex;
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Sum Insured")) {
                        sumInsuredColumnIndex = columnIndex;
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Claimed Amount")) {
                        claimedAmountColumnIndex = columnIndex;
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Paid Amount")) {
                        System.out.println("paidAmountColumnIndex : " + columnIndex);
                        paidAmountColumnIndex = columnIndex;
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Outstanding Amount")) {
                        outstandingAmountColumnIndex = columnIndex;
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
                ClaimsMisEntity claimsMis = new ClaimsMisEntity();
                int policyNumberInteger, claimsNumberInteger, employeeIdInteger, memberCodeInteger;
                ClaimsMisDataStatusValidateDto validateDto = new ClaimsMisDataStatusValidateDto();
                if (dataRow.getCell(policyNumberColumnIndex) != null) {
                    if (dataRow.getCell(policyNumberColumnIndex).getCellType() == CellType.NUMERIC) {
                        policyNumberInteger = (int) dataRow.getCell(policyNumberColumnIndex).getNumericCellValue();
                        claimsMis.setPolicyNumber(String.valueOf(policyNumberInteger));
                    } else {
                        String policyNumber = dataRow.getCell(policyNumberColumnIndex).getStringCellValue();
                        claimsMis.setPolicyNumber(policyNumber);
                    }
                }

                if (dataRow.getCell(claimsNumberColumnIndex) != null) {
                    if (dataRow.getCell(claimsNumberColumnIndex).getCellType() == CellType.NUMERIC) {
                        claimsNumberInteger = (int) dataRow.getCell(claimsNumberColumnIndex).getNumericCellValue();
                        claimsMis.setClaimsNumber(String.valueOf(claimsNumberInteger));
                    } else {
                        String claimsNumber = dataRow.getCell(claimsNumberColumnIndex).getStringCellValue();
                        claimsMis.setClaimsNumber(claimsNumber);
                    }
                }

                if (dataRow.getCell(employeeIdColumnIndex) != null) {
                    if (dataRow.getCell(employeeIdColumnIndex).getCellType() == CellType.NUMERIC) {
                        employeeIdInteger = (int) dataRow.getCell(employeeIdColumnIndex).getNumericCellValue();
                        claimsMis.setEmployeeId(String.valueOf(employeeIdInteger));
                    } else if (dataRow.getCell(employeeIdColumnIndex).getCellType() == CellType.STRING) {
                        String employeeId = dataRow.getCell(employeeIdColumnIndex).getStringCellValue();
                        claimsMis.setEmployeeId(employeeId);
                    }
                }


                String employeeName;
                String gender;
                String relationship;
                String patientName;
                String claimStatus;
                String claimType;
                String networkType;
                String hospitalName;
                String disease;
                String hospitalState;
                String hospitalCity;


                if (employeeNameColumnIndex >= 0) {
                    if (dataRow.getCell(employeeNameColumnIndex) != null) {
                        employeeName = dataRow.getCell(employeeNameColumnIndex).getStringCellValue();
                        claimsMis.setEmployeeName(employeeName);
                    }
                }

                int ageValue;
                if (ageColumnIndex >= 0) {
                    if (dataRow.getCell(ageColumnIndex) != null) {
                        Cell ageCell = dataRow.getCell(ageColumnIndex);
                        if (ageCell.getCellType() == CellType.NUMERIC) {
                            ageValue = (int) ageCell.getNumericCellValue();
                            claimsMis.setAge(ageValue);
                        } else if (ageCell.getCellType() == CellType.STRING) {
                            String stringCellValue = ageCell.getStringCellValue();
                            ageValue = Integer.parseInt(stringCellValue);
                            claimsMis.setAge(ageValue);
                        }
                    }
                }

                if (genderColumnIndex >= 0) {
                    if (dataRow.getCell(genderColumnIndex) != null) {
                        gender = dataRow.getCell(genderColumnIndex).getStringCellValue().trim().replaceAll("\\p{C}", "")
                                .replaceAll("\\s", "").replaceAll("[^\\p{Print}]", "");
                        ;
                        claimsMis.setGender(gender);
                    }
                }

                if (patientNameColumnIndex >= 0) {
                    if (dataRow.getCell(patientNameColumnIndex) != null) {
                        patientName = dataRow.getCell(patientNameColumnIndex).getStringCellValue();
                        claimsMis.setPatientName(patientName);
                    }
                }

                if (relationshipColumnIndex >= 0) {
                    if (dataRow.getCell(relationshipColumnIndex) != null) {
                        relationship = dataRow.getCell(relationshipColumnIndex).getStringCellValue();
                        claimsMis.setRelationship(relationship);
                    }
                }

                double sumInsuredValue;
                if (sumInsuredColumnIndex >= 0) {
                    if (dataRow.getCell(sumInsuredColumnIndex) != null) {
                        Cell sumInsuredCell = dataRow.getCell(sumInsuredColumnIndex);
                        if (sumInsuredCell.getCellType() == CellType.NUMERIC) {
                            sumInsuredValue = sumInsuredCell.getNumericCellValue();
                            claimsMis.setSumInsured(sumInsuredValue);
                        } else if (sumInsuredCell.getCellType() == CellType.STRING) {
                            String stringCellValue = sumInsuredCell.getStringCellValue();
                            // Remove commas from the string
                            stringCellValue = stringCellValue.replaceAll(",", "");
                            try {
                                sumInsuredValue = Double.parseDouble(stringCellValue);
                                claimsMis.setSumInsured(sumInsuredValue);
                            } catch (NumberFormatException e) {
                                // Handle invalid input string
                                // You can log the error or take appropriate action
                            }
                        }
                    }
                }

                double claimedAmountValue;
                if (claimedAmountColumnIndex >= 0) {
                    if (dataRow.getCell(claimedAmountColumnIndex) != null) {
                        Cell claimedAmountCell = dataRow.getCell(claimedAmountColumnIndex);
                        if (claimedAmountCell.getCellType() == CellType.NUMERIC) {
                            claimedAmountValue = claimedAmountCell.getNumericCellValue();
                            claimsMis.setClaimedAmount(claimedAmountValue);
                        } else if (claimedAmountCell.getCellType() == CellType.STRING) {
                            String stringCellValue = claimedAmountCell.getStringCellValue();
                            // Remove commas from the string
                            stringCellValue = stringCellValue.replaceAll(",", "");
                            try {
                                claimedAmountValue = Double.parseDouble(stringCellValue);
                                claimsMis.setClaimedAmount(claimedAmountValue);
                            } catch (NumberFormatException e) {
                                // Handle invalid input string
                                // You can log the error or take appropriate action
                            }
                        }
                    }
                }

                double paidAmountValue;
                if (paidAmountColumnIndex >= 0) {
                    if (dataRow.getCell(paidAmountColumnIndex) != null) {
                        Cell paidAmountCell = dataRow.getCell(paidAmountColumnIndex);
                        if (paidAmountCell.getCellType() == CellType.NUMERIC) {
                            paidAmountValue = paidAmountCell.getNumericCellValue();
                            claimsMis.setPaidAmount(paidAmountValue);
                        } else if (paidAmountCell.getCellType() == CellType.STRING) {
                            String stringCellValue = paidAmountCell.getStringCellValue();
                            // Remove commas from the string
                            stringCellValue = stringCellValue.replaceAll(",", "");
                            try {
                                paidAmountValue = Double.parseDouble(stringCellValue);
                                claimsMis.setPaidAmount(paidAmountValue);
                            } catch (NumberFormatException e) {
                                // Handle invalid input string
                                // You can log the error or take appropriate action
                            }
                        }
                    }
                }

                double outstandingAmountValue;
                if (outstandingAmountColumnIndex >= 0) {
                    if (dataRow.getCell(outstandingAmountColumnIndex) != null) {
                        Cell outstandingAmountCell = dataRow.getCell(outstandingAmountColumnIndex);
                        if (outstandingAmountCell.getCellType() == CellType.NUMERIC) {
                            outstandingAmountValue = outstandingAmountCell.getNumericCellValue();
                            claimsMis.setOutstandingAmount(outstandingAmountValue);
                        } else if (outstandingAmountCell.getCellType() == CellType.STRING) {
                            String stringCellValue = outstandingAmountCell.getStringCellValue();
                            // Remove commas from the string
                            stringCellValue = stringCellValue.replaceAll(",", "");
                            try {
                                outstandingAmountValue = Double.parseDouble(stringCellValue);
                                claimsMis.setOutstandingAmount(outstandingAmountValue);
                            } catch (NumberFormatException e) {
                                // Handle invalid input string
                                // You can log the error or take appropriate action
                            }
                        }
                    }

                }

                if (claimStatusColumnIndex >= 0) {
                    if (dataRow.getCell(claimStatusColumnIndex) != null) {
                        claimStatus = dataRow.getCell(claimStatusColumnIndex).getStringCellValue();
                        claimsMis.setClaimStatus(claimStatus);
                    }
                }

                Date dateOfClaimValue = null;
                if (dateOfClaimColumnIndex >= 0) {
                    if (dataRow.getCell(dateOfClaimColumnIndex) != null) {
                        Cell dateOfClaimCell = dataRow.getCell(dateOfClaimColumnIndex);
                        if (dateOfClaimCell.getCellType() == CellType.NUMERIC || dateOfClaimCell.getCellType() == CellType.FORMULA) {
                            dateOfClaimValue = dateOfClaimCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String admissionDateStr = sdf.format(dateOfClaimValue);
                                dateOfClaimValue = sdf.parse(admissionDateStr);
                                claimsMis.setDateOfClaim(dateOfClaimValue);
                                log.info("admissionDateValue :: {}", dateOfClaimValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else if (dateOfClaimCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = dateOfClaimCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                dateOfClaimValue = dateFormat.parse(stringValue);
                                if (dateOfClaimValue != null) {
                                    claimsMis.setDateOfClaim(dateOfClaimValue);
                                }
                                System.out.println("dateOfClaimValue str :: " + dateOfClaimValue);
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }

                if (claimTypeColumnIndex >= 0) {
                    if (dataRow.getCell(claimTypeColumnIndex) != null) {
                        claimType = dataRow.getCell(claimTypeColumnIndex).getStringCellValue();
                        claimsMis.setClaimType(claimType);
                    }
                }

                if (networkTypeColumnIndex >= 0) {
                    if (dataRow.getCell(networkTypeColumnIndex) != null) {
                        networkType = dataRow.getCell(networkTypeColumnIndex).getStringCellValue();
                        claimsMis.setNetworkType(networkType);
                    }
                }

                if (hospitalNameColumnIndex >= 0) {
                    if (dataRow.getCell(hospitalNameColumnIndex) != null) {
                        hospitalName = dataRow.getCell(hospitalNameColumnIndex).getStringCellValue();
                        claimsMis.setHospitalName(hospitalName);
                    }
                }

                Date admissionDateValue = null;
                if (admissionDateColumnIndex >= 0) {
                    if (dataRow.getCell(admissionDateColumnIndex) != null) {
                        Cell admissionDateCell = dataRow.getCell(admissionDateColumnIndex);
                        if (admissionDateCell.getCellType() == CellType.NUMERIC || admissionDateCell.getCellType() == CellType.FORMULA) {
                            admissionDateValue = admissionDateCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String admissionDateStr = sdf.format(admissionDateValue);
                                admissionDateValue = sdf.parse(admissionDateStr);
                                claimsMis.setAdmissionDate(admissionDateValue);
                                log.info("admissionDateValue :: {}", admissionDateValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            claimsMis.setAdmissionDate(admissionDateValue);
                        } else if (admissionDateCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = admissionDateCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                admissionDateValue = dateFormat.parse(stringValue);
                                if (admissionDateValue != null) {
                                    claimsMis.setAdmissionDate(admissionDateValue);
                                    System.out.println("Admission date set successfully.");
                                }
                                System.out.println("admissionDateValue str :: " + admissionDateValue);
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }

                if (diseaseColumnIndex >= 0) {
                    if (dataRow.getCell(diseaseColumnIndex) != null) {
                        disease = dataRow.getCell(diseaseColumnIndex).getStringCellValue();
                        claimsMis.setDisease(disease);
                    }
                }

                Date dischargeDateValue = null;
                if (dischargeDateColumnIndex >= 0) {
                    if (dataRow.getCell(dischargeDateColumnIndex) != null) {
                        Cell dischargeDateCell = dataRow.getCell(dischargeDateColumnIndex);
                        if (dischargeDateCell.getCellType() == CellType.NUMERIC || dischargeDateCell.getCellType() == CellType.FORMULA) {
                            dischargeDateValue = dischargeDateCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dischargeDateStr = sdf.format(dischargeDateValue);
                                dischargeDateValue = sdf.parse(dischargeDateStr);
                                claimsMis.setDateOfDischarge(dischargeDateValue);
                                log.info("admissionDateValue :: {}", dischargeDateValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else if (dischargeDateCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = dischargeDateCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                dischargeDateValue = dateFormat.parse(stringValue);
                                if (dischargeDateValue != null) {
                                    claimsMis.setDateOfDischarge(dischargeDateValue);
                                }
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }


                if (dataRow.getCell(memberCodeColumnIndex) != null) {
                    if (dataRow.getCell(memberCodeColumnIndex).getCellType() == CellType.NUMERIC) {
                        memberCodeInteger = (int) dataRow.getCell(memberCodeColumnIndex).getNumericCellValue();
                        claimsMis.setMemberCode(String.valueOf(memberCodeInteger));
                    } else {
                        String memberCode = dataRow.getCell(memberCodeColumnIndex).getStringCellValue();
                        claimsMis.setMemberCode(memberCode);
                    }
                }

                Date policyStartDateValue = null;
                if (policyStartDateColumnIndex >= 0) {
                    if (dataRow.getCell(policyStartDateColumnIndex) != null) {
                        Cell policyStartDateCell = dataRow.getCell(policyStartDateColumnIndex);
                        if (policyStartDateCell.getCellType() == CellType.NUMERIC || policyStartDateCell.getCellType() == CellType.FORMULA) {
                            policyStartDateValue = policyStartDateCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dischargeDateStr = sdf.format(policyStartDateValue);
                                policyStartDateValue = sdf.parse(dischargeDateStr);
                                claimsMis.setPolicyStartDate(policyStartDateValue);
                                log.info("policyStartDateValue :: {}", policyStartDateValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else if (policyStartDateCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = policyStartDateCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                policyStartDateValue = dateFormat.parse(stringValue);
                                if (policyStartDateValue != null) {
                                    claimsMis.setPolicyStartDate(policyStartDateValue);
                                }
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }
                Date policyEndDateValue = null;
                if (policyEndDateColumnIndex >= 0) {
                    if (dataRow.getCell(policyEndDateColumnIndex) != null) {
                        Cell policyEndDateCell = dataRow.getCell(policyEndDateColumnIndex);
                        if (policyEndDateCell.getCellType() == CellType.NUMERIC || policyEndDateCell.getCellType() == CellType.FORMULA) {
                            policyEndDateValue = policyEndDateCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dischargeDateStr = sdf.format(policyEndDateValue);
                                policyEndDateValue = sdf.parse(dischargeDateStr);
                                claimsMis.setPolicyEndDate(policyEndDateValue);
                                log.info("policyEndDateValue :: {}", policyEndDateValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else if (policyEndDateCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = policyEndDateCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                policyEndDateValue = dateFormat.parse(stringValue);
                                if (policyEndDateValue != null) {
                                    claimsMis.setPolicyEndDate(policyEndDateValue);
                                }
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }

                if (hospitalStateColumnIndex >= 0) {
                    if (dataRow.getCell(hospitalStateColumnIndex) != null) {
                        hospitalState = dataRow.getCell(hospitalStateColumnIndex).getStringCellValue();
                        claimsMis.setHospitalState(hospitalState);
                    }
                }

                if (hospitalCityColumnIndex >= 0) {
                    if (dataRow.getCell(hospitalCityColumnIndex) != null) {
                        hospitalCity = dataRow.getCell(hospitalCityColumnIndex).getStringCellValue();
                        claimsMis.setHospitalCity(hospitalCity);
                    }
                }

                claimsMis.setCreatedDate(new Date());
                claimsMis.setRecordStatus("ACTIVE");
                claimsMis.setRfqId(rfqId);
                claimsMisData.add(claimsMis);

            }
        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
        }

        return claimsMisData;
    }


    public List<ClientDetailsClaimsMis> storeClaimsMisANDfile1(MultipartFile file, String fileName, String tpaName) {


        List<ClientDetailsClaimsMis> claimsMisData = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = null;
            if (tpaName.equals("HealthIndia")) {
                sheet = workbook.getSheetAt(0);
            } else if (tpaName.equals("Vipul")) {
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
            } else if (tpaName.equals("RCare")) {
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
                        System.out.println("policyNumberColumnIndex : " + policyNumberColumnIndex);
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Claim Number")) {
                        claimsNumberColumnIndex = columnIndex;
                        System.out.println("claimsNumberColumnIndex : " + claimsNumberColumnIndex);
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Employee Id")) {
                        System.out.println("employeeIdColumnIndex : " + employeeIdColumnIndex);
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
                        System.out.println("patientNameColumnIndex : " + patientNameColumnIndex);
                        patientNameColumnIndex = columnIndex;
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Sum Insured")) {
                        sumInsuredColumnIndex = columnIndex;
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Claimed Amount")) {
                        claimedAmountColumnIndex = columnIndex;
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Paid Amount")) {
                        System.out.println("paidAmountColumnIndex : " + columnIndex);
                        paidAmountColumnIndex = columnIndex;
                    } else if (columnName.trim().equals(i.getHeaderName().trim())
                            && i.getHeaderAliasName().trim().equals("Outstanding Amount")) {
                        outstandingAmountColumnIndex = columnIndex;
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
                ClientDetailsClaimsMis claimsMis = new ClientDetailsClaimsMis();
                int policyNumberInteger, claimsNumberInteger, employeeIdInteger, memberCodeInteger;
                ClaimsMisDataStatusValidateDto validateDto = new ClaimsMisDataStatusValidateDto();
                if (dataRow.getCell(policyNumberColumnIndex) != null) {
                    if (dataRow.getCell(policyNumberColumnIndex).getCellType() == CellType.NUMERIC) {
                        policyNumberInteger = (int) dataRow.getCell(policyNumberColumnIndex).getNumericCellValue();
                        claimsMis.setPolicyNumber(String.valueOf(policyNumberInteger));
                    } else {
                        String policyNumber = dataRow.getCell(policyNumberColumnIndex).getStringCellValue();
                        claimsMis.setPolicyNumber(policyNumber);
                    }
                }

                if (dataRow.getCell(claimsNumberColumnIndex) != null) {
                    if (dataRow.getCell(claimsNumberColumnIndex).getCellType() == CellType.NUMERIC) {
                        claimsNumberInteger = (int) dataRow.getCell(claimsNumberColumnIndex).getNumericCellValue();
                        claimsMis.setClaimsNumber(String.valueOf(claimsNumberInteger));
                    } else {
                        String claimsNumber = dataRow.getCell(claimsNumberColumnIndex).getStringCellValue();
                        claimsMis.setClaimsNumber(claimsNumber);
                    }
                }

                if (dataRow.getCell(employeeIdColumnIndex) != null) {
                    if (dataRow.getCell(employeeIdColumnIndex).getCellType() == CellType.NUMERIC) {
                        employeeIdInteger = (int) dataRow.getCell(employeeIdColumnIndex).getNumericCellValue();
                        claimsMis.setEmployeeId(String.valueOf(employeeIdInteger));
                    } else if (dataRow.getCell(employeeIdColumnIndex).getCellType() == CellType.STRING) {
                        String employeeId = dataRow.getCell(employeeIdColumnIndex).getStringCellValue();
                        claimsMis.setEmployeeId(employeeId);
                    }
                }


                String employeeName;
                String gender;
                String relationship;
                String patientName;
                String claimStatus;
                String claimType;
                String networkType;
                String hospitalName;
                String disease;
                String hospitalState;
                String hospitalCity;


                if (employeeNameColumnIndex >= 0) {
                    if (dataRow.getCell(employeeNameColumnIndex) != null) {
                        employeeName = dataRow.getCell(employeeNameColumnIndex).getStringCellValue();
                        claimsMis.setEmployeeName(employeeName);
                    }
                }

                int ageValue;
                if (ageColumnIndex >= 0) {
                    if (dataRow.getCell(ageColumnIndex) != null) {
                        Cell ageCell = dataRow.getCell(ageColumnIndex);
                        if (ageCell.getCellType() == CellType.NUMERIC) {
                            ageValue = (int) ageCell.getNumericCellValue();
                            claimsMis.setAge(ageValue);
                        } else if (ageCell.getCellType() == CellType.STRING) {
                            String stringCellValue = ageCell.getStringCellValue();
                            ageValue = Integer.parseInt(stringCellValue);
                            claimsMis.setAge(ageValue);
                        }
                    }
                }

                if (genderColumnIndex >= 0) {
                    if (dataRow.getCell(genderColumnIndex) != null) {
                        gender = dataRow.getCell(genderColumnIndex).getStringCellValue().trim().replaceAll("\\p{C}", "")
                                .replaceAll("\\s", "").replaceAll("[^\\p{Print}]", "");
                        ;
                        claimsMis.setGender(gender);
                    }
                }

                if (patientNameColumnIndex >= 0) {
                    if (dataRow.getCell(patientNameColumnIndex) != null) {
                        patientName = dataRow.getCell(patientNameColumnIndex).getStringCellValue();
                        claimsMis.setPatientName(patientName);
                    }
                }

                if (relationshipColumnIndex >= 0) {
                    if (dataRow.getCell(relationshipColumnIndex) != null) {
                        relationship = dataRow.getCell(relationshipColumnIndex).getStringCellValue();
                        claimsMis.setRelationship(relationship);
                    }
                }

                double sumInsuredValue;
                if (sumInsuredColumnIndex >= 0) {
                    if (dataRow.getCell(sumInsuredColumnIndex) != null) {
                        Cell sumInsuredCell = dataRow.getCell(sumInsuredColumnIndex);
                        if (sumInsuredCell.getCellType() == CellType.NUMERIC) {
                            sumInsuredValue = sumInsuredCell.getNumericCellValue();
                            claimsMis.setSumInsured(sumInsuredValue);
                        } else if (sumInsuredCell.getCellType() == CellType.STRING) {
                            String stringCellValue = sumInsuredCell.getStringCellValue();
                            // Remove commas from the string
                            stringCellValue = stringCellValue.replaceAll(",", "");
                            try {
                                sumInsuredValue = Double.parseDouble(stringCellValue);
                                claimsMis.setSumInsured(sumInsuredValue);
                            } catch (NumberFormatException e) {
                                // Handle invalid input string
                                // You can log the error or take appropriate action
                            }
                        }
                    }
                }

                double claimedAmountValue;
                if (claimedAmountColumnIndex >= 0) {
                    if (dataRow.getCell(claimedAmountColumnIndex) != null) {
                        Cell claimedAmountCell = dataRow.getCell(claimedAmountColumnIndex);
                        if (claimedAmountCell.getCellType() == CellType.NUMERIC) {
                            claimedAmountValue = claimedAmountCell.getNumericCellValue();
                            claimsMis.setClaimedAmount(claimedAmountValue);
                        } else if (claimedAmountCell.getCellType() == CellType.STRING) {
                            String stringCellValue = claimedAmountCell.getStringCellValue();
                            // Remove commas from the string
                            stringCellValue = stringCellValue.replaceAll(",", "");
                            try {
                                claimedAmountValue = Double.parseDouble(stringCellValue);
                                claimsMis.setClaimedAmount(claimedAmountValue);
                            } catch (NumberFormatException e) {
                                // Handle invalid input string
                                // You can log the error or take appropriate action
                            }
                        }
                    }
                }

                double paidAmountValue;
                if (paidAmountColumnIndex >= 0) {
                    if (dataRow.getCell(paidAmountColumnIndex) != null) {
                        Cell paidAmountCell = dataRow.getCell(paidAmountColumnIndex);
                        if (paidAmountCell.getCellType() == CellType.NUMERIC) {
                            paidAmountValue = paidAmountCell.getNumericCellValue();
                            claimsMis.setPaidAmount(paidAmountValue);
                        } else if (paidAmountCell.getCellType() == CellType.STRING) {
                            String stringCellValue = paidAmountCell.getStringCellValue();
                            // Remove commas from the string
                            stringCellValue = stringCellValue.replaceAll(",", "");
                            try {
                                paidAmountValue = Double.parseDouble(stringCellValue);
                                claimsMis.setPaidAmount(paidAmountValue);
                            } catch (NumberFormatException e) {
                                // Handle invalid input string
                                // You can log the error or take appropriate action
                            }
                        }
                    }
                }

                double outstandingAmountValue;
                if (outstandingAmountColumnIndex >= 0) {
                    if (dataRow.getCell(outstandingAmountColumnIndex) != null) {
                        Cell outstandingAmountCell = dataRow.getCell(outstandingAmountColumnIndex);
                        if (outstandingAmountCell.getCellType() == CellType.NUMERIC) {
                            outstandingAmountValue = outstandingAmountCell.getNumericCellValue();
                            claimsMis.setOutstandingAmount(outstandingAmountValue);
                        } else if (outstandingAmountCell.getCellType() == CellType.STRING) {
                            String stringCellValue = outstandingAmountCell.getStringCellValue();
                            // Remove commas from the string
                            stringCellValue = stringCellValue.replaceAll(",", "");
                            try {
                                outstandingAmountValue = Double.parseDouble(stringCellValue);
                                claimsMis.setOutstandingAmount(outstandingAmountValue);
                            } catch (NumberFormatException e) {
                                // Handle invalid input string
                                // You can log the error or take appropriate action
                            }
                        }
                    }

                }

                if (claimStatusColumnIndex >= 0) {
                    if (dataRow.getCell(claimStatusColumnIndex) != null) {
                        claimStatus = dataRow.getCell(claimStatusColumnIndex).getStringCellValue();
                        claimsMis.setClaimStatus(claimStatus);
                    }
                }


                Date dateOfClaimValue = null;
                if (dateOfClaimColumnIndex >= 0) {
                    if (dataRow.getCell(dateOfClaimColumnIndex) != null) {
                        Cell dateOfClaimCell = dataRow.getCell(dateOfClaimColumnIndex);
                        if (dateOfClaimCell.getCellType() == CellType.NUMERIC || dateOfClaimCell.getCellType() == CellType.FORMULA) {
                            dateOfClaimValue = dateOfClaimCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String admissionDateStr = sdf.format(dateOfClaimValue);
                                dateOfClaimValue = sdf.parse(admissionDateStr);
                                claimsMis.setDateOfClaim(dateOfClaimValue);
                                log.info("admissionDateValue :: {}", dateOfClaimValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else if (dateOfClaimCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = dateOfClaimCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                dateOfClaimValue = dateFormat.parse(stringValue);
                                if (dateOfClaimValue != null) {
                                    claimsMis.setDateOfClaim(dateOfClaimValue);
                                }
                                System.out.println("dateOfClaimValue str :: " + dateOfClaimValue);
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }

                if (claimTypeColumnIndex >= 0) {
                    if (dataRow.getCell(claimTypeColumnIndex) != null) {
                        claimType = dataRow.getCell(claimTypeColumnIndex).getStringCellValue();
                        claimsMis.setClaimType(claimType);
                    }
                }

                if (networkTypeColumnIndex >= 0) {
                    if (dataRow.getCell(networkTypeColumnIndex) != null) {
                        networkType = dataRow.getCell(networkTypeColumnIndex).getStringCellValue();
                        claimsMis.setNetworkType(networkType);
                    }
                }

                if (hospitalNameColumnIndex >= 0) {
                    if (dataRow.getCell(hospitalNameColumnIndex) != null) {
                        hospitalName = dataRow.getCell(hospitalNameColumnIndex).getStringCellValue();
                        claimsMis.setHospitalName(hospitalName);
                    }
                }

                Date admissionDateValue = null;
                if (admissionDateColumnIndex >= 0) {
                    if (dataRow.getCell(admissionDateColumnIndex) != null) {
                        Cell admissionDateCell = dataRow.getCell(admissionDateColumnIndex);
                        if (admissionDateCell.getCellType() == CellType.NUMERIC || admissionDateCell.getCellType() == CellType.FORMULA) {
                            admissionDateValue = admissionDateCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String admissionDateStr = sdf.format(admissionDateValue);
                                admissionDateValue = sdf.parse(admissionDateStr);
                                claimsMis.setAdmissionDate(admissionDateValue);
                                log.info("admissionDateValue :: {}", admissionDateValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            claimsMis.setAdmissionDate(admissionDateValue);
                        } else if (admissionDateCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = admissionDateCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                admissionDateValue = dateFormat.parse(stringValue);
                                if (admissionDateValue != null) {
                                    claimsMis.setAdmissionDate(admissionDateValue);
                                    System.out.println("Admission date set successfully.");
                                }
                                System.out.println("admissionDateValue str :: " + admissionDateValue);
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }

                if (diseaseColumnIndex >= 0) {
                    if (dataRow.getCell(diseaseColumnIndex) != null) {
                        disease = dataRow.getCell(diseaseColumnIndex).getStringCellValue();
                        claimsMis.setDisease(disease);
                    }
                }

                Date dischargeDateValue = null;
                if (dischargeDateColumnIndex >= 0) {
                    if (dataRow.getCell(dischargeDateColumnIndex) != null) {
                        Cell dischargeDateCell = dataRow.getCell(dischargeDateColumnIndex);
                        if (dischargeDateCell.getCellType() == CellType.NUMERIC || dischargeDateCell.getCellType() == CellType.FORMULA) {
                            dischargeDateValue = dischargeDateCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dischargeDateStr = sdf.format(dischargeDateValue);
                                dischargeDateValue = sdf.parse(dischargeDateStr);
                                claimsMis.setDateOfDischarge(dischargeDateValue);
                                log.info("admissionDateValue :: {}", dischargeDateValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else if (dischargeDateCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = dischargeDateCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                dischargeDateValue = dateFormat.parse(stringValue);
                                if (dischargeDateValue != null) {
                                    claimsMis.setDateOfDischarge(dischargeDateValue);
                                }
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }


                if (dataRow.getCell(memberCodeColumnIndex) != null) {
                    if (dataRow.getCell(memberCodeColumnIndex).getCellType() == CellType.NUMERIC) {
                        memberCodeInteger = (int) dataRow.getCell(memberCodeColumnIndex).getNumericCellValue();
                        claimsMis.setMemberCode(String.valueOf(memberCodeInteger));
                    } else {
                        String memberCode = dataRow.getCell(memberCodeColumnIndex).getStringCellValue();
                        claimsMis.setMemberCode(memberCode);
                    }
                }

                Date policyStartDateValue = null;
                if (policyStartDateColumnIndex >= 0) {
                    if (dataRow.getCell(policyStartDateColumnIndex) != null) {
                        Cell policyStartDateCell = dataRow.getCell(policyStartDateColumnIndex);
                        if (policyStartDateCell.getCellType() == CellType.NUMERIC || policyStartDateCell.getCellType() == CellType.FORMULA) {
                            policyStartDateValue = policyStartDateCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dischargeDateStr = sdf.format(policyStartDateValue);
                                policyStartDateValue = sdf.parse(dischargeDateStr);
                                claimsMis.setPolicyStartDate(policyStartDateValue);
                                log.info("policyStartDateValue :: {}", policyStartDateValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else if (policyStartDateCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = policyStartDateCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                policyStartDateValue = dateFormat.parse(stringValue);
                                if (policyStartDateValue != null) {
                                    claimsMis.setPolicyStartDate(policyStartDateValue);
                                }
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }
                Date policyEndDateValue = null;
                if (policyEndDateColumnIndex >= 0) {
                    if (dataRow.getCell(policyEndDateColumnIndex) != null) {
                        Cell policyEndDateCell = dataRow.getCell(policyEndDateColumnIndex);
                        if (policyEndDateCell.getCellType() == CellType.NUMERIC || policyEndDateCell.getCellType() == CellType.FORMULA) {
                            policyEndDateValue = policyEndDateCell.getDateCellValue();
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dischargeDateStr = sdf.format(policyEndDateValue);
                                policyEndDateValue = sdf.parse(dischargeDateStr);
                                claimsMis.setPolicyEndDate(policyEndDateValue);
                                log.info("policyEndDateValue :: {}", policyEndDateValue);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else if (policyEndDateCell.getCellType() == CellType.STRING) {
                            // If the cell is a string, you might need to parse it as a date
                            try {
                                String stringValue = policyEndDateCell.getStringCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                                policyEndDateValue = dateFormat.parse(stringValue);
                                if (policyEndDateValue != null) {
                                    claimsMis.setPolicyEndDate(policyEndDateValue);
                                }
                            } catch (Exception e) {
                                // Ignore or handle the exception
                            }
                        }
                    }
                }

                if (hospitalStateColumnIndex >= 0) {
                    if (dataRow.getCell(hospitalStateColumnIndex) != null) {
                        hospitalState = dataRow.getCell(hospitalStateColumnIndex).getStringCellValue();
                        claimsMis.setHospitalState(hospitalState);
                    }
                }

                if (hospitalCityColumnIndex >= 0) {
                    if (dataRow.getCell(hospitalCityColumnIndex) != null) {
                        hospitalCity = dataRow.getCell(hospitalCityColumnIndex).getStringCellValue();
                        claimsMis.setHospitalCity(hospitalCity);
                    }
                }

                claimsMis.setCreatedDate(new Date());
                claimsMis.setRecordStatus("ACTIVE");
                claimsMisData.add(claimsMis);

            }
        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
        }

        return claimsMisData;
    }


    public List<EmployeeDepedentDetailsEntity> readEmployeesFromExcel(MultipartFile file, String fileName,
                                                                      String rfqId) {

        if (fileName.equals("EmpData")) {

            List<EmployeeDepedentDetailsEntity> employees = empDepRepo.findByrfqId(rfqId);

            return employees.stream().filter(i -> i.getRecordStatus().equals("ACTIVE")).toList();

        }

        return null;
    }

    public List<ClaimsMisEntity> readClaimsFromExcel(MultipartFile file, String fileName, String rfqId) {

        if (fileName.equals("ClaimsMis")) {
            List<ClaimsMisEntity> claimsMisDetails = claimsRepo.findByRfqId(rfqId);
            return claimsMisDetails.stream().filter(i -> i.getRecordStatus().equals("ACTIVE")).toList();
        }

        return null;
    }


}
