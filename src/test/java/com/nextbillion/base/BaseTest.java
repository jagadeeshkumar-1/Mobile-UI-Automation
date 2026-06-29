package com.nextbillion.base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Base test class that manages the Appium driver lifecycle.
 * <p>
 * Each test method gets a fresh driver + app session to ensure isolation.
 * Configuration is read from system properties (overridable via Maven -D flags)
 * with sensible defaults for a local Android emulator.
 */
public abstract class BaseTest {

    protected AndroidDriver driver;
    protected WebDriverWait wait;

    /* ── Configuration defaults ───────────────────────────── */

    private static final String DEFAULT_APPIUM_URL   = "http://127.0.0.1:4723";
    private static final String DEFAULT_PLATFORM     = "Android";
    private static final String DEFAULT_DEVICE       = "emulator-5554";
    private static final String DEFAULT_APK_PATH     = "src/test/resources/apps/mda-2.2.0-25.apk";
    private static final String DEFAULT_APP_PACKAGE  = "com.saucelabs.mydemoapp.android";
    private static final String DEFAULT_APP_ACTIVITY = "com.saucelabs.mydemoapp.android.view.activities.SplashActivity";

    /* ── Lifecycle ────────────────────────────────────────── */

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName(resolve("platform.name", DEFAULT_PLATFORM));
        options.setDeviceName(resolve("device.name", DEFAULT_DEVICE));

        String apkPath = resolve("apk.path", DEFAULT_APK_PATH);
        if (!apkPath.isEmpty()) {
            options.setApp(System.getProperty("user.dir") + "/" + apkPath);
        }

        options.setAppPackage(resolve("app.package", DEFAULT_APP_PACKAGE));
        options.setAppActivity(resolve("app.activity", DEFAULT_APP_ACTIVITY));
        options.setAutomationName("UiAutomator2");
        options.setNoReset(false);      // clean state per test
        options.setFullReset(false);     // but don't uninstall/reinstall every time
        options.setNewCommandTimeout(Duration.ofSeconds(60));

        String appiumUrl = resolve("appium.url", DEFAULT_APPIUM_URL);
        driver = new AndroidDriver(new URL(appiumUrl), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(45));

        // Wait for splash screen to finish and Products page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//android.widget.TextView[@text='Products']")));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /* ── Helpers ──────────────────────────────────────────── */

    /**
     * Resolves a config value from system property, then env var, then default.
     */
    private String resolve(String key, String defaultValue) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) return sysProp;

        String envVar = System.getenv(key.replace('.', '_').toUpperCase());
        if (envVar != null && !envVar.isEmpty()) return envVar;

        return defaultValue;
    }
}
