# Mobile UI Automation — Sauce Labs My Demo App

A mobile UI test automation framework for the [Sauce Labs My Demo App](https://github.com/saucelabs/my-demo-app-android) (Android), built with **Java 17 + Appium 2.x + TestNG + Page Object Model**. Runs locally on an Android emulator and in CI via GitHub Actions.

---

## Table of Contents

1. [Technology Stack](#technology-stack)
2. [Prerequisites](#prerequisites)
3. [Project Structure](#project-structure)
4. [Framework Architecture](#framework-architecture)
5. [Configuration](#configuration)
6. [How to Run Tests](#how-to-run-tests)
7. [Test Groups (Tags)](#test-groups-tags)
8. [Test Coverage](#test-coverage)
9. [Design Decisions](#design-decisions)
10. [CI / CD](#ci--cd)
11. [AI Tool Usage](#ai-tool-usage)
12. [What I'd Do Next](#what-id-do-next)

---

## Technology Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 17 | Language |
| Maven | 3.6+ | Build and dependency management |
| Appium Java Client | 9.3.0 | Mobile automation client |
| Selenium | 4.19.1 | WebDriver foundation (pinned via BOM) |
| UiAutomator2 | latest | Android UI automation engine (Appium driver) |
| TestNG | 7.9.0 | Test framework, runner, and grouping |
| Allure | 2.27.0 | Test result reporting |
| Android SDK | API 30+ | Emulator and platform tools |
| Node.js | 18+ | Required runtime for Appium server |

---

## Prerequisites

- **Java 17+** installed and `JAVA_HOME` set
- **Maven 3.6+** on `PATH`
- **Node.js 18+** (required by Appium)
- **Appium 2.x** installed globally: `npm install -g appium`
- **UiAutomator2 driver**: `appium driver install uiautomator2`
- **Android SDK** with `ANDROID_HOME` set and at least one emulator configured (API 30+)
- **Sauce Labs My Demo App APK** — download from [releases](https://github.com/saucelabs/my-demo-app-android/releases)

### Quick setup verification

```bash
java -version            # should print 17 or higher
mvn -version             # should print 3.6 or higher
node -v                  # should print 18 or higher
appium -v                # should print 2.x
adb devices              # should list your emulator/device
```

### APK setup

```bash
mkdir -p src/test/resources/apps
curl -L -o src/test/resources/apps/mda-2.2.0-25.apk \
  https://github.com/saucelabs/my-demo-app-android/releases/download/2.2.0/mda-2.2.0-25.apk
```

---

## Project Structure

```
Mobile-UI-Automation/
├── .github/
│   └── workflows/
│       └── test.yml                                     # GitHub Actions CI pipeline
├── pom.xml                                              # Maven build + dependencies + Selenium BOM
└── src/
    └── test/
        ├── java/
        │   └── com/nextbillion/
        │       ├── base/
        │       │   ├── BaseTest.java                    # Driver lifecycle (setup/teardown per test)
        │       │   └── BasePage.java                    # Common page interactions with explicit waits
        │       ├── pages/
        │       │   ├── LoginPage.java                   # Login screen — credentials + validation
        │       │   ├── ProductsPage.java                # Product catalog — navigation + menu + cart
        │       │   ├── ProductDetailPage.java           # Product detail — add to cart + back nav
        │       │   ├── CartPage.java                    # Cart — items, quantities, checkout
        │       │   └── CheckoutPage.java                # Checkout flow — shipping, payment, confirmation
        │       └── tests/
        │           ├── LoginTest.java                   # Valid + invalid login (2 tests)
        │           ├── CartTest.java                    # Add item + verify cart (1 test)
        │           └── CheckoutTest.java                # End-to-end checkout flow (1 test)
        └── resources/
            ├── apps/
            │   └── .gitkeep                             # APK goes here (gitignored)
            ├── allure.properties                        # Allure output config
            └── testng.xml                               # Suite definition
```

---

## Framework Architecture

### How It Works (Layer by Layer)

```
Test Class  →  Page Object  →  BasePage  →  Appium Driver  →  App on Emulator
                                   ↑
                               BaseTest
                          (driver lifecycle)
```

| Layer | Class(es) | Responsibility |
|-------|-----------|---------------|
| **Driver Management** | `BaseTest` | Creates/destroys `AndroidDriver` per `@BeforeMethod`/`@AfterMethod`. Reads config from system properties with sensible defaults for a local emulator. Waits for the splash screen to finish before handing control to tests. |
| **Page Abstraction** | `BasePage` | Common interactions: `tap()`, `type()`, `getText()`, `waitForVisible()`, `waitForClickable()`, `isDisplayedQuick()`. All actions wrap explicit `WebDriverWait` — zero `Thread.sleep()` calls. |
| **Page Objects** | `LoginPage`, `ProductsPage`, `ProductDetailPage`, `CartPage`, `CheckoutPage` | Each screen = one class. Locators are `private static final` constants. Public methods expose user-facing actions and return the next page (fluent navigation). |
| **Tests** | `LoginTest`, `CartTest`, `CheckoutTest` | Business-level test methods. No locators, no driver calls — only page object methods and TestNG assertions. |

### Locator Strategy

| Priority | Locator Type | Example | When Used |
|----------|-------------|---------|-----------|
| 1st | `AppiumBy.accessibilityId()` | `"View menu"`, `"Tap to add product to cart"` | Preferred — stable, readable, cross-platform |
| 2nd | `By.id()` (resource-id) | `"com.saucelabs.mydemoapp.android:id/nameET"` | When accessibility ID is unavailable (e.g., EditText fields) |
| 3rd | `By.xpath()` | `"//android.widget.TextView[@text='Products']"` | Last resort — for text-based or structural lookups |

### Page Navigation Pattern

```java
// Each action returns the page you land on — type-safe navigation
ProductsPage productsPage = new ProductsPage(driver, wait);
LoginPage loginPage = productsPage.navigateToLogin();
loginPage.loginAs("bod@example.com", "10203040");
ProductDetailPage detail = productsPage.tapFirstProduct();
detail.addToCart().goBack();
CartPage cart = productsPage.goToCart();
CheckoutPage checkout = cart.proceedToCheckout();
```

This prevents calling methods on the wrong page and makes the IDE auto-suggest only valid next actions.

---

## Configuration

All connection details are resolved from **system properties → environment variables → defaults**, in that order.

| Property | Env Var | Default | Description |
|----------|---------|---------|-------------|
| `appium.url` | `APPIUM_URL` | `http://127.0.0.1:4723` | Appium server endpoint |
| `platform.name` | `PLATFORM_NAME` | `Android` | Target platform |
| `device.name` | `DEVICE_NAME` | `emulator-5554` | Emulator or device ID |
| `apk.path` | `APK_PATH` | `src/test/resources/apps/mda-2.2.0-25.apk` | Path to APK under test |
| `app.package` | `APP_PACKAGE` | `com.saucelabs.mydemoapp.android` | Android app package |
| `app.activity` | `APP_ACTIVITY` | `...view.activities.SplashActivity` | Launch activity |

Override any property via Maven `-D` flags:

```bash
mvn test -Ddevice.name=Pixel_7_API_34 -Dappium.url=http://192.168.1.100:4723
```

---

## How to Run Tests

### 1. Start the Appium server

```bash
appium
```

### 2. Start an Android emulator

```bash
emulator -avd <your_avd_name>
# or via Android Studio → Device Manager → Play
```

### 3. Run the tests

```bash
# Run all 4 tests
mvn test

# Run only Smoke tests
mvn test -Dgroups=Smoke

# Run only Regression tests
mvn test -Dgroups=Regression

# Override device
mvn test -Ddevice.name=Pixel_7_API_34
```

### 4. View the Allure report

```bash
mvn allure:serve    # generates and opens report in browser
```

### Average Execution Time

| Suite | Tests | Approx. Time |
|-------|-------|-------------|
| All tests | 4 | ~60–70 seconds |
| Smoke only | 4 | ~60–70 seconds |

> All 4 tests are tagged as both Smoke and Regression. Execution time depends on emulator speed (cold boot vs. warm).

---

## Test Groups (Tags)

Every test method is annotated with one or more TestNG groups:

| Group | Purpose | When to run |
|-------|---------|-------------|
| **Smoke** | Core happy-path flows | Every PR, every push |
| **Regression** | Full regression coverage | Nightly, pre-release |

Currently all 4 tests belong to both groups. As the suite grows, new edge-case tests would be tagged `Regression` only.

---

## Test Coverage

### LoginTest — 2 tests

| Test | Groups | What It Verifies |
|------|--------|-----------------|
| `validLogin_redirectsToProductsPage` | Smoke, Regression | Valid credentials (`bod@example.com` / `10203040`) → login succeeds → redirected to Products page |
| `invalidLogin_doesNotLogIn` | Smoke, Regression | Invalid credentials → user is returned to Products page (app does not show error in v2.2.0) |

### CartTest — 1 test

| Test | Groups | What It Verifies |
|------|--------|-----------------|
| `addItemToCart_itemAppearsInCart` | Smoke, Regression | Tap first product → add to cart → cart badge = "1" → navigate to cart → item name matches, quantity = 1, checkout button visible |

### CheckoutTest — 1 test

| Test | Groups | What It Verifies |
|------|--------|-----------------|
| `endToEndCheckout_orderCompletesSuccessfully` | Smoke, Regression | Full E2E: login → add item → cart → shipping address → payment → review → place order → "Checkout Complete" confirmation |

**Total: 4 tests** covering login, catalog, cart, and checkout — the core user journey.

---

## Design Decisions

### Why Appium + UiAutomator2?

Appium is the de facto standard for cross-platform mobile test automation. UiAutomator2 is Google's own automation framework with deep access to the Android UI hierarchy and support for modern Android versions (API 21+). Together, they provide the most reliable and well-documented Android automation stack.

### Why pin Selenium via BOM (`dependencyManagement`)?

The Appium Java Client 9.3.0 declares a Selenium version **range** `[4.19.0, 5.0)`. Maven resolves this to the newest available version (e.g., 4.45.0), which has removed classes like `ContextAware` and `LocationContext` that Appium 9.x still depends on. The Selenium BOM in `<dependencyManagement>` forces **all** Selenium artifacts — including transitive ones — to 4.19.1.

### Why `noReset=false` and `fullReset=false`?

- `noReset=false` — clears app data between tests (clean state) without uninstalling
- `fullReset=false` — avoids the slow uninstall/reinstall cycle
- This gives test isolation with acceptable speed

### Why `@BeforeMethod` instead of `@BeforeClass`?

Each test method gets a fresh driver and app session. If one test crashes the app or corrupts state, the next test is unaffected. This is critical for mobile testing where app state is fragile.

### Why not parallel execution?

Mobile UI tests on a single emulator cannot run in parallel — there is only one screen. Parallel execution requires multiple emulators or cloud device farms (BrowserStack, Sauce Labs). The `testng.xml` is set to `parallel="false" thread-count="1"` intentionally.

### Why no Lombok?

Unlike the API project with model classes needing getters/setters, this framework has no POJOs that benefit from Lombok. Page Objects are behavior-driven classes where Lombok adds no value.

### Why `driver.navigate().back()` instead of a back-button locator?

The My Demo App (v2.2.0) does not expose a "Navigate up" accessibility ID on the product detail screen. `driver.navigate().back()` triggers the Android system back action, which is reliable and doesn't depend on any app-specific locator.

---

## CI / CD

### GitHub Actions

The pipeline is defined in `.github/workflows/test.yml`. It runs **automatically** on every push to `main` and on every PR.

#### What it does automatically

| Trigger | Test Group | Reason |
|---------|-----------|--------|
| **Pull request** → `main` | Smoke | Fast feedback — run core flows only |
| **Push** → `main` | Smoke | Post-merge validation |
| **Manual** (workflow_dispatch) | Selectable (Smoke / Regression / All) | On-demand full runs |

#### Pipeline steps

```
1. Checkout code
2. Set up JDK 17 (temurin, with Maven cache)
3. Set up Node.js 20
4. Install Appium + UiAutomator2 driver
5. Download APK from GitHub Releases
6. Enable KVM for hardware-accelerated emulator
7. Boot Android emulator (API 30, x86_64)
8. Start Appium server in background
9. Run Maven tests (group based on trigger)
10. Publish TestNG results (dorny/test-reporter — visible in PR checks)
11. Generate Allure single-file report
12. Upload Allure report as artifact (30-day retention)
```

#### How to trigger manually

1. Go to **Actions** → **Mobile UI Tests**
2. Click **Run workflow**
3. Select the test group (`Smoke`, `Regression`, or `All`)
4. Click **Run workflow**

#### Viewing test results

- **PR checks tab** — TestNG results appear as a check annotation with pass/fail details
- **Artifacts** — download `allure-report` from the workflow run, open `index.html`

#### Key CI design choices

| Choice | Reason |
|--------|--------|
| `reactivecircus/android-emulator-runner` | Boots and manages the emulator lifecycle inside the GitHub runner — no custom Docker image needed |
| KVM acceleration | Without KVM, the x86_64 emulator is unusably slow on GitHub runners; the udev rule enables it |
| `continue-on-error: true` in test step | Ensures Allure report and test results are always published, even if tests fail |
| `--single-file` Allure report | Produces a self-contained HTML file that works when downloaded as an artifact |
| APK downloaded in CI | The APK is gitignored (40+ MB); CI downloads it fresh from GitHub Releases |

---

## AI Tool Usage

This project was developed with AI assistance. Below is a transparent breakdown:

| Area | AI Role | My Role |
|------|---------|---------|
| **Framework architecture** (BaseTest, BasePage, POM) | Generated boilerplate structure | Designed the layered architecture and page navigation pattern |
| **Page Objects** (locators, actions) | Suggested locator strategies | Identified actual UI elements via page source inspection, validated accessibility IDs and resource-IDs against the running app |
| **Test scenarios** | Generated test method skeletons | Defined the 4 test scenarios covering login, cart, and checkout flows |
| **CI/CD pipeline** | Created GitHub Actions workflow | Specified the trigger strategy, emulator config, and artifact handling |
| **README documentation** | Generated structured sections and tables | Provided all technical content and design rationale |
| **Dependency management** | Diagnosed Selenium BOM issue | Understood the Appium ↔ Selenium version incompatibility root cause |

**Tools used:** Windsurf IDE with Claude (Cascade) as the AI coding assistant.

---

## What I'd Do Next

Given more time, these are the improvements I would prioritize:

### 1. Screenshot on Failure
Add a TestNG `ITestListener` that captures a screenshot on every test failure and attaches it to the Allure report. This is the single most valuable debugging aid for mobile UI tests.

```java
@Override
public void onTestFailure(ITestResult result) {
    File screenshot = driver.getScreenshotAs(OutputType.FILE);
    Allure.addAttachment("Failure Screenshot", new FileInputStream(screenshot));
}
```

### 2. Cloud Device Farm Integration
Add a `cloud` Maven profile that switches the Appium URL to BrowserStack or Sauce Labs. This enables:
- Running on real devices (not just emulators)
- Parallel execution across multiple devices
- Testing on different Android versions/manufacturers

### 3. iOS Support
The Page Object Model is already platform-agnostic (uses accessibility IDs). Adding iOS support requires:
- An `IOSDriver` variant in `BaseTest`
- XCUITest automation name
- Potentially a few locator adjustments where accessibility IDs differ

### 4. Data-Driven Tests
Use TestNG `@DataProvider` for login tests. Test multiple credential combinations (empty fields, special characters, SQL injection) without duplicating test methods.

### 5. Gesture Support
Add swipe, scroll, and long-press helpers to `BasePage` using Appium W3C Actions API. The product catalog requires scrolling to reach items beyond the viewport.

### 6. Allure Annotations for Richer Reports
Add `@Step`, `@Description`, `@Severity`, and `@Epic`/`@Feature`/`@Story` annotations throughout. Attach page source XML on failure for debugging without a screenshot.
