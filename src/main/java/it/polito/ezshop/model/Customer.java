package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.InvalidCustomerNameException;

import java.util.Objects;

public class Customer {
    private Integer id;
    private String customerName;
    private LoyaltyCard card;

    public Customer(Integer id, String customerName, LoyaltyCard loyaltyCard) throws InvalidCustomerIdException,
            InvalidCustomerNameException {
        validateId(id);
        validateName(customerName);

        this.id = id;
        this.customerName = customerName;
        this.card = loyaltyCard;
    }

    public static void validateId(Integer id) throws InvalidCustomerIdException {
        if (id == null || id <= 0) {
            throw new InvalidCustomerIdException("Customer ID must be a non-null positive number");
        }
    }

    public static void validateName(String name) throws InvalidCustomerNameException {
        if (name == null || name.equals("")) {
            throw new InvalidCustomerNameException("Customer name must be a non-null non-empty string");
        }
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(String customerName) throws InvalidCustomerNameException {
        validateName(customerName);
        this.customerName = customerName;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) throws InvalidCustomerIdException {
        validateId(id);
        this.id = id;
    }

    public LoyaltyCard getCard() {
        return card;
    }

    public void setCard(LoyaltyCard card) {
        this.card = card;
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
