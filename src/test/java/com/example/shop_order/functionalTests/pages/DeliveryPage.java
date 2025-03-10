package com.example.shop_order.functionalTests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DeliveryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;


    public DeliveryPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        this.js = (JavascriptExecutor) driver;
    }

    public void selectCourierDelivery() {
        WebElement courierRadio = wait.until(ExpectedConditions.elementToBeClickable(By.id("courier")));
        courierRadio.click();
    }

    public void selectInpostDelivery() {
        WebElement inpostRadio = wait.until(ExpectedConditions.elementToBeClickable(By.id("inpost")));
        inpostRadio.click();
    }

    public void selectPickupDelivery() {
        WebElement pickupRadio = wait.until(ExpectedConditions.elementToBeClickable(By.id("pickup")));
        pickupRadio.click();
    }

    public void fillDeliveryAddress(String street, String buildingNumber,
                                    String postalCode, String city, String phone) {
        WebElement streetInput = driver.findElement(By.id("street"));
        streetInput.clear();
        streetInput.sendKeys(street);

        WebElement buildingInput = driver.findElement(By.id("buildingNumber"));
        buildingInput.clear();
        buildingInput.sendKeys(buildingNumber);

        WebElement postalCodeInput = driver.findElement(By.id("postalCode"));
        postalCodeInput.clear();
        postalCodeInput.sendKeys(postalCode);

        WebElement cityInput = driver.findElement(By.id("city"));
        cityInput.clear();
        cityInput.sendKeys(city);

        WebElement phoneInput = driver.findElement(By.id("phone"));
        phoneInput.clear();
        phoneInput.sendKeys(phone);
    }

    public void selectInpostPoint(String pointId) {
        WebElement inpostSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("inpostPoint")));
        Select select = new Select(inpostSelect);
        select.selectByValue(pointId);
    }

    public void selectPickupPoint(String shopId) {
        WebElement pickupSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("pickupPoint")));
        Select select = new Select(pickupSelect);
        select.selectByValue(shopId);
    }

    public void clickNext() {
        WebElement nextButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        );
        // Przewiń do przycisku
        js.executeScript("arguments[0].scrollIntoView(true);", nextButton);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        nextButton.click();
    }

    public boolean isInputRequired(String fieldId) {
        WebElement input = driver.findElement(By.id(fieldId));
        return input.getAttribute("required") != null;
    }

    public void clearAndSetValue(String fieldId, String value) {
        WebElement input = driver.findElement(By.id(fieldId));
        input.clear();
        input.sendKeys(value);
    }

    public String getFieldValue(String fieldId) {
        WebElement input = driver.findElement(By.id(fieldId));
        return input.getAttribute("value");
    }

    public boolean areAllRequiredFieldsFilled() {
        return isInputRequired("street") && !getFieldValue("street").isEmpty() &&
                isInputRequired("buildingNumber") && !getFieldValue("buildingNumber").isEmpty() &&
                isInputRequired("postalCode") && !getFieldValue("postalCode").isEmpty() &&
                isInputRequired("city") && !getFieldValue("city").isEmpty() &&
                isInputRequired("phone") && !getFieldValue("phone").isEmpty();
    }

    public boolean isPostalCodeValid() {
        String postalCode = getFieldValue("postalCode");
        return postalCode.matches("\\d{2}-\\d{3}");
    }


}