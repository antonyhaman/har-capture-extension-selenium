package com.github.kotvertolet.har.hunter.extension.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestClass extends BaseTest {

    @Test
    public void test() {
        driver.get("https://www.google.com/");
        Assertions.assertTrue(driver.findElementByName("q").isDisplayed());
    }
}
