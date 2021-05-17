package it.polito.ezshop.credit_card_circuit;

public class CreditCard {
    private String code;

    public CreditCard(String code, Integer balance) {
        this.code = code;
        this.balance = balance;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    private Integer balance;



}
