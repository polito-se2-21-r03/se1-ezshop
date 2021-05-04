package it.polito.ezshop.model;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AccountBook {

    List<BalanceOperation> balanceOperations = new ArrayList<>();

    public List<BalanceOperation> getTransactions(LocalDate startDate, LocalDate endDate) {
        return this.balanceOperations.stream()
                .filter(b -> b.getDate().isAfter(startDate) && b.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<BalanceOperation> getCreditTransactions(LocalDate startDate, LocalDate endDate) {
        // TODO
        return null;
    }

    public List<BalanceOperation> getDebitTransactions(LocalDate startDate, LocalDate endDate) {
        // TODO
        return null;
    }

    public List<BalanceOperation> getSaleTransactions(LocalDate startDate, LocalDate endDate) {
        // TODO
        return null;
    }

    public List<BalanceOperation> getReturnTransactions(LocalDate startDate, LocalDate endDate) {
        // TODO
        return null;
    }

    public List<BalanceOperation> getOrders(LocalDate startDate, LocalDate endDate) {
        // TODO
        return null;
    }

    public boolean addTransaction(BalanceOperation balanceOperation) {
        // TODO
        return false;
    }

    public boolean removeTransaction(Integer balanceId) {
        // TODO
        return false;
    }

    // TODO modify transactions

    public boolean checkAvailability(double requestedAmount) {
        // TODO
        return false;
    }

    public double computeBalance() {
        // TODO
        return 0;
    }
}
