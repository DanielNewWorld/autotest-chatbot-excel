package pageObjects;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleSheetsReader {
    // API key
    @Value("${google.api.key}")
    private final String apiKey = "AIzaSyDBUN8dPv25HspcBZqyOb8vXjE0zIE8A1c";

    // ID table Google Sheets
    private final String spreadsheetId = "1rGWGKaUxpykQXEB5VAdskdGEHJFXoYa77Q7tHXc4-84";

    // Sheets location and range of cells
    private final String range = "A1:B2";

    public List<List<Object>> readCell() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        // Create Google Sheets API service
        Sheets sheetsService = new Sheets.Builder(httpTransport, jsonFactory, null)
                .setApplicationName("Google Sheets Reader")
                .setGoogleClientRequestInitializer(request -> {
                    request.setDisableGZipContent(true);
                    request.getRequestHeaders().set("X-Goog-Api-Key", apiKey);
                })
                .build();

        // Getting values from the table and range of cells
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        // Getting values from the table and range of cells
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Setting cell values: " + values.get(0).get(0));
        }
        return values;
    }
}