package com.github.kotvertolet.har.collector;

import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.client.ClientUtil;
import com.browserup.bup.proxy.CaptureType;
import com.codeborne.selenide.Configuration;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class HarCollectorExtension implements BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterAllCallback {

    private BrowserUpProxyServer proxy;
    private ByteArrayOutputStream stream;
    private static DesiredCapabilities customCapabilities;

    private HarCollectorExtension() {}

    public static HarCollectorExtensionBuilder builder() {
        return new HarCollectorExtensionBuilder();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        proxy = new BrowserUpProxyServer();
        proxy.start();
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_BINARY_CONTENT, CaptureType.RESPONSE_BINARY_CONTENT);
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        seleniumProxy.setHttpProxy("localhost:" + proxy.getPort());
        seleniumProxy.setSslProxy("localhost:" + proxy.getPort());

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
        //TODO: Check if this necessary
        if (customCapabilities != null) {
            capabilities.asMap().putAll(customCapabilities.asMap());
        }
        Configuration.browserCapabilities = capabilities;
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        stream = new ByteArrayOutputStream();
        proxy.newHar();
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws IOException {
        proxy.getHar().writeTo(stream);
        Allure.addAttachment("har", new ByteArrayInputStream(stream.toByteArray()));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        proxy.stop();
    }

    private static class HarCollectorExtensionBuilder {

        public HarCollectorExtensionBuilder addDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
            customCapabilities = desiredCapabilities;
            return this;
        }

        public HarCollectorExtension build() {
            return new HarCollectorExtension();
        }
    }
}
