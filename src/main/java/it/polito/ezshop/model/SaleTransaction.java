package it.polito.ezshop.model;

import it.polito.ezshop.data.TicketEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SaleTransaction extends Credit implements it.polito.ezshop.data.SaleTransaction {

    private final List<TicketEntry> entries = new ArrayList<>();
    private final List<ReturnTransaction> returnTransactions = new ArrayList<>();
    private double discountRate;

    public SaleTransaction(int balanceId, LocalDate date, List<TicketEntry> entries, double discountRate) {
        this(balanceId, date, entries, null, discountRate);
    }

    public SaleTransaction(int balanceId, LocalDate date, List<TicketEntry> entries,
                           List<ReturnTransaction> returnTransactions, double discountRate) {
        super(balanceId, date, 0.0, OperationStatus.OPEN);

        if (entries != null) {
            this.entries.addAll(entries);
        }
        if (returnTransactions != null) {
            this.returnTransactions.addAll(returnTransactions);
        }

        this.discountRate = discountRate;
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
        return (1 - this.discountRate) * this.entries.stream().mapToDouble(entry -> {
            // compute the subtotal for the entry
            return entry.getAmount() * entry.getPricePerUnit() * (1 - entry.getDiscountRate());
        }).sum();
    }

    @Override
    public void setPrice(double price) {
        // TODO: setting the price of a sale transaction may generate a lot of inconsistencies
        // this.price = price;
    }

    @Override
    public double getMoney() {
        if (status != OperationStatus.PAID && status != OperationStatus.COMPLETED) {
            setMoney(getPrice());
        }
        return super.getMoney();
    }

    public void addReturnTransaction(ReturnTransaction returnTransaction) {
        this.returnTransactions.add(returnTransaction);
    }

    public int computePoints() {
        return ((int) this.getPrice()) / 10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SaleTransaction that = (SaleTransaction) o;
        return Double.compare(that.discountRate, discountRate) == 0 &&
                entries.equals(that.entries) &&
                returnTransactions.equals(that.returnTransactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entries, returnTransactions, discountRate);
    }
}
