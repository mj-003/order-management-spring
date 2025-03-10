package com.example.shop_order.functionalTests.pagesTests;


import com.example.shop_order.functionalTests.BaseSeleniumTest;
import com.example.shop_order.functionalTests.pages.CustomerDetailsPage;
import com.example.shop_order.functionalTests.pages.CustomerTypePage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

class IndividualFormTest extends BaseSeleniumTest {

    @Test
    void testCompleteOrderProcessForIndividualCustomer() {
        driver.get(BASE_URL + "/order/simulate/testSmall");
        waitForPageLoad();

        // Select individual customer type
        CustomerTypePage customerTypePage = new CustomerTypePage(driver, wait);
        customerTypePage.selectIndividualCustomer();
        customerTypePage.clickNext();
        waitForPageLoad();

        // Debug info
        System.out.println("Current URL before form: " + driver.getCurrentUrl());

        CustomerDetailsPage detailsPage = new CustomerDetailsPage(driver, wait);
        detailsPage.fillIndividualForm(
                "Jan",
                "Testowy",
                "jan.testowy@example.com",
                "123456789"
        );

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        System.out.println("Form ready for submission");
        System.out.println("Form values before submission:");
        System.out.println("firstName: " + driver.findElement(By.name("firstName")).getAttribute("value"));
        System.out.println("lastName: " + driver.findElement(By.name("lastName")).getAttribute("value"));
        System.out.println("email: " + driver.findElement(By.name("email")).getAttribute("value"));
        System.out.println("phoneNumber: " + driver.findElement(By.name("phoneNumber")).getAttribute("value"));

        detailsPage.clickNext();

        wait.until(ExpectedConditions.urlContains("/order/delivery"));
        assertTrue(driver.getCurrentUrl().contains("/order/delivery"),
                "Should be redirected to delivery page. Current URL: " + driver.getCurrentUrl());
    }

    @Test
    void testFormValidationForEmptyFields() {
        driver.get(BASE_URL + "/order/simulate/testSmall");
        waitForPageLoad();

        CustomerTypePage customerTypePage = new CustomerTypePage(driver, wait);
        customerTypePage.selectIndividualCustomer();
        customerTypePage.clickNext();
        waitForPageLoad();

        CustomerDetailsPage detailsPage = new CustomerDetailsPage(driver, wait);
        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        );
        submitButton.click();

        WebElement firstNameInput = driver.findElement(By.name("firstName"));
        WebElement lastNameInput = driver.findElement(By.name("lastName"));
        WebElement emailInput = driver.findElement(By.name("email"));
        WebElement phoneInput = driver.findElement(By.name("phoneNumber"));

        assertNotNull(firstNameInput.getAttribute("required"), "Pole firstName powinno być wymagane");
        assertNotNull(lastNameInput.getAttribute("required"), "Pole lastName powinno być wymagane");
        assertNotNull(emailInput.getAttribute("required"), "Pole email powinno być wymagane");
        assertNotNull(phoneInput.getAttribute("required"), "Pole phoneNumber powinno być wymagane");

        assertTrue(firstNameInput.getAttribute("value").isEmpty(),
                "Pole firstName powinno być puste");
        assertTrue(lastNameInput.getAttribute("value").isEmpty(),
                "Pole lastName powinno być puste");
        assertTrue(emailInput.getAttribute("value").isEmpty(),
                "Pole email powinno być puste");
        assertTrue(phoneInput.getAttribute("value").isEmpty(),
                "Pole phoneNumber powinno być puste");

        assertTrue(driver.getCurrentUrl().contains("/order/details"),
                "Powinniśmy pozostać na stronie details po nieudanej walidacji");

        assertTrue(firstNameInput.getAttribute("validationMessage") != null &&
                        !firstNameInput.getAttribute("validationMessage").isEmpty(),
                "Pole firstName powinno pokazywać komunikat o błędzie");
    }

    @Test
    void testFormValidationForPartiallyFilledForm() {
        driver.get(BASE_URL + "/order/simulate/testSmall");
        waitForPageLoad();

        CustomerTypePage customerTypePage = new CustomerTypePage(driver, wait);
        customerTypePage.selectIndividualCustomer();
        customerTypePage.clickNext();
        waitForPageLoad();

        WebElement firstNameInput = driver.findElement(By.name("firstName"));
        WebElement emailInput = driver.findElement(By.name("email"));

        firstNameInput.sendKeys("Jan");
        emailInput.sendKeys("niepoprawny_email"); // niepoprawny format emaila

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        WebElement lastNameInput = driver.findElement(By.name("lastName"));
        WebElement phoneInput = driver.findElement(By.name("phoneNumber"));

        assertTrue(lastNameInput.getAttribute("value").isEmpty(),
                "Pole lastName powinno być puste");
        assertTrue(phoneInput.getAttribute("value").isEmpty(),
                "Pole phoneNumber powinno być puste");

        assertTrue(driver.getCurrentUrl().contains("/order/details"),
                "Powinniśmy pozostać na stronie details po nieudanej walidacji");

        assertEquals("Jan", firstNameInput.getAttribute("value"),
                "Wartość pola firstName powinna zostać zachowana");
        assertEquals("niepoprawny_email", emailInput.getAttribute("value"),
                "Wartość pola email powinna zostać zachowana");
    }
}