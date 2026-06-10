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

public class BookshelvesPage {

    private final WebDriver driver;
    private final KeywordActions actions;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    @FindBy(xpath = "//h1[contains(text(),'Bookshelves')]")
    private WebElement pageHeading;

    public BookshelvesPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new KeywordActions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void navigateToBookshelvesPage() {
        driver.get(ConfigReader.getBookshelvesURL());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")));
        System.out.println("Navigated to Bookshelves page.");
    }

    public void applyStorageTypeOpen() {
        try {
            safeSleep(1000);

            // Click "Storage Type" filter using aria-label
            WebElement storageBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@aria-label='Storage Type filter']")));
            js.executeScript("arguments[0].click();", storageBtn);
            System.out.println("Clicked Storage Type filter.");
            safeSleep(1500);

            // Click "Open Storage" inside dropdown-menu-storage-type
            WebElement openOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='dropdown-menu-storage-type']")));
            //js.executeScript("arguments[0].click();", openOption);
            Select select=new Select(openOption);
            select.selectByValue("Open Storage");
            System.out.println("Selected Open Storage.");
            safeSleep(3000);

            System.out.println("Applied Storage Type = Open Storage filter.");

        } catch (Exception e) {
            System.out.println("Could not apply Storage Type filter: " + e.getMessage());
        }
    }

    public List<String[]> getTopProducts(int count) {
        List<String[]> products = new ArrayList<>();

        try {
            safeSleep(2000);
            js.executeScript("window.scrollTo(0, 0)");
            safeSleep(500);

            // Product names from h3 tags
            List<WebElement> allH3 = driver.findElements(By.tagName("h3"));
            List<String> productNames = new ArrayList<>();
            for (WebElement h3 : allH3) {
                try {
                    String text = h3.getText().trim();
                    if (!text.isEmpty() && text.length() > 5
                            && !text.equalsIgnoreCase("Bookshelves")) {
                        productNames.add(text);
                    }
                } catch (StaleElementReferenceException ignored) {}
            }
            System.out.println("Found " + productNames.size() + " product names.");

            // Deal Prices — find "Deal Price" text elements
            List<String> dealPrices = new ArrayList<>();

            List<WebElement> dealElements = driver.findElements(
                By.xpath("//*[contains(text(),'Deal Price')]"));

            if (!dealElements.isEmpty()) {
                System.out.println("Found " + dealElements.size() + " 'Deal Price' elements.");
                for (WebElement el : dealElements) {
                    try {
                        String fullText = el.getText().trim();
                        String price = extractFirstRupeePrice(fullText);
                        if (!price.isEmpty()) dealPrices.add(price);
                    } catch (StaleElementReferenceException ignored) {}
                }
            }

            // Fallback: pair all ₹ prices, take smaller of each pair
            if (dealPrices.isEmpty()) {
                System.out.println("No 'Deal Price' labels. Using price pairing.");
                List<WebElement> allPriceEls = driver.findElements(
                    By.xpath("//*[starts-with(normalize-space(),'₹') and string-length(normalize-space()) >= 4 and string-length(normalize-space()) <= 10]"));

                List<String> allPrices = new ArrayList<>();
                for (WebElement el : allPriceEls) {
                    try {
                        String text = el.getText().trim();
                        if (text.startsWith("₹")) allPrices.add(text);
                    } catch (StaleElementReferenceException ignored) {}
                }

                for (int i = 0; i + 1 < allPrices.size(); i += 2) {
                    int p1 = extractPrice(allPrices.get(i));
                    int p2 = extractPrice(allPrices.get(i + 1));
                    dealPrices.add(p1 <= p2 ? allPrices.get(i) : allPrices.get(i + 1));
                }
            }

            System.out.println("Found " + dealPrices.size() + " deal prices.");

            int limit = Math.min(count, Math.min(productNames.size(), dealPrices.size()));
            for (int i = 0; i < limit; i++) {
                products.add(new String[]{productNames.get(i), dealPrices.get(i)});
            }

            // BR_002: Duplicate price handling
            if (limit > 0 && dealPrices.size() > limit && productNames.size() > limit) {
                String lastPrice = dealPrices.get(limit - 1);
                if (dealPrices.get(limit).equals(lastPrice)) {
                    products.add(new String[]{productNames.get(limit), lastPrice});
                    System.out.println("Duplicate price! Including: " + productNames.get(limit));
                }
            }

            System.out.println("Total products captured: " + products.size());

        } catch (Exception e) {
            System.out.println("ERROR getting products: " + e.getMessage());
        }
        return products;
    }

    private String extractFirstRupeePrice(String text) {
        if (text == null || !text.contains("₹")) return "";
        int start = text.indexOf("₹");
        int end = start + 1;
        while (end < text.length() && (Character.isDigit(text.charAt(end)) || text.charAt(end) == ',')) {
            end++;
        }
        String price = text.substring(start, end);
        return price.length() >= 4 ? price : "";
    }

    public int extractPrice(String priceText) {
        String digitsOnly = priceText.replaceAll("[^0-9]", "");
        return digitsOnly.isEmpty() ? 0 : Integer.parseInt(digitsOnly);
    }

    private void safeSleep(int millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
    }
}