package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.InvalidCustomerNameException;

import java.util.Objects;

public class Customer {
    private final Integer id;
    private String customerName;
    private LoyaltyCard card;

    public Customer(Integer id, String customerName) throws InvalidCustomerNameException, InvalidCustomerIdException {
        validateID(id);
        this.id = id;
        this.setCustomerName(customerName);
        this.setCard(null);
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

    public void setCard(LoyaltyCard card){
            this.card = card;
        }

    public LoyaltyCard getCard() {
        return card;
    }

    public static void validateID(Integer id) throws InvalidCustomerIdException {
        if (!isValidID(id)) {
            throw new InvalidCustomerIdException("The customer id must be a non-null positive integer");
        }
    }

    public static boolean isValidID(Integer id) {
        return id != null && id > 0;
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
