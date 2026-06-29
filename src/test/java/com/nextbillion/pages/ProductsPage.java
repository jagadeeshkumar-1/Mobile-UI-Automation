package com.nextbillion.pages;

import com.nextbillion.base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for the Products catalog screen (home screen after splash).
 * <p>
 * Displays a grid of products. Each product can be tapped to view details
 * or added to cart directly.
 */
public class ProductsPage extends BasePage {

    /* ── Locators ────────────────────────────────────────── */

    private static final By PRODUCTS_TITLE   = By.xpath("//android.widget.TextView[@text='Products']");
    private static final By CART_BADGE       = By.id("com.saucelabs.mydemoapp.android:id/cartTV");
    private static final By CART_ICON        = AppiumBy.accessibilityId("View cart");
    private static final By MENU_BUTTON      = AppiumBy.accessibilityId("View menu");
    private static final By MENU_LOGIN       = AppiumBy.accessibilityId("Login Menu Item");
    private static final By MENU_LOGOUT      = AppiumBy.accessibilityId("Menu item log out");
    private static final By LOGOUT_OK        = By.id("android:id/button1");

    public ProductsPage(AndroidDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    /* ── Actions ─────────────────────────────────────────── */

    /**
     * Taps the first product in the catalog grid.
     */
    public ProductDetailPage tapFirstProduct() {
        By firstProduct = By.xpath("(//android.widget.ImageView[@content-desc='Product Image'])[1]");
        tap(firstProduct);
        return new ProductDetailPage(driver, wait);
    }

    /**
     * Opens the hamburger menu and navigates to Log In.
     */
    public LoginPage navigateToLogin() {
        tap(MENU_BUTTON);
        tap(MENU_LOGIN);
        return new LoginPage(driver, wait);
    }

    /**
     * Taps the cart icon to navigate to the Cart screen.
     */
    public CartPage goToCart() {
        tap(CART_ICON);
        return new CartPage(driver, wait);
    }

    /* ── Verifications ───────────────────────────────────── */

    public boolean isProductsPageDisplayed() {
        return isDisplayed(PRODUCTS_TITLE);
    }

    public String getCartBadgeCount() {
        try {
            return getText(CART_BADGE);
        } catch (Exception e) {
            return "0";
        }
    }
}
