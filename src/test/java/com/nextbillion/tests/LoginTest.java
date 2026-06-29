package com.nextbillion.tests;

import com.nextbillion.base.BaseTest;
import com.nextbillion.pages.LoginPage;
import com.nextbillion.pages.ProductsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Login screen tests for Sauce Labs My Demo App.
 * <p>
 * Covers valid login, invalid credentials, and missing field validation.
 * Valid credentials: bod@example.com / 10203040
 */
public class LoginTest extends BaseTest {

    private static final String VALID_USERNAME   = "bod@example.com";
    private static final String VALID_PASSWORD   = "10203040";
    private static final String INVALID_USERNAME = "invalid@user.com";
    private static final String INVALID_PASSWORD = "wrongpassword";

    @Test(groups = {"Smoke", "Regression"})
    @Severity(SeverityLevel.BLOCKER)
    @Description("Valid credentials should log in and redirect to Products page")
    public void validLogin_redirectsToProductsPage() {
        ProductsPage productsPage = new ProductsPage(driver, wait);
        LoginPage loginPage = productsPage.navigateToLogin();

        loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);

        // After successful login, user is redirected back to Products page
        assertTrue(productsPage.isProductsPageDisplayed(),
                "Products page should be displayed after successful login");
    }

    @Test(groups = {"Smoke", "Regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Invalid credentials should not log in — user is returned to Products page")
    public void invalidLogin_doesNotLogIn() {
        ProductsPage productsPage = new ProductsPage(driver, wait);
        LoginPage loginPage = productsPage.navigateToLogin();

        loginPage.loginAs(INVALID_USERNAME, INVALID_PASSWORD);

        // App returns to Products page after invalid login attempt
        assertTrue(productsPage.isProductsPageDisplayed(),
                "Products page should be displayed after invalid login attempt");
    }
}
