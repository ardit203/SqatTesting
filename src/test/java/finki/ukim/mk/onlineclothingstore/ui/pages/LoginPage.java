package finki.ukim.mk.onlineclothingstore.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Objects;

public class LoginPage extends BasePage{
    @FindBy(name = "username")
    private WebElement usernameInput;

    private final By usernameLocator = By.name("username");

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    private final By errorAlert = By.cssSelector(".alert.alert-danger");

    public LoginPage(WebDriver driver) {
        super(driver);
    }


    public LoginPage assertLoaded() {
       waitForElementVisible(usernameLocator);
        return this;
    }

    public boolean isErrorPresent() {
        return !driver.findElements(errorAlert).isEmpty();
    }

    public boolean isErrorVisible() {
        List<WebElement> els = driver.findElements(errorAlert);
        return !els.isEmpty() && els.get(0).isDisplayed();
    }

    public String getErrorMessage() {
        List<WebElement> els = driver.findElements(errorAlert);
        return els.isEmpty() ? "" : els.get(0).getText();
    }

    public HomePageLoggedIn loginValid(String username, String password){
        sendKeys(usernameInput, username);
        sendKeys(passwordInput, password);
        click(loginButton);

        wait.until(d -> Objects.requireNonNull(d.getCurrentUrl()).contains("/home"));
        return new HomePageLoggedIn(driver).assertLoaded();
    }

    public LoginPage loginInvalid(String username, String password) {
        sendKeys(usernameInput, username);
        sendKeys(passwordInput, password);
        click(loginButton);

        wait.until(d -> isErrorPresent());
        return this;
    }

}

