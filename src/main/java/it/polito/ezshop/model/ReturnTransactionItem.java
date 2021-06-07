package it.polito.ezshop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReturnTransactionItem {

    private ProductType productType;
    private final double pricePerUnit;
    private final int amount;

    private List<String> rfids = new ArrayList<>();

    public ReturnTransactionItem(ProductType productType, int amount, double pricePerUnit) {
        Objects.requireNonNull(productType);

        this.productType = productType;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getBarCode() {
        return this.productType.getBarCode();
    }

    public int getAmount() {
        return this.amount;
    }

    public double getPricePerUnit() {
        return this.pricePerUnit;
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