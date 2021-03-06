package it.polito.ezshop.model;

import java.time.LocalDate;
import java.util.Objects;

public abstract class BalanceOperation {
    protected final int balanceId;
    protected LocalDate date;
    protected double balanceValue;

    protected OperationStatus status;

    protected BalanceOperation(int balanceId, LocalDate date, double balanceValue, OperationStatus status) {
        this.balanceId = balanceId;
        this.date = date;
        this.balanceValue = balanceValue;
        this.status = status;
    }

    public int getBalanceId() {
        return this.balanceId;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setBalanceValue(double balanceValue) {
        this.balanceValue = balanceValue;
    }

    public double getMoney() {
        return this.balanceValue;
    }

    public void setMoney(double balanceValue) {
        this.balanceValue = balanceValue;
    }

    public OperationStatus getStatus() {
        return status;
    }

    /**
     * Change the status of the operation.
     * Only AccountBook is supposed to call this method.
     *
     * @param status new status
     */
    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceOperation that = (BalanceOperation) o;
        return balanceId == that.balanceId &&
                Double.compare(that.balanceValue, balanceValue) == 0 &&
                date.equals(that.date) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(balanceId);
    }
}
