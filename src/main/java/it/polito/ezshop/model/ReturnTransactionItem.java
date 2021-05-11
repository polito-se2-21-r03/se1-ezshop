package it.polito.ezshop.model;

import it.polito.ezshop.data.ProductType;

public class ReturnTransactionItem{

    private ProductType productType;
    private int Amount;

    public ReturnTransactionItem(ProductType productType, int amount) {
        this.productType = productType;
        this.Amount = amount;
    }

    public String getBarCode() {
        return this.productType.getBarCode();
    }

    public String getProductDescription() {
        return this.productType.getProductDescription();
    }

    public int getAmount() {
        return this.Amount;
    }

    public void setAmount(int amount) {
        this.Amount = amount;
    }

    public double getPricePerUnit() {
        return this.productType.getPricePerUnit();
    }


}