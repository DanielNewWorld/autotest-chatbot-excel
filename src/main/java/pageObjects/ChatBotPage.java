package pageObjects;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatBotPage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(ChatBotPage.class.getName());
    private final WebDriverWait wait;
    private final WebDriverWait wait_sm;
    public ChatBotPage(WebDriver driver, long timeoutInSeconds) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait_sm = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    }

    //QBO Locators
    public final By headerHelpButtonLocator = By.xpath("//button[@data-id='help']");
    public final By globalHeaderHelpButtonLocator = By.xpath("//span[@data-id='global-header-help']/button");
    public final By textAreaLocator = By.xpath("//div[contains(@data-placeholder, 'Type or ask something') and contains(@class, 'TextInput')] | //textarea[contains(@class, 'InputBar')] | //textarea[contains(@class, 'TextArea')]");
    public final By inputBoxLocatorQBO = By.xpath("//textarea[contains(@data-lp-point,'chat_input')]|//textarea[contains(@class, 'StyledInputBar__StyledTextArea')] | //div[contains(@data-placeholder,'Type or ask something')]");
    public final By responseBoxLocatorQBO = By.xpath("//div[contains(@data-lp-cust-id,'transcript_bubble_agent_text')]|//div[contains(@class, 'MessageContainer')] | //div[contains(@class, 'StyledMessageGroup' or 'text-content')]"); // text-content
    public final By responseBoxLocatorAI2 = By.xpath("//div[contains(@class, 'ChatBubble-message') or contains(@class, 'ChatBubble-assistant')] | //div[@title and contains(@class, 'ChatBubble-message')] | //div[@title and contains(@class, 'ChatBubble-message')]/span[contains(@class, 'ChatBubble-text')]"); // text-content
    public final By responseBoxLocatorAI3 = By.xpath("//div[contains(@class,'ChatBubble-message') or contains(@class,'ChatBubble-assistant')] | //div[@title and contains(@class,'ChatBubble-message')] | //div[@title and contains(@class,'ChatBubble-message')]/span[contains(@class, 'ChatBubble-text')] | //div[contains(@class, 'StyledMessageGroup') and contains(@class,'MessageContainer')]");
    public final By intuitAIButton = By.xpath("//button[.//span[text()='Intuit AI']]");
    public final By textAreaLocator2 = By.xpath("//*[contains(text(), 'Type or ask something')] | //*[contains(text(), 'Type something')] | //*[contains(@placeholder, 'Type something')]");
    public final By inputField3rd = By.xpath("//textarea[contains(@class, 'StyledInputBar__StyledTextArea')]");
    public final By intuitAIButtonGlobal = By.xpath("//span[contains(text(), 'Intuit AI')] | //*[contains(text(), 'Intuit AI')] | //span[contains(text(), 'Help')]");
    public final By responseLoading = By.xpath("//div[contains(@class, 'PendingResponse-pending-response') or contains(span, 'Thinking') or contains(@class, 'pending-response')] | //div[contains(@class,'response')]");

    public List<WebElement> getResponseBoxLocatorAI3() {
        return driver.findElements(responseBoxLocatorAI3);
    }
    public WebElement getHeaderHelpButtonLocator() {
        return driver.findElement(headerHelpButtonLocator);
    }
    public WebElement getGlobalHeaderHelpButtonLocator() {
        return driver.findElement(globalHeaderHelpButtonLocator);
    }
    public WebElement getTextAreaLocator() {return driver.findElement(textAreaLocator);}
    public WebElement getTextAreaLocator2() {return driver.findElement(textAreaLocator2);}
    public WebElement getIntuitAIButton() {
        return driver.findElement(intuitAIButton);
    }
    public WebElement getIntuitAIButtonGlobal() {
        return driver.findElement(intuitAIButtonGlobal);
    }
    public WebElement getInputBoxLocator() {
        return driver.findElement(inputBoxLocatorQBO);
    }
    public List<WebElement> getResponseBoxLocator() {
        return driver.findElements(responseBoxLocatorQBO);
    }
    public WebElement getInputField3rd() {return driver.findElement(inputField3rd);}
    public WebElement getResponseLoading() {return driver.findElement(responseLoading);}

    public void openChatbot(WebElement helpButton, WebElement globalHelpButton, String[] xPathHelp) {
        List<WebElement> chatButtons = Arrays.asList(helpButton, globalHelpButton); // prettier-ignore
        for (WebElement button : chatButtons) {
            if (button.isDisplayed()) {
                button.click();
                return;
            }
        }
        this.defaultChatClick(xPathHelp);
    }

    public void goToInputField(WebElement inputBox, String[] xPathInput, String[] xPathAiInput, String[] xPathOptions) {
        WebElement textArea;
        try {
            textArea = inputBox;
            if (textArea.isDisplayed()) {
                wait.until(ExpectedConditions.elementToBeClickable(textArea));
                textArea.click();
                System.out.println("Was ale to click on the input box...\n");
            } else if(!textArea.isDisplayed()) {
                this.defaultChatClick(xPathInput);
                System.out.println("Clicking buttons...\n");
            }else {
                System.out.println("Using AI chat type to send message...");
                this.defaultChatType(xPathAiInput, "test");
            }
        } catch (NoSuchElementException e) {
            this.defaultChatClick(xPathInput);
            System.out.println("Clicking 2nd option buttons...\n");
            this.defaultChatClick(xPathOptions);
        }
    }

    public void sendValueToInput(String message, WebElement inputBoxLocator1, WebElement inputBoxLocator2, WebElement inputBoxLocator3, String[] xPathToWrite2) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        WebElement inputFieldFound = null;
        if (inputBoxLocator1.isDisplayed()) {
            inputFieldFound = inputBoxLocator1;
        } else if (inputBoxLocator2.isDisplayed()) {
            inputFieldFound = inputBoxLocator2;
        } else if (inputBoxLocator3.isDisplayed()) {
            inputFieldFound = inputBoxLocator3;
        }
        if (inputFieldFound != null) { // Check if we found a displayed input field
            try {
                System.out.println("------Input field is displayed - trying to send message - 1");
                wait.until(ExpectedConditions.visibilityOf(inputFieldFound));
                inputFieldFound.sendKeys(message);
                inputFieldFound.sendKeys(Keys.ENTER);
            } catch (NoSuchElementException | ElementClickInterceptedException e) {
                System.out.println("There's may be a popup window in the way - trying to remove and send message.");
                this.defaultChatType(xPathToWrite2, message);
                System.out.println("Using AI chat type to send message... 3rd option");
            }
        } else {
            System.out.println("Input field is not displayed - trying to send message - 2");
            this.defaultChatType(xPathToWrite2, message);
            System.out.println("Using AI chat type to send message... 2nd option");
        }
    }

    public void intuitAIOpenChat(WebElement intuitAiButton, String[] xPathAIHelp) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(intuitAiButton));
                intuitAiButton.click();
                System.out.println("Was able to click on the Intuit AI button...");
            }
            catch (Exception e) {
                System.out.println("There's may be a popup window in the way - trying to remove and click.");
                this.defaultChatClick(xPathAIHelp);
            }
    }

    public void sendTextWithActions(String text) {
        Actions actions = new Actions(driver);
        actions.sendKeys(text).perform();
        actions.sendKeys(Keys.ENTER).perform();
    }

    //DEFAULT CHAT INTERACTION
    public String[] inputAiFieldXpaths = {
            "//div[contains(@data-placeholder, 'Type or ask something') and contains(@class, 'RichTextInput-empty')]", // Primary locator for div
            "//textarea[contains(@class, 'StyledInputBar__StyledTextArea') and contains(@placeholder, 'Type something...')]", // Primary locator for textarea
            "//div[contains(@data-placeholder, 'Type or ask')]",
            "//textarea[contains(@placeholder, 'Type something')]",
            "//div[contains(@class, 'RichTextInput')]",
            "//textarea[contains(@class, 'StyledInputBar')]",
            "//textarea[contains(@class, 'StyledTextArea')]",
            "//div[contains(@data-placeholder, 'ask something')]",
            "//textarea[contains(@aria-label, 'Ask a question')]",
            "//textarea[contains(@class, 'InputBar')]",
            "//textarea[contains(@class, 'TextArea')]",
            "//textarea[contains(@data-lp-point, 'chat_input')]",
            "//textarea[contains(@class, 'chasitorText')]",
            "//textarea[contains(@placeholder, 'Type something...')]"
    };
    public String[] intuitAIXpaths = {
            "//button[.//span[text()='Intuit AI']]", // Primary locator
            "//span[text()='Intuit AI']", // Secondary locator
            "//button[contains(@class, 'intuit-ai')]",
            "//i[contains(@class, 'intuit-ai-icon')]",
            "//button[contains(@id,'intuit-ai-btn')]",
            "//div[contains(text(),'Intuit AI')]",
            "//button[contains(text(),'Intuit AI')]",
            "//span[contains(text(),'Intuit AI')]",
            "//div[contains(text(),'INTUIT AI')]",
            "//button[contains(text(),'INTUIT AI')]",
            "//span[contains(text(),'INTUIT AI')]",
            "//button[@data-id='intuit-ai']",
            "//span[@data-id='intuit-ai-header']/button"
    };
    public String[] xToOpen = {
            "//span[contains(@class, 'chat')]",
            "//i[contains(@class, 'hi-help-o')]",
            "//button[contains(@id,'esw-fab')]",
            "//div[contains(text(),'chat')]",
            "//button[contains(text(),'chat')]",
            "//span[contains(text(),'chat')]",
            "//div[contains(text(),'Chat')]",
            "//button[contains(text(),'Chat')]",
            "//span[contains(text(),'Chat')]",
            "//button[@data-id='help']",
            "//span[@data-id='global-header-help']/button"
    }; //possible XPaths to open chat window
    public String[] xToReach = {
            "//span[contains(text(), 'Web Chat')]",
            "//button[contains(@class,'lp-chat-btn')]",
            "//button[contains(text(), 'Chat Now')]",
            "//button[contains(text(), 'chat now')]"
    }; //first chat buttons

    public String[] xOptions = {
            "//button[contains(@class, 'embeddedServiceLiveAgentStateChatRichItem')][contains(text(), 'Help me with something else')]"
    }; //secondary chat buttons if input boxes not available
    public String[] xRespBox = {
            "//div[contains(@data-lp-cust-id,'transcript_bubble_agent_text')]",
            "(//div[@class='chatMessage agent plainText']//span)",
            "//div[contains(@class, 'agent')]",
            "//div[contains(@class, 'StyledTextMessage')]"
    }; //responses

    //method tries to click selection of xpaths until one works
    public void defaultChatClick(String[] varXpaths) {
        boolean isPresent;
        for (String xpath : varXpaths) {
            List<WebElement> buttons = driver.findElements(By.xpath(xpath));
            isPresent = !buttons.isEmpty();

            if (isPresent) {
                try {
                    WebElement toClick = wait_sm.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
                    toClick.click();
                    break;
                }
                catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.TimeoutException | org.openqa.selenium.ElementClickInterceptedException e) {
                    LOGGER.log(Level.SEVERE, "An exception occurred while processing xpaths for clicking", e);
                }
            }
        }
    }

    //method tries selection of xpaths to enter text until one works
    public void defaultChatType(String[] varXpaths, String msg) {
        for (String xpath : varXpaths) {
            try {
                WebElement input = wait_sm.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
                input.sendKeys(msg);
                input.sendKeys(Keys.ENTER);
                break;
            }
            catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.TimeoutException | org.openqa.selenium.ElementClickInterceptedException e) {
                LOGGER.log(Level.SEVERE, "An exception occurred while processing xpaths for typing", e);
            }
        }
    }

    //method collects text from all visible agent responses with xpaths
    public String defaultChatGetResp(String[] varXpaths) {
        StringBuilder collection = new StringBuilder();
        for (String xpath : varXpaths) {
            try {
                List<WebElement> responses = wait_sm.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));
                for (WebElement element : responses) {
                    collection.append(element.getText()).append("\n");
                }
                break;
            }
            catch (NoSuchElementException | TimeoutException | ElementClickInterceptedException e) {
                LOGGER.log(Level.SEVERE, "An exception occurred while processing xpaths", e);
            }
        }
        return collection.toString();
    }

    public static void waitForPageLoad( WebDriver driver, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        wait.until((ExpectedCondition<Boolean>) wd ->
                wd != null && "complete".equals(((JavascriptExecutor) wd).executeScript("return document.readyState"))
        );
    }
}
