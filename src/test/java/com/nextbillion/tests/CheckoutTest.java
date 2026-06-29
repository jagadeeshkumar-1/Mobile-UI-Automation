package com.nextbillion.tests;

import com.nextbillion.base.BaseTest;
import com.nextbillion.pages.*;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Checkout flow tests for Sauce Labs My Demo App.
 * <p>
 * Covers the end-to-end checkout flow: login → add item → cart →
 * shipping → payment → review → order confirmation.
 */
public class CheckoutTest extends BaseTest {

    private static final String VALID_USERNAME = "bod@example.com";
    private static final String VALID_PASSWORD = "10203040";

    @Test(groups = {"Smoke", "Regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Complete end-to-end checkout: login, add item, fill shipping and payment, place order")
    public void endToEndCheckout_orderCompletesSuccessfully() {
        ProductsPage productsPage = new ProductsPage(driver, wait);

        // Step 1: Log in
        LoginPage loginPage = productsPage.navigateToLogin();
        loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        assertTrue(productsPage.isProductsPageDisplayed(),
                "Should be back on Products page after login");

        // Step 2: Add item to cart
        ProductDetailPage detailPage = productsPage.tapFirstProduct();
        detailPage.addToCart();
        detailPage.goBack();

        // Step 3: Go to cart and proceed to checkout
        CartPage cartPage = productsPage.goToCart();
        assertEquals(cartPage.getCartItemCount(), 1, "Cart should have 1 item");
        CheckoutPage checkout = cartPage.proceedToCheckout();

        // Step 4: Fill shipping address
        assertTrue(checkout.isShippingFormDisplayed(),
                "Shipping address form should be displayed");
        checkout.fillShippingAddress(
                "Jane Doe",
                "123 Test Street",
                "San Francisco",
                "94105",
                "United States"
        );
        checkout.tapToPayment();

        // Step 5: Fill payment details
        assertTrue(checkout.isPaymentFormDisplayed(),
                "Payment form should be displayed");
        checkout.fillPaymentDetails(
                "Jane Doe",
                "4111111111111111",
                "12/28",
                "123"
        );
        checkout.tapReviewOrder();

        // Step 6: Place order
        checkout.tapPlaceOrder();

        // Step 7: Verify order confirmation
        assertTrue(checkout.isOrderComplete(),
                "Order complete message should be displayed");
    }
}
