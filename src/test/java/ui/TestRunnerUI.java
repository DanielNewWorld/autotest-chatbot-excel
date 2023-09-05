package ui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import tests.BaseTest;
import tests.ChatBotTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TestRunnerUI extends Application {
    private final TextField txtExcelPath = new TextField();
    private final Button btnBrowse = new Button("Browse files");
    private final TextField txtURL = new TextField();
    private final CheckBox aiCheck = new CheckBox("Run automation with \"Intuit Ai\" feature");
    private final ComboBox<String> browserBox = new ComboBox<>();
    private final ComboBox<String> timeoutBox = new ComboBox<>();
    private final Button btnRunTests = new Button("Run automation");
    private final Button saveChanges = new Button("Apply changes");
    private Stage primaryStage;
    private final VBox root = new VBox(10);
    private final Scene scene = new Scene(root, 500, 500);
    private final Properties properties = new Properties();

    @Override
    public void start(Stage primaryStage) {
        try{
            try (FileReader fileReader = new FileReader("src\\main\\resources\\data.properties")) {
                properties.load(fileReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String filePath = properties.getProperty("FILE_PATH");
            //String isHeadless = properties.getProperty("HEADLESS");
            String browserProp = properties.getProperty("browser");
            String timeoutProp = properties.getProperty("timeout");
            String iaiProp = properties.getProperty("intuitAI");
            String urlProp = properties.getProperty("qboURL");
            this.primaryStage = primaryStage;
            Label pathLabel = new Label("Path to Excel file with automation data");
            browserBox.getItems().addAll("Chrome", "Firefox", "Edge");
            timeoutBox.getItems().addAll("5", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "120");
            if (filePath.isEmpty()) {
                txtExcelPath.setText(properties.getProperty("defaultFILE_PATH"));
            } else {
                txtExcelPath.setText(filePath);
            }
            btnBrowse.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    txtExcelPath.setText(selectedFile.getAbsolutePath());
                    properties.setProperty("FILE_PATH", selectedFile.getAbsolutePath());
                }
            });

            Label urlLabel = new Label("URL of website");
            txtURL.setText(urlProp);
            txtURL.setOnAction(e -> properties.setProperty("qboURL", txtURL.getText()));

            Label browserLabel = new Label("Select browser of preference to run automation");
            Label timeoutLabel = new Label("Set timeout for actions");
            if (browserProp.equals("chrome")) {browserBox.setValue("Chrome");}
            if (browserProp.equals("firefox")) {browserBox.setValue("Firefox");}
            if (browserProp.equals("edge")) {browserBox.setValue("Edge");}

            if (iaiProp.equals("true")) {aiCheck.setSelected(true);}

            timeoutBox.setValue(timeoutProp);
            btnRunTests.setOnAction(e -> runTests());
            aiCheck.setOnAction(e -> {
                if (aiCheck.isSelected()) {
                    properties.setProperty("intuitAI", "true");
                }
                else {
                    properties.setProperty("intuitAI", "false");
                }
            });
            browserBox.setOnAction(e -> {
                if (browserBox.getValue().equals("Chrome")) {
                    properties.setProperty("browser", "chrome");
                }
                if (browserBox.getValue().equals("Firefox")) {
                    properties.setProperty("browser", "firefox");
                }
                if (browserBox.getValue().equals("Edge")) {
                    properties.setProperty("browser", "edge");
                }
            });
            timeoutBox.setOnAction(e -> properties.setProperty("timeout", timeoutBox.getValue()));

            saveChanges.setOnAction(e -> storeProps());

            root.setPadding(new Insets(10, 50, 50, 50));
            root.getChildren().addAll(pathLabel, txtExcelPath, btnBrowse, urlLabel, txtURL, aiCheck, browserLabel, browserBox, timeoutLabel, timeoutBox, btnRunTests, saveChanges);
            primaryStage.setTitle("Test Runner");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(this::handleWindowClose);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runTests() {
        String excelFilePath = txtExcelPath.getText();
        String url = txtURL.getText();
        Stage stage = (Stage) btnRunTests.getScene().getWindow();
        if (!url.isEmpty()) {
            BaseTest.setURL(url); // Pass the URL to the BaseTest class
        }
        File file = new File(excelFilePath);
        // If the file does not exist or is not a valid Excel file, prompt the user to browse for the file
        if (!file.exists() || !file.getName().endsWith(".xlsx")) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                excelFilePath = selectedFile.getAbsolutePath();
                txtExcelPath.setText(excelFilePath);
                properties.setProperty("FILE_PATH", selectedFile.getAbsolutePath());// Update the text field with the new path
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("File Selection Required");
                alert.setHeaderText(null);
                alert.setContentText("You must select a valid Excel file to proceed.");
                alert.showAndWait();
                return;
            }
        }
        BaseTest.setFilePath(excelFilePath); // Pass the file path to the BaseTest class
        TestNG testNG = new TestNG();
        testNG.setTestClasses(new Class[] { ChatBotTest.class }); // the test classes
        testNG.addListener(new TestListenerAdapter());
        storeProps();
        stage.close();
        testNG.run();
    }

    private void handleWindowClose(WindowEvent event) {
        Platform.exit();
        System.exit(0);
    }

    private void storeProps() {
        try (FileOutputStream fileOutputStream = new FileOutputStream("src\\main\\resources\\data.properties")) {
            properties.store(fileOutputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
