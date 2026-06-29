package com.nextbillion.tests;

import com.nextbillion.base.BaseTest;
import com.nextbillion.pages.CartPage;
import com.nextbillion.pages.ProductDetailPage;
import com.nextbillion.pages.ProductsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Cart functionality tests for Sauce Labs My Demo App.
 * <p>
 * Covers adding an item to the cart and verifying cart contents.
 */
public class CartTest extends BaseTest {

    @Test(groups = {"Smoke", "Regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Add a product to cart from detail page, verify cart badge updates and cart contains the item")
    public void addItemToCart_itemAppearsInCart() {
        ProductsPage productsPage = new ProductsPage(driver, wait);

        // Tap first product to see detail
        ProductDetailPage detailPage = productsPage.tapFirstProduct();
        String productName = detailPage.getProductTitle();

        // Add to cart
        detailPage.addToCart();

        // Verify cart badge shows "1"
        assertEquals(detailPage.getCartBadgeCount(), "1",
                "Cart badge should show 1 after adding one item");

        // Navigate to cart and verify
        detailPage.goBack();
        CartPage cartPage = productsPage.goToCart();

        assertEquals(cartPage.getCartItemCount(), 1,
                "Cart should contain exactly 1 item");
        assertEquals(cartPage.getFirstItemName(), productName,
                "Cart item name should match the product added");
        assertEquals(cartPage.getFirstItemQuantity(), "1",
                "Cart item quantity should be 1");
        assertTrue(cartPage.isCheckoutButtonDisplayed(),
                "Proceed To Checkout button should be visible");
    }
}
