package com.github.kotvertolet.har.collector;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.PageLoadStrategy.*;

@ExtendWith(HarCollectorExtension.class)
public class SomeTest {

    public SomeTest() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(false));
    }

    @Test
    public void test() {
        Configuration.pageLoadStrategy = "none";
        open("https://www.google.com/");
        $x("//input[@name='q']").val("selenide").submit();
        Assertions.assertTrue(true);
    }
}
