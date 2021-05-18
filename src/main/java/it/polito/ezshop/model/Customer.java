package it.polito.ezshop.model;

import java.util.Objects;

public class Customer {
    private int id;
    private String customerName;
    private LoyaltyCard card;

    public Customer(String customerName, int id, LoyaltyCard loyaltyCard) {
        this.customerName = customerName;
        this.id = id;
        this.card = loyaltyCard;
    }

    @Deprecated
    public Customer(String customerName, String customerCard, int id, int points) {
        this.customerName = customerName;
        this.id = id;

        if (customerCard != null && !customerCard.equals("")) {
            //this.card = new LoyaltyCard(customerCard, points);
        }
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
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
