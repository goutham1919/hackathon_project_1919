package com.hackathonproject.keywords;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// Keyword-driven part of the hybrid framework.
// Each method = one reusable action (click, type, scroll, hover, etc.)
// Two versions per action: By locator version and WebElement (@FindBy) version.
public class KeywordActions {

    private static final int WAIT_SECONDS = 15;

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    public KeywordActions(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        this.js     = (JavascriptExecutor) driver;
    }

    // ===== CLICK =====

    public void click(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            System.out.println("ERROR clicking locator: " + locator);
            throw e;
        }
    }

    public void clickElement(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            System.out.println("ERROR clicking element: " + element);
            throw e;
        }
    }

    // ===== JS CLICK (use when normal click is blocked by overlays) =====

    public void jsClick(By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            js.executeScript("arguments[0].click();", el);
        } catch (Exception e) {
            System.out.println("ERROR JS clicking locator: " + locator);
            throw e;
        }
    }

    public void jsClickElement(WebElement element) {
        try {
            js.executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            System.out.println("ERROR JS clicking element: " + element);
            throw e;
        }
    }

    // ===== TYPE =====

    public void type(By locator, String text) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            el.clear();
            el.sendKeys(text);
        } catch (Exception e) {
            System.out.println("ERROR typing in locator: " + locator);
            throw e;
        }
    }

    public void typeInElement(WebElement element, String text) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            System.out.println("ERROR typing in element: " + element);
            throw e;
        }
    }

    // ===== GET TEXT =====

    public String getText(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
        } catch (Exception e) {
            System.out.println("ERROR getting text from locator: " + locator);
            throw e;
        }
    }

    public String getTextFromElement(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            return element.getText().trim();
        } catch (Exception e) {
            System.out.println("ERROR getting text from element: " + element);
            throw e;
        }
    }

    // ===== WAIT =====

    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForElementVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ===== SCROLL =====

    // Scrolls the page until the element is visible on screen before interacting
    public void scrollToElement(WebElement element) {
        try {
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            // Short pause to let the page settle after scroll
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            System.out.println("ERROR scrolling to element: " + element);
        }
    }

    public void scrollDown(int pixels) {
        js.executeScript("window.scrollBy(0," + pixels + ")");
    }

    public void scrollToTop() {
        js.executeScript("window.scrollTo(0, 0)");
    }

    // ===== HOVER =====

    public void hoverOver(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            new Actions(driver).moveToElement(element).perform();
        } catch (Exception e) {
            System.out.println("ERROR hovering over element: " + element);
            throw e;
        }
    }

    // ===== IS DISPLAYED =====

    public boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ===== NAVIGATE =====

    public void navigateTo(String url) {
        driver.get(url);
    }
}