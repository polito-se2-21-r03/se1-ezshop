package it.polito.ezshop.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReturnTransaction extends Debit {

    private final List<ReturnTransactionItem> entries = new ArrayList<>();
    private final SaleTransaction saleTransaction;
    private double price;

    public ReturnTransaction(int balanceId, LocalDate date, List<ReturnTransactionItem> entries,
                             SaleTransaction saleTransaction, double price) {
        super(balanceId, date, 0.0, TYPE_RETURN, OperationStatus.OPEN);

        Objects.requireNonNull(saleTransaction, "saleTransaction must not be null");

        if (entries != null) {
            this.entries.addAll(entries);
        }

        this.saleTransaction = saleTransaction;
        this.price = price;
    }

    public List<ReturnTransactionItem> getEntries() {
        return this.entries;
    }

    public void setEntries(List<ReturnTransactionItem> entries) {
        this.entries.clear();
        if (entries != null) {
            this.entries.addAll(entries);
        }
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public SaleTransaction getSaleTransaction() {
        return this.saleTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReturnTransaction that = (ReturnTransaction) o;
        return Double.compare(that.price, price) == 0 &&
                entries.equals(that.entries) &&
                saleTransaction.equals(that.saleTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entries, saleTransaction, price);
    }
}
