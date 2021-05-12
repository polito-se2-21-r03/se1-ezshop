package it.polito.ezshop.model;

import java.util.Objects;

public class Customer implements it.polito.ezshop.data.Customer {
    private String customerName;
    private String customerCard;
    private Integer id;
    private Integer points;

    public Customer(String customerName, String customerCard, Integer id, Integer points) {
        this.customerName = customerName;
        this.customerCard = customerCard;
        this.id = id;
        this.points = points;
    }

    @Override
    public String getCustomerName() {
        return this.customerName;
    }

    @Override
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String getCustomerCard() {
        return this.customerCard;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        this.customerCard = customerCard;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getPoints() {
        return this.points;
    }

    @Override
    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerName, customer.customerName) &&
                Objects.equals(customerCard, customer.customerCard) &&
                Objects.equals(id, customer.id) &&
                Objects.equals(points, customer.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
