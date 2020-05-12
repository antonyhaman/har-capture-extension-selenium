package com.github.kotvertolet.har.collector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

@ExtendWith(HarCollectorExtension.class)
public class SomeTest {

    @Test
    public void test() {
        open("https://www.google.com/");
        $x("//input[@name='q']").val("selenide").submit();
        Assertions.assertTrue(true);
    }
}
