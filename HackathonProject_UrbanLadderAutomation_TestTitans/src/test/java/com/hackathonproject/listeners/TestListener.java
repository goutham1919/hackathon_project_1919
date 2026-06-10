package com.hackathonproject.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.hackathonproject.base.BaseTest;
import com.hackathonproject.utils.ExtentReportManager;
import com.hackathonproject.utils.ScreenshotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

// Listens to TestNG events and logs results + screenshots to ExtentReports
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("======= SUITE STARTED: " + context.getName() + " =======");
        ExtentReportManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.info(">>> TEST STARTED: " + testName);
        ExtentReportManager.createTest(testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.info("PASSED: " + testName);
        logScreenshotToReport(testName + "_PASS", testName + " - PASSED", Status.PASS, "Test Passed: " + testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.error("FAILED: " + testName);
        log.error("Reason: " + result.getThrowable().getMessage());
        logScreenshotToReport(testName + "_FAIL", testName + " - FAILED", Status.FAIL, "Test Failed: " + testName + " | Error: " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.warn("SKIPPED: " + testName);
        logScreenshotToReport(testName + "_SKIP", testName + " - SKIPPED", Status.SKIP, "Test Skipped: " + testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("======= SUITE FINISHED: " + context.getName() + " =======");
        log.info("Passed : " + context.getPassedTests().size());
        log.info("Failed : " + context.getFailedTests().size());
        log.info("Skipped: " + context.getSkippedTests().size());
        ExtentReportManager.flushReport();
        log.info("Extent Report saved at: reports/ExtentReport.html");
    }

    // Takes a screenshot and attaches it to the Extent Report for any test event
    private void logScreenshotToReport(String screenshotName, String screenshotTitle, Status status, String message) {
        WebDriver driver = BaseTest.getDriver();
        if (driver == null) return;

        String path = ScreenshotUtil.takeScreenshot(driver, screenshotName);
        log.info("Screenshot saved: " + path);

        ExtentTest test = ExtentReportManager.getTest();
        if (test == null) return;

        test.log(status, message);
        try {
            test.addScreenCaptureFromPath(path, screenshotTitle);
        } catch (Exception e) {
            log.warn("Could not attach screenshot to Extent Report: " + e.getMessage());
        }
    }
}