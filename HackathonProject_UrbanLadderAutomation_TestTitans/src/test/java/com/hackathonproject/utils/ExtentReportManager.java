package com.hackathonproject.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

// Manages the single ExtentReports instance for the whole test run.
// TestListener calls createTest() and getTest() to log results per test.
public class ExtentReportManager {

    private static final String REPORT_PATH = "reports/ExtentReport.html";

    private static ExtentReports        extent;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    // Creates the report instance once — reuses it for all subsequent calls
    public static ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);
            sparkReporter.config().setDocumentTitle("Urban Ladder Automation Report");
            sparkReporter.config().setReportName("Test Execution Report");
            sparkReporter.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Project",     "Urban Ladder Automation");
            extent.setSystemInfo("Tester",      "QA Automation Team");
            extent.setSystemInfo("Environment", "Production");
        }
        return extent;
    }

    // Creates a new test entry in the report and stores it for the current thread
    public static ExtentTest createTest(String testName) {
        ExtentTest test = getInstance().createTest(testName);
        extentTest.set(test);
        return test;
    }

    // Returns the ExtentTest for the current thread so test classes can log to it
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void flushReport() {
        if (extent != null) extent.flush();
    }
}