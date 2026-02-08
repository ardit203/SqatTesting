package finki.ukim.mk.onlineclothingstore.ui.pages;


import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ProductDetailsPage extends BasePage {

    @FindBy(css = "a[href='/cart']")
    private WebElement cartLink;

    @FindBy(css = "a[href='/profile']")
    private WebElement profileLink;

    // Page identity (stable)
    @FindBy(css = "h1.h3")
    private WebElement productName;

    // Updated by JS (initializeSizes/changeSize)
    @FindBy(id = "stock")
    private WebElement stockSpan;

    // Quantity exists only when authenticated (sec:authorize)
    @FindBy(id = "quantity-details")
    private WebElement quantityInput;

    @FindBy(css = "button.add-to-cart-btn")
    private WebElement addToCartButton;

    // Sizes are injected dynamically into #sizes as:
    // <button class="... size-btn" data-stock=".." data-size="..">...</button>
    private final By sizeButtons = By.cssSelector("#sizes .size-btn");
    private final By activeSizeButton = By.cssSelector("#sizes .size-btn.active-size");

    // Optional: cart indicator from your header
    private final By cartSign = By.id("cart-sign");

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    public ProductDetailsPage assertLoaded() {
        waitForElementVisible(productName);
        // wait until JS injected size buttons
        wait.until(d -> !d.findElements(sizeButtons).isEmpty());
        return this;
    }

    public CartPage navigateToCartPage(){
        click(cartLink);
        return new CartPage(driver).assertLoaded();
    }

    public String getProductName() {
        return productName.getText().trim();
    }

    public int getDisplayedStock() {
        // JS sets stockSpan.textContent; wait for non-empty
        wait.until(d -> !stockSpan.getText().trim().isEmpty());
        return Integer.parseInt(stockSpan.getText().trim());
    }

    public ProductDetailsPage chooseSize(String size) {
        By btn = By.cssSelector("#sizes .size-btn[data-size='" + size + "']");
        WebElement sizeBtn = waitForElementClickable(btn);

        // Capture expected stock from dataset BEFORE clicking
        String expectedStock = sizeBtn.getAttribute("data-stock");

        sizeBtn.click();

        // confirm active-size applied (changeSize adds active-size)
        wait.until(d -> {
            List<WebElement> actives = d.findElements(activeSizeButton);
            return !actives.isEmpty() && size.equalsIgnoreCase(actives.get(0).getAttribute("data-size"));
        });

        // confirm #stock updated to clicked button's data-stock
        if (expectedStock != null) {
            wait.until(d -> stockSpan.getText().trim().equals(expectedStock));
        }

        return this;
    }

    public String getSelectedSize() {
        WebElement active = waitForElementPresence(activeSizeButton);
        return active.getAttribute("data-size");
    }

    public ProductDetailsPage setQuantity(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be >= 1");

        // If not logged in, quantityInput won't exist. Fail clearly:
        try {
            waitForElementVisible(quantityInput);
        } catch (TimeoutException e) {
            throw new IllegalStateException("Quantity input not visible. Are you logged in?");
        }

        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(quantity));
        return this;
    }

    public ProductDetailsPage clickAddToCart() {
        // If not logged in, add button won't exist.
        click(addToCartButton);
        return this;
    }

    /**
     * Optional: wait for cart badge to appear after add-to-cart.
     * Adjust depending on how your UI updates cart sign (style/class).
     */
    public ProductDetailsPage waitForCartSignVisible() {
        wait.until(d -> {
            List<WebElement> els = d.findElements(cartSign);
            if (els.isEmpty()) return false;
            WebElement el = els.get(0);
            // If your UI uses display:none inline, this covers it too
            String style = el.getAttribute("style");
            return el.isDisplayed() || (style != null && !style.contains("display:none"));
        });
        return this;
    }
}
