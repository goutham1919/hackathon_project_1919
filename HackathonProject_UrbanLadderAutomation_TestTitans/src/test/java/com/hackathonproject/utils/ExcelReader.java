package com.hackathonproject.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

// Reads test data from .xlsx files using Apache POI.
// Used by GiftCardTest DataProvider to load rows from the GiftCardData sheet.
public class ExcelReader {

    // Returns all data rows as a 2D array — skips the header row (row 0)
    public static Object[][] readExcelData(String filePath, String sheetName) {
        Object[][] data = null;

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet     = workbook.getSheet(sheetName);
            int totalRows   = sheet.getPhysicalNumberOfRows();
            int totalCols   = sheet.getRow(0).getPhysicalNumberOfCells();

            data = new Object[totalRows - 1][totalCols];

            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < totalCols; j++) {
                    data[i - 1][j] = getCellValueAsString(row.getCell(j));
                }
            }

        } catch (IOException e) {
            System.out.println("ERROR: Could not read Excel file: " + filePath + " — " + e.getMessage());
        }

        return data;
    }

    // Returns a single cell value by row and column index
    public static String readCell(String filePath, String sheetName, int rowNum, int colNum) {
        String value = "";

        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row   row   = sheet.getRow(rowNum);
            value = getCellValueAsString(row.getCell(colNum));

        } catch (IOException e) {
            System.out.println("ERROR: Could not read cell [" + rowNum + "," + colNum + "] — " + e.getMessage());
        }

        return value;
    }

    // Converts any cell type to a String so DataProvider always receives String values
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue();
            case NUMERIC:
                double numVal = cell.getNumericCellValue();
                // Return as whole number if there is no decimal part (e.g. phone numbers)
                return (numVal == Math.floor(numVal)) ? String.valueOf((long) numVal) : String.valueOf(numVal);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default:      return "";
        }
    }
}