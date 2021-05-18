package it.polito.ezshop.model.adapters;

import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.model.TicketEntry;

/**
 * TicketEntryAdapter adapts it.polito.ezshop.model.TicketEntry to the
 * it.polito.ezshop.data.TicketEntry interface
 */
public class TicketEntryAdapter implements it.polito.ezshop.data.TicketEntry {

    private final TicketEntry ticketEntry;

    public TicketEntryAdapter (TicketEntry entry) {
        this.ticketEntry = entry;
    }

    @Override
    public String getBarCode() {
        return ticketEntry.getProductType().getBarCode();
    }

    @Override
    public void setBarCode(String barCode) {
        try {
            ticketEntry.getProductType().setBarCode(barCode);
        } catch (InvalidProductCodeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getProductDescription() {
        return ticketEntry.getProductType().getProductDescription();
    }

    @Override
    public void setProductDescription(String productDescription) {
        try {
            ticketEntry.getProductType().setBarCode(productDescription);
        } catch (InvalidProductCodeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public int getAmount() {
        return ticketEntry.getAmount();
    }

    @Override
    public void setAmount(int amount) {
        ticketEntry.setAmount(amount);
    }

    @Override
    public double getPricePerUnit() {
        return ticketEntry.getPricePerUnit();
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        ticketEntry.setPricePerUnit(pricePerUnit);
    }

    @Override
    public double getDiscountRate() {
        return ticketEntry.getDiscountRate();
    }

    @Override
    public void setDiscountRate(double discountRate) {
        ticketEntry.setDiscountRate(discountRate);
    }
}
