package it.polito.ezshop.model;

import java.util.*;
import java.util.stream.Collectors;

import static it.polito.ezshop.utils.Utils.generateId;

public class AccountBook {

    private final List<BalanceOperation> balanceOperations = new ArrayList<>();
    private Double balance = 0.0;

    /**
     * Returns the balance operation with the given ID
     *
     * @param transactionId transaction ID of the requested balance operation
     * @return balance operation with given ID
     *         null if it doesn't exist
     */
    public BalanceOperation getTransaction(int transactionId) {
        return this.balanceOperations.stream()
                .filter(b -> b.getBalanceId() == transactionId)
                .findAny()
                .orElse(null);
    }

    /**
     * Returns all balance operations
     *
     * @return list of balance operations
     */
    public List<BalanceOperation> getAllTransactions() {
        return this.balanceOperations;
    }

    /**
     * Returns all credit transactions
     *
     * @return list of credit transactions
     */
    public List<Credit> getCreditTransactions() {
        return this.balanceOperations.stream()
                .filter(b -> b instanceof Credit)
                .map(b -> (Credit) b)
                .collect(Collectors.toList());
    }

    /**
     * Returns all debit transactions
     *
     * @return list of debit transactions
     */
    public List<Debit> getDebitTransactions() {
        return this.balanceOperations.stream()
                .filter(b -> b instanceof Debit)
                .map(b -> (Debit) b)
                .collect(Collectors.toList());
    }

    /**
     * Returns all sale transactions
     *
     * @return list of sale transactions
     */
    public List<SaleTransaction> getSaleTransactions() {
        return this.balanceOperations.stream()
                .filter(b -> b instanceof SaleTransaction)
                .map(b -> (SaleTransaction) b)
                .collect(Collectors.toList());
    }

    /**
     * Returns all return transactions
     *
     * @return list of return transactions
     */
    public List<ReturnTransaction> getReturnTransactions() {
        return this.balanceOperations.stream()
                .filter(b -> b instanceof ReturnTransaction)
                .map(b -> (ReturnTransaction) b)
                .collect(Collectors.toList());
    }

    /**
     * Returns all orders
     *
     * @return list of orders
     */
    public List<Order> getOrders() {
        return this.balanceOperations.stream()
                .filter(b -> b instanceof Order)
                .map(b -> (Order) b)
                .collect(Collectors.toList());
    }

    /**
     * Adds a balance operation to the transaction list.
     * Changes the account book's balance if the operation status requires so.
     */
    public void addTransaction(BalanceOperation balanceOperation) {
        if (balanceOperation.getStatus().affectsBalance()) {
            this.balance += balanceOperation.getMoney();
        }
        this.balanceOperations.add(balanceOperation);
    }

    /**
     * Removes the balance operation with the given ID from the transaction list.
     * Changes the account book's balance if the operation status requires so.
     *
     * @param balanceId ID of the balance operation to be deleted
     */
    public void removeTransaction(int balanceId) {

        BalanceOperation balanceOperation = this.getTransaction(balanceId);

        if (balanceOperation.getStatus().affectsBalance()) {
            this.balance -= balanceOperation.getMoney();
        }

        this.balanceOperations.remove(balanceOperation);
    }

    /**
     * Updates the status of the balance operation with the given ID.
     * The account book's balance is updated automatically if the new status requires so.
     *
     * @param balanceId ID of the balance operation
     * @param newStatus new status for the balance operation
     */
    public void setTransactionStatus(int balanceId, OperationStatus newStatus) {
        BalanceOperation balanceOperation = this.getTransaction(balanceId);
        OperationStatus previousStatus = balanceOperation.getStatus();

        // balance operation previously did not count towards account book balance but now does
        if (!previousStatus.affectsBalance() && newStatus.affectsBalance()) {
            this.balance += balanceOperation.getMoney();
        }

        // balance operation previously did count towards account book balance but does not anymore
        if (previousStatus.affectsBalance() && !newStatus.affectsBalance()) {
            this.balance += balanceOperation.getMoney();
        }

        balanceOperation.setStatus(newStatus);
    }

    /**
     * Check if the requested amount of funds is available in the account book's balance
     *
     * @param requestedAmount amount of money required
     * @return true iff the current balance allows for the requested amount to be spent
     */
    public boolean checkAvailability(double requestedAmount) {
        return this.balance >= requestedAmount;
    }

    /**
     * Returns the current balance available in the account book
     *
     * @return available balance
     */
    public double getBalance() {
        return this.balance;
    }

    /**
     * Recomputes the balance from the history of transactions
     *
     * @return available balance
     */
    public double computeBalance() {
        this.balance = this.balanceOperations.stream()
                .filter(b -> b.getStatus().affectsBalance())
                .map(BalanceOperation::getMoney)
                .reduce(Double::sum)
                .orElse(0.0);
        return this.balance;
    }

    /**
     * Generate a new unique ID that is not used by any balance operation in the account book
     *
     * @return unique ID
     */
    public int generateNewId() {
        List<Integer> currentIds = this.balanceOperations.stream()
                .map(BalanceOperation::getBalanceId)
                .collect(Collectors.toList());
        return generateId(currentIds);
    }

    /**
     * Reset the account book to its initial state (no transactions, zero balance)
     */
    public void reset() {
        this.balanceOperations.clear();
        this.balance = 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountBook that = (AccountBook) o;
        return balanceOperations.equals(that.balanceOperations) &&
                balance.equals(that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balanceOperations, balance);
    }
}
