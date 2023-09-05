package pageObjects;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.InputStream;

public class ExcelReader {
    public String getStringExcel(int questionNumber) {
        String filePath = "C:\\Users\\kulie\\Downloads\\Yara Tech\\chatBOT.xlsx";
        String sheetName = "Sheet" + questionNumber;
        String cellReference = "A1";

        try (InputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.out.println("Sheet wi '" + sheetName + " not found.");
                return "";
            }

            CellReference ref = new CellReference(cellReference);
            Row row = sheet.getRow(ref.getRow());
            if (row == null) {
                System.out.println("Cell '" + cellReference + " not found.");
                return "";
            }

            Cell cell = row.getCell(ref.getCol());
            if (cell == null) {
                System.out.println("cell address: " + cellReference + "' cell is null.");
                return "";
            }

            //System.out.println("cell ==  " + cellReference + ": " + cellValue);
            return cell.getStringCellValue();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}