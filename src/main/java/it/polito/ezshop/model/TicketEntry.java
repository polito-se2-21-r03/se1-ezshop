package it.polito.ezshop.model;

import it.polito.ezshop.data.ProductType;

public class TicketEntry implements it.polito.ezshop.data.TicketEntry{

    private ProductType productType;
    private int Amount;
    private double discountRate;

    public TicketEntry(ProductType productType, int amount, double discount) {
        this.productType = productType;
        this.Amount = amount;
        this.discountRate = discount;
    }


    @Override
    public String getBarCode() {
        return this.productType.getBarCode();
    }

    @Override
    public void setBarCode(String barCode) {
        this.productType.setBarCode(barCode);
    }

    @Override
    public String getProductDescription() {
        return this.productType.getProductDescription();
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.productType.setProductDescription(productDescription);
    }

    @Override
    public int getAmount() {
        return this.Amount;
    }

    @Override
    public void setAmount(int amount) {
        this.Amount = amount;
    }

    @Override
    public double getPricePerUnit() {
        return this.productType.getPricePerUnit();
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        this.productType.setPricePerUnit(pricePerUnit);
    }

    @Override
    public double getDiscountRate() {
        return this.discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}
