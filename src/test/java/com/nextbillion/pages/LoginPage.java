package com.nextbillion.pages;

import com.nextbillion.base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for the Login screen of Sauce Labs My Demo App.
 * <p>
 * Accessed via: hamburger menu → Log In, or automatically when an
 * auth-required action is triggered (e.g. checkout).
 */
public class LoginPage extends BasePage {

    /* ── Locators ────────────────────────────────────────── */

    private static final By USERNAME_FIELD   = By.id("com.saucelabs.mydemoapp.android:id/nameET");
    private static final By PASSWORD_FIELD   = By.id("com.saucelabs.mydemoapp.android:id/passwordET");
    private static final By LOGIN_BUTTON     = By.id("com.saucelabs.mydemoapp.android:id/loginBtn");
    private static final By ERROR_MESSAGE    = By.xpath("//android.widget.TextView[contains(@text,'Provided credentials')]");
    private static final By USERNAME_ERROR   = By.xpath("//android.widget.TextView[contains(@text,'Username is required')]");
    private static final By PASSWORD_ERROR   = By.xpath("//android.widget.TextView[contains(@text,'Password is required')]");

    public LoginPage(AndroidDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    /* ── Actions ─────────────────────────────────────────── */

    public LoginPage enterUsername(String username) {
        type(USERNAME_FIELD, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(PASSWORD_FIELD, password);
        return this;
    }

    public void tapLogin() {
        tap(LOGIN_BUTTON);
    }

    /**
     * Full login flow — enter credentials and tap Login.
     */
    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        tapLogin();
    }

    /* ── Verifications ───────────────────────────────────── */

    public boolean isErrorMessageDisplayed() {
        return isDisplayedQuick(ERROR_MESSAGE, 5);
    }

    public boolean isUsernameErrorDisplayed() {
        return isDisplayedQuick(USERNAME_ERROR, 5);
    }

    public boolean isPasswordErrorDisplayed() {
        return isDisplayedQuick(PASSWORD_ERROR, 5);
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(LOGIN_BUTTON);
    }
}
