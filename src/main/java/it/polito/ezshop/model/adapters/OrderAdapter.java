package it.polito.ezshop.model.adapters;

import it.polito.ezshop.model.OperationStatus;
import it.polito.ezshop.model.Order;

import java.util.Objects;

public class OrderAdapter implements it.polito.ezshop.data.Order {

    private final Order order;

    public OrderAdapter(Order order) {
        Objects.requireNonNull(order);
        this.order = order;
    }

    @Override
    public Integer getBalanceId() {
        return order.getBalanceId();
    }

    @Override
    public void setBalanceId(Integer balanceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProductCode() {
        return order.getProductCode();
    }

    @Override
    public void setProductCode(String productCode) {
        throw new UnsupportedOperationException();
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
        if (order.getStatus() == OperationStatus.PAID || order.getStatus() == OperationStatus.COMPLETED) {
            return "PAYED";
        } else if (order.getStatus() == OperationStatus.CLOSED) {
            return "ISSUED";
        }
        return order.getStatus().name();
    }

    @Override
    public void setStatus(String status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getOrderId() {
        return order.getBalanceId();
    }

    @Override
    public void setOrderId(Integer orderId) {
        throw new UnsupportedOperationException();
    }
}
