package finki.ukim.mk.onlineclothingstore.ui.tests;

import finki.ukim.mk.onlineclothingstore.ui.pages.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static finki.ukim.mk.onlineclothingstore.ui.pages.BasePage.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("ui")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UITests {
    protected final String username = "username";
    protected final String password = "USER@123";
    protected final String name = "User";
    protected final String surname = "User";
    protected final String product1 = "Nirvana";
    protected final String product2 = "Contrast Heart Rib";

    protected String newUsername = "newUsername";
    protected String newPassword = "newUSER@123";

    String email = "user.user@example.com";
    String phone = "+38970123456";
    String address = "Partizanski Odredi 15";
    String city = "Skopje";
    Integer zip = 1000;
    String country = "North Macedonia";
    String paymentMethod = "CASH";


    protected WebDriver driver;
    protected HomePageNotLoggedIn homePage;


    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-save-password-bubble");


        options.addArguments("--guest");


        options.addArguments("--disable-features=AutofillServerCommunication,AutofillProfileImport,AutofillEnableAccountWalletStorage");

        Map<String, Object> prefs = new HashMap<>();


        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);

        prefs.put("autofill.profile_enabled", false);
        prefs.put("autofill.credit_card_enabled", false);
        prefs.put("profile.autofill_profile_enabled", false);
        prefs.put("profile.autofill_credit_card_enabled", false);

        options.setExperimentalOption("prefs", prefs);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        get(driver, "");
        homePage = new HomePageNotLoggedIn(driver);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void UI_AUTH_01_validLogin() {
        LoginPage loginPage = homePage.navigateToLoginPage();
        HomePageLoggedIn homePageLoggedIn = loginPage.loginValid(username, password);
    }

    @Test
    void UI_AUTH_02_invalidLogin() {
        LoginPage loginPage = homePage.navigateToLoginPage();
        loginPage = loginPage.loginInvalid("user", "USER@123");
        assertEquals("Incorrect username or password.", loginPage.getErrorMessage());
    }

    @Test
    void UI_REG_01_validRegister() {
        RegisterPage registerPage = homePage.navigateToRegisterPage();
        LoginPage loginPage = registerPage.registerValid(name, surname, newUsername, newPassword, newPassword);
        HomePageLoggedIn homePageLoggedIn = loginPage.loginValid(newUsername, newPassword);
    }

    @Test
    void UI_REG_02_usernameAlreadyExists() {
        RegisterPage registerPage = homePage.navigateToRegisterPage();
        registerPage = registerPage.registerInvalid(name, surname, username, newPassword, newPassword);
        assertEquals(String.format("User with username %s already exists!", username), registerPage.getErrorMessage());
    }

    @Test
    void UI_REG_03_passwordsDoNotMatch() {
        RegisterPage registerPage = homePage.navigateToRegisterPage();
        registerPage = registerPage.registerInvalid(name, surname, newPassword, password, newPassword);
        assertEquals("Passwords do not match!", registerPage.getErrorMessage());
    }

    @Test
    void E2E_01() {
        RegisterPage registerPage = homePage.navigateToRegisterPage();
        LoginPage loginPage = registerPage.registerValid(name, surname, newUsername, newPassword, newPassword);
        HomePageLoggedIn homePageLoggedIn = loginPage.loginValid(newUsername, newPassword);

        ProductsPage productsPage = homePageLoggedIn.navigateToProductsPage();
        productsPage = productsPage.applyFilters(product1, "T-Shirts", "KIDS", null, null, null);

        ProductDetailsPage detailsPage = productsPage.navigateToProductDetails(product1);
        String size = detailsPage.getSelectedSize();
        detailsPage = detailsPage.chooseSize("XS");
        assertNotEquals(size, detailsPage.getSelectedSize());
        detailsPage = detailsPage.setQuantity(3);
        detailsPage = detailsPage.clickAddToCart();

        CartPage cartPage = detailsPage.navigateToCartPage();
        Integer qty = cartPage.getQuantity(1L);
        cartPage = cartPage.setQuantityAndWait(1L, 4);
        assertNotEquals(qty, cartPage.getQuantity(1L));


        OrderPage orderPage = cartPage.confirmOrderRedirected();


        ProfilePage profilePage = orderPage.placeOrderWaitOutcome(email, phone, address, city, zip, country, paymentMethod,
                null, null, null, null);
        profilePage = profilePage.openPreviewForFirstOrder();
        profilePage = profilePage.closePreviewOverlay();
        loginPage = profilePage.logout();
    }

    @Test
    void E2E_02() {
        LoginPage loginPage = homePage.navigateToLoginPage();
        HomePageLoggedIn homePageLoggedIn = loginPage.loginValid(username, password);

        ProductsPage productsPage = homePageLoggedIn.navigateToProductsPage();
        productsPage = productsPage.addToCartFromOverlay(product1, "S", 3);
        productsPage = productsPage.addToCartFromOverlay(product2, "XL", 4);

        CartPage cartPage = productsPage.navigateToCart();
        cartPage.removeItem(cartPage.getFirstCartVariantId());

        OrderPage orderPage = cartPage.confirmOrderRedirected();

        ProfilePage profilePage = orderPage.placeOrderWaitOutcome(email, phone, address, city, zip, country, paymentMethod,
                null, null, null, null);
        profilePage = profilePage.openPreviewForFirstOrder();
        profilePage = profilePage.closePreviewOverlay();
        profilePage = profilePage.cancelOrderIfPending(1);

        loginPage = profilePage.logout();
    }
}
