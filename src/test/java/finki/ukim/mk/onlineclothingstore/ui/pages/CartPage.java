package finki.ukim.mk.onlineclothingstore.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CartPage extends BasePage {

    @FindBy(id = "subtotal")
    private WebElement subtotalSpan;

    @FindBy(id = "total")
    private WebElement totalSpan;

    private final By totalLocator = By.id("total");

    @FindBy(css = ".cart-variant")
    private List<WebElement> cartVariantRows;

    @FindBy(id = "check-for-stocks")
    private WebElement confirmOrderButton;

    @FindBy(css = "a[href='/products']")
    private WebElement continueBrowsingLink;


    private final By errorOverlay = By.id("errorOverlay");
    private final By errorTitle = By.cssSelector("#errorOverlay #errTitle");
    private final By errorMsg = By.cssSelector("#errorOverlay #errMsg");
    private final By errorCloseBtn = By.cssSelector("#errorOverlay .err-close");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage assertLoaded() {
        waitForElementVisible(totalLocator);
        return this;
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



    public boolean hasItems() {
        return !cartVariantRows.isEmpty();
    }

    public int itemCount() {
        return cartVariantRows.size();
    }

    public long getFirstCartVariantId() {
        if (cartVariantRows.isEmpty()) {
            throw new IllegalStateException("Cart has no items.");
        }
        return Long.parseLong(cartVariantRows.get(0).getAttribute("data-cart-variant-id"));
    }

    private WebElement rowByCartVariantId(long cartVariantId) {
        By rowLocator = By.cssSelector(".cart-variant[data-cart-variant-id='" + cartVariantId + "']");
        return waitForElementVisible(rowLocator);
    }

    private WebElement quantityInput(long cartVariantId) {
        return waitForElementVisible(By.id(cartVariantId + "-quantity"));
    }

    private WebElement lineTotal(long cartVariantId) {
        return waitForElementVisible(By.id(cartVariantId + "-total"));
    }

    public int getQuantity(long cartVariantId) {
        return Integer.parseInt(quantityInput(cartVariantId).getAttribute("value"));
    }

    public String getLineTotalText(long cartVariantId) {
        return lineTotal(cartVariantId).getText().trim();
    }

    public double getLineTotalValue(long cartVariantId) {
        return parseMoney(getLineTotalText(cartVariantId));
    }


    public CartPage setQuantityAndWait(long cartVariantId, int newQty) {
        if (newQty < 1) throw new IllegalArgumentException("Quantity must be >= 1");

        String oldSubtotal = getSubtotalText();
        String oldTotal = getTotalText();
        String oldLineTotal = getLineTotalText(cartVariantId);

        WebElement qty = quantityInput(cartVariantId);
        click(qty);

        sendKeys(qty, String.valueOf(newQty));
        System.out.println("ok");

        wait.until(d -> {
            String currentVal = quantityInput(cartVariantId).getAttribute("value");
            return currentVal.equals(String.valueOf(newQty));
        });

        return this;
    }

    /* -------------------- REMOVE ITEM -------------------- */

    public CartPage removeItem(long cartVariantId) {
        WebElement row = rowByCartVariantId(cartVariantId);
        WebElement removeLink = row.findElement(By.cssSelector("a.btn-outline-danger"));
        click(removeLink);
        waitForElementInvisible(row);
        return this;
    }


    public OrderPage confirmOrderRedirected() {
        String oldUrl = getCurrentUrl();

        click(confirmOrderButton);

        wait.until(d -> !d.getCurrentUrl().equals(oldUrl) || isErrorOverlayOpen());

        if (isErrorOverlayOpen()) {
            throw new AssertionError("Confirm order failed: error overlay opened.");
        }

        return new OrderPage(driver).assertLoaded();
    }



    public ProductsPage continueBrowsing() {
        click(continueBrowsingLink);
        return new ProductsPage(driver).assertLoaded();
    }

    public boolean isErrorOverlayPresentInDom() {
        return exists(errorOverlay);
    }

    public boolean isErrorOverlayOpen() {
        List<WebElement> overlays = driver.findElements(errorOverlay);
        if (overlays.isEmpty()) return false;

        String cls = overlays.get(0).getAttribute("class");
        return cls != null && cls.contains("is-open");
    }

    public CartPage waitForErrorOverlayOpen() {
        wait.until(d -> isErrorOverlayOpen());
        return this;
    }

    public String getErrorTitle() {
        if (!isErrorOverlayPresentInDom()) return "";
        return driver.findElement(errorTitle).getText().trim();
    }

    public String getErrorMessage() {
        if (!isErrorOverlayPresentInDom()) return "";
        return driver.findElement(errorMsg).getText().trim();
    }

    public CartPage closeErrorOverlayIfOpen() {
        if (isErrorOverlayOpen()) {
            driver.findElement(errorCloseBtn).click();
            wait.until(d -> !isErrorOverlayOpen());
        }
        return this;
    }

    private double parseMoney(String moneyText) {
        String cleaned = moneyText.replaceAll("[^0-9.]", "");
        if (cleaned.isBlank()) return 0.0;
        return Double.parseDouble(cleaned);
    }
}
