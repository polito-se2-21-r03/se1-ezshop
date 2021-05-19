package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SaleTransaction extends Credit {

    private final List<TicketEntry> entries = new ArrayList<>();
    private final List<ReturnTransaction> returnTransactions = new ArrayList<>();

    /**
     * Discount rate of the whole sale transaction
     */
    private double discountRate;

    public SaleTransaction(int balanceId, LocalDate date) {
        super(balanceId, date, 0.0, OperationStatus.OPEN);
        this.discountRate = 0.0;
    }

    public SaleTransaction(int balanceId, LocalDate date, List<TicketEntry> entries, double discountRate)
            throws InvalidDiscountRateException {
        super(balanceId, date, 0.0, OperationStatus.OPEN);

        validateDiscount(discountRate);

        this.discountRate = discountRate;

        if (entries != null) {
            this.entries.addAll(entries);
            recomputeBalanceValue();
        }
    }

    public static void validateId(Integer id) throws InvalidTransactionIdException {
        if (id == null || id <= 0) {
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }
    }

    public static void validateDiscount(double discount) throws InvalidDiscountRateException {
        if (discount >= 1.00 || discount < 0) {
            throw new InvalidDiscountRateException("Discount Rate must be between 0 and 1");
        }
    }

    /**
     * Add a product to the transaction. The transaction must be in the OPEN state.
     * The balance value of the transaction is updated.
     *
     * @param product      to add
     * @param amount       of the product to add
     */
    public void addSaleTransactionItem(ProductType product, int amount)
            throws InvalidQuantityException, IllegalStateException {
        if (this.getStatus() != OperationStatus.OPEN) {
            throw new IllegalStateException("Sale transaction is not OPEN.");
        }

        TicketEntry entry = this.entries.stream()
                .filter(x -> x.getProductType().getBarCode().equals(product.getBarCode()))
                .findFirst()
                .orElse(null);

        if (entry != null) {
            entry.setAmount(entry.getAmount() + amount);
        } else {
            entries.add(new TicketEntry(product, amount));
        }

        recomputeBalanceValue();
    }

    /**
     * Remove a product from the transaction.
     *
     * @param product to remove
     * @param amount  of the product to remove
     * @return true if the product is removed, false otherwise
     */
    public boolean removeSaleTransactionItem(ProductType product, int amount) throws InvalidQuantityException {
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

    public boolean applyDiscountToProduct(String productCode, double discountRate) throws InvalidDiscountRateException {
        if (status != OperationStatus.OPEN) {
            throw new IllegalStateException("Sale transaction is not OPEN.");
        }

        TicketEntry entry = entries.stream()
                .filter(x -> x.getProductType().getBarCode().equals(productCode))
                .findFirst().orElse(null);

        if (entry == null) {
            return false;
        }

        entry.setDiscountRate(discountRate);
        recomputeBalanceValue();
        return true;
    }

    public List<TicketEntry> getTransactionItems() {
        return this.entries;
    }

    public double getDiscountRate() {
        return this.discountRate;
    }

    public void setDiscountRate(double discountRate) throws InvalidDiscountRateException {
        if (status.affectsBalance()) {
            throw new IllegalStateException("Sale transaction has already been paid.");
        }
        validateDiscount(discountRate);
        this.discountRate = discountRate;
        this.recomputeBalanceValue();
    }

    /**
     * Compute the total of the transaction.
     *
     * @return the total of the transaction
     */
    public double computeTotal() {
        return (1 - this.discountRate) * this.entries.stream()
                .mapToDouble(TicketEntry::computeTotal).sum();
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
