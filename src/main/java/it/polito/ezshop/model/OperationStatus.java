package it.polito.ezshop.model;

public enum OperationStatus {
    OPEN("open"),
    CLOSED("closed"),
    PAID("paid");

    private final String value;

    OperationStatus(String value) { this.value = value; }

    public String getValue() { return value; }
}
