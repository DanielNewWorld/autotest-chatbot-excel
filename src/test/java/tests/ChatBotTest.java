package tests;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageObjects.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatBotTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(ChatBotTest.class.getName());
    private ChatBotPage chatBotPage;
    private LoginPage loginPage;

    @BeforeMethod
    public void setupPages() {
        driver.manage().deleteAllCookies();
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        chatBotPage = new ChatBotPage(driver, timeout);
        loginPage = new LoginPage(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
    }

    @Test(priority = 1)
    public void singleMsgChatBotQBO() throws IOException, InterruptedException {
        LOGGER.info("-----Starting test - Navigating to URL");
        chatBotPage.navigateTo(BaseTest.getQboURL()); // NAVIGATE ---- to URL

        LOGGER.info("-----Wait for page to load");
        ChatBotPage.waitForPageLoad(driver, timeout); // wait for page to load

        LOGGER.info("-----Allowing user to Login In");
        loginPage.allowUserToLogin(); // ALLOW USER TO ----- LOGIN
        LOGGER.info("-----On the page and logged in");
        ChatBotPage.waitForPageLoad(driver, timeout);

        if (BaseTest.isAI.equals("true")) {
            try { // ----------------------- OPEN CHATBOT INTUIT AI
                LOGGER.info("----Trying to CLICK, Intuit AI button is DISPLAYED with 1st element."); // ----- IF DISPLAYED  CHECKING 1ST ELEMENT
                chatBotPage.intuitAIOpenChat(chatBotPage.getIntuitAIButton(), chatBotPage.intuitAIXpaths);
                LOGGER.info("-----Intuit AI Button Click 1st test Passed"); // INTUIT AI CHATBOT OPENED
            } catch (NoSuchElementException exception) {
                LOGGER.info("-----Trying to CLICK, Intuit AI button is DISPLAYED with 2nd element.");
                chatBotPage.intuitAIOpenChat(chatBotPage.getIntuitAIButtonGlobal(), chatBotPage.intuitAIXpaths);
                LOGGER.info("-----Intuit AI Button Click 2nd test Passed"); // INTUIT AI CHATBOT OPENED
            }
        } else { // ----------------------- OPEN CHATBOT QUICK BOOKS ONLINE
            LOGGER.info("Automation started with Intuit AI feature off. Clicking on the Help button.");
            chatBotPage.openChatbot(
                    chatBotPage.getHeaderHelpButtonLocator(),
                    chatBotPage.getGlobalHeaderHelpButtonLocator(),
                    chatBotPage.xToOpen
            );
        }

        LOGGER.info("-----Chatbot opened");

        try { // ----------- CLICK ON INPUT FIELD
            LOGGER.info("-----Trying to CLICK, Input field");
            if (chatBotPage.getTextAreaLocator().isDisplayed()){
                wait.until(ExpectedConditions.elementToBeClickable(chatBotPage.getTextAreaLocator()));
            }
            chatBotPage.goToInputField(chatBotPage.getTextAreaLocator(), chatBotPage.xToReach, chatBotPage.inputAiFieldXpaths, chatBotPage.xOptions); // go to input field
            LOGGER.info("-----Successfully CLICKED on INPUT field");
        }catch (NoSuchElementException ex) {
            if (chatBotPage.getTextAreaLocator2().isDisplayed()){
                wait.until(ExpectedConditions.elementToBeClickable(chatBotPage.getTextAreaLocator2()));
            }
            LOGGER.log(Level.WARNING, "Clicking on Input field DID NOT WORK with 2nd option. Trying the alternative method.", ex);
            chatBotPage.goToInputField(chatBotPage.getTextAreaLocator2(), chatBotPage.xToReach, chatBotPage.inputAiFieldXpaths, chatBotPage.xOptions);
        }

        try (FileInputStream inputStream = new FileInputStream(BaseTest.getFilePath());
            Workbook workbook = new XSSFWorkbook(inputStream)) {
            LOGGER.info("-----Reading from Excel file");
            Sheet sheet = workbook.getSheetAt(sheetNumber); // ----- GETTING EXCEL SHEET READY
            int rowNumber = sheet.getPhysicalNumberOfRows(); // Get number of rows
            Thread.sleep(BaseTest.timeout* 500L); //give time to close popup

            for (int i = 0; i < rowNumber; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String cellValueData = new DataFormatter().formatCellValue(row.getCell(0));
                LOGGER.info("----- Before sending a message: " + cellValueData);

                try { // ----- INTUIT AI SENDING INPUT
                    LOGGER.info("----------Sending value to Input field in INTUIT AI... option 1");
                    chatBotPage.sendValueToInput(cellValueData, chatBotPage.getInputBoxLocator(), chatBotPage.getTextAreaLocator(), chatBotPage.getInputField3rd(), chatBotPage.inputAiFieldXpaths); // send value to input field
                    LOGGER.info("----------Input sent to INTUIT AI Input field successfully option 1");
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
                } catch (NoSuchElementException ex) { // ----- SENDING INPUT WITH ACTIONS
                    LOGGER.log(Level.WARNING, "------Failed to Send INPUT with INTUIT AI. Trying the alternative method. option 2", ex);
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout)); // --Go to input field
                    try {
                        chatBotPage.goToInputField(chatBotPage.getTextAreaLocator(), chatBotPage.xToReach, chatBotPage.inputAiFieldXpaths, chatBotPage.xOptions);
                        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
                        chatBotPage.sendTextWithActions(cellValueData);  // SENDING INPUT WITH ACTIONS
                        LOGGER.info("--------Message sent using Actions successfully");
                    }catch (NoSuchElementException e) {
                        LOGGER.log(Level.SEVERE, "An unexpected error occurred. Using 3rd option", e);
                        chatBotPage.getTextAreaLocator2().sendKeys(cellValueData);
                        chatBotPage.getTextAreaLocator2().sendKeys(Keys.ENTER);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "An unexpected error occurred.", e);
                }

                // ----- INTUIT AI GET RESPONSE
                if (BaseTest.isAI.equals("true")) {
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
                    try { // ----- INTUIT AI GET RESPONSE 1st option
                        LOGGER.info("-----Trying to GET RESPONSE using Intuit AI 1st option");
                        String response = getLastElementText(chatBotPage.responseBoxLocatorAI2, timeout); // Choose appropriate locator and timeout
                        sheet.getRow(i).createCell(1).setCellValue(response);
                        LOGGER.info("-----Response received using Intuit AI 1st option");
                    } catch (NoSuchElementException ex) { // ----- INTUIT AI GET RESPONSE 2nd option
                        LOGGER.log(Level.WARNING, "Element TO GET RESPONSE not found. Trying the alternative method.", ex);
                        String alternativeResponse = getLastElementText(chatBotPage.responseBoxLocatorAI3, timeout); // Choose appropriate alternative locator and timeout
                        sheet.getRow(i).createCell(1).setCellValue(alternativeResponse);
                        LOGGER.info("-----Response received using Intuit AI 2nd option");
                    }
                }else { // ----- DEFAULT GET RESPONSE
                    ChatBotPage.waitForPageLoad(driver, timeout);
                    try {
                        if (waitForNewResponse(driver, chatBotPage, timeout)) {
                            LOGGER.info("------Write the Response to the Excel sheet using 1st option");
                            this.writeResponseWithDefault(sheet, i, 1);
                            LOGGER.info("------Response received 1st option is working");
                        } else {
                            LOGGER.warning("Response was not received within the expected timeout using the 2nd option.");
                        }
                    } catch (NoSuchElementException ex) {
                        LOGGER.log(Level.WARNING, "Element TO GET RESPONSE not found. Trying the alternative method 2nd.", ex);
                        ChatBotPage.waitForPageLoad(driver, timeout); // -------- DEFAULT GET RESPONSE2
                        this.writeResponseWithDefault2(sheet, i, 1); // WRITE DEFAULT2 ----- the response to the Excel sheet
                        LOGGER.info("Response received 2nd option is working");
                    }
                }

            }
            // Save the Excel file
            try (FileOutputStream outputStream = new FileOutputStream(FILE_PATH)) {
                workbook.write(outputStream);
                System.out.println("write outputStream to the Excel sheet");
            }
        }
        System.out.println("Processing complete.");
    }

    private boolean waitForNewResponse(WebDriver driver, ChatBotPage chatBotPage, int timeout) {
        int initialResponseCount = chatBotPage.getResponseBoxLocator().size();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeout))
                    .until(d -> chatBotPage.getResponseBoxLocator().size() > initialResponseCount);
            return true;
        } catch (TimeoutException e) {
            LOGGER.warning("--- While Waiting for Response, No response received within the expected timeout.");
            return false;
        }
    }

    public void writeResponseWithDefault(Sheet sheet, int getRow, int getCell){
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        String response1 = chatBotPage.getResponseBoxLocator().get(chatBotPage.getResponseBoxLocator().size() - 1).getText(); // Get the latest response
        sheet.getRow(getRow).createCell(getCell).setCellValue(response1); // WRITE DEFAULT ----- the response to the Excel sheet
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
    }

    public void writeResponseWithDefault2(Sheet sheet, int getRow, int getCell){
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        String chatBotResponse2 = chatBotPage.defaultChatGetResp(chatBotPage.xRespBox);
        sheet.getRow(getRow).createCell(getCell).setCellValue(chatBotResponse2); // WRITE DEFAULT2 ----- the response to the Excel sheet
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
    }

    // Utility Function
    public String getLastElementText(By locator, long timeoutInSeconds) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutInSeconds));
        try {
            if(chatBotPage.getResponseLoading().isDisplayed()){
                WebDriverWait waitLonger = new WebDriverWait(driver, Duration.ofSeconds(180));
                waitLonger.until(ExpectedConditions.invisibilityOf(chatBotPage.getResponseLoading()));
            }
        } catch (NoSuchElementException | TimeoutException e) {
            LOGGER.warning("--- Timeout waiting for loading element to disappear."+ e);
        }
        Thread.sleep(5000);
        String res = chatBotPage.getResponseBoxLocatorAI3().get(chatBotPage.getResponseBoxLocatorAI3().size() - 1).getText();
        try {
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, 0));
            List<WebElement> responses = driver.findElements(locator);
            if (!responses.isEmpty()) {
                res = responses.get(responses.size() - 1).getText();
                return res;
            }else {
                LOGGER.warning("--- No responses found.");
            }
        } catch (TimeoutException e) {
            LOGGER.warning("--- Timeout waiting for element to appear. "+ e);
        }
        return res;
    }



}