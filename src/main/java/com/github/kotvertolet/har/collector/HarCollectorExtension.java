package com.github.kotvertolet.har.collector;

import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.client.ClientUtil;
import com.browserup.bup.proxy.CaptureType;
import com.codeborne.selenide.Configuration;
import io.qameta.allure.Attachment;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class HarCollectorExtension implements BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterAllCallback {

    private BrowserUpProxyServer proxy;
    private String caseName;
    private File outputFile;
    private ByteArrayOutputStream stream;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        proxy = new BrowserUpProxyServer();
        proxy.start(0);
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_BINARY_CONTENT, CaptureType.RESPONSE_BINARY_CONTENT);
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
        Configuration.browserCapabilities = capabilities;
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        AtomicReference<String> testCase = new AtomicReference<>("[NO TEST CASE]");
        extensionContext.getElement().ifPresent(e -> {
            if (e.getAnnotation(TmsLink.class) != null)
                testCase.set(String.format("[%s]", e.getAnnotation(TmsLink.class).value()));
        });
        caseName = String.format("%s - %s", testCase.get(), extensionContext.getDisplayName());
        proxy.newHar(caseName);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws IOException {
        String status = extensionContext.getExecutionException().isPresent() ? "[FAILED]" : "[PASSED]";
        outputFile = new File(status + " - " + caseName + ".har");
        outputFile.createNewFile();
        proxy.getHar().writeTo(outputFile);

        //proxy.getHar().writeTo(outputFile);
    }

    @Attachment(value = "HAR archive", type = "har")
    private byte[] createAttachment(Path path) {
        byte[] result;
        if (path == null) {
            result = "Cannot attach file".getBytes();
        } else {
            try {
                result = Files.readAllBytes(path);
            } catch (IOException e) {
                result = e.getMessage().getBytes();
            }
        }
        return result;
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        proxy.stop();
    }
}
