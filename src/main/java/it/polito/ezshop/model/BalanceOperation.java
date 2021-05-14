package it.polito.ezshop.model;

import java.time.LocalDate;
import java.util.Objects;

public abstract class BalanceOperation implements it.polito.ezshop.data.BalanceOperation {
    protected int balanceId;
    protected LocalDate date;
    protected double balanceValue;

    /**
     * One of TYPE_CREDIT, TYPE_SALE, TYPE_DEBIT, TYPE_RETURN, TYPE_ORDER
     */
    protected String type;


    protected OperationStatus status;

    protected BalanceOperation() {}

    protected BalanceOperation(int balanceId, LocalDate date, double balanceValue, OperationStatus status) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(status, "status must not be null");

        this.balanceId = balanceId;
        this.date = date;
        this.balanceValue = balanceValue;
        this.status = status;
    }

    @Override
    public int getBalanceId() {
        return this.balanceId;
    }

    @Override
    public void setBalanceId(int balanceId) {
        this.balanceId = balanceId;
    }

    @Override
    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public double getMoney() {
        return this.balanceValue;
    }

    @Override
    public void setMoney(double balanceValue) {
        this.balanceValue = balanceValue;
    }

    @Override
    @Deprecated
    public String getType() {
        return this.getClass().toString();
    }

    @Override
    @Deprecated
    public void setType(String type) { }

    public String getStatus() {
        return status.name();
    }

    public void setStatus(String status) {
        this.status = OperationStatus.valueOf(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceOperation that = (BalanceOperation) o;
        return balanceId == that.balanceId &&
                Double.compare(that.balanceValue, balanceValue) == 0 &&
                date.equals(that.date) &&
                type.equals(that.type) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(balanceId);
    }
}
