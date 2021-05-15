package it.polito.ezshop.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReturnTransaction extends Debit {

    private final List<ReturnTransactionItem> entries = new ArrayList<>();
    private final int saleTransactionId;

    public ReturnTransaction(int balanceId, int saleTransactionId, LocalDate date) {
        this(balanceId, saleTransactionId, date, null);
    }

    public ReturnTransaction(int balanceId, int saleTransactionId, LocalDate date, List<ReturnTransactionItem> entries) {
        super(balanceId, date, 0.0, OperationStatus.OPEN);

        this.saleTransactionId = saleTransactionId;

        if (entries != null) {
            this.entries.addAll(entries);
        }
    }

    public int getSaleTransactionId() {
        return saleTransactionId;
    }

    public List<ReturnTransactionItem> getTransactionItems() {
        return this.entries;
    }

    public double getPrice() {
        return this.entries.stream().mapToDouble(ReturnTransactionItem::computeValue).sum();
    }

    @Override
    public double getMoney() {
        if (status != OperationStatus.PAID && status != OperationStatus.COMPLETED) {
            setMoney(getPrice());
        }
        return super.getMoney();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReturnTransaction that = (ReturnTransaction) o;
        return entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entries);
    }
}
