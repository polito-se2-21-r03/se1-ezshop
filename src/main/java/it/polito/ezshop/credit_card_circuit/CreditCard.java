package it.polito.ezshop.credit_card_circuit;

import it.polito.ezshop.model.*;
import it.polito.ezshop.utils.Utils;
import it.polito.ezshop.credit_card_circuit.TextualCreditCardCircuit.*;

public class CreditCard {
    private String code;

    public CreditCard(String code, Double balance) {
        this.code = code;
        this.balance = balance;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    private Double balance;

    /**
     * Check if the requested amount of balance is available in the credit card text file
     *
     * @param requestedAmount amount of money required
     * @return true iff the current balance allows for the requested amount to be spent
     */
    public boolean checkAvailability(double requestedAmount) {
        return requestedAmount <= this.balance;
    }

    /**
     * change the balance with updateAmount
     *
     * @param updateAmount amount of money required to the change balance
     * @return true iff the current balance has been changed successfully otherwise false
     */
    public boolean updateBalance(double updateAmount) {

        setBalance(this.balance + updateAmount);
        return true;
    }
}
