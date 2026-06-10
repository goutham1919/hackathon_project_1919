package com.hackathonproject.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Reads values from config.properties at project root.
// Used by test classes and utilities that need paths and timeouts.
public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
    	try (InputStream file = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties"))  {
            properties.load(file);
        } catch (IOException e) {
            System.out.println("ERROR: Could not read config.properties — " + e.getMessage());
        }
    }

    // ===== General =====
    public static String getBaseURL()        { return properties.getProperty("baseURL"); }
    public static String getBrowser()        { return properties.getProperty("browser"); }
    public static String getScreenshotPath() { return properties.getProperty("screenshotPath"); }
    public static String getReportPath()     { return properties.getProperty("reportPath"); }

    // ===== Test Data Files =====
    public static String getTestDataPath()   { return properties.getProperty("testDataPath"); }
    public static String getKeywordsPath()   { return properties.getProperty("keywordsPath"); }

    // ===== Page URLs =====
    public static String getBookshelvesURL()     { return properties.getProperty("bookshelvesURL"); }
    public static String getNewArrivalsURL()     { return properties.getProperty("newArrivalsURL"); }
    public static String getOasisCollectionURL() { return properties.getProperty("oasisCollectionURL"); }
    public static String getLivingRoomURL()      { return properties.getProperty("livingRoomURL"); }
    public static String getGiftCardURL()        { return properties.getProperty("giftCardURL"); }

    // ===== Driver Paths =====
    public static String getChromeDriverPath() { return properties.getProperty("chromeDriverPath"); }
    public static String getEdgeDriverPath()   { return properties.getProperty("edgeDriverPath"); }
    public static String getFirefoxDriverPath() { return properties.getProperty("firefoxDriverPath"); }

    // ===== Waits =====
    public static int getExplicitWait() {
        return parseIntProperty("explicitWait", 15);
    }

    public static int getImplicitWait() {
        return parseIntProperty("implicitWait", 10);
    }

    // Safely parses an int property — returns the default value if missing or malformed
    private static int parseIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            System.out.println("WARNING: Could not parse '" + key + "'. Using default: " + defaultValue);
            return defaultValue;
        }
    }
}