package it.polito.ezshop.model;

import java.util.List;

public class ReturnTransaction extends Debit {
    private Integer TicketNumber;
    private List<ReturnTransactionItem> entries;
    private SaleTransaction saleTransaction;
    private double Price;

    public ReturnTransaction(Integer ticketNumber, List<ReturnTransactionItem> entries, SaleTransaction saleTransaction, double price) {
        this.TicketNumber = ticketNumber;
        this.entries = entries;
        this.saleTransaction = saleTransaction;
        this.Price = price;
    }

    public Integer getTicketNumber() {
        return this.TicketNumber;
    }

    public void setTicketNumber(Integer ticketNumber) {
        this.TicketNumber = ticketNumber;
    }

    public List<it.polito.ezshop.model.ReturnTransactionItem> getEntries() {
        return this.entries;
    }

    public void setEntries(List<ReturnTransactionItem> entries) {
        this.entries=entries;
    }

    public double getPrice() {
        return this.Price;
    }

    public void setPrice(double price) {
        this.Price = price;
    }

    public SaleTransaction getSaleTransaction() {
        return this.saleTransaction;
    }

}
