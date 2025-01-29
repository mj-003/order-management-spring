package com.example.shop_order.functionalTests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
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
        WebElement pointsInput = driver.findElement(By.id("loyaltyPoints"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pointsInput);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        pointsInput.clear();
        pointsInput.sendKeys(points);

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
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pointsMessage")));
            WebElement message = driver.findElement(By.id("pointsMessage"));
            return message.getAttribute("class").contains("text-danger");
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public void clickPayButton() {
        WebElement payButton = driver.findElement(By.cssSelector("button[type='submit']"));
        payButton.click();
    }

    public String getTotal() {
        WebElement totalElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'd-flex')]//strong[contains(text(), 'zł')]")
        ));
        return totalElement.getText().replaceAll("[^0-9.]", ""); // Zostaw tylko liczby i kropkę
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
            Thread.sleep(1000);

            List<WebElement> spans = driver.findElements(By.tagName("span"));
            WebElement pointsLabel = spans.stream()
                    .filter(span -> span.getText().equals("Wykorzystane punkty:"))
                    .findFirst()
                    .orElse(null);

            if (pointsLabel != null) {
                WebElement parent = pointsLabel.findElement(By.xpath("./parent::div"));
                WebElement pointsValue = parent.findElement(By.className("text-success"));
                return pointsValue.isDisplayed() && !pointsValue.getText().isEmpty();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForPageReload() {
        WebElement oldBody = driver.findElement(By.tagName("body"));
        wait.until(ExpectedConditions.stalenessOf(oldBody));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    public void waitForSuccessfulOperation() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#promoCodeMessage.text-success")),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#pointsMessage.text-success"))
        ));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}