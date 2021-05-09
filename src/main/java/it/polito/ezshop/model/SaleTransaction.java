package it.polito.ezshop.model;

import it.polito.ezshop.data.TicketEntry;

import java.util.List;


public class SaleTransaction implements it.polito.ezshop.data.SaleTransaction {
    private Integer TicketNumber;
    private List<TicketEntry> entries;
    private double discoutRate;
    private double price;

    public SaleTransaction(Integer ticketNumber, List<TicketEntry> entries, double discoutRate, double price) {
        this.TicketNumber = ticketNumber;
        this.entries = entries;
        this.discoutRate = discoutRate;
        this.price = price;
    }

    @Override
    public Integer getTicketNumber() {
        return this.TicketNumber;
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        this.TicketNumber = ticketNumber;
    }

    @Override
    public List<TicketEntry> getEntries() {
        return this.entries;
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        this.entries=entries;
    }

    @Override
    public double getDiscountRate() {
        return this.discoutRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discoutRate = discountRate;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }
}
