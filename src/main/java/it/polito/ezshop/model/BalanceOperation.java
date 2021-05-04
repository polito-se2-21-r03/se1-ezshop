package it.polito.ezshop.model;

import java.time.LocalDate;

public class BalanceOperation implements it.polito.ezshop.data.BalanceOperation {

    private Integer balanceId;
    private LocalDate date;
    private double money;
    private String type;
    private OperationStatus status;

    @Override
    public Integer getBalanceId() { return this.balanceId; }

    @Override
    public void setBalanceId(Integer balanceId) { this.balanceId = balanceId; }

    @Override
    public LocalDate getDate() { return this.date; }

    @Override
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public double getMoney() { return this.money; }

    @Override
    public void setMoney(double money) { this.money = money; }

    @Override
    public String getType() { return this.type; }

    @Override
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status.getValue(); }

    public void setStatus(String status) { this.status = OperationStatus.valueOf(status); }
}
