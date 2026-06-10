package com.hackathonproject.base;

import com.hackathonproject.utils.ConfigReader;
import com.hackathonproject.utils.ScreenshotUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.time.Duration;

public class BaseTest {

    private static final ThreadLocal<WebDriver> threadDriver = new ThreadLocal<>();

    protected WebDriverWait wait;
    protected Logger log = LogManager.getLogger(this.getClass());

    public static WebDriver getDriver() {
        return threadDriver.get();
    }

    @BeforeTest
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {

        log.info("===== Opening browser: " + browser + " =====");
        WebDriver driver = null;

        try {
            switch (browser.toLowerCase()) {
                case "edge":
                    try {
                        String edgePath = ConfigReader.getEdgeDriverPath();
                        if (edgePath != null && new java.io.File(edgePath).exists()) {
                            System.setProperty("webdriver.edge.driver", edgePath);
                        } else {
                            throw new RuntimeException("Local driver not found");
                        }
                    } catch (Exception e) {
                        WebDriverManager.edgedriver().setup();
                    }
                    EdgeOptions edgeOptions = new EdgeOptions();
//                    edgeOptions.addArguments("--headless=new");
//                    edgeOptions.addArguments("--window-size=1920,1080");
                    edgeOptions.addArguments("--disable-notifications");
                    edgeOptions.addArguments("--disable-popup-blocking");
                    driver = new EdgeDriver(edgeOptions);
                    driver.manage().window().maximize();
                    break;
            case "firefox":
                try {
                    String firefoxPath = ConfigReader.getFirefoxDriverPath();
                    if (firefoxPath != null && new java.io.File(firefoxPath).exists()) {
                        System.setProperty("webdriver.gecko.driver", firefoxPath);
                    } else {
                        throw new RuntimeException("Local driver not found");
                    }
                } catch (Exception e) {
                    WebDriverManager.firefoxdriver().setup();
                }
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--width=1280");
                firefoxOptions.addArguments("--height=800");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            default:
                driver = createChromeDriver();
            }
        } catch (Exception e) {
            log.warn("Could not start " + browser + ". Falling back to Chrome. Reason: " + e.getMessage());
            driver = createChromeDriver();
        }

        threadDriver.set(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));

        driver.get(ConfigReader.getBaseURL());
        log.info("Navigated to: " + ConfigReader.getBaseURL());
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest(ITestResult result) {
        WebDriver driver = getDriver();
        if (driver == null) return;

        String status = result.isSuccess() ? "PASS" : "FAIL";
        String screenshotName = result.getMethod().getMethodName() + "_" + status;
        String path = ScreenshotUtil.takeScreenshot(driver, screenshotName);
        log.info("Screenshot saved: " + path);

        try {
            driver.get(ConfigReader.getBaseURL());
            log.info("Navigated back to homepage.");
        } catch (Exception e) {
            log.warn("Could not navigate to homepage: " + e.getMessage());
        }
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = getDriver();
        if (driver != null) {
            log.info("===== Closing browser =====");
            driver.quit();
            threadDriver.remove();
        }
    }

    private WebDriver createChromeDriver() {
        try {
            String driverPath = ConfigReader.getChromeDriverPath();
            if (driverPath != null && new java.io.File(driverPath).exists()) {
                System.setProperty("webdriver.chrome.driver", driverPath);
            } else {
                throw new RuntimeException("Local driver not found");
            }
        } catch (Exception e) {
            WebDriverManager.chromedriver().setup();
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1280,800");
        options.addArguments("--window-position=0,0");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        return new ChromeDriver(options);
    }
}