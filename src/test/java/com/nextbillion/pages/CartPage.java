package com.nextbillion.pages;

import com.nextbillion.base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Page Object for the Cart screen.
 * <p>
 * Displays items added to cart with quantities, prices, and a
 * "Proceed To Checkout" button.
 */
public class CartPage extends BasePage {

    /* ── Locators ────────────────────────────────────────── */

    private static final By CART_ITEMS       = By.id("com.saucelabs.mydemoapp.android:id/titleTV");
    private static final By ITEM_PRICE       = By.id("com.saucelabs.mydemoapp.android:id/priceTV");
    private static final By ITEM_QUANTITY    = By.id("com.saucelabs.mydemoapp.android:id/noTV");
    private static final By TOTAL_PRICE      = By.id("com.saucelabs.mydemoapp.android:id/totalPriceTV");
    private static final By CHECKOUT_BUTTON  = AppiumBy.accessibilityId("Confirms products for checkout");
    private static final By NO_ITEMS_TEXT    = By.xpath("//android.widget.TextView[contains(@text,'No Items')]");

    public CartPage(AndroidDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    /* ── Actions ─────────────────────────────────────────── */

    public CheckoutPage proceedToCheckout() {
        tap(CHECKOUT_BUTTON);
        return new CheckoutPage(driver, wait);
    }

    /* ── Verifications ───────────────────────────────────── */

    public int getCartItemCount() {
        try {
            waitForVisible(CART_ITEMS);
            List<WebElement> items = driver.findElements(CART_ITEMS);
            return items.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getFirstItemName() {
        return getText(CART_ITEMS);
    }

    public String getCartTitle() {
        return getText(By.id("com.saucelabs.mydemoapp.android:id/productTV"));
    }

    public String getFirstItemPrice() {
        return getText(ITEM_PRICE);
    }

    public String getFirstItemQuantity() {
        return getText(ITEM_QUANTITY);
    }

    public String getTotalPrice() {
        return getText(TOTAL_PRICE);
    }

    public boolean isCartEmpty() {
        return isDisplayedQuick(NO_ITEMS_TEXT, 3);
    }

    public boolean isCheckoutButtonDisplayed() {
        return isDisplayedQuick(CHECKOUT_BUTTON, 5);
    }
}
