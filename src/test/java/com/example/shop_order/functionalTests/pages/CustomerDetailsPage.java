package com.example.shop_order.functionalTests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CustomerDetailsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CustomerDetailsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void fillIndividualForm(String firstName, String lastName, String email, String phoneNumber) {
        WebElement firstNameInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("firstName"))
        );
        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);

        WebElement lastNameInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("lastName"))
        );
        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);

        WebElement emailInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("email"))
        );
        emailInput.clear();
        emailInput.sendKeys(email);

        WebElement phoneInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("phoneNumber"))
        );
        phoneInput.clear();
        phoneInput.sendKeys(phoneNumber);
    }

    public void clickNext() {
        wait.until(driver -> {
            try {
                WebElement button = driver.findElement(By.cssSelector("button[type='submit']"));
                return button.isEnabled() && button.isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });

        System.out.println("About to click submit button");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/order/delivery"),
                ExpectedConditions.presenceOfElementLocated(By.className("error-message"))
        ));

        System.out.println("After click URL: " + driver.getCurrentUrl());
    }
}