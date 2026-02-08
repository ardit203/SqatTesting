package finki.ukim.mk.onlineclothingstore.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ProductsPage extends BasePage {

    @FindBy(css = "a[href='/cart']")
    private WebElement cartLink;

    @FindBy(css = "a[href='/profile']")
    private WebElement profileLink;

    @FindBy(css = "form[action='/products']")
    private WebElement filterForm;

    private By filterLocator = By.cssSelector("form[action='/products']");

    @FindBy(css = "form[action='/products'] input[name='name']")
    private WebElement searchInput;

    @FindBy(css = "form[action='/products'] select[name='categoryId']")
    private WebElement categorySelect;

    @FindBy(css = "form[action='/products'] select[name='department']")
    private WebElement departmentSelect;

    @FindBy(css = "form[action='/products'] select[name='sort']")
    private WebElement sortSelect;

    @FindBy(css = "form[action='/products'] input[name='greaterThan']")
    private WebElement minPriceInput;

    @FindBy(css = "form[action='/products'] input[name='lessThan']")
    private WebElement maxPriceInput;

    @FindBy(css = "form[action='/products'] button.btn.btn-primary.w-100")
    private WebElement applyFiltersBtn;

    @FindBy(css = ".product-card")
    private List<WebElement> productCards;

    private final By overlay = By.id("variantOverlay");
    private final By overlayClose = By.cssSelector("#variantOverlay .close-products-overlay");
    private final By overlayQty = By.id("quantity-product");
    private final By overlayVariantButtons = By.cssSelector("#variantOverlay .check-before-request-btn");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public ProductsPage assertLoaded() {
        waitForElementVisible(filterLocator);
        return this;
    }


    public ProductsPage setSearch(String name) {
        if(name==null){
            return this;
        }
        sendKeys(searchInput, name);
        return this;
    }

    public ProductsPage selectCategoryByVisibleText(String categoryName) {
        if(categoryName==null){
            return this;
        }
        new Select(categorySelect).selectByVisibleText(categoryName);
        return this;
    }

    public ProductsPage selectDepartmentByVisibleText(String department) {
        if(department==null){
            return this;
        }
        new Select(departmentSelect).selectByVisibleText(department);
        return this;
    }

    public ProductsPage sortByValue(String sortValue) {
        if(sortValue==null){
            return this;
        }
        // values: byPriceAsc, byPriceDesc
        new Select(sortSelect).selectByValue(sortValue);
        return this;
    }

    public ProductsPage setPriceRange(Integer min, Integer max) {
        if (min != null) sendKeys(minPriceInput, String.valueOf(min));
        if (max != null) sendKeys(maxPriceInput, String.valueOf(max));
        return this;
    }

    public ProductsPage applyFilters(String name, String categoryName, String department, String sort, Integer min, Integer max) {
        setSearch(name);
        selectCategoryByVisibleText(categoryName);
        selectDepartmentByVisibleText(department);
        sortByValue(sort);
        setPriceRange(min, max);
        click(applyFiltersBtn);

        waitForElementVisible(filterLocator);
        return this;
    }

    public int productCount() {
        return productCards.size();
    }

    public ProductDetailsPage navigateToProductDetails(String productName) {
        WebElement card = findCardByName(productName);
        WebElement detailsLink = card.findElement(By.cssSelector("a[href^='/products/details/']"));
        click(detailsLink);
        return new ProductDetailsPage(driver).assertLoaded();
    }

    public CartPage navigateToCart(){
        click(cartLink);
        return new CartPage(driver).assertLoaded();
    }

    public ProductsPage clickAddToCartByProductName(String productName) {
        WebElement card = findCardByName(productName);
        WebElement addBtn = card.findElement(By.cssSelector("button.add-to-cart-from-products-btn"));
        click(addBtn);
        waitForElementPresence(overlay);
        return this;
    }

    public ProductsPage addToCartFromOverlay(String productName, String size, int quantity) {
        clickAddToCartByProductName(productName);

        WebElement qtyInput = waitForElementVisible(overlayQty);
        qtyInput.clear();
        qtyInput.sendKeys(String.valueOf(quantity));

        By variantBtnBySize = By.cssSelector("#variantOverlay .check-before-request-btn[data-size='" + size + "']");
        WebElement variantBtn = waitForElementClickable(variantBtnBySize);
        variantBtn.click();

        waitForElementInvisible(overlay);
        return this;
    }

    public ProductsPage closeOverlayIfOpen() {
        if (!driver.findElements(overlay).isEmpty()) {
            driver.findElement(overlayClose).click();
            waitForElementInvisible(overlay);
        }
        return this;
    }

    private WebElement findCardByName(String productName) {
        if (productCards.isEmpty())
            throw new IllegalStateException("No products are displayed on the Products page.");

        for (WebElement card : productCards) {
            String name = card.findElement(By.cssSelector(".card-title")).getText().trim();
            if (name.equalsIgnoreCase(productName.trim())) return card;
        }
        throw new NoSuchElementException("Product card not found for name: " + productName);
    }
}

