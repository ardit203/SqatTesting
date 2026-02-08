package finki.ukim.mk.onlineclothingstore.ui.pages;


import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ProductDetailsPage extends BasePage {

    @FindBy(css = "a[href='/cart']")
    private WebElement cartLink;

    @FindBy(css = "a[href='/profile']")
    private WebElement profileLink;

    @FindBy(css = "h1.h3")
    private WebElement productName;

    @FindBy(id = "stock")
    private WebElement stockSpan;

    @FindBy(id = "quantity-details")
    private WebElement quantityInput;

    @FindBy(css = "button.add-to-cart-btn")
    private WebElement addToCartButton;

    private final By sizeButtons = By.cssSelector("#sizes .size-btn");
    private final By activeSizeButton = By.cssSelector("#sizes .size-btn.active-size");

    private final By cartSign = By.id("cart-sign");

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    public ProductDetailsPage assertLoaded() {
        waitForElementVisible(productName);
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
        wait.until(d -> !stockSpan.getText().trim().isEmpty());
        return Integer.parseInt(stockSpan.getText().trim());
    }

    public ProductDetailsPage chooseSize(String size) {
        By btn = By.cssSelector("#sizes .size-btn[data-size='" + size + "']");
        WebElement sizeBtn = waitForElementClickable(btn);

        String expectedStock = sizeBtn.getAttribute("data-stock");

        sizeBtn.click();

        wait.until(d -> {
            List<WebElement> actives = d.findElements(activeSizeButton);
            return !actives.isEmpty() && size.equalsIgnoreCase(actives.get(0).getAttribute("data-size"));
        });

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
        click(addToCartButton);
        return this;
    }

    public ProductDetailsPage waitForCartSignVisible() {
        wait.until(d -> {
            List<WebElement> els = d.findElements(cartSign);
            if (els.isEmpty()) return false;
            WebElement el = els.get(0);
            String style = el.getAttribute("style");
            return el.isDisplayed() || (style != null && !style.contains("display:none"));
        });
        return this;
    }

}
