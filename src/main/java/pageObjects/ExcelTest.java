package pageObjects;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTest {
    static final String FILE_PATH = "C:\\Users\\kulie\\Downloads\\Yara Tech\\chatBOT.xlsx";
    static final String SHEET_NAME = "Sheet1";

    public static void readAndWriteExcel() throws IOException {
        FileInputStream inputStream = new FileInputStream(FILE_PATH);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        // Reading from the sheet
        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = new DataFormatter().formatCellValue(cell);
                System.out.println(cellValue);
            }
        }

        // Writing to the sheet (appending a new row with some data)
        int newRowNum = sheet.getLastRowNum() + 1;
        Row newRow = sheet.createRow(newRowNum);
        Cell newCell = newRow.createCell(0);
        newCell.setCellValue("New Data");
        inputStream.close(); // Close input stream before writing
        try (FileOutputStream outputStream = new FileOutputStream(FILE_PATH)) {
            workbook.write(outputStream);
        }
        workbook.close();
    }
}
