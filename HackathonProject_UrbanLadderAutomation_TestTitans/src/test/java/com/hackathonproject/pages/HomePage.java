package com.hackathonproject.pages;

import com.hackathonproject.keywords.KeywordActions;
import com.hackathonproject.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {

    private final WebDriver driver;
    private final KeywordActions actions;
    private final WebDriverWait wait;

    @FindBy(xpath = "//span[normalize-space()='New Arrivals']")
    private WebElement newArrivalsLink;

    @FindBy(xpath = "//a[@href='/collection/bookshelves?src=top_category_bookshelves']")
    private WebElement bookshelvesLink;

    @FindBy(xpath = "//img[@alt='Bookshelves']")
    private WebElement bookshelvesImage;

    @FindBy(linkText = "Gift Cards")
    private WebElement giftCardsLink;

    @FindBy(xpath = "//img[@alt='logo']")
    private WebElement logoImage;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.actions = new KeywordActions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        PageFactory.initElements(driver, this);
    }

    public void clickNewArrivals() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(newArrivalsLink));
            actions.clickElement(newArrivalsLink);
            System.out.println("Clicked New Arrivals.");
        } catch (Exception e) {
            System.out.println("Could not click New Arrivals. Using URL.");
            driver.get(ConfigReader.getNewArrivalsURL());
        }
    }

    public void clickBookshelves() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(bookshelvesLink));
            actions.scrollToElement(bookshelvesLink);
            actions.jsClickElement(bookshelvesLink);
            System.out.println("Clicked Bookshelves.");
        } catch (Exception e) {
            System.out.println("Could not click Bookshelves. Using URL.");
            driver.get(ConfigReader.getBookshelvesURL());
        }
    }

    public void goToGiftCards() {
        driver.get(ConfigReader.getGiftCardURL());
        System.out.println("Navigated to Gift Cards page.");
    }

    public void goToBookshelvesPage() {
        driver.get(ConfigReader.getBookshelvesURL());
        System.out.println("Navigated to Bookshelves page.");
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isLogoDisplayed() {
        try { return logoImage.isDisplayed(); }
        catch (Exception e) { return false; }
    }
}