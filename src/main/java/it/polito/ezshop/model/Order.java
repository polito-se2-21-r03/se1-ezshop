package it.polito.ezshop.model;

public class Order extends Debit implements it.polito.ezshop.data.Order {

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
    public void setStatus(String status) { }

    @Override
    public Integer getOrderId() { return this.getBalanceId(); }

    @Override
    public void setOrderId(Integer orderId) { this.setBalanceId(orderId); }
}
