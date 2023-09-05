package pageObjects;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage extends BasePage {
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver; wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public WebElement getSignInMenuElement() {
        return driver.findElement(By.xpath("//span[contains(text(), 'Sign in')]"));
    }

    public WebElement getAvatarElement() {
        return driver.findElement(By.xpath("//span[@data-cy='test-avatar-div']"));
    }

    public WebElement getLabelIdElement() {
        return driver.findElement(By.xpath("//span[@id='labelId']"));
    }

    public boolean isUserLoggedIn() {
        try {
            return this.getAvatarElement().isDisplayed() || this.getLabelIdElement().isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void allowUserToLogin() {
        // Wait for the sign-in menu to be visible
        wait.until(ExpectedConditions.visibilityOf(getSignInMenuElement()));
        try {
            // Wait for the user to log in with a timeout
            WebDriverWait waitLogin = new WebDriverWait(driver, Duration.ofSeconds(60));
            waitLogin.until(driver -> isUserLoggedIn());
            System.out.println("User is logged in");
        } catch (TimeoutException e) {
            WebDriverWait waitLogin = new WebDriverWait(driver, Duration.ofSeconds(60));
            waitLogin.until(driver -> isUserLoggedIn());
            System.out.println("User did not log in within the expected time");
            // Here you can add additional logic like notifying the user, retrying, etc.
        }
    }
}