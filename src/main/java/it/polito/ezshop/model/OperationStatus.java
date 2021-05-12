package it.polito.ezshop.model;

/**
 * Every balance operation has a status. Status can be
 *
 * OPEN:      Balance operation is being created and can be modified freely. The operation has no effect on the system yet.
 * CLOSED:    Balance operation has been created and can not be modified anymore. Still no effects on system at this stage.
 * PAID:      Balance operation has been paid, system balance is updated.
 * COMPLETED: Balance operation is completed, all changes are recorded in system.
 */
public enum OperationStatus {
<<<<<<< HEAD
    OPEN,
    CLOSED,
    PAID,
    COMPLETED
=======
    OPEN("open"),
    CLOSED("closed"),
    PAID("paid"),
    COMPLETED("completed");

    private final String value;

    public OperationStatus(String value) { this.value = value; }

    public String getValue() { return value; }
>>>>>>> 9647bda (fr7-8 methods have been added)
}
