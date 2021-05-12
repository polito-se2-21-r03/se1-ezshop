package it.polito.ezshop.model;

import it.polito.ezshop.data.TicketEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class SaleTransaction extends Credit implements it.polito.ezshop.data.SaleTransaction {

    private final List<TicketEntry> entries = new ArrayList<>();
    private final List<ReturnTransaction> returnTransactions = new ArrayList<>();
    private double discountRate;
    private double price;

    public SaleTransaction(int balanceId, LocalDate date, List<TicketEntry> entries,
                           List<ReturnTransaction> returnTransactions, double discountRate, double price) {
        super(balanceId, date, 0.0, TYPE_SALE, OperationStatus.OPEN);

        if (entries != null) {
            this.entries.addAll(entries);
        }
        if (returnTransactions != null) {
            this.returnTransactions.addAll(returnTransactions);
        }

        this.discountRate = discountRate;
        this.price = price;
    }

    @Override
    public Integer getTicketNumber() {
        return this.getBalanceId();
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        this.setBalanceId(ticketNumber);
    }

    @Override
    public List<TicketEntry> getEntries() {
        return this.entries;
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        this.entries.clear();
        if (entries != null) {
            this.entries.addAll(entries);
        }
    }

    @Override
    public double getDiscountRate() {
        return this.discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    public List<ReturnTransaction> getReturnTransactions() {
        return this.returnTransactions;
    }

    public void setReturnTransactions(List<ReturnTransaction> returnTransactions) {
        this.returnTransactions.clear();
        if (returnTransactions != null) {
            this.returnTransactions.addAll(returnTransactions);
        }
    }

    public void addReturnTransactions(ReturnTransaction... returnTransactions) {
        this.returnTransactions.addAll(Arrays.asList(returnTransactions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SaleTransaction that = (SaleTransaction) o;
        return Double.compare(that.discountRate, discountRate) == 0 &&
                Double.compare(that.price, price) == 0 &&
                entries.equals(that.entries) &&
                returnTransactions.equals(that.returnTransactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entries, returnTransactions, discountRate, price);
    }
}
