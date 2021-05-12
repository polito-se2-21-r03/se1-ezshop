package it.polito.ezshop.model;

import java.time.LocalDate;

public class Credit extends BalanceOperation {
    private String nameAndSurname;
    private String cardNumber;
    private Integer validBalance;

    public String getNameAndSurname() {
        return nameAndSurname;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Integer getValidBalance() {
        return validBalance;
    }

    public Credit(String nameAndSurname, String cardNumber, Integer validBalance) {
        this.nameAndSurname = nameAndSurname;
        this.cardNumber = cardNumber;
        this.validBalance = validBalance;
    }

}
