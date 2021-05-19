package it.polito.ezshop.model.adapters;

import it.polito.ezshop.model.*;

import java.time.LocalDate;

public class BalanceOperationAdapter implements it.polito.ezshop.data.BalanceOperation {

    public static final String ORDER = "ORDER";
    public static final String SALE = "SALE";
    public static final String RETURN = "RETURN";
    public static final String CREDIT = "CREDIT";
    public static final String DEBIT = "DEBIT";


    private final BalanceOperation balanceOperation;

    public BalanceOperationAdapter(BalanceOperation balanceOperation) {
        this.balanceOperation = balanceOperation;
    }

    @Override
    public int getBalanceId() {
        return balanceOperation.getBalanceId();
    }

    @Override
    public void setBalanceId(int balanceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocalDate getDate() {
        return balanceOperation.getDate();
    }

    @Override
    public void setDate(LocalDate date) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getMoney() {
        return balanceOperation.getMoney();
    }

    @Override
    public void setMoney(double money) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType() {
        if (balanceOperation instanceof Order) {
            return ORDER;
        } else if (balanceOperation instanceof SaleTransaction) {
            return SALE;
        } else if (balanceOperation instanceof ReturnTransaction) {
            return RETURN;
        } else if (balanceOperation instanceof Credit) {
            return CREDIT;
        }
        return DEBIT;
    }

    @Override
    public void setType(String type) {
        throw new UnsupportedOperationException();
    }

    public BalanceOperation getTransaction() {
        return this.balanceOperation;
    }
}
