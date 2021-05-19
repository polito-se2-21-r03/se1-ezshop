package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidCustomerNameException;

import java.util.Objects;

public class Customer {
    private final int id;
    private String customerName;
    private LoyaltyCard card;

    public Customer(int id, String customerName) throws InvalidCustomerNameException {
        this.id = id;
        this.setCustomerName(customerName);
        this.setCard(null);
    }

    @Deprecated
    public Customer(int id, String customerName, LoyaltyCard loyaltyCard) throws InvalidCustomerNameException {
        this.id = id;
        setCustomerName(customerName);
        this.card = loyaltyCard;
    }

    @Deprecated
    public Customer(String customerName, String customerCard, int id, int points) {
        this.customerName = customerName;
        this.id = id;

        if (customerCard != null && !customerCard.equals("")) {
            this.card = new LoyaltyCard(customerCard, points);
        }
    }

    public Integer getId() {
        return this.id;
    }

    public void setCustomerName(String customerName) throws InvalidCustomerNameException {
        validateName(customerName);
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCard(LoyaltyCard card) {
        this.card = card;
    }

    public LoyaltyCard getCard() {
        return card;
    }

    public static void validateName(String name) throws InvalidCustomerNameException {
        if (!isValidName(name)) {
            throw new InvalidCustomerNameException("Customer name can not be null or empty");
        }
    }

    public static boolean isValidName(String name) {
        return name != null && !name.equals("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerName, customer.customerName) &&
                Objects.equals(card, customer.card) &&
                Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
