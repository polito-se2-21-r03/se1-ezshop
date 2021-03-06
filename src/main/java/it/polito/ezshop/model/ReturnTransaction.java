package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidRFIDException;

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

        Objects.requireNonNull(date, "date must not be null");

        this.saleTransactionId = saleTransactionId;

        if (entries != null) {
            this.entries.addAll(entries);
            recomputeBalanceValue();
        }
    }

    public int getSaleTransactionId() {
        return saleTransactionId;
    }

    public List<ReturnTransactionItem> getTransactionItems() {
        return this.entries;
    }

    public void addReturnTransactionItem(ProductType product, int amount, double pricePerUnit) {
        if (status == OperationStatus.OPEN) {
            entries.add(new ReturnTransactionItem(product, amount, pricePerUnit));
            recomputeBalanceValue();
        }
    }

    public void addReturnTransactionItemRFID(ProductType product, String RFID, double pricePerUnit) throws InvalidRFIDException {
        if (status == OperationStatus.OPEN) {
            ReturnTransactionItem entry = entries.stream().filter(e -> e.getProductType().getId() == product.getId())
                    .findAny()
                    .orElse(null);
            if (entry == null) {
                entry = new ReturnTransactionItem(product, pricePerUnit);
                entries.add(entry);
            }
            entry.addRFID(RFID);
            recomputeBalanceValue();
        }
    }

    public double computeTotal() {
        return this.entries.stream().mapToDouble(ReturnTransactionItem::computeValue).sum();
    }

    @Override
    public double getMoney() {
        return super.getMoney();
    }

    private void recomputeBalanceValue() {
        setMoney(computeTotal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReturnTransaction that = (ReturnTransaction) o;
        return saleTransactionId == that.saleTransactionId && entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), saleTransactionId);
    }
}
