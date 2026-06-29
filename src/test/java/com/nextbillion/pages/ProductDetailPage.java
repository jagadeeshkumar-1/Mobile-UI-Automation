package com.nextbillion.pages;

import com.nextbillion.base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for the Product Detail screen.
 * <p>
 * Shown after tapping a product from the catalog. Displays product name,
 * price, description, and an "Add To Cart" button.
 */
public class ProductDetailPage extends BasePage {

    /* ── Locators ────────────────────────────────────────── */

    private static final By PRODUCT_TITLE    = By.id("com.saucelabs.mydemoapp.android:id/productTV");
    private static final By PRODUCT_PRICE    = By.id("com.saucelabs.mydemoapp.android:id/priceTV");
    private static final By ADD_TO_CART_BTN  = AppiumBy.accessibilityId("Tap to add product to cart");
    private static final By CART_BADGE       = By.id("com.saucelabs.mydemoapp.android:id/cartTV");

    public ProductDetailPage(AndroidDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    /* ── Actions ─────────────────────────────────────────── */

    public ProductDetailPage addToCart() {
        tap(ADD_TO_CART_BTN);
        return this;
    }

    public ProductsPage goBack() {
        driver.navigate().back();
        return new ProductsPage(driver, wait);
    }

    /* ── Verifications ───────────────────────────────────── */

    public String getProductTitle() {
        return getText(PRODUCT_TITLE);
    }

    public String getProductPrice() {
        return getText(PRODUCT_PRICE);
    }

    public String getCartBadgeCount() {
        try {
            return getText(CART_BADGE);
        } catch (Exception e) {
            return "0";
        }
    }
}
