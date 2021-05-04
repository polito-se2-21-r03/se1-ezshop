package it.polito.ezshop.model;

import java.time.LocalDate;

public class Order implements it.polito.ezshop.data.Order {

    private String productCode;
    private double pricePerUnit;
    private int quantity;

    @Override
    public String getProductCode() { return this.productCode; }

    @Override
    public void setProductCode(String productCode) { this.productCode = productCode; }

    @Override
    public double getPricePerUnit() { return this.pricePerUnit; }

    @Override
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    @Override
    public int getQuantity() { return this.quantity; }

    @Override
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public Integer getOrderId() { return this.getBalanceId(); }

    @Override
    public void setOrderId(Integer orderId) { this.setBalanceId(orderId); }

    // BalanceOperation methods (can't inherit because BalanceOperation.balanceId has type int, but Order requires type Integer :

    private Integer balanceId;
    private LocalDate date;
    private double money;
    private String type;
    private OperationStatus status;

    @Override
    public Integer getBalanceId() { return this.balanceId; }

    @Override
    public void setBalanceId(Integer balanceId) { this.balanceId = balanceId; }

    public LocalDate getDate() { return this.date; }

    public void setDate(LocalDate date) { this.date = date; }

    public double getMoney() { return this.money; }

    public void setMoney(double money) { this.money = money; }

    public String getType() { return this.type; }

    public void setType(String type) { this.type = type; }

    public String getStatus() { return status.getValue(); }

    public void setStatus(String status) { this.status = OperationStatus.valueOf(status); }
}
