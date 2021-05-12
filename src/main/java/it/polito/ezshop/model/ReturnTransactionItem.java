package it.polito.ezshop.model;

import it.polito.ezshop.data.ProductType;

import java.util.Objects;

public class ReturnTransactionItem{

    private final ProductType productType;
    private int Amount;

    public ReturnTransactionItem(ProductType productType, int amount) {
        this.productType = productType;
        this.Amount = amount;
    }

    public String getBarCode() {
        return this.productType.getBarCode();
    }

    public String getProductDescription() {
        return this.productType.getProductDescription();
    }

    public int getAmount() {
        return this.Amount;
    }

    public void setAmount(int amount) {
        this.Amount = amount;
    }

    public double getPricePerUnit() {
        return this.productType.getPricePerUnit();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnTransactionItem that = (ReturnTransactionItem) o;
        return Amount == that.Amount &&
                Objects.equals(productType, that.productType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productType, Amount);
    }
}