package com.hackathonproject.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

// Saves a screenshot to the screenshots/ folder with a timestamped filename.
public class ScreenshotUtil {

    private static final String SCREENSHOT_FOLDER = "screenshots";

    public static String takeScreenshot(WebDriver driver, String testName) {
        String filePath = "";

        try {
            File folder = new File(SCREENSHOT_FOLDER);
            if (!folder.exists()) folder.mkdirs();

            // _SSS (milliseconds) prevents FileAlreadyExistsException when
            // TestListener and BaseTest @AfterMethod both screenshot in the same second
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            filePath = SCREENSHOT_FOLDER + "/" + testName + "_" + timestamp + ".png";

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // REPLACE_EXISTING ensures no crash if two screenshots land on the same filename
            Files.copy(source.toPath(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Screenshot saved: " + filePath);

        } catch (IOException e) {
            System.out.println("ERROR: Could not save screenshot — " + e.getMessage());
        }

        return filePath;
    }
}