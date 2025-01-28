package com.example.shop_order.functionalTests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

// Strona wyboru typu klienta
public class CustomerTypePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "individual")
    private WebElement individualRadio;

    @FindBy(id = "company")
    private WebElement companyRadio;

    @FindBy(css = "button[type='submit']")
    private WebElement nextButton;

    public CustomerTypePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    public void selectIndividualCustomer() {
        individualRadio.click();
    }

    public void selectCompanyCustomer() {
        companyRadio.click();
    }

    public void clickNext() {
        nextButton.click();
    }
}

