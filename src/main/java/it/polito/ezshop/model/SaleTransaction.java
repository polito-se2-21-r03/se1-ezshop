package it.polito.ezshop.model;

import java.time.LocalDate;
import java.util.*;

public class SaleTransaction extends Credit {

    private final List<TicketEntry> entries = new ArrayList<>();
    private final List<ReturnTransaction> returnTransactions = new ArrayList<>();

    /**
     * Discount rate of the whole sale transaction
     */
    private double discountRate;

    public SaleTransaction(int balanceId, LocalDate date) {
        this(balanceId, date, null, null, 0.0);
    }

    public SaleTransaction(int balanceId, LocalDate date, List<TicketEntry> entries, double discountRate) {
        this(balanceId, date, entries, null, discountRate);
    }

    public SaleTransaction(int balanceId, LocalDate date, List<TicketEntry> entries,
                           List<ReturnTransaction> returnTransactions, double discountRate) {
        super(balanceId, date, 0.0, OperationStatus.OPEN);

        if (returnTransactions != null) {
            this.returnTransactions.addAll(returnTransactions);
        }

        this.discountRate = discountRate;

        if (entries != null) {
            this.entries.addAll(entries);
            recomputeBalanceValue();
        }
    }

    /**
     * Add a product to the transaction. The transaction must be in the OPEN state.
     * The balance value of the transaction is updated.
     *
     * @param product      to add
     * @param amount       of the product to add
     * @param pricePerUnit of the product
     * @param discountRate of the product
     */
    public void addSaleTransactionItem(ProductType product, int amount, double pricePerUnit, double discountRate) {
        if (status == OperationStatus.OPEN) {
            TicketEntry entry = this.entries.stream()
                    .filter(x -> x.getProductType().getBarCode().equals(product.getBarCode()))
                    .findFirst()
                    .orElse(null);

            if (entry != null) {
                entry.setAmount(entry.getAmount() + amount);
            } else {
                entries.add(new TicketEntry(product, amount, pricePerUnit, discountRate));
            }

            recomputeBalanceValue();
        }
    }

    /**
     * Remove a product from the transaction.
     *
     * @param product to remove
     * @param amount  of the product to remove
     * @return true if the product is removed, false otherwise
     */
    public boolean removeSaleTransactionItem(ProductType product, int amount) {
        TicketEntry entry = entries.stream()
                .filter(x -> x.getProductType().getBarCode().equals(product.getBarCode()))
                .findFirst()
                .orElse(null);

        if (entry == null || entry.getAmount() < amount) {
            return false;
        }

        if (entry.getAmount() > amount) {
            entry.setAmount(entry.getAmount() - amount);
        } else {
            entries.remove(entry);
        }

        if (status == OperationStatus.OPEN) {
            recomputeBalanceValue();
        }
        return true;
    }

    public boolean applyDiscountToProduct(String productCode, double discountRate) {
        if (status == OperationStatus.OPEN) {
            Optional<TicketEntry> entry = entries.stream()
                    .filter(x -> x.getProductType().getBarCode().equals(productCode))
                    .findFirst();
            entry.ifPresent((value) -> value.setDiscountRate(discountRate));

            recomputeBalanceValue();
            return entry.isPresent();
        }
        return false;
    }

    public List<TicketEntry> getTransactionItems() {
        return Collections.unmodifiableList(this.entries);
    }

    public double getDiscountRate() {
        return this.discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    /**
     * Compute the total of the transaction.
     *
     * @return the total of the transaction
     */
    public double computeTotal() {
        return (1 - this.discountRate) * this.entries.stream().mapToDouble(entry -> {
            // compute the subtotal for the entry
            return entry.getAmount() * entry.getPricePerUnit() * (1 - entry.getDiscountRate());
        }).sum();
    }

    @Override
    public double getMoney() {
        return super.getMoney();
    }

    private void recomputeBalanceValue() {
        setMoney(computeTotal());
    }

    public int computePoints() {
        return ((int) this.computeTotal()) / 10;
    }

    public void addReturnTransaction(ReturnTransaction returnTransaction) {
        this.returnTransactions.add(returnTransaction);
    }

    /**
     * Return all the return transactions related to this sale transaction
     */
    public List<ReturnTransaction> getReturnTransactions() {
        return Collections.unmodifiableList(this.returnTransactions);
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
