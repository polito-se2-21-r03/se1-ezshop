package it.polito.ezshop.model;

import java.util.Objects;

public class TicketEntry {

    private final ProductType productType;
    private int amount;
    private double discountRate;
    private double pricePerUnit;

    public TicketEntry(ProductType productType, int amount, double pricePerUnit, double discount) {
        this.productType = productType;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.discountRate = discount;
    }

    public ProductType getProductType() {
        return productType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketEntry that = (TicketEntry) o;
        return amount == that.amount &&
                Double.compare(that.pricePerUnit, pricePerUnit) == 0 &&
                Double.compare(that.discountRate, discountRate) == 0 &&
                Objects.equals(productType, that.productType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productType, amount, discountRate);
    }
}
