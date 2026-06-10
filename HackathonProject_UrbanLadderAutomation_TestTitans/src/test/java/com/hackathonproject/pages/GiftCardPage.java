package com.hackathonproject.pages;

import com.hackathonproject.keywords.KeywordActions;
import com.hackathonproject.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class GiftCardPage {

    private final WebDriver driver;
    private final KeywordActions actions;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    // ===== CARD SELECTION =====
    @FindBy(xpath = "//div[@class='minHeight ']//div[3]//div[1]//div[1]//img[1]")
    private WebElement happyAnniversaryCard;

    @FindBy(xpath = "//img[contains(@alt,'Anniversary') or contains(@alt,'anniversary')]")
    private WebElement anniversaryCardBackup;

    // ===== AMOUNT AND QUANTITY =====
    @FindBy(id = "denomination")
    private WebElement amountInput;

    @FindBy(id = "quantity")
    private WebElement quantityInput;

    // ===== SENDER DETAILS =====
    @FindBy(xpath = "//div[@id='sender-details']//input[@id='firstname']")
    private WebElement senderFirstName;

    @FindBy(xpath = "//div[@id='sender-details']//input[@id='lastname']")
    private WebElement senderLastName;

    @FindBy(css = "#sender-details #email")
    private WebElement senderEmail;

    @FindBy(id = "telephone")
    private WebElement senderPhone;

    // ===== RECEIVER DETAILS =====
    @FindBy(xpath = "//div[@id='receiver-details']//input[@id='firstname']")
    private WebElement receiverFirstName;

    @FindBy(xpath = "//div[@id='receiver-details']//input[@id='lastname']")
    private WebElement receiverLastName;

    @FindBy(css = "#receiver-details #email")
    private WebElement receiverEmail;

    // ===== GIFT MESSAGE =====
    @FindBy(id = "giftMessage")
    private WebElement giftMessageInput;

    // ===== SCROLL TARGETS =====
    @FindBy(xpath = "//div[@class='justify-content-center mt-2 mb-2 row']")
    private WebElement scrollTargetCards;

    @FindBy(xpath = "//body/div[@id='app']/div[contains(@class,'App__MainDiv')]"
            + "/div[contains(@class,'minHeight')]/div/div/div[@class='productContainer row']/div[7]/div[1]")
    private WebElement scrollTargetForm;

    // ===== ERROR MESSAGE LOCATORS =====
    @FindBy(css = ".error-message, .field-error, .validation-error, .invalid-feedback, .error, .text-danger")
    private WebElement errorByCss;

    @FindBy(xpath = "//*[contains(@class,'error') or contains(@class,'invalid') or contains(@class,'alert-danger')]")
    private WebElement errorByXpath;

    @FindBy(className = "error")
    private WebElement errorByClass;

    public GiftCardPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new KeywordActions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void navigateToGiftCards() {
        driver.get(ConfigReader.getGiftCardURL());
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@class='justify-content-center mt-2 mb-2 row']")));
        System.out.println("Navigated to Gift Cards page (woohoo.in).");
    }

    public void selectAnniversaryCard() {
        try {
            try { actions.scrollToElement(scrollTargetCards); }
            catch (Exception e) { actions.scrollDown(600); }
            wait.until(ExpectedConditions.elementToBeClickable(happyAnniversaryCard));
            actions.jsClickElement(happyAnniversaryCard);
            System.out.println("Selected Happy Anniversary card.");
        } catch (Exception e) {
            try {
                actions.jsClickElement(anniversaryCardBackup);
                System.out.println("Selected Anniversary card (alt-text backup).");
            } catch (Exception e2) {
                System.out.println("Specific Anniversary card not found. Clicking 3rd card in grid.");
                try {
                    List<WebElement> cards = driver.findElements(By.xpath("//div[@class='productContainer row']//img"));
                    if (cards.size() >= 3) js.executeScript("arguments[0].click();", cards.get(2));
                } catch (Exception e3) {
                    System.out.println("Could not select any card design.");
                }
            }
        }
    }

    public void enterAmount(String amount) {
        try {
            actions.typeInElement(amountInput, amount);
            System.out.println("Entered amount: " + amount);
        } catch (Exception e) {
            System.out.println("Could not enter amount: " + e.getMessage());
        }
    }

    public void enterQuantity(String qty) {
        try {
            actions.typeInElement(quantityInput, qty);
            System.out.println("Entered quantity: " + qty);
        } catch (Exception e) {
            System.out.println("Could not enter quantity: " + e.getMessage());
        }
    }

    public void fillForm(String sFirstName, String sLastName, String sEmail, String sPhone,
                          String rFirstName, String rLastName, String rEmail, String message) {
        try {
            try { actions.scrollToElement(scrollTargetForm); }
            catch (Exception e) { actions.scrollDown(800); }

            safeType(senderFirstName, sFirstName, "Sender First Name");
            safeType(senderLastName, sLastName, "Sender Last Name");
            safeType(senderEmail, sEmail, "Sender Email");
            safeType(senderPhone, sPhone, "Sender Phone");
            safeType(receiverFirstName, rFirstName, "Receiver First Name");
            safeType(receiverLastName, rLastName, "Receiver Last Name");
            safeType(receiverEmail, rEmail, "Receiver Email");
            safeType(giftMessageInput, message, "Gift Message");

            System.out.println("Form filled — Sender: " + sFirstName + " " + sLastName
                    + " | Receiver: " + rFirstName + " " + rLastName + " (" + rEmail + ")");
        } catch (Exception e) {
            System.out.println("ERROR filling form: " + e.getMessage());
        }
    }

    public void clickSubmit() {
        try {
            WebElement emailField = driver.findElement(By.cssSelector("#receiver-details #email"));
            emailField.sendKeys(Keys.ENTER);
            System.out.println("Pressed Enter on receiver email to submit.");
        } catch (Exception e) {
            System.out.println("Could not press Enter on receiver email.");
        }
        wait.until(ExpectedConditions.urlContains("woohoo"));
    }

    public String captureErrorMessage() {
        String capturedError = "";

        try {
            wait.until(ExpectedConditions.visibilityOf(errorByCss));
            capturedError = errorByCss.getText().trim();
        } catch (Exception e1) {
            try {
                wait.until(ExpectedConditions.visibilityOf(errorByXpath));
                capturedError = errorByXpath.getText().trim();
            } catch (Exception e2) {
                try {
                    wait.until(ExpectedConditions.visibilityOf(errorByClass));
                    capturedError = errorByClass.getText().trim();
                } catch (Exception e3) {
                    try {
                        List<WebElement> errors = driver.findElements(
                            By.xpath("//*[contains(@class,'error') or contains(@class,'invalid')]"));
                        for (WebElement err : errors) {
                            String text = err.getText().trim();
                            if (!text.isEmpty()) { capturedError = text; break; }
                        }
                    } catch (Exception e4) {
                        System.out.println("Could not find error message with any strategy.");
                    }
                }
            }
        }

        if (!capturedError.isEmpty()) {
            System.out.println("ERROR MESSAGE CAPTURED: " + capturedError);
        } else {
            System.out.println("No error message found on page.");
        }
        return capturedError;
    }

    public boolean isStillOnGiftCardPage() {
        String url = driver.getCurrentUrl();
        return url.contains("gift-card") || url.contains("woohoo");
    }

    private void safeType(WebElement element, String text, String fieldName) {
        try {
            actions.typeInElement(element, text);
            System.out.println("  Filled: " + fieldName);
        } catch (Exception e) {
            System.out.println("  WARNING: Could not fill " + fieldName);
        }
    }
}