package it.polito.ezshop.model.adapters;

import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.model.SaleTransaction;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SaleTransactionAdapter adapts it.polito.ezshop.model.SaleTransaction to the
 * it.polito.ezshop.data.SaleTransaction interface
 */
public class SaleTransactionAdapter implements it.polito.ezshop.data.SaleTransaction {

    private final SaleTransaction saleTransaction;

    public SaleTransactionAdapter(SaleTransaction saleTransaction) {
        this.saleTransaction = saleTransaction;
    }

    @Override
    public Integer getTicketNumber() {
        return saleTransaction.getBalanceId();
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        saleTransaction.setBalanceId(ticketNumber);
    }

    @Override
    public List<TicketEntry> getEntries() {
        return saleTransaction.getTransactionItems()
                .stream().map(TicketEntryAdapter::new)
                .collect(Collectors.toList());
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        // TODO: not implemented
    }

    @Override
    public double getDiscountRate() {
        return saleTransaction.getDiscountRate();
    }

    @Override
    public void setDiscountRate(double discountRate) {
        saleTransaction.setDiscountRate(discountRate);
    }

    @Override
    public double getPrice() {
        return saleTransaction.computeTotal();
    }

    @Override
    public void setPrice(double price) {
        // TODO: not implemented
    }
}
