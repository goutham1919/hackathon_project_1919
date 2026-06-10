package com.hackathonproject.tests;

import com.aventstack.extentreports.Status;
import com.hackathonproject.base.BaseTest;
import com.hackathonproject.keywords.KeywordEngine;
import com.hackathonproject.pages.CollectionsPage;
import com.hackathonproject.utils.ExtentReportManager;
import com.hackathonproject.utils.ScreenshotUtil;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;

@Epic("Urban Ladder Automation")
@Feature("Flow 2 - Collections Navigation")
public class CollectionsTest extends BaseTest {

    @Test(description = "Navigate New Arrivals > Oasis > Living Room > Solid Wood filter")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigate collections and extract product list into ArrayList")
    public void testCollectionsNavigation() {

        SoftAssert softAssert = new SoftAssert();
        log.info("========== FLOW 2: COLLECTIONS NAVIGATION TEST ==========");

        // Keyword-Driven Execution — reads steps from Keywords.xlsx
        KeywordEngine engine = new KeywordEngine(getDriver());
        engine.executeTestCase("CollectionsTest");

        CollectionsPage collectionsPage = new CollectionsPage(getDriver());

        // Step 1: Click New Arrivals
        // (Already done by KeywordEngine — commented to avoid double navigation)
//        log.info("Step 1: Clicking New Arrivals...");
//        collectionsPage.clickNewArrivals();
//        ExtentReportManager.getTest().log(Status.INFO, "Clicked New Arrivals.");

//        // Step 2: Click Oasis Collection
//        // (Already done by KeywordEngine — commented to avoid double navigation)
//        log.info("Step 2: Clicking Oasis Collection...");
//        collectionsPage.clickOasisCollection();
//        ExtentReportManager.getTest().log(Status.INFO, "Clicked Oasis Collection.");

        // Step 3: Click Living Room
        // (Already done by KeywordEngine — commented to avoid double navigation)
//        log.info("Step 3: Clicking Living Room...");
//        collectionsPage.clickLivingRoom();
//        ExtentReportManager.getTest().log(Status.INFO, "Clicked Living Room.");

        // Step 4: Verify breadcrumb confirms we are on the right page
        String breadcrumb = collectionsPage.getBreadcrumbText();
        log.info("Breadcrumb: " + breadcrumb);
        ExtentReportManager.getTest().log(Status.INFO, "Breadcrumb: " + breadcrumb);
        softAssert.assertFalse(breadcrumb.isEmpty(), "Breadcrumb is empty — page may not have loaded correctly.");

        // Step 5: Apply Solid Wood filter
        // (Already done by KeywordEngine — commented to avoid double filter click)
//        log.info("Step 4: Applying Solid Wood filter...");
//        collectionsPage.applySolidWoodFilter();
//        ExtentReportManager.getTest().log(Status.INFO, "Applied Solid Wood filter.");

        // Step 6: Capture all product names into ArrayList
        log.info("Step 5: Capturing product names...");
        ArrayList<String> productNames = collectionsPage.getAllProductNames();

        // Print results to console
        System.out.println("\n============================================");
        System.out.println("  COLLECTIONS - SOLID WOOD PRODUCTS");
        System.out.println("  Path: New Arrivals > Oasis > Living Room");
        System.out.println("  Filter: Primary Material = Solid Wood");
        System.out.println("============================================");
        for (int i = 0; i < productNames.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + productNames.get(i));
            log.info("Product " + (i + 1) + ": " + productNames.get(i));
        }
        System.out.println("  Total products: " + productNames.size());
        System.out.println("============================================\n");

        ExtentReportManager.getTest().log(Status.INFO, "Total products captured: " + productNames.size());

        // Step 7: Assertions — all soft so every check is reported individually
        String title = collectionsPage.getPageTitle();
        softAssert.assertFalse(title.isEmpty(),
                "Page title is empty.");
        softAssert.assertFalse(productNames.isEmpty(),
                "Product list is empty — filter may not have worked.");
        softAssert.assertTrue(productNames.size() > 0,
                "Product count is not greater than 0.");

        for (int i = 0; i < productNames.size(); i++) {
            softAssert.assertFalse(productNames.get(i).trim().isEmpty(),
                    "Product " + (i + 1) + " has an empty name.");
        }

        // Step 8: Take screenshot at end of test
        ScreenshotUtil.takeScreenshot(getDriver(), "testCollectionsNavigation_END");

        ExtentReportManager.getTest().log(Status.PASS, "Flow 2 assertions completed.");
        log.info("========== FLOW 2 COMPLETED ==========");

        softAssert.assertAll();
    }
}