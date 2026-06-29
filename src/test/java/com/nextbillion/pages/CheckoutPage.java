package com.nextbillion.pages;

import com.nextbillion.base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for the Checkout flow screens.
 * <p>
 * The checkout flow has multiple steps:
 * 1. Shipping address form
 * 2. Payment details form
 * 3. Order review
 * 4. Order confirmation
 * <p>
 * This page object covers all steps as they share the same activity.
 */
public class CheckoutPage extends BasePage {

    /* ── Shipping address locators ───────────────────────── */

    private static final By FULL_NAME_FIELD  = By.id("com.saucelabs.mydemoapp.android:id/fullNameET");
    private static final By ADDRESS_LINE1    = By.id("com.saucelabs.mydemoapp.android:id/address1ET");
    private static final By CITY_FIELD       = By.id("com.saucelabs.mydemoapp.android:id/cityET");
    private static final By ZIP_CODE_FIELD   = By.id("com.saucelabs.mydemoapp.android:id/zipET");
    private static final By COUNTRY_FIELD    = By.id("com.saucelabs.mydemoapp.android:id/countryET");
    private static final By TO_PAYMENT_BTN   = AppiumBy.accessibilityId("Saves user info for checkout");

    /* ── Payment locators ────────────────────────────────── */

    private static final By CARD_HOLDER      = By.id("com.saucelabs.mydemoapp.android:id/nameET");
    private static final By CARD_NUMBER      = By.id("com.saucelabs.mydemoapp.android:id/cardNumberET");
    private static final By EXPIRY_DATE      = By.id("com.saucelabs.mydemoapp.android:id/expirationDateET");
    private static final By SECURITY_CODE    = By.id("com.saucelabs.mydemoapp.android:id/securityCodeET");
    private static final By REVIEW_ORDER_BTN = AppiumBy.accessibilityId("Saves payment info and launches screen to review checkout data");

    /* ── Review / confirmation locators ──────────────────── */

    private static final By PLACE_ORDER_BTN  = AppiumBy.accessibilityId("Completes the process of checkout");
    private static final By ORDER_COMPLETE   = By.xpath("//android.widget.TextView[contains(@text,'Checkout Complete')]");
    private static final By CONTINUE_SHOPPING = AppiumBy.accessibilityId("Tap to open catalog");

    public CheckoutPage(AndroidDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    /* ── Shipping address actions ────────────────────────── */

    public CheckoutPage fillShippingAddress(String fullName, String addressLine1,
                                            String city, String zipCode, String country) {
        type(FULL_NAME_FIELD, fullName);
        type(ADDRESS_LINE1, addressLine1);
        type(CITY_FIELD, city);
        type(ZIP_CODE_FIELD, zipCode);
        type(COUNTRY_FIELD, country);
        return this;
    }

    public CheckoutPage tapToPayment() {
        tap(TO_PAYMENT_BTN);
        return this;
    }

    /* ── Payment actions ─────────────────────────────────── */

    public CheckoutPage fillPaymentDetails(String cardHolder, String cardNumber,
                                           String expiryDate, String securityCode) {
        type(CARD_HOLDER, cardHolder);
        type(CARD_NUMBER, cardNumber);
        type(EXPIRY_DATE, expiryDate);
        type(SECURITY_CODE, securityCode);
        return this;
    }

    public CheckoutPage tapReviewOrder() {
        tap(REVIEW_ORDER_BTN);
        return this;
    }

    /* ── Review / Place order ────────────────────────────── */

    public CheckoutPage tapPlaceOrder() {
        tap(PLACE_ORDER_BTN);
        return this;
    }

    /* ── Verifications ───────────────────────────────────── */

    public boolean isOrderComplete() {
        return isDisplayed(ORDER_COMPLETE);
    }

    public boolean isShippingFormDisplayed() {
        return isDisplayed(FULL_NAME_FIELD);
    }

    public boolean isPaymentFormDisplayed() {
        return isDisplayed(CARD_NUMBER);
    }
}
