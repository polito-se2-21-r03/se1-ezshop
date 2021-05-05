package it.polito.ezshop.model;

import java.time.LocalDate;

public class Order extends BalanceOperation {

    private String productCode;
    private double pricePerUnit;
    private int quantity;

    public String getProductCode() { return this.productCode; }

    public void setProductCode(String productCode) { this.productCode = productCode; }

    public double getPricePerUnit() { return this.pricePerUnit; }

    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public int getQuantity() { return this.quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Integer getOrderId() { return this.getBalanceId(); }

    public void setOrderId(Integer orderId) { this.setBalanceId(orderId); }

    public Order(int balanceId, LocalDate date, double money, OperationStatus status, String productCode, double pricePerUnit, int quantity) {
        super(balanceId, date, money, "order", status);
        this.productCode = productCode;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
    }
}
