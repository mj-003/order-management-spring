package com.example.shop_order.DTOs;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CompanyCustomerForm extends CustomerForm {
    @NotBlank(message = "Nazwa firmy jest wymagana")
    private String companyName;

    @NotBlank(message = "NIP jest wymagany")
    @Pattern(regexp = "\\d{10}", message = "Nieprawidłowy format NIP")
    private String nip;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String toString() {
        return "CompanyCustomerForm(companyName=" + this.getCompanyName() + ", nip=" + this.getNip() + ")";
    }

}
