package it.polito.ezshop.model;

import java.time.LocalDate;

public class Credit extends BalanceOperation {

    public Credit(int balanceId, LocalDate date, double money, OperationStatus status) {
        super(balanceId, date, money, status);
    }
}
