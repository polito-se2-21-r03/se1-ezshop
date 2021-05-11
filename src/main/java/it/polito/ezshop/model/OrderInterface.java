package it.polito.ezshop.model;

import java.time.LocalDate;

public class OrderInterface implements it.polito.ezshop.data.Order {

    private String productCode;
    private double pricePerUnit;
    private int quantity;

    // BalanceOperation attributes
    private Integer balanceId;
    private LocalDate date;
    private double money;
    private String type;
    private OperationStatus status;

    public OrderInterface(Order order) {
        this.productCode = order.getProductCode();
        this.pricePerUnit = order.getPricePerUnit();
        this.quantity = order.getQuantity();
        this.balanceId = order.getBalanceId();
        this.date = order.getDate();
        this.money = order.getMoney();
        this.type = order.getType();
        this.status = OperationStatus.valueOf(order.getStatus());
    }

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

    // BalanceOperation methods
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

    public String getStatus() { return status.name(); }

    public void setStatus(String status) { this.status = OperationStatus.valueOf(status); }
}
