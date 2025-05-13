package com.fstart.service.common.utils;

import com.fstart.service.model.startup.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ExcelHelper
 *
 * @author: VuongVT2
 * @since: 2022/04/15
 */
public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"id", "logo", "name", "shortDescription", "description", "originalLink", "founded", "startupSize", "status"};
    static String SHEET_STARTUP = "Startup";
    static String SHEET_FOUNDER = "Founder";
    static String SHEET_STARTUP_FOUNDER = "StartupFounder";
    static String SHEET_STARTUP_FIELD = "StartupField";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static ExcelForm excelToStartup(InputStream is) {
        try {
            ExcelForm excelForm = new ExcelForm();
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheetStartup = workbook.getSheet(SHEET_STARTUP);
            Iterator<Row> rowsStartup = sheetStartup.iterator();

            List<StartupExcelForm> startupExcelForms = new ArrayList<>();

            int rowNumber = 0;

            while (rowsStartup.hasNext()) {
                StartupExcelForm startupExcelForm = new StartupExcelForm();
                Row currentRow = rowsStartup.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();


                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            startupExcelForm.setId((long) currentCell.getNumericCellValue());
                            break;
                        case 1:
                            startupExcelForm.setLogo(currentCell.getStringCellValue());
                            break;
                        case 2:
                            startupExcelForm.setName(currentCell.getStringCellValue());
                            break;
                        case 3:
                            startupExcelForm.setShortDescription(currentCell.getStringCellValue());
                            break;
                        case 4:
                            startupExcelForm.setDescription(currentCell.getStringCellValue());
                            break;
                        case 5:
                            startupExcelForm.setOriginalLink(currentCell.getStringCellValue());
                            break;
                        case 6:
                            startupExcelForm.setFounded((int) currentCell.getNumericCellValue());
                            break;
                        case 7:
                            startupExcelForm.setStartupSize((int) currentCell.getNumericCellValue());
                            break;
                        case 8:
                            startupExcelForm.setStatus(currentCell.getStringCellValue());
                            break;
                        case 9:
                            startupExcelForm.setCountryId(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                startupExcelForms.add(startupExcelForm);
            }
            excelForm.setStartupExcelForms(startupExcelForms);


            Sheet sheetFounder = workbook.getSheet(SHEET_FOUNDER);
            Iterator<Row> rowsFounder = sheetFounder.iterator();
            rowNumber = 0;

            List<FounderData> founders = new ArrayList<>();
            while (rowsFounder.hasNext()) {
                Row currentRow = rowsFounder.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();


                int cellIdx = 0;
                FounderData founderData = new FounderData();
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            founderData.setId((long) currentCell.getNumericCellValue());
                            break;
                        case 1:
                            founderData.setName(currentCell.getStringCellValue());
                            break;
                        case 2:
                            founderData.setSocialNetwork(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                founders.add(founderData);

            }
            excelForm.setFounders(founders);

            Sheet sheetStartupFounder = workbook.getSheet(SHEET_STARTUP_FOUNDER);
            Iterator<Row> rowsStartupFounder = sheetStartupFounder.iterator();
            rowNumber = 0;
            List<StartupFounderForm> startupFounderForms = new ArrayList<>();
            while (rowsStartupFounder.hasNext()) {
                StartupFounderForm startupFounderForm = new StartupFounderForm();
                Row currentRow = rowsStartupFounder.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();


                int cellIdx = 0;

                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            startupFounderForm.setStartupId((long) currentCell.getNumericCellValue());
                            break;
                        case 1:
                            startupFounderForm.setFounderId((long) currentCell.getNumericCellValue());
                            break;
                        case 2:
                        default:
                            break;
                    }
                    cellIdx++;
                }
                startupFounderForms.add(startupFounderForm);
            }
            excelForm.setStartupFounderForms(startupFounderForms);

            Sheet sheetStartupField = workbook.getSheet(SHEET_STARTUP_FIELD);
            Iterator<Row> rowsStartupField = sheetStartupField.iterator();
            rowNumber = 0;
            List<StartupFieldForm> startupFieldForms = new ArrayList<>();
            while (rowsStartupField.hasNext()) {
                Row currentRow = rowsStartupField.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();


                int cellIdx = 0;
                StartupFieldForm startupFieldForm = new StartupFieldForm();
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            startupFieldForm.setStartupId((long) currentCell.getNumericCellValue());
                            break;
                        case 1:
                            startupFieldForm.setFieldId(currentCell.getStringCellValue());
                            break;
                        case 2:
                        default:
                            break;
                    }
                    cellIdx++;
                }
                startupFieldForms.add(startupFieldForm);
            }
            excelForm.setStartupFieldForms(startupFieldForms);
            workbook.close();

            return excelForm;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
