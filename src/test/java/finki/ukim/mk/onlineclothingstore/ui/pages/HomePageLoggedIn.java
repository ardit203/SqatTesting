package finki.ukim.mk.onlineclothingstore.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePageLoggedIn extends HomePage {
    @FindBy(css = "a[href='/cart']")
    private WebElement cartLink;

    @FindBy(css = "a[href='/profile']")
    private WebElement profileLink;

    @FindBy(css = "a[href='/logout']")
    private WebElement logoutLink;

    @FindBy(css = "a[href='/products']")
    private WebElement productsLink;

    public HomePageLoggedIn(WebDriver driver) {
        super(driver);
    }

    public HomePageLoggedIn assertLoaded() {
        waitForElementVisible(navbarLocator);
        return this;
    }

    public ProductsPage navigateToProductsPage(){
        click(productsLink);
        return new ProductsPage(driver).assertLoaded();
    }
}

