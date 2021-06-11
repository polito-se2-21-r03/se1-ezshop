package it.polito.ezshop.model.adapters;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.TicketEntry;

import java.util.Objects;

/**
 * TicketEntryAdapter adapts it.polito.ezshop.model.TicketEntry to the
 * it.polito.ezshop.data.TicketEntry interface
 */
public class TicketEntryAdapter implements it.polito.ezshop.data.TicketEntry {

    private final TicketEntry ticketEntry;

    public TicketEntryAdapter(TicketEntry entry) {
        Objects.requireNonNull(entry);
        this.ticketEntry = entry;
    }

    /**
     * For debugging purposes only!!!
     */
    public TicketEntry get() {
        return this.ticketEntry;
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
            ticketEntry.getProductType().setProductDescription(productDescription);
        } catch (InvalidProductDescriptionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public int getAmount() {
        return ticketEntry.getAmount();
    }

    @Override
    public void setAmount(int amount) {
        try {
            ticketEntry.setAmount(amount);
        } catch (InvalidQuantityException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public double getPricePerUnit() {
        return ticketEntry.getPricePerUnit();
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        try {
            ticketEntry.setPricePerUnit(pricePerUnit);
        } catch (InvalidPricePerUnitException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public double getDiscountRate() {
        return ticketEntry.getDiscountRate();
    }

    @Override
    public void setDiscountRate(double discountRate) {
        try {
            ticketEntry.setDiscountRate(discountRate);
        } catch (InvalidDiscountRateException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketEntryAdapter that = (TicketEntryAdapter) o;
        return Objects.equals(ticketEntry, that.ticketEntry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketEntry);
    }
}
