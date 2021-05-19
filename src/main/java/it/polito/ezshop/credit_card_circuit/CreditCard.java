package it.polito.ezshop.credit_card_circuit;

import java.util.Objects;

public class CreditCard {
    private final String code;
    private Double balance;

    public CreditCard(String code, Double balance) {
        this.code = code;
        this.balance = balance;
    }

    public String getCode() {
        return code;
    }

    public Double getBalance() {
        return balance;
    }

    private void setBalance(Double balance) {
        this.balance = balance;
    }

    /**
     * Check if the requested amount of balance is available in the credit card text file
     *
     * @param requestedAmount amount of money required
     * @return true iff the current balance allows for the requested amount to be spent
     */
    public boolean checkAvailability(double requestedAmount) {
        if (requestedAmount < 0) {
            return false;
        }

        return requestedAmount <= this.balance;
    }

    /**
     * change the balance with updateAmount
     *
     * @param updateAmount amount of money required to the change balance
     * @return true iff the current balance has been changed successfully otherwise false
     */
    public boolean updateBalance(double updateAmount) {
        if (updateAmount < 0 && !checkAvailability(-updateAmount)) {
            return false;
        }

        setBalance(this.balance + updateAmount);
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s;%.2f", this.code, this.balance).replace(",", ".");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditCard that = (CreditCard) o;
        return Objects.equals(code, that.code) && Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, balance);
    }
}
