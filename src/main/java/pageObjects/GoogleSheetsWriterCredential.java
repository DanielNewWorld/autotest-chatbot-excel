package pageObjects;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsWriterCredential {

    private static final String APPLICATION_NAME = "Writer";
    private static final String SPREADSHEET_ID = "1rGWGKaUxpykQXEB5VAdskdGEHJFXoYa77Q7tHXc4-84";
    private static final String RANGE = "Sheet2"; // Change this to the desired sheet name

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        String pathToCredentials = "D:\\secret.json"; // Replace with the actual path to your credentials file

        GoogleCredential credentials = GoogleCredential.fromStream(new FileInputStream(pathToCredentials))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        Sheets sheetsService = new Sheets.Builder(httpTransport, jsonFactory, credentials)
                .setApplicationName(APPLICATION_NAME)
                .build();

        List<List<Object>> values = Collections.singletonList(Collections.singletonList("Your data to write"));

        ValueRange body = new ValueRange()
                .setValues(values);

        sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, RANGE, body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }
}