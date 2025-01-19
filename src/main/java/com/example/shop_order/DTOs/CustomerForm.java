package com.example.shop_order.DTOs;

import com.example.shop_order.enums.DeliveryType;
import com.example.shop_order.model.Address;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class CustomerForm {
    @NotBlank(message = "Email jest wymagany")
    private String email;

    @NotBlank(message = "Numer telefonu jest wymagany")
    private String phoneNumber;

    @Valid
    private Address address;
    private String firstName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String toString() {
        return "CustomerForm(email=" + this.getEmail() + ", phoneNumber=" + this.getPhoneNumber() + ", address=" + this.getAddress() + ")";
    }


}

