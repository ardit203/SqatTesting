package finki.ukim.mk.onlineclothingstore.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class OrderPage extends BasePage {

    @FindBy(css = "form[action='/order'][method='post']")
    private WebElement orderForm;


    @FindBy(css = "h2.h5")
    private WebElement checkoutTitle;

    @FindBy(css = "input[name='email']")
    private WebElement emailInput;

    private final  By emailLocator = By.cssSelector("input[name='email']");

    @FindBy(css = "input[name='phone']")
    private WebElement phoneInput;

    @FindBy(css = "input[name='address']")
    private WebElement addressInput;

    @FindBy(css = "input[name='city']")
    private WebElement cityInput;

    @FindBy(css = "input[name='zip']")
    private WebElement zipInput;

    @FindBy(css = "input[name='country']")
    private WebElement countryInput;

    @FindBy(css = "input[type='radio'][name='paymentMethod'][value='CARD']")
    private WebElement cardRadio;

    @FindBy(css = "input[type='radio'][name='paymentMethod'][value='CASH']")
    private WebElement cashRadio;


    @FindBy(id = "cardFields")
    private WebElement cardFieldsBox;

    @FindBy(css = "input[name='cardName']")
    private WebElement cardNameInput;

    @FindBy(css = "input[name='cardNumber']")
    private WebElement cardNumberInput;

    @FindBy(css = "input[name='cardExpiry']")
    private WebElement cardExpiryInput;

    @FindBy(css = "input[name='cardCvc']")
    private WebElement cardCvcInput;


    @FindBy(id = "consent")
    private WebElement consentCheckbox;

    @FindBy(css = "button[type='submit'].btn.btn-primary")
    private WebElement placeOrderButton;

    @FindBy(css = "a[href='/cart']")
    private WebElement backToCartLink;


    @FindBy(id = "subtotal")
    private WebElement subtotalSpan;

    @FindBy(id = "total")
    private WebElement totalSpan;


    private final By errorAlert = By.cssSelector("form[action='/order'] .alert.alert-danger");

    public OrderPage(WebDriver driver) {
        super(driver);
    }

    public OrderPage assertLoaded() {
        waitForElementVisible(emailLocator);
        return this;
    }


    public OrderPage fillCustomerDetails(String email, String phone, String address,
                                         String city, String zip, String country) {
        sendKeys(emailInput, email);
        sendKeys(phoneInput, phone);
        sendKeys(addressInput, address);
        sendKeys(cityInput, city);
        sendKeys(zipInput, zip);
        sendKeys(countryInput, country);
        return this;
    }


    public OrderPage selectCardPayment() {
        if (!cardRadio.isSelected()) cardRadio.click();
        wait.until(d -> isCardFieldsVisible());
        return this;
    }

    public OrderPage selectCashPayment() {
        if (!cashRadio.isSelected()) cashRadio.click();
        wait.until(d -> !isCardFieldsVisible());
        return this;
    }


    public boolean isCardFieldsVisible() {
        String display = cardFieldsBox.getCssValue("display");
        return display != null && !display.equalsIgnoreCase("none");
    }

    public OrderPage fillCardDetails(String name, String number, String expiry, String cvc) {
        selectCardPayment();
        sendKeys(cardNameInput, name);
        sendKeys(cardNumberInput, number);
        sendKeys(cardExpiryInput, expiry);
        sendKeys(cardCvcInput, cvc);
        return this;
    }

    /* -------------------- CONSENT -------------------- */

    public OrderPage acceptConsent() {
        if (!consentCheckbox.isSelected()) consentCheckbox.click();
        return this;
    }

    public OrderPage uncheckConsentIfChecked() {
        if (consentCheckbox.isSelected()) consentCheckbox.click();
        return this;
    }


    public ProfilePage placeOrderWaitOutcome(String email, String phone, String address, String city, Integer zip, String country,
                                             String paymentMethod, String name, String number, String expiry, String cvc) {

        fillCustomerDetails(email, phone, address, city, zip.toString(), country);

        if ("CARD".equals(paymentMethod)) {
            fillCardDetails(name, number, expiry, cvc);
        } else {
            selectCashPayment();
        }

        acceptConsent();

        String oldUrl = driver.getCurrentUrl();
        click(placeOrderButton);

        wait.until(d -> !d.getCurrentUrl().equals(oldUrl) || isErrorShown());

        if (isErrorShown()) {
            throw new AssertionError("Placing order failed: " + getErrorMessage());
        }

        return new ProfilePage(driver).assertLoaded();
    }


    public boolean isErrorShown() {
        return !driver.findElements(errorAlert).isEmpty();
    }

    public String getErrorMessage() {
        List<WebElement> els = driver.findElements(errorAlert);
        return els.isEmpty() ? "" : els.get(0).getText().trim();
    }


    public CartPage backToCart() {
        click(backToCartLink);
        return new CartPage(driver).assertLoaded();
    }


    public String getSubtotalText() {
        return subtotalSpan.getText().trim();
    }

    public String getTotalText() {
        return totalSpan.getText().trim();
    }

    public double getSubtotalValue() {
        return parseMoney(getSubtotalText());
    }

    public double getTotalValue() {
        return parseMoney(getTotalText());
    }

    private double parseMoney(String moneyText) {
        String cleaned = moneyText.replaceAll("[^0-9.]", "");
        if (cleaned.isBlank()) return 0.0;
        return Double.parseDouble(cleaned);
    }
}
