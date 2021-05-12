package it.polito.ezshop.model;

import java.time.LocalDate;

public class Debit extends BalanceOperation {

    public Debit(int balanceId, LocalDate date, double money, OperationStatus status) {
        super(balanceId, date, money, TYPE_DEBIT, status);
    }

    public Debit(int balanceId, LocalDate date, double money, String type, OperationStatus status) {
        super(balanceId, date, money, type, status);
    }
}
