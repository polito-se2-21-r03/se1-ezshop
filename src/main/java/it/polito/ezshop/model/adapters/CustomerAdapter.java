package it.polito.ezshop.model.adapters;

import it.polito.ezshop.model.Customer;

public class CustomerAdapter implements it.polito.ezshop.data.Customer {

    private final Customer customer;

    public CustomerAdapter(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String getCustomerName() {
        return customer.getCustomerName();
    }

    @Override
    public void setCustomerName(String customerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCustomerCard() {
        if (customer.getCard() == null) {
            return null;
        }

        return customer.getCard().getCode();
    }

    @Override
    public void setCustomerCard(String customerCard) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getId() {
        return customer.getId();
    }

    @Override
    public void setId(Integer id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getPoints() {
        if (customer.getCard() == null) {
            return 0;
        }

        return customer.getCard().getPoints();
    }

    @Override
    public void setPoints(Integer points) {
        throw new UnsupportedOperationException();
    }
}
