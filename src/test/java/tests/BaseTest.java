package tests;
import io.github.bonigarcia.wdm.WebDriverManager;
import javafx.application.Application;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import ui.TestRunnerUI;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class BaseTest {
    protected WebDriver driver;
    public Properties properties;
    public WebDriverWait wait;
    public static int timeout;
    public static String FILE_PATH;
    public static String qboURL;
    public static String isAI;
    public int sheetNumber;
    public static void setFilePath(String filePath) {
        FILE_PATH = filePath;
    }
    public static void setURL(String url) {
        qboURL = url;
    }
    public static String getFilePath() {return FILE_PATH;}
    public static String getQboURL() {return qboURL;}

    @BeforeMethod
    public void setup() throws IOException {
        Application.launch(TestRunnerUI.class);
        properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+"\\src\\main\\resources\\data.properties");
        properties.load(fileInputStream);
        String browserName = properties.getProperty("browser");
        sheetNumber = Integer.parseInt(properties.getProperty("sheetNumber"));
        isAI = properties.getProperty("intuitAI");
        timeout = Integer.parseInt(properties.getProperty("timeout"));
        String HEADLESS = properties.getProperty("HEADLESS");
        if (qboURL == null) {
            qboURL = properties.getProperty("qboURL");
        }
        System.out.println("browser name: " + browserName);
        switch (browserName) {
            case "chrome" -> {
                ChromeOptions optionsGC = new ChromeOptions();
                if (HEADLESS.equals("true")) {
                    optionsGC.addArguments("--headless");
                    System.out.println("headless options");
                }
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(optionsGC);
            }
            case "firefox" -> {
                FirefoxOptions optionsF = new FirefoxOptions();
                if (HEADLESS.equals("true")) {
                    optionsF.addArguments("--headless");
                    System.out.println("headless options");
                }
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(optionsF);
            }
            case "edge" -> {
                EdgeOptions optionsE = new EdgeOptions();
                if (HEADLESS.equals("true")) {
                    optionsE.addArguments("--headless");
                    System.out.println("headless options");
                }
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(optionsE);
            }
            case "opera" -> {
            }
            default -> throw new IllegalStateException("Unexpected value: " + browserName);
        }
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            this.quitDriver();
        }
    }

    // Add this method to be called from the UI's close event
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}