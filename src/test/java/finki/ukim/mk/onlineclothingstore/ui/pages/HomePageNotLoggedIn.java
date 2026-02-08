package finki.ukim.mk.onlineclothingstore.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePageNotLoggedIn extends HomePage {
    @FindBy(css = "a[href='/login']")
    private WebElement loginLink;

    @FindBy(css = "a[href='/register']")
    private WebElement registerLink;

    public HomePageNotLoggedIn(WebDriver driver) {
        super(driver);
    }

    public HomePageNotLoggedIn assertLoaded() {
       waitForElementVisible(navbarLocator);
        return this;
    }

    public LoginPage navigateToLoginPage(){
        click(loginLink);
        return new LoginPage(driver).assertLoaded();
    }

    public RegisterPage navigateToRegisterPage(){
        click(registerLink);
        return new RegisterPage(driver).assertLoaded();
    }

}

