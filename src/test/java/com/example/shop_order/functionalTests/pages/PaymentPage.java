package com.example.shop_order.functionalTests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.NoSuchElementException;

// Strona płatności
public class PaymentPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public PaymentPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void selectBlikPayment() {
        WebElement blikRadio = driver.findElement(By.id("blik"));
        blikRadio.click();
    }

    public void selectCardPayment() {
        WebElement cardRadio = driver.findElement(By.id("card"));
        cardRadio.click();
    }

    public void selectTransferPayment() {
        WebElement transferRadio = driver.findElement(By.id("transfer"));
        transferRadio.click();
    }

    public void applyPromoCode(String code) {
        WebElement promoInput = driver.findElement(By.id("promoCode"));
        promoInput.clear();
        promoInput.sendKeys(code);
        driver.findElement(By.id("applyPromoCode")).click();
    }

    public void applyLoyaltyPoints(String points) {
        // Najpierw przewiń do elementu
        WebElement pointsInput = driver.findElement(By.id("loyaltyPoints"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pointsInput);

        // Poczekaj chwilę na zakończenie przewijania
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wyczyść i wypełnij pole
        pointsInput.clear();
        pointsInput.sendKeys(points);

        // Znajdź przycisk i kliknij go przez JavaScript
        WebElement applyButton = driver.findElement(By.id("applyPoints"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyButton);
    }

    public String getPromoCodeMessage() {
        WebElement messageElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("promoCodeMessage"))
        );
        return messageElement.getText();
    }

    public String getPointsMessage() {
        WebElement messageElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("pointsMessage"))
        );
        return messageElement.getText();
    }

    public boolean isPromoCodeError() {
        WebElement messageElement = driver.findElement(By.id("promoCodeMessage"));
        return messageElement.getAttribute("class").contains("text-danger");
    }

    public boolean isPointsError() {
        WebElement messageElement = driver.findElement(By.id("pointsMessage"));
        return messageElement.getAttribute("class").contains("text-danger");
    }

    public void clickPayButton() {
        WebElement payButton = driver.findElement(By.cssSelector("button[type='submit']"));
        payButton.click();
    }

    public String getTotal() {
        WebElement totalElement = driver.findElement(
                By.cssSelector(".right-summary strong:last-child")
        );
        return totalElement.getText().replace(" zł", "");
    }

    public boolean isDiscountDisplayed() {
        try {
            return !driver.findElements(By.cssSelector(".text-danger")).isEmpty();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isLoyaltyPointsDisplayed() {
        try {
            return !driver.findElements(By.cssSelector(".text-success")).isEmpty();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}