package pageObjects;
import org.openqa.selenium.WebDriver;


public class BasePage {
    protected WebDriver driver;

    // Define the locator for the chatbot button

    public BasePage() {
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

}
