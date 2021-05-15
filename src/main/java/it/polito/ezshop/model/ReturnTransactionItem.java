package it.polito.ezshop.model;

import java.util.Objects;

public class ReturnTransactionItem {

    private final ProductType productType;
    private double pricePerUnit;
    private int amount;

    public ReturnTransactionItem(ProductType productType, int amount, double pricePerUnit) {
        this.productType = productType;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
    }

    public String getBarCode() {
        return this.productType.getBarCode();
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPricePerUnit() {
        return this.pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public double computeValue() {
        return this.amount * this.pricePerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnTransactionItem that = (ReturnTransactionItem) o;
        return Double.compare(that.pricePerUnit, pricePerUnit) == 0
                && amount == that.amount
                && Objects.equals(productType, that.productType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productType, pricePerUnit, amount);
    }
}