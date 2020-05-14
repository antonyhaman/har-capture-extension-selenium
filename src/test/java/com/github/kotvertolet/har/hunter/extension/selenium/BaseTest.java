package com.github.kotvertolet.har.hunter.extension.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BaseTest {

    private static final ChromeOptions chromeOptions;
    static {
        System.setProperty("webdriver.chrome.driver", "F:/CODING/chromedriver.exe");
        chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("start-maximized");
    }

    @RegisterExtension
    static HarCollectorExtension harCollectorExtension = HarCollectorExtension.builder().addCapabilities(chromeOptions).build();
    public ChromeDriver driver;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver(chromeOptions);
    }

    @AfterEach
    public void dispose() {
        driver.quit();
    }
}
