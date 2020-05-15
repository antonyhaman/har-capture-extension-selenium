package com.github.kotvertolet.har.capture.extension.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HarCaptureExtensionTest extends BaseTest {

    @Test
    public void checkHarCapturing() {
        driver.get("https://www.google.com/");
        Assertions.assertTrue(driver.findElementByName("q").isDisplayed());
    }
}
