package finki.ukim.mk.onlineclothingstore.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class HomePage extends BasePage {
    @FindBy(css = "header .navbar")
    protected WebElement navbarBrand;
    protected final By navbarLocator = By.cssSelector("header .navbar");

    public HomePage(WebDriver driver) {
        super(driver);
    }
}
