package finki.ukim.mk.onlineclothingstore.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;


    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    protected WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }


    protected Boolean waitForElementInvisible(By locator){
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected Boolean waitForElementInvisible(WebElement element){
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    protected WebElement waitForElementVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitForElementClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitForElementPresence(By locator){
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected boolean exists(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected void sendKeys(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    protected void sendKeys(By locator, String text) {
        WebElement el = waitForElementVisible(locator);
        el.clear();
        el.sendKeys(text);
    }



    protected void click(WebElement element) {
        waitForElementClickable(element).click();
    }

    protected void click(By locator) {
        waitForElementClickable(locator).click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }


    public static void get(WebDriver driver, String relativeUrl) {
        String url = "http://localhost:9999" + relativeUrl;
        driver.get(url);
    }
}
