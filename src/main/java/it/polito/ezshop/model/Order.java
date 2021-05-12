package it.polito.ezshop.model;

import java.time.LocalDate;
import java.util.Objects;

public class Order extends BalanceOperation {

    private String productCode;
    private double pricePerUnit;
    private int quantity;

    public Order(int balanceId, LocalDate date, double money, OperationStatus status, String productCode,
                 double pricePerUnit, int quantity) {
        super(balanceId, date, money, TYPE_ORDER, status);

        Objects.requireNonNull(productCode, "productCode must not be null");

        this.productCode = productCode;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
    }

    public String getProductCode() {
        return this.productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public double getPricePerUnit() {
        return this.pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return Double.compare(order.pricePerUnit, pricePerUnit) == 0 &&
                quantity == order.quantity &&
                productCode.equals(order.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), productCode, pricePerUnit, quantity);
    }
}
