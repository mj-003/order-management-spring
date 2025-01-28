package com.example.shop_order.functionalTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class BaseSeleniumTest {
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver()
                .clearDriverCache()
                .clearResolutionCache()
                .setup();
    }
    @BeforeEach
    void init() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--ignore-certificate-errors");
//        options.addArguments("--headless=new");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill-keyboard-accessory-view");
        options.setExperimentalOption("prefs", Map.of(
                "profile.password_manager_enabled", false,
                "credentials_enable_service", false,
                "profile.default_content_setting_values.notifications", 2
        ));

        // Add these new options
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        try {
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
            driver.manage().window().maximize();
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        } catch (Exception e) {
            System.out.println("Exception during driver initialization: " + e.getMessage());
            if (driver != null) {
                driver.quit();
            }
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Pomocnicza metoda do sprawdzania czy strona się załadowała
    protected void waitForPageLoad() {
        try {
            Thread.sleep(1000); // Krótkie opóźnienie dla stabilności
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


