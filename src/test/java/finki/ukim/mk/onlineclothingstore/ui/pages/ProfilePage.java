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

    // Recent orders table may or may not exist (thymeleaf if)
    @FindBy(css = "table.table")
    private List<WebElement> ordersTables; // list -> safe if not present

    @FindBy(css = "tbody tr")
    private List<WebElement> orderRows; // safe if no orders (empty)

    // No-orders block exists only when orders empty
    @FindBy(css = ".card-body.text-center.py-5")
    private List<WebElement> noOrdersBlocks;

    @FindBy(css = "a[href='/products'].btn.btn-primary")
    private List<WebElement> startShoppingButtons; // only in no-orders block

    /* -------------------- DYNAMIC OVERLAY (CREATED BY JS) -------------------- */

    private final By previewOverlay = By.id("orderPreviewOverlay");                 // created dynamically
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

    /* -------------------- ORDERS STATE -------------------- */

    public boolean hasOrders() {
        return !orderRows.isEmpty();
    }

    public boolean isNoOrdersBlockShown() {
        return !noOrdersBlocks.isEmpty() && noOrdersBlocks.get(0).isDisplayed();
    }

    public int ordersCount() {
        return orderRows.size();
    }

    /* -------------------- PREVIEW BUTTONS -------------------- */

    public ProfilePage openPreviewForOrderId(long orderId) {
        By previewBtn = By.cssSelector("button.preview-order[data-order-id='" + orderId + "']");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(previewBtn));
        btn.click();

        // overlay gets created + class ord-show added
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

    /* -------------------- PREVIEW OVERLAY READ -------------------- */

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
        return driver.findElement(overlayTitle).getText().trim(); // "Order #123"
    }

    public String getPreviewSubtitle() {
        if (!isPreviewOverlayPresentInDom()) return "";
        return driver.findElement(overlaySubtitle).getText().trim(); // "2 item(s)"
    }

    public int getPreviewItemsCount() {
        if (!isPreviewOverlayPresentInDom()) return 0;
        return driver.findElements(overlayItems).size();
    }

    public ProfilePage closePreviewOverlay() {
        if (!isPreviewOverlayPresentInDom()) return this;

        // Click X
        driver.findElement(overlayCloseX).click();

        // closePreviewOrder() removes overlay from DOM
        wait.until(d -> !exists(previewOverlay));
        return this;
    }

    /* -------------------- CANCEL (ONLY WHEN PENDING) -------------------- */

    public ProfilePage cancelOrderIfPending(long orderId) {
        // Cancel link exists only when status is PENDING (thymeleaf th:if)
        By cancelLink = By.cssSelector("a[href='/order/cancel/" + orderId + "']");
        if (driver.findElements(cancelLink).isEmpty()) {
            // Not pending (or cancel not available)
            return this;
        }

        String oldUrl = driver.getCurrentUrl();
        driver.findElement(cancelLink).click();

        // Usually redirect happens. If not, at least wait for URL or page refresh.
        wait.until(d -> !d.getCurrentUrl().equals(oldUrl) || d.getPageSource() != null);
        return this;
    }

    /* -------------------- NO-ORDERS CTA -------------------- */

    public ProductsPage startShopping() {
        if (startShoppingButtons.isEmpty())
            throw new IllegalStateException("Start shopping button not visible (orders might exist).");

        click(startShoppingButtons.get(0));
        return new ProductsPage(driver).assertLoaded();
    }
}
