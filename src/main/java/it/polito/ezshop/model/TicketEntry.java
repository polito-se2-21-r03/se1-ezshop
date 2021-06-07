package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidQuantityException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TicketEntry {

    private ProductType productType;
    private double pricePerUnit;
    private int amount; // = 3
    private double discountRate;

    private List<String> rfids = new ArrayList<>(); // rfids = { "1", "2", "3"}

    public TicketEntry(ProductType productType, int amount) throws InvalidQuantityException {
        // verify productType is not null
        Objects.requireNonNull(productType);

        // validate amount
        validateAmount(amount);

        this.productType = productType;
        this.amount = amount;
        this.pricePerUnit = productType.getPricePerUnit();
        this.discountRate = 0.0;
    }

    public TicketEntry(ProductType productType, int amount, double discount) throws InvalidQuantityException,
            InvalidDiscountRateException {
        // verify productType is not null
        Objects.requireNonNull(productType);

        // validate amount and discount
        validateAmount(amount);
        validateDiscount(discount);

        this.productType = productType;
        this.amount = amount;
        this.pricePerUnit = productType.getPricePerUnit();
        this.discountRate = discount;
    }

    public static void validateAmount(int amount) throws InvalidQuantityException {
        if (amount < 0) {
            throw new InvalidQuantityException();
        }
    }

    public static void validateDiscount(double discount) throws InvalidDiscountRateException {
        if (discount < 0 || discount >= 1.0) {
            throw new InvalidDiscountRateException();
        }
    }

    private static void validatePrice(double pricePerUnit) throws InvalidPricePerUnitException {
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException();
        }
    }

    public double computeTotal() {
        return (1.0 - discountRate) * amount * pricePerUnit;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) throws InvalidQuantityException {
        validateAmount(amount);
        this.amount = amount;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) throws InvalidDiscountRateException {
        validateDiscount(discountRate);
        this.discountRate = discountRate;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) throws InvalidPricePerUnitException {
        validatePrice(pricePerUnit);
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
