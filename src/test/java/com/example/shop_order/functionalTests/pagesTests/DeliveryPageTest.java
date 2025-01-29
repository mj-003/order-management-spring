package com.example.shop_order.functionalTests.pagesTests;

import com.example.shop_order.functionalTests.BaseSeleniumTest;
import com.example.shop_order.functionalTests.pages.CustomerDetailsPage;
import com.example.shop_order.functionalTests.pages.CustomerTypePage;
import com.example.shop_order.functionalTests.pages.DeliveryPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class DeliveryPageTest extends BaseSeleniumTest {

    private void goToDeliveryPage() {
        driver.get(BASE_URL + "/order/simulate/testSmall");
        waitForPageLoad();

        CustomerTypePage customerTypePage = new CustomerTypePage(driver, wait);
        customerTypePage.selectIndividualCustomer();
        customerTypePage.clickNext();

        CustomerDetailsPage detailsPage = new CustomerDetailsPage(driver, wait);
        detailsPage.fillIndividualForm(
                "Jan",
                "Testowy",
                "jan.testowy@example.com",
                "123456789"
        );
        detailsPage.clickNext();
        wait.until(ExpectedConditions.urlContains("/order/delivery"));
    }

    @Test
    void testInpostDelivery() {
        goToDeliveryPage();

        DeliveryPage deliveryPage = new DeliveryPage(driver, wait);
        deliveryPage.selectInpostDelivery();

        assertTrue(driver.findElement(By.id("inpostSelection")).isDisplayed());

        deliveryPage.selectInpostPoint("WAW123");
        driver.findElement(By.id("inpostPhone")).sendKeys("123456789");

        deliveryPage.clickNext();

        assertTrue(driver.getCurrentUrl().contains("/order/payment"));
    }

    @Test
    void testPickupDelivery() {
        goToDeliveryPage();

        DeliveryPage deliveryPage = new DeliveryPage(driver, wait);
        deliveryPage.selectPickupDelivery();

        assertTrue(driver.findElement(By.id("pickupPointSelection")).isDisplayed());

        deliveryPage.selectPickupPoint("SHOP1");
        deliveryPage.clickNext();

        assertTrue(driver.getCurrentUrl().contains("/order/payment"));
    }

    @Test
    void testDeliveryFormValidation() {
        goToDeliveryPage();
        DeliveryPage deliveryPage = new DeliveryPage(driver, wait);

        deliveryPage.selectCourierDelivery();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("courierAddressForm")));

        deliveryPage.clickNext();
        assertTrue(driver.getCurrentUrl().contains("/order/delivery"),
                "Powinniśmy pozostać na stronie dostawy gdy formularz jest pusty");

        deliveryPage.fillDeliveryAddress(
                "Testowa",
                "",  // pusty numer budynku
                "00-123",
                "Warszawa",
                "123456789"
        );
        deliveryPage.clickNext();
        assertTrue(driver.getCurrentUrl().contains("/order/delivery"),
                "Powinniśmy pozostać na stronie dostawy gdy brakuje numeru budynku");

        deliveryPage.fillDeliveryAddress(
                "Testowa",
                "123",
                "00-123",
                "Warszawa",
                "123456789"
        );
        deliveryPage.clickNext();

        wait.until(ExpectedConditions.urlContains("/order/payment"));
        assertTrue(driver.getCurrentUrl().contains("/order/payment"),
                "Powinniśmy przejść do strony płatności po poprawnym wypełnieniu formularza");
    }

    @Test
    void testDeliveryCostCalculation() {
        goToDeliveryPage();
        DeliveryPage deliveryPage = new DeliveryPage(driver, wait);

        // Test kosztu dla kuriera
        deliveryPage.selectCourierDelivery();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("courierAddressForm")));
        WebElement courierCostLabel = driver.findElement(
                By.cssSelector("label[for='courier'] .d-flex span:last-child")
        );
        assertEquals("12,99 zł", courierCostLabel.getText().trim());

        // Test kosztu dla InPost
        deliveryPage.selectInpostDelivery();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inpostSelection")));
        WebElement inpostCostLabel = driver.findElement(
                By.cssSelector("label[for='inpost'] .d-flex span:last-child")
        );
        assertEquals("9,99 zł", inpostCostLabel.getText().trim());

        // Test kosztu dla odbioru osobistego
        deliveryPage.selectPickupDelivery();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickupPointSelection")));
        WebElement pickupCostLabel = driver.findElement(
                By.cssSelector("label[for='pickup'] .d-flex span:last-child")
        );
        assertEquals("0,00 zł", pickupCostLabel.getText().trim());
    }
}