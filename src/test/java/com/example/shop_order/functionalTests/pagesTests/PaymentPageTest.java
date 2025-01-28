package com.example.shop_order.functionalTests.pagesTests;

import com.example.shop_order.functionalTests.BaseSeleniumTest;
import com.example.shop_order.functionalTests.pages.CustomerDetailsPage;
import com.example.shop_order.functionalTests.pages.CustomerTypePage;
import com.example.shop_order.functionalTests.pages.DeliveryPage;
import com.example.shop_order.functionalTests.pages.PaymentPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

class PaymentPageTest extends BaseSeleniumTest {
    private PaymentPage paymentPage;

    private void goToPaymentPage() {
        driver.get(BASE_URL + "/order/simulate/testSmall");
        waitForPageLoad();

        // Przejście przez typ klienta
        CustomerTypePage customerTypePage = new CustomerTypePage(driver, wait);
        customerTypePage.selectIndividualCustomer();
        customerTypePage.clickNext();

        // Wypełnienie danych klienta
        CustomerDetailsPage detailsPage = new CustomerDetailsPage(driver, wait);
        detailsPage.fillIndividualForm(
                "Jan",
                "Testowy",
                "jan.testowy@example.com",
                "123456789"
        );
        detailsPage.clickNext();

        // Wybór dostawy
        DeliveryPage deliveryPage = new DeliveryPage(driver, wait);
        deliveryPage.selectCourierDelivery();
        deliveryPage.fillDeliveryAddress(
                "Testowa",
                "123",
                "00-123",
                "Warszawa",
                "123456789"
        );
        deliveryPage.clickNext();

        // Inicjalizacja strony płatności
        paymentPage = new PaymentPage(driver, wait);
    }

    @Test
    void testPaymentMethodSelection() {
        goToPaymentPage();

        // Domyślnie powinien być wybrany BLIK
        assertTrue(driver.findElement(By.id("blik")).isSelected(),
                "BLIK powinien być domyślnie wybrany");

        // Test wyboru karty
        paymentPage.selectCardPayment();
        assertTrue(driver.findElement(By.id("card")).isSelected(),
                "Płatność kartą powinna być wybrana");

        // Test wyboru przelewu
        paymentPage.selectTransferPayment();
        assertTrue(driver.findElement(By.id("transfer")).isSelected(),
                "Przelew powinien być wybrany");
    }

    @Test
    void testPromoCode() {
        goToPaymentPage();

        // Test niepoprawnego kodu
        paymentPage.applyPromoCode("INVALID_CODE");

        // Poczekaj na pojawienie się komunikatu
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("promoCodeMessage")));
        assertTrue(paymentPage.isPromoCodeError(),
                "Powinien pojawić się komunikat błędu dla niepoprawnego kodu");

        // Test poprawnego kodu
        paymentPage.applyPromoCode("WELCOME10");

        // Poczekaj na zniknięcie błędu i pojawienie się rabatu
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("#promoCodeMessage.text-danger")
        ));

        // Poczekaj na odświeżenie strony
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".text-danger")
        ));

        assertTrue(paymentPage.isDiscountDisplayed(),
                "Rabat powinien być widoczny w podsumowaniu");
    }

    @Test
    void testLoyaltyPoints() {
        goToPaymentPage();

        // Test poprawnej liczby punktów
        paymentPage.applyLoyaltyPoints("100");
        assertFalse(paymentPage.isPointsError(),
                "Nie powinno być błędu dla poprawnej liczby punktów");
        assertTrue(paymentPage.isLoyaltyPointsDisplayed(),
                "Wykorzystane punkty powinny być widoczne w podsumowaniu");
    }

    @Test
    void testNegativeLoyaltyPoints() {
        goToPaymentPage();

        WebElement pointsInput = driver.findElement(By.id("loyaltyPoints"));
        pointsInput.sendKeys("-100");

        assertEquals("0", pointsInput.getAttribute("value"),
                "Liczba punktów nie powinna być ujemna");
    }

    @Test
    void testCompletePaymentProcess() {
        goToPaymentPage();

        // Wybierz metodę płatności
        paymentPage.selectBlikPayment();

        // Zastosuj kod rabatowy
        paymentPage.applyPromoCode("VALID_CODE");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".text-danger")
        ));

        // Zastosuj punkty
        paymentPage.applyLoyaltyPoints("100");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".text-danger")
        ));

        // Zatwierdź płatność
        paymentPage.clickPayButton();

        // Sprawdź czy przeszliśmy do potwierdzenia
        wait.until(ExpectedConditions.urlContains("/order/success"));
        assertTrue(driver.getCurrentUrl().contains("/order/success"),
                "Powinniśmy zostać przekierowani na stronę potwierdzenia");
    }

    @Test
    void testOrderSummaryUpdates() {
        goToPaymentPage();

        // Zapisz początkową sumę
        String initialTotal = paymentPage.getTotal();

        // Zastosuj kod rabatowy
        paymentPage.applyPromoCode("VALID_CODE");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".text-danger")
        ));

        // Sprawdź czy suma się zmieniła
        String afterPromoTotal = paymentPage.getTotal();
        assertNotEquals(initialTotal, afterPromoTotal,
                "Suma powinna się zmienić po zastosowaniu kodu rabatowego");

        // Zastosuj punkty
        paymentPage.applyLoyaltyPoints("100");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".text-danger")
        ));

        // Sprawdź czy suma się znowu zmieniła
        String finalTotal = paymentPage.getTotal();
        assertNotEquals(afterPromoTotal, finalTotal,
                "Suma powinna się zmienić po wykorzystaniu punktów");
    }
}
