package it.polito.ezshop.model.adapters;

import it.polito.ezshop.model.Order;

public class OrderAdapter implements it.polito.ezshop.data.Order {

    private final Order order;

    public OrderAdapter(Order order) {
        this.order = order;
    }

    @Override
    public Integer getBalanceId() {
        return order.getBalanceId();
    }

    @Override
    public void setBalanceId(Integer balanceId) {
        order.setBalanceId(balanceId);
    }

    @Override
    public String getProductCode() {
        return order.getProductCode();
    }

    @Override
    public void setProductCode(String productCode) {
        order.setProductCode(productCode);
    }

    @Override
    public double getPricePerUnit() {
        return order.getPricePerUnit();
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        order.setPricePerUnit(pricePerUnit);
    }

    @Override
    public int getQuantity() {
        return order.getQuantity();
    }

    @Override
    public void setQuantity(int quantity) {
        order.setQuantity(quantity);
    }

    @Override
    public String getStatus() {
        if (order.getStatus().equals("PAID")) {
            return "PAYED";
        } else if (order.getStatus().equals("CLOSED")) {
            return "ISSUED";
        }
        return order.getStatus();
    }

    @Override
    public void setStatus(String status) {
        // TODO: setStatus is not reachable
    }

    @Override
    public Integer getOrderId() {
        return order.getBalanceId();
    }

    @Override
    public void setOrderId(Integer orderId) {
        order.setBalanceId(orderId);
    }
}
