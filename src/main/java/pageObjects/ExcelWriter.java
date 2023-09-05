package pageObjects;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelWriter {

    public static void putStringExcel(int columnNumber, String putString) {
        String filePath = "C:\\Users\\kulie\\Downloads\\Yara Tech\\chatBOT.xlsx";

        // Here, I'm assuming that the sheet name is dependent on the column number.
        String sheetName = "Sheet" + columnNumber;

        try (FileInputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheet(sheetName);

            // Determine the new row number by adding 1 to the last row number
            int newRowNum = sheet.getLastRowNum() + 1;

            // Create a new row and cell
            Row newRow = sheet.createRow(newRowNum);
            Cell newCell = newRow.createCell(columnNumber); // Using the columnNumber here

            // Write the data to the new cell
            newCell.setCellValue(putString);

            // Open a FileOutputStream to write the updated file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
