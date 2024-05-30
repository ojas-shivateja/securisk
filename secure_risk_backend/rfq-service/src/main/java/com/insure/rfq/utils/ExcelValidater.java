package com.insure.rfq.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.InputStream;

public class ExcelValidater {
    public void validateExcelFile(InputStream inputStream) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);

        // Assuming the data is in the first sheet
        Sheet sheet = workbook.getSheetAt(0);

        // Iterate through rows
        for (Row row : sheet) {
            // Check for merged cells
            if (isRowMerged(sheet, row)) {
                // Handle merged cells
                System.out.println("Row " + (row.getRowNum() + 1) + " contains merged cells.");
            }

            // Iterate through cells
            for (Cell cell : row) {
                // Check for blank cells
                if (cell.getCellType() == CellType.BLANK) {
                    // Handle blank cells
                    System.out.println("Row " + (row.getRowNum() + 1) + ", Column " + (cell.getColumnIndex() + 1) + " is blank.");
                }

                // Check for empty cells
                if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) {
                    // Handle empty cells
                    System.out.println("Row " + (row.getRowNum() + 1) + ", Column " + (cell.getColumnIndex() + 1) + " is empty.");
                }
            }
        }

        workbook.close();
    }

    private boolean isRowMerged(Sheet sheet, Row row) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            if (mergedRegion.getFirstRow() <= row.getRowNum() && row.getRowNum() <= mergedRegion.getLastRow()) {
                return true;
            }
        }
        return false;
    }
}
