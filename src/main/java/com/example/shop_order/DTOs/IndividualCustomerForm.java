package com.example.shop_order.DTOs;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IndividualCustomerForm extends CustomerForm {
    @NotBlank(message = "Imię jest wymagane")
    private String firstName;

    @NotBlank(message = "Nazwisko jest wymagane")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString() {
        return "IndividualCustomerForm(firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ")";
    }
}
