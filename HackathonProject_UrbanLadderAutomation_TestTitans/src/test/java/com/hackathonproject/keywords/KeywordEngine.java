package com.hackathonproject.keywords;

import com.hackathonproject.utils.ConfigReader;
import com.hackathonproject.utils.ExcelReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class KeywordEngine {

    private final WebDriver driver;
    private final KeywordActions actions;
    private static final String SHEET_NAME = "Keywords";

    public KeywordEngine(WebDriver driver) {
        this.driver = driver;
        this.actions = new KeywordActions(driver);
    }

    public void executeTestCase(String testCaseName) {
        System.out.println("\n[KeywordEngine] Executing keyword steps for: " + testCaseName);

        try {
            Object[][] data = ExcelReader.readExcelData(ConfigReader.getKeywordsPath(), SHEET_NAME);

            if (data == null || data.length == 0) {
                System.out.println("[KeywordEngine] WARNING: No data found in Keywords.xlsx");
                return;
            }

            int stepCount = 0;

            for (Object[] row : data) {
                String tcName      = row[0] != null ? row[0].toString().trim() : "";
                String stepNo      = row[1] != null ? row[1].toString().trim() : "";
                String keyword     = row[2] != null ? row[2].toString().trim() : "";
                String locator     = row[3] != null ? row[3].toString().trim() : "";
                String testData    = row[4] != null ? row[4].toString().trim() : "";
                String description = row[5] != null ? row[5].toString().trim() : "";

                if (!tcName.equalsIgnoreCase(testCaseName)) continue;

                stepCount++;
                System.out.println("[KeywordEngine] Step " + stepNo + ": " + keyword.toUpperCase()
                        + " | " + description);

                executeKeyword(keyword, locator, testData);
            }

            System.out.println("[KeywordEngine] Completed " + stepCount + " steps for: " + testCaseName + "\n");

        } catch (Exception e) {
            System.out.println("[KeywordEngine] ERROR executing test case: " + testCaseName);
            e.printStackTrace();
        }
    }

    private void executeKeyword(String keyword, String locator, String data) {
        try {
            switch (keyword.toLowerCase()) {
                case "navigate":
                    actions.navigateTo(data);
                    Thread.sleep(2000);
                    break;
                case "click":
                    actions.click(resolveLocator(locator));
                    Thread.sleep(1000);
                    break;
                case "jsclick":
                    actions.jsClick(resolveLocator(locator));
                    Thread.sleep(1000);
                    break;
                case "type":
                    actions.type(resolveLocator(locator), data);
                    break;
                case "gettext":
                    String text = actions.getText(resolveLocator(locator));
                    System.out.println("[KeywordEngine] Captured text: " + text);
                    break;
                case "scrolldown":
                    int pixels = Integer.parseInt(data);
                    actions.scrollDown(pixels);
                    Thread.sleep(500);
                    break;
                default:
                    System.out.println("[KeywordEngine] WARNING: Unknown keyword: " + keyword);
                    break;
            }
        } catch (Exception e) {
            System.out.println("[KeywordEngine] Step FAILED for keyword '" + keyword
                    + "' with locator '" + locator + "': " + e.getMessage());
        }
    }

    private By resolveLocator(String locator) {
        if (locator == null || locator.isEmpty()) {
            throw new IllegalArgumentException("Locator string is empty!");
        }
        if (locator.startsWith("//") || locator.startsWith("(")) {
            return By.xpath(locator);
        }
        if (locator.startsWith("#") || locator.startsWith(".")
                || locator.contains("[") || locator.contains(">")
                || locator.contains(" ")) {
            return By.cssSelector(locator);
        }
        if (locator.matches("^[a-zA-Z][a-zA-Z0-9_-]*$")) {
            return By.id(locator);
        }
        return By.cssSelector(locator);
    }
}