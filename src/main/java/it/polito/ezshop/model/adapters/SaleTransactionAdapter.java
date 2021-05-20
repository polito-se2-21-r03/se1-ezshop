package it.polito.ezshop.model.adapters;

import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.model.SaleTransaction;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SaleTransactionAdapter adapts it.polito.ezshop.model.SaleTransaction to the
 * it.polito.ezshop.data.SaleTransaction interface
 */
public class SaleTransactionAdapter implements it.polito.ezshop.data.SaleTransaction {

    private final SaleTransaction saleTransaction;

    public SaleTransactionAdapter(SaleTransaction saleTransaction) {
        Objects.requireNonNull(saleTransaction);
        this.saleTransaction = saleTransaction;
    }

    @Override
    public Integer getTicketNumber() {
        return saleTransaction.getBalanceId();
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        throw new UnsupportedOperationException("Changing transaction ID is not supported");
    }

    @Override
    public List<TicketEntry> getEntries() {
        return saleTransaction.getTransactionItems()
                .stream().map(TicketEntryAdapter::new)
                .collect(Collectors.toList());
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        throw new UnsupportedOperationException("Changing transaction entries is not supported");
    }

    @Override
    public double getDiscountRate() {
        return saleTransaction.getDiscountRate();
    }

    @Override
    public void setDiscountRate(double discountRate) {
        try {
            saleTransaction.setDiscountRate(discountRate);
        } catch (InvalidDiscountRateException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public double getPrice() {
        return saleTransaction.computeTotal();
    }

    @Override
    public void setPrice(double price) {
        throw new UnsupportedOperationException("Changing transaction price is not supported");
    }
}
