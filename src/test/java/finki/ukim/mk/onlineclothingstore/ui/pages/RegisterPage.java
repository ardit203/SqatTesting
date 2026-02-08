package finki.ukim.mk.onlineclothingstore.ui.pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class RegisterPage extends BasePage {
    @FindBy(name = "name")
    private WebElement nameInput;

    @FindBy(name = "surname")
    private WebElement surnameInput;

    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(name = "repeatedPassword")
    private WebElement repeatedPasswordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement registerButton;

    private final By errorAlert = By.cssSelector(".alert.alert-danger");


    public RegisterPage(WebDriver driver) {
        super(driver);
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

    public RegisterPage assertLoaded() {
        waitForElementVisible(usernameInput);
        return this;
    }


    public LoginPage registerValid(String name, String surname, String username, String password, String repeatedPassword){
        sendKeys(nameInput, name);
        sendKeys(surnameInput, surname);
        sendKeys(usernameInput, username);
        sendKeys(passwordInput, password);
        sendKeys(repeatedPasswordInput, repeatedPassword);
        click(registerButton);
        return new LoginPage(driver).assertLoaded();
    }

    public RegisterPage registerInvalid(String name, String surname, String username, String password, String repeatedPassword){
        sendKeys(nameInput, name);
        sendKeys(surnameInput, surname);
        sendKeys(usernameInput, username);
        sendKeys(passwordInput, password);
        sendKeys(repeatedPasswordInput, repeatedPassword);
        click(registerButton);

        wait.until(d -> isErrorPresent());
        return this;
    }



}
