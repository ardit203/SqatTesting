package finki.ukim.mk.onlineclothingstore.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ProfilePage extends BasePage {

    /* -------------------- PAGE IDENTITY (STATIC) -------------------- */

    @FindBy(css = "a[href='/logout']")
    private WebElement logoutBtn;

    @FindBy(css = ".profile-header h2")
    private WebElement fullNameHeader;

    @FindBy(css = ".profile-header p")
    private WebElement usernameText;

    @FindBy(css = "table.table")
    private List<WebElement> ordersTables;

    @FindBy(css = "tbody tr")
    private List<WebElement> orderRows;

    @FindBy(css = ".card-body.text-center.py-5")
    private List<WebElement> noOrdersBlocks;

    @FindBy(css = "a[href='/products'].btn.btn-primary")
    private List<WebElement> startShoppingButtons;



    private final By previewOverlay = By.id("orderPreviewOverlay");
    private final By overlayTitle = By.cssSelector("#orderPreviewOverlay #ordTitle");
    private final By overlaySubtitle = By.cssSelector("#orderPreviewOverlay #ordSubtitle");
    private final By overlayList = By.cssSelector("#orderPreviewOverlay #ordList");
    private final By overlayItems = By.cssSelector("#orderPreviewOverlay .ord-item");
    private final By overlayCloseX = By.cssSelector("#orderPreviewOverlay .ord-x");

    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    public ProfilePage assertLoaded() {
        wait.until(ExpectedConditions.visibilityOf(fullNameHeader));
        wait.until(ExpectedConditions.visibilityOf(usernameText));
        return this;
    }

    public LoginPage logout(){
        click(logoutBtn);
        return new LoginPage(driver).assertLoaded();
    }

    public boolean hasOrders() {
        return !orderRows.isEmpty();
    }

    public boolean isNoOrdersBlockShown() {
        return !noOrdersBlocks.isEmpty() && noOrdersBlocks.get(0).isDisplayed();
    }

    public int ordersCount() {
        return orderRows.size();
    }

    public ProfilePage openPreviewForOrderId(long orderId) {
        By previewBtn = By.cssSelector("button.preview-order[data-order-id='" + orderId + "']");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(previewBtn));
        btn.click();

        wait.until(d -> isPreviewOverlayOpen());
        return this;
    }

    public ProfilePage openPreviewForFirstOrder() {
        if (orderRows.isEmpty()) throw new IllegalStateException("No orders available to preview.");

        WebElement firstRow = orderRows.get(0);
        WebElement btn = firstRow.findElement(By.cssSelector("button.preview-order"));
        click(btn);

        wait.until(d -> isPreviewOverlayOpen());
        return this;
    }

    public boolean isPreviewOverlayPresentInDom() {
        return exists(previewOverlay);
    }

    public boolean isPreviewOverlayOpen() {
        List<WebElement> overlays = driver.findElements(previewOverlay);
        if (overlays.isEmpty()) return false;

        String cls = overlays.get(0).getAttribute("class");
        return cls != null && cls.contains("ord-show");
    }

    public String getPreviewTitle() {
        if (!isPreviewOverlayPresentInDom()) return "";
        return driver.findElement(overlayTitle).getText().trim();
    }

    public String getPreviewSubtitle() {
        if (!isPreviewOverlayPresentInDom()) return "";
        return driver.findElement(overlaySubtitle).getText().trim();
    }

    public int getPreviewItemsCount() {
        if (!isPreviewOverlayPresentInDom()) return 0;
        return driver.findElements(overlayItems).size();
    }

    public ProfilePage closePreviewOverlay() {
        if (!isPreviewOverlayPresentInDom()) return this;

        // Click X
        driver.findElement(overlayCloseX).click();

        wait.until(d -> !exists(previewOverlay));
        return this;
    }


    public ProfilePage cancelOrderIfPending(long orderId) {
        By cancelLink = By.cssSelector("a[href='/order/cancel/" + orderId + "']");
        if (driver.findElements(cancelLink).isEmpty()) {
            return this;
        }

        String oldUrl = driver.getCurrentUrl();
        driver.findElement(cancelLink).click();
        wait.until(d -> !d.getCurrentUrl().equals(oldUrl) || d.getPageSource() != null);
        return this;
    }

    public ProductsPage startShopping() {
        if (startShoppingButtons.isEmpty())
            throw new IllegalStateException("Start shopping button not visible (orders might exist).");

        click(startShoppingButtons.get(0));
        return new ProductsPage(driver).assertLoaded();
    }

}
