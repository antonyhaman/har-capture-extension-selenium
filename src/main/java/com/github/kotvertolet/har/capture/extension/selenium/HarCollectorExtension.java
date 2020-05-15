package com.github.kotvertolet.har.capture.extension.selenium;

import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.client.ClientUtil;
import com.browserup.bup.proxy.CaptureType;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HarCollectorExtension implements BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterAllCallback {

    private BrowserUpProxyServer proxy;
    private static MutableCapabilities customCapabilities;

    private HarCollectorExtension() {
        // NOP
    }

    public static HarCollectorExtensionBuilder builder() {
        return new HarCollectorExtensionBuilder();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        proxy = new BrowserUpProxyServer();
        proxy.setTrustAllServers(true);
        proxy.start();
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_BINARY_CONTENT, CaptureType.RESPONSE_BINARY_CONTENT);
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        seleniumProxy.setHttpProxy("localhost:" + proxy.getPort());
        seleniumProxy.setSslProxy("localhost:" + proxy.getPort());

        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
        capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        if (customCapabilities != null) {
            customCapabilities.merge(capabilities);
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        proxy.newHar();
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        proxy.getHar().writeTo(outputStream);
        Allure.addAttachment("http-activity.har", new ByteArrayInputStream(outputStream.toByteArray()));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        proxy.stop();
    }

    public static class HarCollectorExtensionBuilder {

        public HarCollectorExtensionBuilder addCapabilities(MutableCapabilities desiredCapabilities) {
            customCapabilities = desiredCapabilities;
            return this;
        }

        public HarCollectorExtension build() {
            return new HarCollectorExtension();
        }
    }
}
