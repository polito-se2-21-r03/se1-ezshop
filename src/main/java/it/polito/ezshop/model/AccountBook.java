package it.polito.ezshop.model;

import it.polito.ezshop.data.SaleTransaction;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static it.polito.ezshop.utils.Utils.generateId;

public class AccountBook {

    private final List<BalanceOperation> balanceOperations = new ArrayList<>();
    private Double balance;

    private static boolean statusRequiresBalanceUpdate(OperationStatus status) {
        return status == OperationStatus.PAID || status == OperationStatus.COMPLETED;
    }

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
     * Returns all balance operations with a given time frame.
     *
     * @param startDate returned balance operations where made after this date
     * @param endDate returned balance operations where made before this date
     * @return list of balance operations within timeframe
     */
    public List<BalanceOperation> getTransactions(LocalDate startDate, LocalDate endDate) {
        return this.balanceOperations.stream()
                .filter(b -> b.getDate().isAfter(startDate) && b.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    /**
     * Returns all balance operations
     *
     * @return list of balance operations
     */
    public List<BalanceOperation> getTransactions() {
        return this.balanceOperations;
    }

    /**
     * Returns all credit transactions
     *
     * @return list of credit transactions
     */
    public List<BalanceOperation> getCreditTransactions() {
        return this.balanceOperations.stream()
                .filter(b -> Credit.class.isAssignableFrom(b.getClass()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all debit transactions
     *
     * @return list of debit transactions
     */
    public List<BalanceOperation> getDebitTransactions() {
        return this.balanceOperations.stream()
                .filter(b -> Debit.class.isAssignableFrom(b.getClass()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all sale transactions
     *
     * @return list of sale transactions
     */
    public List<BalanceOperation> getSaleTransactions() {
        return this.balanceOperations.stream()
                .filter(b -> SaleTransaction.class.isAssignableFrom(b.getClass()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all return transactions
     *
     * @return list of return transactions
     */
    public List<BalanceOperation> getReturnTransactions() {
        return this.balanceOperations.stream()
                .filter(b -> ReturnTransaction.class.isAssignableFrom(b.getClass()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all orders
     *
     * @return list of orders
     */
    public List<BalanceOperation> getOrders() {
        return this.balanceOperations.stream()
                .filter(b -> Order.class.isAssignableFrom(b.getClass()))
                .collect(Collectors.toList());
    }

    /**
     * Adds a balance operation to the transaction list.
     * Changes the account book's balance if the operation status requires so.
     */
    public void addTransaction(BalanceOperation balanceOperation) {
        if (statusRequiresBalanceUpdate(OperationStatus.valueOf(balanceOperation.getStatus()))) {
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

        if (statusRequiresBalanceUpdate(OperationStatus.valueOf(balanceOperation.getStatus()))) {
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
        OperationStatus previousStatus = OperationStatus.valueOf(balanceOperation.getStatus());

        // balance operation previously did not count towards account book balance but now does
        if (!statusRequiresBalanceUpdate(previousStatus) && statusRequiresBalanceUpdate(newStatus)) {
            this.balance += balanceOperation.getMoney();
        }

        // balance operation previously did count towards account book balance but does not anymore
        if (statusRequiresBalanceUpdate(previousStatus) && !statusRequiresBalanceUpdate(newStatus)) {
            this.balance += balanceOperation.getMoney();
        }

        this.balanceOperations.remove(balanceOperation);
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
                .filter(b -> statusRequiresBalanceUpdate(OperationStatus.valueOf(b.getStatus())))
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
