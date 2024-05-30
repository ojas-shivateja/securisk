package com.insure.rfq.service.impl;

import com.insure.rfq.dto.ClientListNetworkHospitalDataStatus;
import com.insure.rfq.dto.ClientListNetworkHospitalHeadersMapping1Dto;
import com.insure.rfq.dto.GetAllClientListNetWorkHospitalDto;
import com.insure.rfq.entity.*;
import com.insure.rfq.exception.InvalidClientList;
import com.insure.rfq.exception.InvalidProduct;
import com.insure.rfq.exception.TpaNotFoundException;
import com.insure.rfq.repository.*;
import com.insure.rfq.service.ClientListNetworkHospitalityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientListNetworkHospitalityServiceImpl implements ClientListNetworkHospitalityService {


    private ClinetListNetworkHospitalRepository clinetListNetworkHospitalRepository;
    private ClientListRepository clientListRepository;
    private ProductRepository productRepository;
    private TpaRepository tpaRepository;
    private ClientListNetworkHospitalHeadersMappingRepository clientListNetworkHospitalHeadersMappingRepository;

    @Autowired
    public ClientListNetworkHospitalityServiceImpl(ClinetListNetworkHospitalRepository clinetListNetworkHospitalRepository, ClientListRepository clientListRepository, ProductRepository productRepository, TpaRepository tpaRepository, ClientListNetworkHospitalHeadersMappingRepository ClientListNetworkHospitalHeadersMappingRepository) {
        this.clinetListNetworkHospitalRepository = clinetListNetworkHospitalRepository;
        this.clientListRepository = clientListRepository;
        this.productRepository = productRepository;
        this.tpaRepository = tpaRepository;
        this.clientListNetworkHospitalHeadersMappingRepository = ClientListNetworkHospitalHeadersMappingRepository;
    }


    private Workbook getWorkbook(MultipartFile file, String tpaName) throws IOException {
        String extension = FileNameUtils.getExtension(file.getOriginalFilename());
        String fileName = file.getOriginalFilename();

        System.out.println("extension :: " + extension);
        System.out.println("fileName :: " + fileName);

        if (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".XLSX") || fileName.endsWith(".xlsb") || fileName.endsWith(".XLSB"))) {
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
                return String.valueOf(cell.getNumericCellValue()); // For simplistate, assuming formula results in a numeric value
            default:
                return ""; // Return empty string for unsupported cell types
        }
    }


    @Override
    public ClientListNetworkHospitalHeadersMapping1Dto validateHeadersBasedOnTpa(MultipartFile multipartFile, String tpaName) throws IOException {
        Sheet sheet = null;

        try {
            Workbook workbook = getWorkbook(multipartFile, tpaName);
            ClientListNetworkHospitalHeadersMapping1Dto clientListNetworkHospitalHeadersMappingDto = new ClientListNetworkHospitalHeadersMapping1Dto();

            if (tpaName.equals("HealthIndia")) {
                sheet = workbook.getSheetAt(0);
            } else if (tpaName.equals("Vidal")) {
                sheet = workbook.getSheetAt(0);
            } else if (tpaName.equals("StarHealth")) {
                sheet = workbook.getSheetAt(0);
            } else if (tpaName.equals("Medseva")) {
                sheet = workbook.getSheetAt(0);
            } else if (tpaName.equals("GodiGit")) {
                sheet = workbook.getSheetAt(0);
            } else if (tpaName.equals("List_reliance")) {
                sheet = workbook.getSheetAt(0);
            } else if (tpaName.equals("ICICI")) {
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
        ClientListNetworkHospitalHeadersMapping1Dto headerDto = new ClientListNetworkHospitalHeadersMapping1Dto();
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
        List<ClientListNetworkHospitalHeadersMappingEntity> validHeaders = clientListNetworkHospitalHeadersMappingRepository.findByTpaName(byTpaName.getTpaName());
        //log.info("data:{}",  validHeaders.toString());
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
                        if (validHeaders.stream().anyMatch(header -> columnName.equals(header.getHeaderName()) || columnName.equals(header.getHeaderAliasName()))) {
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
                                if (columnName.equals(header.getHeaderName()) || columnName.equals(header.getHeaderAliasName())) {
                                    // Set flags based on header names
                                    switch (header.getHeaderName()) {
                                        case "HOSPITAL NAME":
                                            headerDto.setHospitalNameStatus(true);
                                            break;
                                        case "ADDRESS":
                                            headerDto.setAddressStatus(true);
                                            break;
                                        case "CITY":
                                            headerDto.setCityStatus(true);
                                            break;
                                        case "STATE":
                                            headerDto.setStateStatus(true);
                                            break;
                                        case "PINCODE":
                                            headerDto.setPinCodeStatus(true);
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
    public List<ClientListNetworkHospitalDataStatus> validateValuesBasedOnTpa(MultipartFile multipartFile, String tpaName) throws IOException {

        List<ClientListNetworkHospitalDataStatus> list = new ArrayList<>();

        try (Workbook workbook = getWorkbook(multipartFile, tpaName)) {
            Sheet sheet = null;
            int numberOfSheets = workbook.getNumberOfSheets();
            log.info("Sheet name  and it's no of sheets :{} ", numberOfSheets);
            for (int i = 0; i < numberOfSheets; i++) {
                if (tpaName.equals("HealthIndia") || tpaName.equals("Vidal") || tpaName.equals("Starhealth") || tpaName.equals("Medseva") || tpaName.equals("godigit") || tpaName.equals("List_reliance") || tpaName.equals("ICICI")) {
                    sheet = workbook.getSheetAt(i);
                } else {
                    log.warn("Unknown TPA name: {}", tpaName);
                    // Handle this case appropriately, such as skipping the sheet or throwing an exception
                    continue;
                }
                if (sheet == null) {
                    log.warn("Sheet {} is null", i);
                    // Handle this case appropriately, such as skipping the sheet or throwing an exception
                    continue;
                    //sheet = workbook.getSheetAt(i);
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
    public void validateBasedOnSheet(Sheet sheet, String tpaName, List<ClientListNetworkHospitalDataStatus> clientListNetworkHospitalDataStatuses) {

        int hospitalNameIndex = -1;
        int addressIndex = -1;
        int cityIndex = -1;
        int stateIndex = -1;
        int pinCodeIndex = -1;

        boolean hospitalColumnIndexFlag = false;
        boolean addressColumnIndexFlag = false;
        boolean cityColumnIndexFlag = false;
        boolean stateColumnIndexFlag = false;
        boolean pinCodeColumnIndexFlag = false;

        Iterator<Row> rowIterator = sheet.iterator();
        Tpa byTpaName = tpaRepository.findByTpaName(tpaName);
        List<ClientListNetworkHospitalHeadersMappingEntity> validHeaders = clientListNetworkHospitalHeadersMappingRepository.findByTpaName(byTpaName.getTpaName());
        log.info("No of Rows from Excel :{}", sheet.getPhysicalNumberOfRows());

        Row headerRow = null;

         /*
            This Loop Finds Exact Row where Headers are Present
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
                    for (ClientListNetworkHospitalHeadersMappingEntity header : validHeaders) {
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
	            This Loop Finds Index for Column from Excel and also maps it with DB's name
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
                for (ClientListNetworkHospitalHeadersMappingEntity header : validHeaders) {
                    String headerName = header.getHeaderName().trim();
                    String headerAliasName = header.getHeaderAliasName().trim();
                    if (columnName.equalsIgnoreCase(headerName) || columnName.equalsIgnoreCase(headerAliasName)) {
                        log.info("Mapping Excel column '{}' to header alias '{}'", columnName, headerAliasName);
                        switch (headerName) {
                            case "HOSPITAL NAME":
                                hospitalNameIndex = columnIndex;
                                log.info("Captured employeeIdColumnIndex from excel :{}", hospitalNameIndex);
                                break;
                            case "ADDRESS":
                                addressIndex = columnIndex;
                                log.info("Captured employeeNameColumnIndex from excel :{}", addressIndex);
                                break;
                            case "CITY":
                                cityIndex = columnIndex;
                                log.info("Captured dateOfBirthColumnIndex from excel :{}", stateIndex);
                                break;
                            case "STATE":
                                stateIndex = columnIndex;
                                log.info("Captured genderColumnIndex from excel :{}", stateIndex);
                                break;
                            case "PINCODE":
                                pinCodeIndex = columnIndex;
                                log.info("Captured ageColumnIndex from excel :{}", pinCodeIndex);
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
            This Loop Validates the data
         */
        while (rowIterator.hasNext()) {
            Row dataRow = rowIterator.next();
            if (hospitalNameIndex >= 0 && addressIndex >= 0 && cityIndex >= 0 && stateIndex >= 0 && pinCodeIndex >= 0) {
                if (dataRow.getCell(hospitalNameIndex) != null && dataRow.getCell(addressIndex) != null && dataRow.getCell(cityIndex) != null && dataRow.getCell(stateIndex) != null && dataRow.getCell(pinCodeIndex) != null) {
                }
                if (dataRow.getCell(hospitalNameIndex).getCellType() == CellType.BLANK && dataRow.getCell(addressIndex).getCellType() == CellType.BLANK && dataRow.getCell(cityIndex).getCellType() == CellType.BLANK && dataRow.getCell(stateIndex).getCellType() == CellType.BLANK && dataRow.getCell(pinCodeIndex).getCellType() == CellType.BLANK) {
                    continue;
                }
            }
            String remarks;
            ClientListNetworkHospitalDataStatus validateDto = new ClientListNetworkHospitalDataStatus();
            /**
             * HospitalName Validation
             */
            String hospitalName;
            if (dataRow.getCell(hospitalNameIndex) != null) {
                Cell hospitalNameCell = dataRow.getCell(hospitalNameIndex);
//                log.info("Employee Name From Excel : {} and it's dataType is :{}",employeeNameCell,employeeNameCell.getCellType());
                if (hospitalNameCell.getCellType() == CellType.NUMERIC) {
                    hospitalName = String.valueOf(hospitalNameCell.getNumericCellValue()).trim();
                    validateDto.setHospitalName(String.valueOf(hospitalName));
                    validateDto.setHospitalNameStatus(false);
                } else if (hospitalNameCell.getCellType() == CellType.STRING) {
                    hospitalName = hospitalNameCell.getStringCellValue().trim();
                    validateDto.setHospitalName(hospitalName);
                    validateDto.setHospitalNameStatus(true);
                } else if (hospitalNameCell.getCellType() == CellType._NONE) {
                    hospitalColumnIndexFlag = true;
                    validateDto.setHospitalNameStatus(false);
                    validateDto.setHospitalNameErrorMessage("Hospital Name None :: ");
                } else if (hospitalNameCell.getCellType() == CellType.BLANK) {
                    hospitalColumnIndexFlag = true;
                    validateDto.setHospitalNameStatus(false);
                    validateDto.setHospitalNameErrorMessage("Hospital Name Blank :: ");
                } else if (hospitalNameCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = hospitalNameCell.getErrorCellValue();
                    validateDto.setHospitalName(String.valueOf(errorCellValue).trim());
                    validateDto.setHospitalNameStatus(false);
                    validateDto.setHospitalNameErrorMessage("Hospital Name Error :: ");
                } else if (hospitalNameCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = hospitalNameCell.getBooleanCellValue();
                    validateDto.setHospitalName(String.valueOf(booleanCellValue).trim());
                    validateDto.setHospitalNameStatus(false);
                    validateDto.setHospitalNameErrorMessage("Hospital Name Boolean :: ");
                } else if (hospitalNameCell.getCellType() == CellType.FORMULA) {
                    switch (hospitalNameCell.getCellType()) {
                        case NUMERIC: {
                            hospitalName = String.valueOf(hospitalNameCell.getNumericCellValue()).trim();
                            validateDto.setHospitalName(String.valueOf(hospitalName));
                            validateDto.setHospitalNameStatus(false);
                            break;
                        }
                        case STRING: {
                            hospitalName = hospitalNameCell.getStringCellValue().trim();
                            validateDto.setHospitalName(hospitalName);
                            validateDto.setHospitalNameStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = hospitalNameCell.getBooleanCellValue();
                            validateDto.setHospitalName(String.valueOf(booleanCellValue).trim());
                            validateDto.setHospitalNameStatus(false);
                            validateDto.setHospitalNameErrorMessage("Hospital Name Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = hospitalNameCell.getErrorCellValue();
                            validateDto.setHospitalName(String.valueOf(errorCellValue).trim());
                            validateDto.setHospitalNameStatus(false);
                            validateDto.setHospitalNameErrorMessage("Hospital Name Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            hospitalColumnIndexFlag = true;
                            validateDto.setHospitalNameStatus(false);
                            validateDto.setHospitalNameErrorMessage("Hospital Name None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            hospitalColumnIndexFlag = true;
                            validateDto.setHospitalNameStatus(false);
                            validateDto.setHospitalNameErrorMessage("Hospital Name Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException("Unexpected value: " + hospitalNameCell.getCachedFormulaResultType());
                    }
                }
            }
            /**
             * Address Validation
             */
            String address;
            if (dataRow.getCell(addressIndex) != null) {
                Cell addressCell = dataRow.getCell(addressIndex);
//                log.info("Employee Name From Excel : {} and it's dataType is :{}",employeeNameCell,employeeNameCell.getCellType());
                if (addressCell.getCellType() == CellType.NUMERIC) {
                    address = String.valueOf(addressCell.getNumericCellValue()).trim();
                    validateDto.setAddress(String.valueOf(address));
                    validateDto.setAddressStatus(false);
                } else if (addressCell.getCellType() == CellType.STRING) {
                    address = addressCell.getStringCellValue().trim();
                    validateDto.setAddress(address);
                    validateDto.setAddressStatus(true);
                } else if (addressCell.getCellType() == CellType._NONE) {
                    addressColumnIndexFlag = true;
                    validateDto.setAddressStatus(false);
                    validateDto.setAddressErrorMessage("Address Name None :: ");
                } else if (addressCell.getCellType() == CellType.BLANK) {
                    addressColumnIndexFlag = true;
                    validateDto.setAddressStatus(false);
                    validateDto.setAddressErrorMessage("Address Name Blank :: ");
                } else if (addressCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = addressCell.getErrorCellValue();
                    validateDto.setAddress(String.valueOf(errorCellValue).trim());
                    validateDto.setAddressStatus(false);
                    validateDto.setAddressErrorMessage("Address Name Error :: ");
                } else if (addressCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = addressCell.getBooleanCellValue();
                    validateDto.setAddress(String.valueOf(booleanCellValue).trim());
                    validateDto.setAddressStatus(false);
                    validateDto.setAddressErrorMessage("Address Name Boolean :: ");
                } else if (addressCell.getCellType() == CellType.FORMULA) {
                    switch (addressCell.getCellType()) {
                        case NUMERIC: {
                            address = String.valueOf(addressCell.getNumericCellValue()).trim();
                            validateDto.setAddress(String.valueOf(address));
                            validateDto.setAddressStatus(false);
                            break;
                        }
                        case STRING: {
                            address = addressCell.getStringCellValue().trim();
                            validateDto.setAddress(address);
                            validateDto.setAddressStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = addressCell.getBooleanCellValue();
                            validateDto.setAddress(String.valueOf(booleanCellValue).trim());
                            validateDto.setAddressStatus(false);
                            validateDto.setAddressErrorMessage("Address Name Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = addressCell.getErrorCellValue();
                            validateDto.setAddress(String.valueOf(errorCellValue).trim());
                            validateDto.setAddressStatus(false);
                            validateDto.setAddressErrorMessage("Address Name Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            addressColumnIndexFlag = true;
                            validateDto.setAddressStatus(false);
                            validateDto.setAddressErrorMessage("Address Name None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            addressColumnIndexFlag = true;
                            validateDto.setAddressStatus(false);
                            validateDto.setAddressErrorMessage("Address Name Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException("Unexpected value: " + addressCell.getCachedFormulaResultType());
                    }
                }
            }
            /**
             * City Validation
             */
            String city;
            if (dataRow.getCell(cityIndex) != null) {
                Cell cityCell = dataRow.getCell(cityIndex);
//                log.info("Employee Name From Excel : {} and it's dataType is :{}",employeeNameCell,employeeNameCell.getCellType());
                if (cityCell.getCellType() == CellType.NUMERIC) {
                    city = String.valueOf(cityCell.getNumericCellValue()).trim();
                    validateDto.setCity(String.valueOf(city));
                    validateDto.setCityStatus(false);
                } else if (cityCell.getCellType() == CellType.STRING) {
                    city = cityCell.getStringCellValue().trim();
                    validateDto.setCity(city);
                    validateDto.setCityStatus(true);
                } else if (cityCell.getCellType() == CellType._NONE) {
                    cityColumnIndexFlag = true;
                    validateDto.setCityStatus(false);
                    validateDto.setCityErrorMessage("City Name None :: ");
                } else if (cityCell.getCellType() == CellType.BLANK) {
                    cityColumnIndexFlag = true;
                    validateDto.setCityStatus(false);
                    validateDto.setCityErrorMessage("Hospital Name Blank :: ");
                } else if (cityCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = cityCell.getErrorCellValue();
                    validateDto.setCity(String.valueOf(errorCellValue).trim());
                    validateDto.setCityStatus(false);
                    validateDto.setCityErrorMessage("Hospital Name Error :: ");
                } else if (cityCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = cityCell.getBooleanCellValue();
                    validateDto.setCity(String.valueOf(booleanCellValue).trim());
                    validateDto.setCityStatus(false);
                    validateDto.setCityErrorMessage("Hospital Name Boolean :: ");
                } else if (cityCell.getCellType() == CellType.FORMULA) {
                    switch (cityCell.getCellType()) {
                        case NUMERIC: {
                            city = String.valueOf(cityCell.getNumericCellValue()).trim();
                            validateDto.setCity(String.valueOf(city));
                            validateDto.setCityStatus(false);
                            break;
                        }
                        case STRING: {
                            city = cityCell.getStringCellValue().trim();
                            validateDto.setCity(city);
                            validateDto.setCityStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = cityCell.getBooleanCellValue();
                            validateDto.setCity(String.valueOf(booleanCellValue).trim());
                            validateDto.setCityStatus(false);
                            validateDto.setCityErrorMessage("Hospital Name Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = cityCell.getErrorCellValue();
                            validateDto.setCity(String.valueOf(errorCellValue).trim());
                            validateDto.setCityStatus(false);
                            validateDto.setCityErrorMessage("Hospital Name Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            cityColumnIndexFlag = true;
                            validateDto.setCityStatus(false);
                            validateDto.setCityErrorMessage("Hospital Name None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            cityColumnIndexFlag = true;
                            validateDto.setCityStatus(false);
                            validateDto.setCityErrorMessage("Hospital Name Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException("Unexpected value: " + cityCell.getCachedFormulaResultType());
                    }
                }
            }
            /**
             * State Validation
             */
            String state;
            if (dataRow.getCell(stateIndex) != null) {
                Cell stateCell = dataRow.getCell(stateIndex);
//                log.info("Employee Name From Excel : {} and it's dataType is :{}",employeeNameCell,employeeNameCell.getCellType());
                if (stateCell.getCellType() == CellType.NUMERIC) {
                    state = String.valueOf(stateCell.getNumericCellValue()).trim();
                    validateDto.setState(String.valueOf(state));
                    validateDto.setStateStatus(false);
                } else if (stateCell.getCellType() == CellType.STRING) {
                    state = stateCell.getStringCellValue().trim();
                    validateDto.setState(state);
                    validateDto.setStateStatus(true);
                } else if (stateCell.getCellType() == CellType._NONE) {
                    stateColumnIndexFlag = true;
                    validateDto.setStateStatus(false);
                    validateDto.setStateErrorMessage("State Name None :: ");
                } else if (stateCell.getCellType() == CellType.BLANK) {
                    stateColumnIndexFlag = true;
                    validateDto.setStateStatus(false);
                    validateDto.setStateErrorMessage("State Name Blank :: ");
                } else if (stateCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = stateCell.getErrorCellValue();
                    validateDto.setState(String.valueOf(errorCellValue).trim());
                    validateDto.setStateStatus(false);
                    validateDto.setStateErrorMessage("State Name Error :: ");
                } else if (stateCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = stateCell.getBooleanCellValue();
                    validateDto.setState(String.valueOf(booleanCellValue).trim());
                    validateDto.setStateStatus(false);
                    validateDto.setStateErrorMessage("Hospital Name Boolean :: ");
                } else if (stateCell.getCellType() == CellType.FORMULA) {
                    switch (stateCell.getCellType()) {
                        case NUMERIC: {
                            state = String.valueOf(stateCell.getNumericCellValue()).trim();
                            validateDto.setState(String.valueOf(state));
                            validateDto.setStateStatus(false);
                            break;
                        }
                        case STRING: {
                            state = stateCell.getStringCellValue().trim();
                            validateDto.setState(state);
                            validateDto.setStateStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = stateCell.getBooleanCellValue();
                            validateDto.setState(String.valueOf(booleanCellValue).trim());
                            validateDto.setStateStatus(false);
                            validateDto.setStateErrorMessage("State Name Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = stateCell.getErrorCellValue();
                            validateDto.setState(String.valueOf(errorCellValue).trim());
                            validateDto.setStateStatus(false);
                            validateDto.setStateErrorMessage("State Name Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            stateColumnIndexFlag = true;
                            validateDto.setStateStatus(false);
                            validateDto.setStateErrorMessage("state Name None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            stateColumnIndexFlag = true;
                            validateDto.setStateStatus(false);
                            validateDto.setStateErrorMessage("state Name Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException("Unexpected value: " + stateCell.getCachedFormulaResultType());
                    }
                }
            }
            /**
             * pinCode Validation
             */
            String pinCode;
            if (dataRow.getCell(pinCodeIndex) != null) {
                Cell pinCodeCell = dataRow.getCell(pinCodeIndex);
//                log.info("pincode Name From Excel : {} and it's dataType is :{}", pinCodeCell, pinCodeCell.getCellType());
                if (pinCodeCell.getCellType() == CellType.NUMERIC) {
                    double pinCodeValue = pinCodeCell.getNumericCellValue();
                    long pinCodeLong = (long) pinCodeValue;
                    // Check if the value is an integer
                    if (pinCodeValue % 1 == 0) {
                        // It's an integer
                        validateDto.setPinCode(pinCodeLong);
                    } else {
                        // It's a double
                        validateDto.setPinCode((long) pinCodeLong);
                    }
                    validateDto.setPinCodeStatus(false);

                } else if (pinCodeCell.getCellType() == CellType.STRING) {
                    // If it's a string, parse it as needed
                    String pinCodeString = pinCodeCell.getStringCellValue();
                    try {
                        long pinCodeLong = Long.parseLong(pinCodeString);
                        validateDto.setPinCode(pinCodeLong);
                        validateDto.setPinCodeStatus(false);
                    } catch (NumberFormatException e) {
                        // Handle parsing error if needed
                    }
                } else if (pinCodeCell.getCellType() == CellType.STRING) {
                    pinCode = pinCodeCell.getStringCellValue().trim();
                    validateDto.setPinCode(Long.parseLong(pinCode));
                    validateDto.setPinCodeStatus(true);
                } else if (pinCodeCell.getCellType() == CellType._NONE) {
                    pinCodeColumnIndexFlag = true;
                    validateDto.setPinCodeStatus(false);
                    validateDto.setPinCodeErrorMessage("PinCode Name None :: ");
                } else if (pinCodeCell.getCellType() == CellType.BLANK) {
                    pinCodeColumnIndexFlag = true;
                    validateDto.setPinCodeStatus(false);
                    validateDto.setPinCodeErrorMessage("PinCode Name Blank :: ");
                } else if (pinCodeCell.getCellType() == CellType.ERROR) {
                    byte errorCellValue = pinCodeCell.getErrorCellValue();
                    validateDto.setPinCode(Long.parseLong(String.valueOf(errorCellValue).trim()));
                    validateDto.setPinCodeStatus(false);
                    validateDto.setPinCodeErrorMessage("PinCode Name Error :: ");
                } else if (pinCodeCell.getCellType() == CellType.BOOLEAN) {
                    boolean booleanCellValue = pinCodeCell.getBooleanCellValue();
                    validateDto.setPinCode(Long.parseLong(String.valueOf(booleanCellValue).trim()));
                    validateDto.setPinCodeStatus(false);
                    validateDto.setPinCodeErrorMessage("PinCode Name Boolean :: ");
                } else if (pinCodeCell.getCellType() == CellType.FORMULA) {
                    switch (pinCodeCell.getCellType()) {
                        case NUMERIC: {
                            double pinCodeValue = pinCodeCell.getNumericCellValue();
                            long pinCodeLong = (long) pinCodeValue;
                            // Check if the value is an integer
                            if (pinCodeValue % 1 == 0) {
                                // It's an integer
                                validateDto.setPinCode(pinCodeLong);
                            } else {
                                // It's a double
                                validateDto.setPinCode((long) pinCodeLong);
                            }
                            validateDto.setPinCodeStatus(false);
                            break;
                        }

                        case STRING: {
                            // If it's a string, attempt to parse it as a long
                            String pinCodeString = pinCodeCell.getStringCellValue().trim();
                            try {
                                long pinCodeLong = Long.parseLong(pinCodeString);
                                validateDto.setPinCode(pinCodeLong);
                            } catch (NumberFormatException e) {
                                // Handle parsing error if needed
                            }
                            validateDto.setPinCodeStatus(true);
                            break;
                        }
                        case BOOLEAN: {
                            boolean booleanCellValue = pinCodeCell.getBooleanCellValue();
                            validateDto.setPinCode(booleanCellValue ? 1L : 0L); // Assuming 1 for true and 0 for false
                            validateDto.setPinCodeStatus(false);
                            validateDto.setPinCodeErrorMessage("PinCode Name Boolean Formula :: ");
                            break;
                        }
                        case ERROR: {
                            byte errorCellValue = pinCodeCell.getErrorCellValue();
                            validateDto.setPinCode(Long.parseLong(String.valueOf(errorCellValue).trim()));
                            validateDto.setPinCodeStatus(false);
                            validateDto.setPinCodeErrorMessage("PinCode Name Error Formula :: ");
                            break;
                        }
                        case _NONE: {
                            pinCodeColumnIndexFlag = true;
                            validateDto.setPinCodeStatus(false);
                            validateDto.setPinCodeErrorMessage("PinCode Name None Formula :: ");
                            break;
                        }
                        case BLANK: {
                            pinCodeColumnIndexFlag = true;
                            validateDto.setPinCodeStatus(false);
                            validateDto.setPinCodeErrorMessage("PinCode Name Blank Formula :: ");
                            break;
                        }
                        default:
                            throw new IllegalArgumentException("Unexpected value: " + pinCodeCell.getCachedFormulaResultType());
                    }
                }
            }
            if (hospitalColumnIndexFlag && addressColumnIndexFlag && cityColumnIndexFlag && stateColumnIndexFlag && pinCodeColumnIndexFlag) {
                log.info("Blank Row");
            } else {
                clientListNetworkHospitalDataStatuses.add(validateDto);
            }
        }
    }

    @Override
    public String uploadNetworkHospitalData(List<ClientListNetworkHospitalDataStatus> clientListMemberDetailsDataStatuses, Long clientListId, Long productId) {

        List<ClientListNetworkHospitalEntity> clientListNetworkHospitalEntities = clientListMemberDetailsDataStatuses.stream().map(dto -> {
            ClientListNetworkHospitalEntity entity = new ClientListNetworkHospitalEntity();
            entity.setHospitalName(dto.getHospitalName());
            entity.setAddress(dto.getAddress());
            entity.setCity(dto.getCity());
            entity.setState(dto.getState());
            entity.setPinCode(dto.getPinCode());
            entity.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            entity.setRecordStatus("ACTIVE");
            if (clientListId != null) {
                ClientList clientList = clientListRepository.findById(clientListId).orElseThrow(() -> new InvalidClientList("ClientList is not Found"));
                entity.setClientListId(clientList);
                entity.setRfqId(clientList.getRfqId());
            }
            if (productId != null) {
                Product product = productRepository.findById(productId).orElseThrow(() -> new InvalidProduct("Product is not Found"));
                entity.setProductId(product);
            }
            return entity;
        }).collect(Collectors.toList());
        clinetListNetworkHospitalRepository.deleteByClientListIdAndProductId(clientListId, productId);


        clinetListNetworkHospitalRepository.saveAll(clientListNetworkHospitalEntities);
        return "upload successfully";
    }

    @Override
    public List<GetAllClientListNetWorkHospitalDto> getAllclientListEnrollmentData(Long clientListId, Long productId) {
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

        return clinetListNetworkHospitalRepository.findAll().stream().filter(entity -> entity.getRecordStatus().equalsIgnoreCase("ACTIVE")).filter(i -> clientListId != 0 && i.getClientListId().getCid() == clientListId).filter(i -> productId != 0 && i.getProductId().getProductId().equals(productId)).map(i -> {
            GetAllClientListNetWorkHospitalDto getAllClientListNetWorkHospitalDto = new GetAllClientListNetWorkHospitalDto();
            getAllClientListNetWorkHospitalDto.setHospitalName(i.getHospitalName());
            getAllClientListNetWorkHospitalDto.setAddress(i.getAddress());
            getAllClientListNetWorkHospitalDto.setCity(i.getCity());
            getAllClientListNetWorkHospitalDto.setState(i.getState());
            getAllClientListNetWorkHospitalDto.setPinCode(i.getPinCode());

            return getAllClientListNetWorkHospitalDto;

        }).toList();
    }

    @Override
    public byte[] getNetworkHospitalInExcelFormat(Long clientListId, Long productId) {
        List<ClientListNetworkHospitalEntity> clientListNetworkHospitalEntities = clinetListNetworkHospitalRepository.findAll().stream().filter(c -> c.getRecordStatus().equalsIgnoreCase("ACTIVE")).filter(i -> clientListId != 0 && i.getClientListId().getCid() == clientListId).filter(i -> productId != 0 && i.getProductId().getProductId().equals(productId)).toList();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Network Hospital List");

            // Create headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"HOSPITAL NAME", "ADDRESS", "CITY", "STATE", "PIN CODE"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            //Report Data
            int rowNum = 1;
            for (ClientListNetworkHospitalEntity members : clientListNetworkHospitalEntities) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(members.getHospitalName());
                row.createCell(1).setCellValue(members.getAddress());
                row.createCell(2).setCellValue(members.getCity());
                row.createCell(3).setCellValue(members.getState());
                row.createCell(4).setCellValue(members.getPinCode());
            }
            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception properly in your application
            return new byte[0]; // Return empty byte array if an error occurs;
        }
    }
}




