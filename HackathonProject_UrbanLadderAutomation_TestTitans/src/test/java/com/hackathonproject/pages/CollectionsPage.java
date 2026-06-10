package com.hackathonproject.pages;

import com.hackathonproject.keywords.KeywordActions;
import com.hackathonproject.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CollectionsPage {

    private final WebDriver driver;
    private final KeywordActions actions;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    @FindBy(xpath = "//span[normalize-space()='New Arrivals']")
    private WebElement newArrivalsLink;

    @FindBy(xpath = "//a[@href='/collection/oasis-living-room-collection']")
    private WebElement livingRoomLink;

    @FindBy(xpath = "//a[contains(text(),'Living Room')]")
    private WebElement livingRoomTextLink;

    @FindBy(xpath = "//a[contains(text(),'Home')]/parent::*")
    private WebElement breadcrumbContainer;

    @FindBy(css = "h1")
    private WebElement pageHeading;

    public CollectionsPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new KeywordActions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void clickNewArrivals() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(newArrivalsLink));
            actions.clickElement(newArrivalsLink);
            safeSleep(2000);
            System.out.println("Clicked New Arrivals.");
        } catch (Exception e) {
            System.out.println("Could not click New Arrivals. Using URL.");
            driver.get(ConfigReader.getNewArrivalsURL());
            safeSleep(2000);
        }
    }

    public void clickOasisCollection() {
        try {
            actions.scrollDown(400);
            safeSleep(1000);
            WebElement oasisLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'oasis')] | //a[contains(text(),'Oasis')]")));
            js.executeScript("arguments[0].click();", oasisLink);
            safeSleep(2000);
            System.out.println("Clicked Oasis Collection.");
        } catch (Exception e) {
            System.out.println("Could not click Oasis. Using URL.");
            driver.get(ConfigReader.getOasisCollectionURL());
            safeSleep(2000);
        }
    }

    public void clickLivingRoom() {
        try {
            actions.scrollDown(200);
            safeSleep(1000);
            wait.until(ExpectedConditions.elementToBeClickable(livingRoomLink));
            js.executeScript("arguments[0].click();", livingRoomLink);
            safeSleep(2000);
            System.out.println("Clicked Living Room.");
        } catch (Exception e) {
            try {
                js.executeScript("arguments[0].click();", livingRoomTextLink);
                safeSleep(2000);
                System.out.println("Clicked Living Room (text locator).");
            } catch (Exception e2) {
                System.out.println("Could not click Living Room. Using URL.");
                driver.get(ConfigReader.getLivingRoomURL());
                safeSleep(2000);
            }
        }
    }

    public void applySolidWoodFilter() {
        try {
            safeSleep(1000);

            // Click "Primary Material" filter using aria-label
            WebElement materialBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@aria-label='Primary Material filter']")));
            js.executeScript("arguments[0].click();", materialBtn);
            System.out.println("Clicked Primary Material filter.");
            safeSleep(1500);

            // Click "Solid Wood" inside dropdown-menu-primary-material
            WebElement solidWoodOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='dropdown-menu-primary-material']")));
            //js.executeScript("arguments[0].click();", solidWoodOption);
            Select select=new Select(solidWoodOption);
            select.selectByValue("Solid Wood");
            System.out.println("Selected Solid Wood.");
            safeSleep(3000);

            System.out.println("Applied Primary Material = Solid Wood filter.");

        } catch (Exception e) {
            System.out.println("Could not apply Solid Wood filter: " + e.getMessage());
        }
    }

    public ArrayList<String> getAllProductNames() {
        ArrayList<String> productList = new ArrayList<>();
        try {
            js.executeScript("window.scrollTo(0, 0)");
            safeSleep(1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3")));

            List<WebElement> nameElements = driver.findElements(By.cssSelector("h3"));
            System.out.println("Found " + nameElements.size() + " h3 elements on page.");

            for (WebElement el : nameElements) {
                try {
                    String name = el.getText().trim();
                    if (!name.isEmpty() && name.length() > 5
                            && !name.contains("Oasis")
                            && !name.contains("Collection")
                            && !name.equals("Primary Material")
                            && !name.equals("Sort By")) {
                        productList.add(name);
                    }
                } catch (StaleElementReferenceException ignored) {}
            }
        } catch (Exception e) {
            System.out.println("ERROR extracting product names: " + e.getMessage());
        }
        return productList;
    }

    public String getBreadcrumbText() {
        try {
            return actions.getTextFromElement(breadcrumbContainer);
        } catch (Exception e) {
            try {
                List<WebElement> crumbs = driver.findElements(By.xpath("//a[contains(@href,'/')]"));
                StringBuilder sb = new StringBuilder();
                for (WebElement c : crumbs) {
                    String t = c.getText().trim();
                    if (t.equals("Home") || t.equals("New Arrivals")
                            || t.contains("Oasis") || t.contains("Living")) {
                        if (sb.length() > 0) sb.append(" / ");
                        sb.append(t);
                    }
                }
                return sb.toString();
            } catch (Exception e2) {
                return "";
            }
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    private void safeSleep(int millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
    }
}