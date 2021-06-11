package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidRFIDException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.polito.ezshop.utils.Utils.*;

public class ReturnTransactionItem {

    private ProductType productType;
    private final double pricePerUnit;
    private final List<String> RFIDs = new ArrayList<>();

    public ReturnTransactionItem(ProductType productType, int amount, double pricePerUnit) throws IllegalArgumentException {
        Objects.requireNonNull(productType);

        this.productType = productType;
        this.pricePerUnit = pricePerUnit;

        this.increaseAmount(amount);
    }

    public ReturnTransactionItem(ProductType productType, double pricePerUnit) {
        Objects.requireNonNull(productType);

        this.productType = productType;
        this.pricePerUnit = pricePerUnit;
    }

    /**
     * Increase amount of product in return transaction item by adding dummy RFIDs to list of RFIDs
     *
     * @param amount number of products that should be added to this return transaction item
     */
    public void increaseAmount(int amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Can only increase number of products in a return transaction item by a positive amount.");
        }
        while (amount > 0) {
            this.RFIDs.add(DUMMY_RFID);
            amount--;
        }
    }

    public void addRFID(String RFID) throws InvalidRFIDException {
        validateRFID(RFID);
        this.RFIDs.add(RFID);
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
        return this.RFIDs.size();
    }

    public double getPricePerUnit() {
        return this.pricePerUnit;
    }

    public double computeValue() {
        return this.getAmount() * this.pricePerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnTransactionItem that = (ReturnTransactionItem) o;
        return Double.compare(that.pricePerUnit, pricePerUnit) == 0
                && RFIDs.equals(that.RFIDs)
                && Objects.equals(productType, that.productType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productType, pricePerUnit, RFIDs);
    }
}